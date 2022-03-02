#include "Simulation.h"

#include <Shape.h>

#include <ranges>

Simulation::Simulation(size_t width, size_t height)
    : bounds(0, 0, width, height)
    , lightMap(width, height)
{
}

Energy Simulation::PhotosynthesizeAt(QPointF location, const QColor& leafColour, const QColor& shadowColour) const
{
    if (!bounds.contains(location.toPoint())) {
        return 0_j;
    }

    double energyGained = 0;
    // minus shadow to stop leaf shading itself
    LightMap::Colour availableLight = lightMap.GetLightMinusShadowAt(location.x(), location.y(), shadowColour);
    /*
     * The leaf colour represents the light a leaf DOESN'T absorb.
     */
    energyGained += std::max(0, (availableLight.red - leafColour.red()))
                    + std::max(0, (availableLight.green - leafColour.green()))
                    + std::max(0, (availableLight.blue - leafColour.blue()));
    /*
     * If energy gained is over 200 the extra energy is subtracted
     * from the energy gained. In nature photosynthesis is inhibited
     * by too much light and dark adapted species are actually at a
     * large disadvantage in normal conditions.
     */
    if (energyGained > 200)
        energyGained = std::max(0.0, 200.0 - (energyGained - 200.0));

    return energyGained;
}

const std::vector<Plant>& Simulation::GetPlants() const
{
    return plants;
}

const LightMap& Simulation::GetLightMap() const
{
    return lightMap;
}

void Simulation::AddPlant(Plant&& plant)
{
    if (bounds.contains(QPoint(plant.GetPlantX(), 0))) {
        // Can't add directly to plants as we may be mid tick
        seeds.emplace_back(std::move(plant));
    }
}

void Simulation::Tick()
{
    std::ranges::move(seeds, std::back_inserter(plants));
    seeds.clear();

    // Iterate plants once and tick/grow/remove dead plants
    std::erase_if(plants, [this](Plant& p) -> bool
    {
        if (p.GetProportionGrown() < 1.0) {
            p.Grow();
            if (p.GetProportionGrown() >= 1.0) {
                for (const auto& [ stem, hasLeaf ] : p.GetNodes()) {
                    if (hasLeaf) {
                        lightMap.AddShadow(stem.p2().x() - (p.GetLeafSize() / 2.0), stem.p2().y(), p.GetLeafSize(), p.GetShadowColour());
                    }
                }
            }
        } else {
            p.Tick(*this);
        }

        bool dead = p.GetEnergy() <= 0_j;
        if (dead && p.GetProportionGrown() >= 1.0) {
            for (const auto& [ stem, hasLeaf ] : p.GetNodes()) {
                if (hasLeaf) {
                    lightMap.RemoveShadow(stem.p2().x() - (p.GetLeafSize() / 2.0), stem.p2().y(), p.GetLeafSize(), p.GetShadowColour());
                }
            }
        }
        return dead;
    });
}
