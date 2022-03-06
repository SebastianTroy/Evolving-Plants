#include "SimulationViewWidget.h"

#include "GeneFactory.h"

#include <QPainter>
#include <QLocale>
#include <QScrollBar>

SimulationViewWidget::SimulationViewWidget(QWidget* parent)
    : QAbstractScrollArea(parent)
    , simulationDriver(this)
    , repaintDriver(this)
    , sim(3000, 400)
{
    for (int x = 15; x < sim.GetLightMap().GetRect().width(); x += 30) {
        sim.AddPlant(*Plant::Generate(GeneFactory::CreateDefaultGenome(), 400_j, x));
    }

    verticalScrollBar()->setInvertedAppearance(true);
    setVerticalScrollBarPolicy(Qt::ScrollBarPolicy::ScrollBarAsNeeded);
    setHorizontalScrollBarPolicy(Qt::ScrollBarPolicy::ScrollBarAsNeeded);

    simulationDriver.setSingleShot(false);
    simulationDriver.setInterval(0);
    connect(&simulationDriver, &QTimer::timeout, this, [&]()
    {
        sim.Tick();
        ++tickCount;
    });

    repaintDriver.setSingleShot(false);
    repaintDriver.setInterval(1000 / 60);
    connect(&repaintDriver, &QTimer::timeout, viewport(), static_cast<void(QWidget::*)()>(&QWidget::update));
}

void SimulationViewWidget::paintEvent(QPaintEvent* /*event*/)
{
    QPainter paint(viewport());

    // Paint the sky
    paint.fillRect(viewport()->rect(), QGradient(QGradient::SkyGlider));

    paint.drawText(0, 20, QLocale::system().toString(tickCount));

    // Flip painter vertically so x=0 is at bottom of the screen
    paint.translate(0, viewport()->height());
    paint.scale(1, -1);

    // Paint the ground
    const int groundHeght = 5;
    paint.fillRect(QRect(0, 0, viewport()->width(), groundHeght), Qt::green);

    // Move the painter so x=0 is at groundHeght
    paint.translate(0, groundHeght);

    QRect viewportArea = viewport()->rect().translated(horizontalScrollBar()->value(), verticalScrollBar()->value());
    paint.translate(-viewportArea.topLeft());

    if (viewLight) {
        paint.drawImage(viewportArea.topLeft(), sim.GetLightMap().GetLightImage(viewportArea));
    }

    // Paint the plants, shortest last so they aren't hidden by taller plant's stems
    std::vector<const Plant*> sortedPlants;
    for (const Plant& plant : sim.GetPlants()) {
        if (viewportArea.intersects(plant.GetBounds().toRect())) {
            sortedPlants.push_back(&plant);
        }
    }
    std::stable_sort(std::begin(sortedPlants), std::end(sortedPlants), [](const Plant* a, const Plant* b)
    {
        return a->GetBounds().height() > b->GetBounds().height();
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
    simulationDriver.start();
    repaintDriver.start();
}

void SimulationViewWidget::resizeEvent(QResizeEvent* /*event*/)
{
    QSize areaSize = viewport()->size();
    QSize widgetSize = sim.GetLightMap().GetRect().size();

    verticalScrollBar()->setPageStep(areaSize.height());
    horizontalScrollBar()->setPageStep(areaSize.width());
    verticalScrollBar()->setRange(0, widgetSize.height() - areaSize.height());
    horizontalScrollBar()->setRange(0, widgetSize.width() - areaSize.width());
}
