#ifndef PLANT_H
#define PLANT_H

#include "Gene.h"

#include <Energy.h>
#include <MinMax.h>

#include <QPointF>
#include <QLineF>
#include <QRectF>
#include <QColor>

#include <memory>
#include <vector>

class Simulation;
class LightMap;
class Phenotype;

class Plant {
public:
    Plant(const Plant& other) = delete;
    Plant(Plant&& other) = default;

    static std::shared_ptr<Plant> Generate(std::vector<std::shared_ptr<Gene>>&& genetics, Energy energy, double x);

    void Tick(Simulation& sim, LightMap& lightmap);

    const std::vector<std::shared_ptr<Gene>>& GetGenetics() const;
    double GetPlantX() const;
    const QRectF GetBounds() const;
    double GetProportionGrown() const;
    double GetAge() const;
    const QColor& GetLeafColour() const;
    const QColor& GetShadowColour() const;
    void ForEachStem(std::function<void(QLineF stem, double thickness, bool hasLeaf, double leafSize)>&& action) const;
    bool Contains(const QPointF& p) const;

    const Energy& GetEnergy() const;
    const Energy& GetMetabolism() const;
    bool IsAlive() const;

    void AddShadows(LightMap& lightMap) const;
    void RemoveShadows(LightMap& lightMap) const;

    void Kill();

    Plant& operator=(const Plant& other) = delete;
    Plant& operator=(Plant&& other) = default;

private:
    friend std::shared_ptr<Plant>;

    struct Stem {
        QLineF stem;
        qreal thickness;
        bool hasLeaf;
        double leafSize;
    };

    qulonglong age;
    std::vector<std::shared_ptr<Gene>> genes;
    QColor leafColour;
    QColor shadowColour;
    double plantX;
    QRectF bounds;
    Energy seedSize;
    std::vector<Stem> nodes;

    Energy energy;
    Energy metabolism;
    double proportionGrown;
    int timeToNextSeed;

    Plant(std::vector<std::shared_ptr<Gene>>&& genes, const Phenotype& phenotype, Energy startingEnergy, double xPosition);

    static QColor CalculateShadowColour(const QColor& leafColour);

    Energy PhotosynthesizeAt(LightMap& lightMap, QPointF location) const;
};

#endif // PLANT_H
