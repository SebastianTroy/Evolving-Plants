#include "Simulation.h"

#include <Shape.h>

#include <algorithm>
#include <ranges>

Simulation::Simulation(size_t width, size_t height)
    : lightMap(width, height)
    , tickCount(0)
    , plantCount(0)
{
    elapsedTime.start();
}

std::shared_ptr<Plant> Simulation::GetPlantAt(const QPointF& location) const
{
    for (auto& plant : plants) {
        if (plant->Contains(location)) {
            return plant;
        }
    }
    return nullptr;
}

const std::vector<std::shared_ptr<Plant>>& Simulation::GetPlants() const
{
    return plants;
}

const uint64_t& Simulation::GetTickCount() const
{
    return tickCount;
}

uint64_t Simulation::GetLivingPlantCount() const
{
    return plants.size();
}

const uint64_t& Simulation::GetTotalPlantCount() const
{
    return plantCount;
}

const QElapsedTimer& Simulation::GetRuntime() const
{
    return elapsedTime;
}

const LightMap& Simulation::GetLightMap() const
{
    return lightMap;
}

void Simulation::AddPlant(const std::shared_ptr<Plant>& plant)
{
    if (plant && lightMap.GetRect().intersects(plant->GetBounds().toRect())) {
        // Can't add directly to plants as we may be mid tick
        seeds.push_back(plant);
    }
}

void Simulation::RemovePlantsAt(const QPointF& location)
{
    for (auto& plant : plants) {
        if (plant->Contains(location)) {
            plant->Kill();
        }
    }
}

void Simulation::Tick()
{
    plantCount += seeds.size();
    std::ranges::move(seeds, std::back_inserter(plants));
    seeds.clear();

    // Iterate plants once and tick/grow/remove dead plants
    std::erase_if(plants, [this](std::shared_ptr<Plant>& p) -> bool
    {
        p->Tick(*this, lightMap);
        return !p->IsAlive();
    });

    ++tickCount;
}

void Simulation::SetBounds(int64_t width, int64_t height)
{
    LightMap newLightMap(width, height);
    for (const auto& plant : plants) {
        if (plant->IsAlive()) {
            plant->AddShadows(newLightMap);
        }
    }
    lightMap = std::move(newLightMap);
}
