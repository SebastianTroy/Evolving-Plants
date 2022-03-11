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
    Phenotype();

    ///
    /// The following is intended for use by Gene sub-classes
    ///
    Energy metabolism;
    Energy seedSize;
    QColor leafColour;

    /**
     * To ensure the node tree is maintained in a valid state, limited access is
     * provided via these functions.
     *
     * Nodes can have custom parameters stored on a per node basis in a map,
     * these are entirely for the users own purposes and unless they are used
     * here they will have no effect on the final node structure.
     */
    void AddNode(Vec2 stemVector, double stemThickness, double leafRadius, std::map<int, double>&& customParameters = {});
    void AscendNodeTree();
    void DescendNodeTree();
    void SelectNextNode();
    void SelectPreviousNode();
    std::map<int, double>& GetCurrentNodesParameters();
    // Provide access to user settable Node items, but not protected variables
    void ForEachNode(const std::function<void(std::map<int, double>& nodeParams, Vec2& stemVector, double& nodeThickness, double& leafRadius)>& action);

    ///
    /// The following is intended for use by the Plant class
    ///

    void ForEachStem(double plantX, std::function<void(QLineF stem, double thickness, bool hasLeaf, double leafSize)>&& action) const;
    QRectF GetBounds(double plantX) const;
    bool IsValid() const;

private:
    /**
     * A Node represents a part of a plant, its base is its parent's lcoation
     * (or {0, 0} if the Node is root), and it extends to location.
     */
    struct Node {
        Node* parentNode;
        std::vector<std::shared_ptr<Node>> daughterNodes;

        // User settable
        std::map<int, double> customParameters;
        Vec2 stemVector;
        double thickness;
        double leafRadius;
    };

    void ForEachNode(Node& node, const std::function<void (Node& node, const Point& nodeLocation)>& action);
    void ForEachNode(const Node& node, const std::function<void (const Node& node, const Point& nodeLocation)>& action) const;

    std::shared_ptr<Node> root;
    Node* currentNode;
};

#endif // PHENOTYPE_H
