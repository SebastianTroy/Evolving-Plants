#include "Simulation.h"

#include <Shape.h>

#include <algorithm>
#include <ranges>

Simulation::Simulation(size_t width, size_t height)
    : lightMap(width, height)
{
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
    if (lightMap.GetRect().intersects(plant.GetBounds().toRect())) {
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
        p.Tick(*this, lightMap);
        return !p.IsAlive();
    });
}
