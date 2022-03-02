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

    Energy metabolism = 1.0_j;

    std::shared_ptr<Node> root = std::make_shared<Node>(nullptr, std::vector<std::shared_ptr<Node>>{}, startAngle, startLength, Point{});
    std::shared_ptr<Node> currentNode = root;

    for (const Genetics::Instruction& instruction : genetics.GetInstructions()) {
        // Every instruction ups metabolism
        metabolism += 0.1_j;
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
            metabolism += 1_j;
            currentNode->distance += startLength;
        } else if (instruction == Genetics::Instruction::GROW_LEFT) {
            currentNode->rotation -= util::Tau / 9;
        } else if (instruction == Genetics::Instruction::GROW_RIGHT) {
            currentNode->rotation += util::Tau / 9;
        } else if (instruction == Genetics::Instruction::SKIP) {
            // (disincentivise long empty genomes)
            metabolism += 0.1_j;
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

    // FIXME perhaps make this part of the genetics
    const double leafSize = 15;

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
            plantRange.ExpandToContain(n.location.x - (leafSize / 2.0));
            plantRange.ExpandToContain(n.location.x + (leafSize / 2.0));
        }
        height = std::max(height, n.location.y);
        lean += (n.location.x - x) * (isLeaf ? 2.0 : 1.0);

        stems.push_back(Stem{ QLineF(start.x, start.y, n.location.x, n.location.y), isLeaf });
    });

    if (std::abs(lean) / height > 0.6) {
        return std::nullopt;
    }

    return std::optional<Plant>(std::in_place, Plant{ std::move(genetics), std::move(plantRange), x, height, leafSize, std::move(stems), metabolism });
}

void Plant::Tick(Simulation& sim)
{
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

    for (const auto& [ stem, hasLeaf ] : nodes) {
        if (hasLeaf) {
            energy += sim.PhotosynthesizeAt(stem.p2() + QPointF(Random::Number<double>(leafSize / -2, leafSize / 2), 0), genes.GetLeafColour(), shadowColour);
        }
    }
}

void Plant::Grow()
{
    // FIXME set grow speed based on final height, use energy to grow
    // Fully grown in 20 ticks
    proportionGrown += 0.05;
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

double Plant::GetLeafSize() const
{
    return leafSize;
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
    for (const auto& [ stem, hasLeaf ] : nodes) {
        if (hasLeaf && QLineF(p, stem.p2()).length() <= leafSize) {
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

Plant::Plant(Genetics&& genes, util::MinMax<double>&& bounds, double xPosition, double plantHeight, double leafSize, std::vector<Stem>&& nodes, Energy metabolism)
    : genes(std::move(genes))
    , shadowColour(CalculateShadowColour(genes.GetLeafColour()))
    , plantX(xPosition)
    , plantHeight(plantHeight)
    , bounds(std::move(bounds))
    , leafSize(leafSize)
    , nodes(std::move(nodes))
    , energy(genes.GetSeedEnergy() / 3.0)
    , metabolism(metabolism)
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
