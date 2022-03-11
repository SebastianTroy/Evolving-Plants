#include "Phenotype.h"

Phenotype::Phenotype()
    : metabolism(0_j)
    , seedSize(0_j)
    , leafColour(Qt::white)
    , root(std::make_shared<Node>(nullptr, std::vector<std::shared_ptr<Node>>{}, std::map<int, double>{}, Vec2{}, 0.0, 0.0))
    , currentNode(root.get())
{
}

void Phenotype::AddNode(Vec2 stemVector, double stemThickness, double leafRadius, std::map<int, double>&& customParameters)
{
    currentNode->daughterNodes.push_back(std::make_shared<Node>(currentNode, std::vector<std::shared_ptr<Node>>{}, std::move(customParameters), stemVector, stemThickness, leafRadius));
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

void Phenotype::SelectNextNode()
{
    // Iterate all nodes and when we find the current one, set current to the next node
    bool nodeIsNext = false;
    ForEachNode(*currentNode, [&, this](Node& node, const Point&)
    {
        if (nodeIsNext) {
            currentNode = &node;
            nodeIsNext = false;
        }
        if (&node == currentNode) {
            nodeIsNext = true;
        }
    });
}

void Phenotype::SelectPreviousNode()
{
    // Iterate all nodes and when we find the current one, set current to the previous node
    Node* lastNode = root.get();
    ForEachNode(*currentNode, [&, this](Node& node, const Point&)
    {
        if (&node == currentNode) {
            currentNode = lastNode;
        }
        lastNode = &node;
    });
}

std::map<int, double>& Phenotype::GetCurrentNodesParameters()
{
    return currentNode->customParameters;
}

void Phenotype::ForEachNode(const std::function<void (std::map<int, double>&, Vec2&, double&, double&)>& action)
{
    ForEachNode(*root, [&](Node& node, const Point& /*nodeLocation*/)
    {
        std::invoke(action, node.customParameters, node.stemVector, node.thickness, node.leafRadius);
    });
}

void Phenotype::ForEachStem(double plantX, std::function<void (QLineF, double, bool, double)>&& action) const
{
    ForEachNode(*root, [&](const Node& node, const Point& nodeTip)
    {
        Point tip = nodeTip;
        Point base = nodeTip - node.stemVector;
        QLineF stem(base.x + plantX, base.y, tip.x + plantX, tip.y);
        bool hasLeaf = node.daughterNodes.empty();
        std::invoke(action, stem, node.thickness, hasLeaf, node.leafRadius * 2);
    });
}

QRectF Phenotype::GetBounds(double plantX) const
{
    util::MinMax<double> xRange(plantX, plantX);
    util::MinMax<double> yRange(0, 0);

    ForEachNode(*root, [&](const Node& n, const Point& nodeTip)
    {
        bool hasLeaf = n.daughterNodes.empty();
        if (hasLeaf) {
            xRange.ExpandToContain(plantX + nodeTip.x + n.leafRadius);
            xRange.ExpandToContain(plantX + nodeTip.x - n.leafRadius);
            yRange.ExpandToContain(nodeTip.y + n.leafRadius);
            yRange.ExpandToContain(nodeTip.y - n.leafRadius);
        } else {
            xRange.ExpandToContain(plantX + nodeTip.x);
            yRange.ExpandToContain(nodeTip.y);
        }
    });
    return QRectF(xRange.Min(), yRange.Min(), xRange.Max() - xRange.Min(), yRange.Max() - yRange.Min());
}

bool Phenotype::IsValid() const
{
    double lean = 0;
    double height = 0;
    ForEachNode(*root, [&](const Node& n, const Point& nodeLocation)
    {
        bool hasLeaf = n.daughterNodes.empty();
        height = std::max(height, nodeLocation.y);
        lean += (nodeLocation.x) * (hasLeaf ? 1.25 : 0.5);
    });
    return (metabolism > 0) && (seedSize > 0) && (std::abs(lean) / height <= 0.6);
}

void Phenotype::ForEachNode(Node& node, const std::function<void (Node&, const Point&)>& action)
{
    // We want to efficiently track the node tip positions as we go along

    // Find the position of the start node's parent
    Point nodeTip = Point{ 0, 0 };
    {
        Node* currentNode = node.parentNode;
        while (currentNode) {
            nodeTip = nodeTip + currentNode->stemVector;
            currentNode = currentNode->parentNode;
        }
    }

    // A recursive function that pushes and pops the current node location as we go up and down the tree
    std::function<void(Node&, const std::function<void (Node&, const Point&)>&)> ForEachNodeRecursive;
    ForEachNodeRecursive = [&](Node& node, const std::function<void (Node&, const Point&)>& action)
    {
        nodeTip = nodeTip + node.stemVector;
        std::invoke(action, node, nodeTip);
        for (std::shared_ptr<Node>& child : node.daughterNodes) {
            ForEachNodeRecursive(*child, action);
        }
        nodeTip = nodeTip - node.stemVector;
    };

    ForEachNodeRecursive(node, action);
}

void Phenotype::ForEachNode(const Node& node, const std::function<void (const Node&, const Point&)>& action) const
{
    // We want to efficiently track the node tip positions as we go along

    // Find the position of the start node's parent
    Point nodeTip = Point{ 0, 0 };
    {
        Node* currentNode = node.parentNode;
        while (currentNode) {
            nodeTip = nodeTip + currentNode->stemVector;
            currentNode = currentNode->parentNode;
        }
    }

    // A recursive function that pushes and pops the current node location as we go up and down the tree
    std::function<void(const Node&, const std::function<void (const Node&, const Point&)>&)> ForEachNodeRecursive;
    ForEachNodeRecursive = [&](const Node& node, const std::function<void (const Node&, const Point&)>& action)
    {
        nodeTip = nodeTip + node.stemVector;
        std::invoke(action, node, nodeTip);
        for (const std::shared_ptr<Node>& child : node.daughterNodes) {
            ForEachNodeRecursive(*child, action);
        }
        nodeTip = nodeTip - node.stemVector;
    };

    ForEachNodeRecursive(node, action);
}
