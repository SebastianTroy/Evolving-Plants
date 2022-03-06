#ifndef PHENOTYPE_H
#define PHENOTYPE_H

#include <Shape.h>
#include <MinMax.h>
#include <Energy.h>

#include <QLineF>
#include <QRectF>
#include <QColor>

#include <vector>
#include <memory>

/**
 * A phenotype is the physical expression of a Genotype.
 *
 * The aim for this class is both to simplify the Plant constructor and to give
 * a mutable set of variables to the genetics to populate, removing the need for
 * a plant to have any concept of what the genes actually do.
 */
class Phenotype {
public:
    Energy metabolism;
    Energy seedSize;
    QColor leafColour;
    double leafRadius;
    double stemUnitLength;
    double stemRotationAngle;

    Phenotype();

    /**
     * To ensure the node tree is maintained in a valid state, limited access is
     * provided via these functions
     */
    void AddNode();
    void AscendNodeTree();
    void DescendNodeTree();
    void IncreaseCurrentNodesLength(unsigned units);
    void RotateCurrentNode(int units);

    /**
     * Must be called after all genes have been expressed, but before the plant
     * is constructed.
     */
    void Finalise();

    void ForEachStem(double xPosition, std::function<void(QLineF stem, double thickness, bool hasLeaf)>&& action) const;
    QRectF GetBounds(double xPosition) const;
    bool IsValid() const;

private:
    /**
     * A Node represents a part of a plant, its base is its parent's lcoation
     * (or {0, 0} if the Node is root), and it extends to location.
     */
    struct Node {
        Node* parentNode;
        std::vector<std::shared_ptr<Node>> daughterNodes;
        int rotationSteps;
        unsigned additionalUnitsOfLength;

        // Calculated once after all gneetics have been processed
        Point location;
        double thickness;
    };

    void ForEachNode(Node& node, const std::function<void(Node&)>& action);
    void ForEachNode(const Node& node, const std::function<void(const Node&)>& action) const;

    bool finalised;
    std::shared_ptr<Node> root;
    Node* currentNode;
    double height;
    double lean;
};

#endif // PHENOTYPE_H
