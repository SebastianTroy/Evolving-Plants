#include "Plant.h"

#include "Simulation.h"

#include <Shape.h>

#include <memory>

std::optional<Plant> Plant::Generate(Genetics&& genetics, double x)
{
    struct Node {
        std::shared_ptr<Node> parentNode;
        std::vector<std::shared_ptr<Node>> daughterNodes;
        double rotation;
        double distance;
        Point location;
    };

    const double startLength = 25;
    const double startAngle = 0;

    Energy metabolism = 0.0_j;

    std::shared_ptr<Node> root = std::make_shared<Node>(nullptr, std::vector<std::shared_ptr<Node>>{}, startAngle, startLength, Point{});
    std::shared_ptr<Node> currentNode = root;

    for (const Genetics::Instruction& instruction : genetics.GetInstructions()) {
        // Every instruction ups metabolism
        metabolism += 5.0_j;
        if (instruction == Genetics::Instruction::ADD_NODE) {
            currentNode->daughterNodes.push_back(std::make_shared<Node>(currentNode, std::vector<std::shared_ptr<Node>>{}, startAngle, startLength, Point{}));
            currentNode = currentNode->daughterNodes.back();
        } else if (instruction == Genetics::Instruction::CLIMB_NODE_TREE) {
            if (!currentNode->daughterNodes.empty()) {
                currentNode = currentNode->daughterNodes.back();
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

    std::function<void(Node&, std::function<void(Node&)>)> ForEachNode = [&](Node& node, std::function<void(Node&)> action) -> void
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

        bool isLeaf = n.daughterNodes.empty();

        plantRange.ExpandToContain(n.location.x);
        if (isLeaf) {
            plantRange.ExpandToContain(n.location.x - (LEAF_SIZE / 2.0));
            plantRange.ExpandToContain(n.location.x + (LEAF_SIZE / 2.0));
        }
        height = std::max(height, n.location.y);
        lean += (n.location.x - x) * (isLeaf ? 2.0 : 1.0);

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
        stems.push_back(Stem{ QLineF(start.x, start.y, n.location.x, n.location.y), thickness, isLeaf });
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
        proportionGrown += 5.0 / plantHeight;
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

    for (const auto& [ stem, thickness, hasLeaf ] : nodes) {
        Q_UNUSED(thickness);
        if (hasLeaf) {
            energy += PhotosynthesizeAt(lightMap, stem.p2() + QPointF(Random::Number<double>(LEAF_SIZE / -2, LEAF_SIZE / 2), 0));
        }
    }

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
    return plantHeight;
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

const std::vector<Plant::Stem>& Plant::GetNodes() const
{
    return nodes;
}

bool Plant::Contains(QPointF p) const
{
    for (const auto& [ stem, thickness, hasLeaf ] : nodes) {
        Q_UNUSED(thickness);
        if (hasLeaf && QLineF(p, stem.p2()).length() <= LEAF_SIZE) {
            return true;
        }
    }
    return false;
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
    QPointF plantLocation(plantX, 0.0);
    for (const auto& [ stem, thickness, hasLeaf ] : nodes) {
        Q_UNUSED(thickness)
        if (hasLeaf) {
            QLineF scaledStem = stem.translated(-plantLocation);
            scaledStem.setLength(stem.length() * proportionGrown);
            scaledStem.translate(plantLocation);

            lightMap.AddShadow(scaledStem.p2().x() - (proportionGrown * (LEAF_SIZE / 2.0)), scaledStem.p2().y(), proportionGrown * LEAF_SIZE, shadowColour);
        }
    }
}

void Plant::RemoveShadows(LightMap& lightMap) const
{
    QPointF plantLocation(plantX, 0.0);
    for (const auto& [ stem, thickness, hasLeaf ] : nodes) {
        Q_UNUSED(thickness)
        if (hasLeaf) {
            QLineF scaledStem = stem.translated(-plantLocation);
            scaledStem.setLength(stem.length() * proportionGrown);
            scaledStem.translate(plantLocation);

            lightMap.RemoveShadow(scaledStem.p2().x() - (proportionGrown * (LEAF_SIZE / 2.0)), scaledStem.p2().y(), proportionGrown * LEAF_SIZE, shadowColour);
        }
    }
}

Energy Plant::PhotosynthesizeAt(LightMap& lightMap, QPointF location) const
{
    if (!lightMap.GetRect().contains(location.toPoint())) {
        return 0_j;
    }

    double energyGained = 0;
    // minus shadow to stop leaf shading itself
    LightMap::Colour availableLight = lightMap.GetLightMinusShadowAt(location.x(), location.y(), shadowColour);
    /*
     * The leaf colour represents the light a leaf DOESN'T absorb.
     */
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
