#include "Plant.h"

#include "Simulation.h"

#include <Shape.h>

#include <memory>

std::optional<Plant> Plant::Generate(Genetics&& genetics, double x)
{
    struct Node {
        Node* parentNode;
        std::vector<std::shared_ptr<Node>> daughterNodes;
        double rotation;
        double distance;
        Point location;
    };

    const double startLength = 25;
    const double startAngle = 0;

    Energy metabolism = 0.0_j;

    std::shared_ptr<Node> root = std::make_shared<Node>(nullptr, std::vector<std::shared_ptr<Node>>{}, startAngle, startLength, Point{});
    Node* currentNode = root.get();

    for (const Genetics::Instruction& instruction : genetics.GetInstructions()) {
        // Every instruction ups metabolism
        metabolism += 5.0_j;
        if (instruction == Genetics::Instruction::ADD_NODE) {
            currentNode->daughterNodes.push_back(std::make_shared<Node>(currentNode, std::vector<std::shared_ptr<Node>>{}, startAngle, startLength, Point{}));
            currentNode = currentNode->daughterNodes.back().get();
        } else if (instruction == Genetics::Instruction::CLIMB_NODE_TREE) {
            if (!currentNode->daughterNodes.empty()) {
                currentNode = currentNode->daughterNodes.back().get();
            }
        } else if (instruction == Genetics::Instruction::DESCEND_NODE_TREE) {
            if (currentNode->parentNode) {
                currentNode = currentNode->parentNode;
            }
        } else if (instruction == Genetics::Instruction::GROW_UP) {
            currentNode->distance += startLength;
        } else if (instruction == Genetics::Instruction::GROW_LEFT) {
            currentNode->rotation -= util::Tau / 9;
        } else if (instruction == Genetics::Instruction::GROW_RIGHT) {
            currentNode->rotation += util::Tau / 9;
        } else if (instruction == Genetics::Instruction::SKIP) {
            // (disincentivise long empty genomes)
            metabolism += 0.5_j;
        } else if (instruction == Genetics::Instruction::END_ALL) {
            break;
        }
    }

    std::function<void(Node&, std::function<void(Node&)>)> ForEachNode = [&](Node& node, std::function<void(Node&)>&& action) -> void
    {
        std::invoke(action, node);
        for (std::shared_ptr<Node>& child : node.daughterNodes) {
            ForEachNode(*child, action);
        }
    };

    util::MinMax<double> plantRange;
    double lean = 0;
    double height = 0;
    std::vector<Stem> stems;
    ForEachNode(*root, [&](Node& n)
    {
        Point start = n.parentNode ? n.parentNode->location : Point{ x, 0 };
        n.location = ApplyOffset(start, n.rotation, n.distance);

        bool hasLeaf = n.daughterNodes.empty();

        plantRange.ExpandToContain(n.location.x);
        if (hasLeaf) {
            plantRange.ExpandToContain(n.location.x - (MAX_LEAF_SIZE / 2.0));
            plantRange.ExpandToContain(n.location.x + (MAX_LEAF_SIZE / 2.0));
        }
        height = std::max(height, n.location.y);
        lean += (n.location.x - x) * (hasLeaf ? 2.0 : 1.0);

        float thickness = 1.0f;
        unsigned descendantCount = 0;
        ForEachNode(n, [&descendantCount](Node&)
        {
            ++descendantCount;
        });
        // Don't count ourselves!
        --descendantCount;
        thickness += descendantCount * 0.25;

        // TODO increase thickness based on number of descendant nodes
        stems.push_back(Stem{ QLineF(start.x, start.y, n.location.x, n.location.y), thickness, hasLeaf });
    });

    if (std::abs(lean) / height > 0.6) {
        return std::nullopt;
    }

    metabolism += std::pow(height, 2.0) * 0.5_j;

    return std::optional<Plant>(std::in_place, Plant{ std::move(genetics), std::move(plantRange), x, height, std::move(stems), metabolism });
}

void Plant::Tick(Simulation& sim, LightMap& lightMap)
{
    if (proportionGrown < 1.0) {
        // Shadows will be zero width when `proportionGrown == 0` so no need to add them first
        RemoveShadows(lightMap);
        proportionGrown  = std::min(proportionGrown + (5.0 / plantHeight), 1.0);
        AddShadows(lightMap);
    }

    // FIXME do something nicer than: every 500 ticks that the plant is alive have 1 offspring
    if (timeToNextSeed < 0 && energy > genes.GetSeedEnergy()) {
        // FIXME search sim for appropriate partner
        Genetics childGenes = genes.Mutated(genes, Random::Number(0, 1)); // FIXME make mutation rate user settable
        std::optional<Plant> plant = Generate(std::move(childGenes), Random::Number(bounds.Min() - plantHeight, bounds.Max() + plantHeight));
        if (plant) {
            sim.AddPlant(std::move(plant.value()));
        }
        energy -= genes.GetSeedEnergy();
        timeToNextSeed = 500;
    } else {
        --timeToNextSeed;
    }

    metabolism += 0.02;
    energy -= metabolism * 0.01;

    ForEachStem([this, &lightMap](const QLineF& stem, double /*thickness*/, bool hasLeaf)
    {
        if (hasLeaf) {
            energy += PhotosynthesizeAt(lightMap, stem.p2() + QPointF(Random::Number<double>(GetLeafSize() / -2, GetLeafSize() / 2), 0));
        }
    });

    if (!IsAlive()) {
        RemoveShadows(lightMap);
    }
}

