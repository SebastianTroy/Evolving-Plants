#include "Phenotype.h"

Phenotype::Phenotype()
    : metabolism(0_j)
    , seedSize(0_j)
    , leafColour(Qt::white)
    , leafRadius(1)
    , stemUnitLength(5)
    , stemRotationAngle(0.1)
    , finalised(false)
    , root(std::make_shared<Node>(nullptr, std::vector<std::shared_ptr<Node>>{}, 0, 0, Point{}))
    , currentNode(root.get())
    , height(0)
    , lean(0)
{
}

void Phenotype::AddNode()
{
    currentNode->daughterNodes.push_back(std::make_shared<Node>(currentNode, std::vector<std::shared_ptr<Node>>{}, 0, 0, Point{}));
}

void Phenotype::AscendNodeTree()
{
    if (!currentNode->daughterNodes.empty()) {
        currentNode = currentNode->daughterNodes.back().get();
    }
}

void Phenotype::DescendNodeTree()
{
    if (currentNode->parentNode) {
        currentNode = currentNode->parentNode;
    }
}

void Phenotype::IncreaseCurrentNodesLength(unsigned units)
{
    currentNode->additionalUnitsOfLength += units;
}

void Phenotype::RotateCurrentNode(int units)
{
    currentNode->rotationSteps += units;
}

void Phenotype::Finalise()
{
    if (!finalised) {
        finalised = true;

        ForEachNode(*root, [&](Node& n)
        {
            Point start = n.parentNode ? n.parentNode->location : Point{ 0, 0 };
            n.location = ApplyOffset(start, n.rotationSteps * stemRotationAngle, (n.additionalUnitsOfLength + 1) * stemUnitLength);

            bool hasLeaf = n.daughterNodes.empty();
            height = std::max(height, n.location.y);
            lean += (n.location.x) * (hasLeaf ? 2.0 : 1.0);

            unsigned descendantCount = 0;
            ForEachNode(n, [&descendantCount](Node&)
            {
                ++descendantCount;
            });
            // Don't count ourselves!
            --descendantCount;
            n.thickness += descendantCount * 0.25;
        });

        metabolism += std::pow(height, 2.0) * 0.5_j;
    }
}

void Phenotype::ForEachStem(double xPosition, std::function<void (QLineF, double, bool)>&& action) const
{
    ForEachNode(*root, [&](const Node& node)
    {
        Point base = node.parentNode ? node.parentNode->location : Point{ 0, 0 };
        Point tip = node.location;
        QLineF stem(base.x + xPosition, base.y, tip.x + xPosition, tip.y);
        bool hasLeaf = node.daughterNodes.empty();
        std::invoke(action, stem, node.thickness, hasLeaf);
    });
}

QRectF Phenotype::GetBounds(double xPosition) const
{
    util::MinMax<double> xRange(xPosition, xPosition);
    util::MinMax<double> yRange(0, 0);
    ForEachNode(*root, [&](const Node& n)
    {
        bool hasLeaf = n.daughterNodes.empty();
        if (hasLeaf) {
            xRange.ExpandToContain(n.location.x + leafRadius);
            xRange.ExpandToContain(n.location.x - leafRadius);
            yRange.ExpandToContain(n.location.y + leafRadius);
            yRange.ExpandToContain(n.location.y - leafRadius);
        } else {
            xRange.ExpandToContain(n.location.x);
            yRange.ExpandToContain(n.location.y);
        }
    });
    return QRectF(xRange.Min(), yRange.Min(), xRange.Max() - xRange.Min(), yRange.Max() - yRange.Min());
}

bool Phenotype::IsValid() const
{
    return (metabolism > 0) && (seedSize > 0) && (std::abs(lean) / height <= 0.6);
}

void Phenotype::ForEachNode(Node& node, const std::function<void (Node&)>& action)
{
    std::invoke(action, node);
    for (std::shared_ptr<Node>& child : node.daughterNodes) {
        ForEachNode(*child, action);
    }
}

void Phenotype::ForEachNode(const Node& node, const std::function<void (const Node&)>& action) const
{
    std::invoke(action, node);
    for (const std::shared_ptr<Node>& child : node.daughterNodes) {
        ForEachNode(*child, action);
    }
}
