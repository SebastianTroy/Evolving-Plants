#include "SimulationViewWidget.h"

#include <Random.h>

#include <QPainter>
#include <QLocale>

SimulationViewWidget::SimulationViewWidget(QWidget* parent)
    : QWidget(parent)
    , simulationDriver(this)
    , sim(0, 0)
{
    simulationDriver.setSingleShot(false);
    simulationDriver.setInterval(0);
    connect(&simulationDriver, &QTimer::timeout, this, [&]()
    {
        for (int i = 0; i < 100; ++i) {
            sim.Tick();
            ++tickCount;
        }
        update();
    });
}

void SimulationViewWidget::paintEvent(QPaintEvent* /*event*/)
{
    QPainter paint(this);

    // Paint the sky
    paint.fillRect(rect(), QGradient(QGradient::SkyGlider));

    paint.drawText(0, 20, QLocale::system().toString(tickCount));

    // Flip painter vertically so x=0 is at bottom of the screen
    paint.translate(0, height());
    paint.scale(1, -1);

    // Paint the ground
    const int groundHeght = 5;
    paint.fillRect(QRect(0, 0, width(), groundHeght), Qt::green);

    // Move the painter so x=0 is at groundHeght
    paint.translate(0, groundHeght);

    if (viewLight) {
        QRect viewport = sim.GetLightMap().GetRect().intersected(rect());
        paint.drawImage(QPoint(0, 0), sim.GetLightMap().GetLightImage(viewport));
    }

    // Paint the plants, shortest last so they aren't hidden by taller plant's stems
    std::vector<const Plant*> sortedPlants;
    for (const Plant& plant : sim.GetPlants()) {
        // TODO only copy and sort plants visible on screen
        sortedPlants.push_back(&plant);
    }
    std::stable_sort(std::begin(sortedPlants), std::end(sortedPlants), [](const Plant* a, const Plant* b)
    {
        return a->GetHeight() > b->GetHeight();
    });

    paint.setRenderHint(QPainter::RenderHint::Antialiasing, true);

    for (const Plant* plantPtr : sortedPlants) {
        const Plant& plant = *plantPtr;

        QPointF plantLocation(plant.GetPlantX(), 0);
        plant.ForEachStem([&](const QLineF& stem, double thickness, bool hasLeaf)
        {
            QPen pen(QColor::fromRgb(73, 39, 14));
            pen.setWidthF(std::max(1.0, thickness));
            paint.setPen(pen);
            paint.drawLine(stem);

            if (hasLeaf) {
                paint.setPen(Qt::black);
                paint.setBrush(plant.GetLeafColour());
                double radius = (plant.GetLeafSize() / 2) * plant.GetProportionGrown();
                paint.drawEllipse(stem.p2(), radius, radius * 0.66);
            }
        });
    }
}

void SimulationViewWidget::showEvent(QShowEvent*)
{
    if (sim.GetLightMap().GetRect().width() == 0) {
        sim = Simulation(width(), height());
        for (int x = 0; x < width(); x += width() / 30) {
            sim.AddPlant(*Plant::Generate(Genetics("", 400_j, QColor::fromRgb(Random::Number<QRgb>(0xFF000000, 0xFFFFFFFF))), x));
        }
        simulationDriver.start();
    }
}