const Genetics& Plant::GetGenetics() const
{
    return genes;
}

double Plant::GetPlantX() const
{
    return plantX;
}

double Plant::GetMinX() const
{
    return bounds.Min();
}

double Plant::GetMaxX() const
{
    return bounds.Max();
}

double Plant::GetHeight() const
{
    return plantHeight * proportionGrown;
}

double Plant::GetProportionGrown() const
{
    return proportionGrown;
}

const QColor& Plant::GetLeafColour() const
{
    return genes.GetLeafColour();
}

const QColor& Plant::GetShadowColour() const
{
    return shadowColour;
}

void Plant::ForEachStem(std::function<void (QLineF, double, bool)>&& action) const
{
    if (proportionGrown < 1) {
        QPointF plantLocation(plantX, 0.0);
        for (const auto& [ stem, thickness, hasLeaf ] : nodes) {
            QPointF scaledStemStart = ((stem.p1() - plantLocation) * proportionGrown) + plantLocation;
            QPointF scaledStemEnd = ((stem.p2() - plantLocation) * proportionGrown) + plantLocation;
            QLineF scaledStem(scaledStemStart, scaledStemEnd);

            action(scaledStem, thickness * proportionGrown, hasLeaf);
        }
    } else {
        for (const auto& [ stem, thickness, hasLeaf ] : nodes) {
            action(stem, thickness, hasLeaf);
        }
    }
}

bool Plant::Contains(const QPointF& p) const
{
    bool collides = false;
    ForEachStem([&collides, &p, leafSize = GetLeafSize()](const QLineF& stem, double /*thickness*/, bool hasLeaf)
    {
        if (hasLeaf && QLineF(p, stem.p2()).length() <= leafSize) {
            collides = true;
        }
    });
    return collides;
}

double Plant::GetLeafSize() const
{
    return MAX_LEAF_SIZE * proportionGrown;
}

const Energy& Plant::GetEnergy() const
{
    return energy;
}

const Energy& Plant::GetMetabolism() const
{
    return metabolism;
}

bool Plant::IsAlive() const
{
    return GetEnergy() >= 0_j;
}

Plant::Plant(Genetics&& genes, util::MinMax<double>&& bounds, double xPosition, double plantHeight, std::vector<Stem>&& nodes, Energy metabolism)
    : genes(std::move(genes))
    , shadowColour(CalculateShadowColour(genes.GetLeafColour()))
    , plantX(xPosition)
    , plantHeight(plantHeight)
    , bounds(std::move(bounds))
    , nodes(std::move(nodes))
    , energy(std::sqrt(genes.GetSeedEnergy()))
    , metabolism(metabolism)
    , proportionGrown(0.0)
    , timeToNextSeed(50)
{
}

QColor Plant::CalculateShadowColour(const QColor& leafColour)
{
    QColor shadow;
    shadow.setRedF(qreal{ 1.0 } - leafColour.redF());
    shadow.setGreenF(qreal{ 1.0 } - leafColour.greenF());
    shadow.setBlueF(qreal{ 1.0 } - leafColour.blueF());
    return shadow;
}

void Plant::AddShadows(LightMap& lightMap) const
{
    ForEachStem([this, &lightMap](const QLineF& stem, double /*thickness*/, bool hasLeaf)
    {
        if (hasLeaf) {
            lightMap.AddShadow(stem.p2().x() - (GetLeafSize() / 2.0), stem.p2().y(), GetLeafSize(), shadowColour);
        }
    });
}

void Plant::RemoveShadows(LightMap& lightMap) const
{
    ForEachStem([this, &lightMap](const QLineF& stem, double /*thickness*/, bool hasLeaf)
    {
        if (hasLeaf) {
            lightMap.RemoveShadow(stem.p2().x() - (GetLeafSize() / 2.0), stem.p2().y(), GetLeafSize(), shadowColour);
        }
    });
}

Energy Plant::PhotosynthesizeAt(LightMap& lightMap, QPointF location) const
{
    if (!lightMap.GetRect().contains(location.toPoint())) {
        return 0_j;
    }

    LightMap::Colour availableLight = lightMap.GetLightAt(location.x(), location.y());
    // minus shadow to stop leaf shading itself
    availableLight.red = std::clamp(availableLight.red + shadowColour.red(), 0, 255);
    availableLight.green = std::clamp(availableLight.green + shadowColour.green(), 0, 255);
    availableLight.blue = std::clamp(availableLight.blue + shadowColour.blue(), 0, 255);

    /*
     * The leaf colour represents the light a leaf DOESN'T absorb.
     */
    double energyGained = 0;
    energyGained += std::max(0, (availableLight.red - GetLeafColour().red()))
                  + std::max(0, (availableLight.green - GetLeafColour().green()))
                  + std::max(0, (availableLight.blue - GetLeafColour().blue()));
    /*
     * If energy gained is over 200 the extra energy is subtracted
     * from the energy gained. In nature photosynthesis is inhibited
     * by too much light and dark adapted species are actually at a
     * large disadvantage in normal conditions.
     */
    const Energy energyCap = 150_j;
    if (energyGained > energyCap) {
        energyGained = std::max(0.0_j, energyCap - (energyGained - energyCap));
    }

    return energyGained;
}
