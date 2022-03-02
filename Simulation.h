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

    Energy PhotosynthesizeAt(QPointF location, const QColor& leafColour, const QColor& shadowColour) const;
    const std::vector<Plant>& GetPlants() const;
    const LightMap& GetLightMap() const;

    void AddPlant(Plant&& plant);
    void Tick();

    Simulation& operator=(const Simulation& other) = delete;
    Simulation& operator=(Simulation&& other) = default;

private:
    QRect bounds;

    LightMap lightMap;

    std::vector<Plant> seeds;
    std::vector<Plant> plants;
};

#endif // SIMULATION_H
