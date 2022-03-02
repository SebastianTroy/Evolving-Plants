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

//    LightMap map(100, 100);
//    double x_ = 20.1;
//    size_t y_ = 75;
//    size_t width_ = 20;
//    QColor colour_ = Qt::blue;
//    map.AddShadow(x_, y_, width_, colour_);
//    map.RemoveShadow(x_, y_, width_, colour_);
//    paint.drawImage(QPoint(0, 0), map.GetLightImage());
//    return;


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
        paint.drawImage(QPoint(0, 0), sim.GetLightMap().GetLightImage());
    }

    // Paint the plant
    for (const Plant& plant : sim.GetPlants()) {
        for (const auto& [ stem, hasLeaf ] : plant.GetNodes()) {
            paint.drawLine(stem);
            if (hasLeaf) {
                paint.setBrush(plant.GetLeafColour());
                paint.drawEllipse(stem.p2(), plant.GetLeafSize() / 2, plant.GetLeafSize() / 3);
            }
        }
    }
}

void SimulationViewWidget::showEvent(QShowEvent*)
{
    sim = Simulation(width(), height());
    for (int x = 0; x < width(); x += width() / 30) {
        sim.AddPlant(*Plant::Generate(Genetics("", 40000_j, QColor::fromRgb(Random::Number<QRgb>(0xFF000000, 0xFFFFFFFF))), x));
    }
    simulationDriver.start();
}
