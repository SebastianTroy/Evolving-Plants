#include "Plant.h"

#include "Simulation.h"
#include "Phenotype.h"

#include <Random.h>

#include <memory>

std::shared_ptr<Plant> Plant::Generate(std::vector<std::shared_ptr<Gene>>&& genetics, Energy energy, double x)
{
    Phenotype phenotype;

    for (const auto& gene : genetics) {
        gene->Express(phenotype);
    }

    if (!phenotype.IsValid()) {
        return nullptr;
    }

    return std::make_shared<Plant>(Plant(std::move(genetics), phenotype, energy, x));
}

void Plant::Tick(Simulation& sim, LightMap& lightMap)
{
    ++age;

    if (proportionGrown < 1.0) {
        // Shadows will be zero width when `proportionGrown == 0` so no need to add them first
        RemoveShadows(lightMap);
        proportionGrown  = std::min(proportionGrown + (5.0 / bounds.height()), 1.0);
        AddShadows(lightMap);
    }

    // FIXME do something nicer than: every 500 ticks that the plant is alive have 1 offspring
    if (timeToNextSeed < 0 && energy > seedSize) {
        // FIXME search sim for appropriate partner
        // FIXME make mutation rate user settable
        auto childGenes = genes;
        Random::ForNItems(childGenes, Random::Number(0, 1), [](std::shared_ptr<Gene>& item)
        {
            item = item->Mutated();
        });
        std::shared_ptr<Plant> plant = Generate(std::move(childGenes), std::sqrt(seedSize), Random::Number(bounds.left() - bounds.height(), bounds.right() + bounds.height()));
        if (plant) {
            sim.AddPlant(plant);
        }
        energy -= seedSize;
        timeToNextSeed = 500;
    } else {
        --timeToNextSeed;
    }

    metabolism += 0.02;
    energy -= metabolism * 0.01;

    ForEachStem([this, &lightMap](const QLineF& stem, double /*thickness*/, bool hasLeaf, double leafSize)
    {
        if (hasLeaf) {
            energy += PhotosynthesizeAt(lightMap, stem.p2() + QPointF(Random::Number<double>(leafSize / -2, leafSize / 2), 0));
        }
    });

    if (!IsAlive()) {
        RemoveShadows(lightMap);
    }
}

const std::vector<std::shared_ptr<Gene> >& Plant::GetGenetics() const
{
    return genes;
}

double Plant::GetPlantX() const
{
    return plantX;
}

const QRectF Plant::GetBounds() const
{
    return bounds;
}

double Plant::GetProportionGrown() const
{
    return proportionGrown;
}

double Plant::GetAge() const
{
    return age;
}

const QColor& Plant::GetLeafColour() const
{
    return leafColour;
}

const QColor& Plant::GetShadowColour() const
{
    return shadowColour;
}

void Plant::ForEachStem(std::function<void (QLineF, double, bool, double)>&& action) const
{
    if (proportionGrown < 1) {
        QPointF plantLocation(plantX, 0.0);
        for (const auto& [ stem, thickness, hasLeaf, leafSize ] : nodes) {
            QPointF scaledStemStart = ((stem.p1() - plantLocation) * proportionGrown) + plantLocation;
            QPointF scaledStemEnd = ((stem.p2() - plantLocation) * proportionGrown) + plantLocation;
            QLineF scaledStem(scaledStemStart, scaledStemEnd);

            action(scaledStem, thickness * proportionGrown, hasLeaf, leafSize * proportionGrown);
        }
    } else {
        for (const auto& [ stem, thickness, hasLeaf, leafSize ] : nodes) {
            action(stem, thickness, hasLeaf, leafSize);
        }
    }
}

bool Plant::Contains(const QPointF& p) const
{
    bool collides = false;
    if (bounds.contains(p)) {
        ForEachStem([&collides, &p](const QLineF& stem, double /*thickness*/, bool hasLeaf, double leafSize)
        {
            if (hasLeaf && QLineF(p, stem.p2()).length() <= leafSize) {
                collides = true;
            }
        });
    }
    return collides;
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

void Plant::AddShadows(LightMap& lightMap) const
{
    ForEachStem([this, &lightMap](const QLineF& stem, double /*thickness*/, bool hasLeaf, double leafSize)
    {
        if (hasLeaf) {
            lightMap.AddShadow(stem.p2().x() - (leafSize / 2.0), stem.p2().y(), leafSize, shadowColour);
        }
    });
}

void Plant::RemoveShadows(LightMap& lightMap) const
{
    ForEachStem([this, &lightMap](const QLineF& stem, double /*thickness*/, bool hasLeaf, double leafSize)
    {
        if (hasLeaf) {
            lightMap.RemoveShadow(stem.p2().x() - (leafSize / 2.0), stem.p2().y(), leafSize, shadowColour);
        }
    });
}

void Plant::Kill()
{
    metabolism = std::abs(energy) * 10;
    energy = -metabolism;
}

Plant::Plant(std::vector<std::shared_ptr<Gene>>&& genes, const Phenotype& phenotype, Energy startingEnergy, double xPosition)
    : age(0)
    , genes(std::move(genes))
    , leafColour(phenotype.leafColour)
    , shadowColour(CalculateShadowColour(leafColour))
    , plantX(xPosition)
    , bounds(phenotype.GetBounds(plantX))
    , seedSize(phenotype.seedSize)
    , energy(startingEnergy)
    , metabolism(phenotype.metabolism)
    , proportionGrown(0.0)
    , timeToNextSeed(50)
{
    phenotype.ForEachStem(xPosition, [this](QLineF stem, double thickness, bool hasLeaf, double leafSize)
    {
        nodes.emplace_back(stem, thickness, hasLeaf, leafSize);
    });
}

QColor Plant::CalculateShadowColour(const QColor& leafColour)
{
    QColor shadow;
    shadow.setRedF(qreal{ 1.0 } - leafColour.redF());
    shadow.setGreenF(qreal{ 1.0 } - leafColour.greenF());
    shadow.setBlueF(qreal{ 1.0 } - leafColour.blueF());
    return shadow;
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
