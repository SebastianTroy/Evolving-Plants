#ifndef SIMULATION_H
#define SIMULATION_H

#include "Plant.h"
#include "LightMap.h"

#include <QRect>
#include <QColor>
#include <QPointF>

#include <vector>

class Simulation {
public:
    Simulation(size_t width, size_t height);

    std::shared_ptr<Plant> GetPlantAt(const QPointF& location) const;
    const std::vector<std::shared_ptr<Plant>>& GetPlants() const;
    const uint64_t& GetTickCount() const;
    const LightMap& GetLightMap() const;

    void AddPlant(const std::shared_ptr<Plant>& plant);
    void RemovePlantsAt(const QPointF& location);
    void Tick();
    void SetBounds(int64_t width, int64_t height);

    Simulation& operator=(const Simulation& other) = delete;
    Simulation& operator=(Simulation&& other) = default;

private:
    LightMap lightMap;
    uint64_t tickCount;

    std::vector<std::shared_ptr<Plant>> seeds;
    std::vector<std::shared_ptr<Plant>> plants;
};

#endif // SIMULATION_H
