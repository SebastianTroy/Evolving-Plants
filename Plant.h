#ifndef PLANT_H
#define PLANT_H

#include "Genetics.h"

#include <Energy.h>
#include <MathConstants.h>
#include <MinMax.h>

#include <QPointF>
#include <QLineF>

#include <optional>

class Simulation;
class LightMap;

class Plant {
public:
    static inline constexpr int LEAF_SIZE = 15;

    struct Stem {
        QLineF stem;
        qreal thickness;
        bool hasLeaf;
    };

    Plant(const Plant& other) = delete;
    Plant(Plant&& other) = default;

    static std::optional<Plant> Generate(Genetics&& genetics, double x);

    void Tick(Simulation& sim, LightMap& lightmap);

    const Genetics& GetGenetics() const;
    double GetPlantX() const;
    double GetMinX() const;
    double GetMaxX() const;
    double GetHeight() const;
    double GetProportionGrown() const;
    const QColor& GetLeafColour() const;
    const QColor& GetShadowColour() const;
    const std::vector<Stem>& GetNodes() const;
    bool Contains(QPointF p) const;

    const Energy& GetEnergy() const;
    const Energy& GetMetabolism() const;
    bool IsAlive() const;

    Plant& operator=(const Plant& other) = delete;
    Plant& operator=(Plant&& other) = default;

private:
    Genetics genes;
    QColor shadowColour;
    double plantX;
    double plantHeight;
    util::MinMax<double> bounds;
    std::vector<Stem> nodes;

    Energy energy;
    Energy metabolism;
    double proportionGrown;
    int timeToNextSeed;

    Plant(Genetics&& genes, util::MinMax<double>&& bounds, double xPosition, double plantHeight, std::vector<Stem>&& nodes, Energy metabolism);

    static QColor CalculateShadowColour(const QColor& leafColour);

    void AddShadows(LightMap& lightMap) const;
    void RemoveShadows(LightMap& lightMap) const;

    Energy PhotosynthesizeAt(LightMap& lightMap, QPointF location) const;
};

#endif // PLANT_H
