#include "SimulationViewWidget.h"

#include "GeneFactory.h"

#include <QPainter>
#include <QLocale>
#include <QScrollBar>
#include <QCoreApplication>
#include <QWheelEvent>

SimulationViewWidget::SimulationViewWidget(QWidget* parent)
    : QAbstractScrollArea(parent)
    , simulationDriver(this)
    , repaintDriver(this)
    , viewLight(false)
    , leftMouseButtonAction(MouseButtonAction::AddPlantSeed)
    , rightMouseButtonAction(MouseButtonAction::SelectPlant)
    , selectedPlant(nullptr)
{
    verticalScrollBar()->setInvertedAppearance(true);
    setVerticalScrollBarPolicy(Qt::ScrollBarPolicy::ScrollBarAsNeeded);
    setHorizontalScrollBarPolicy(Qt::ScrollBarPolicy::ScrollBarAsNeeded);

    repaintDriver.setSingleShot(false);
    repaintDriver.setInterval(1000 / 60);
    connect(&repaintDriver, &QTimer::timeout, viewport(), static_cast<void(QWidget::*)()>(&QWidget::update));

    simulationDriver.setSingleShot(false);
    simulationDriver.setInterval(0);
    connect(&simulationDriver, &QTimer::timeout, this, &SimulationViewWidget::Tick);
}

void SimulationViewWidget::SetSimulation(std::shared_ptr<Simulation> sim)
{
    this->sim = sim;
    UpdateScrollBars();
}

void SimulationViewWidget::UpdateScrollBars()
{
    if (sim) {
        QSize areaSize = viewport()->size();
        QSize widgetSize = sim->GetLightMap().GetRect().size();

        verticalScrollBar()->setPageStep(areaSize.height());
        horizontalScrollBar()->setPageStep(areaSize.width());
        verticalScrollBar()->setSingleStep(areaSize.height() / 100);
        horizontalScrollBar()->setSingleStep(areaSize.width() / 100);
        verticalScrollBar()->setRange(0, widgetSize.height() - areaSize.height());
        horizontalScrollBar()->setRange(0, widgetSize.width() - areaSize.width());
        viewport()->update();
    }
}

void SimulationViewWidget::SetShowLight(bool showLight)
{
    viewLight = showLight;
}

void SimulationViewWidget::SetPaused(bool paused)
{
    if (paused) {
        simulationDriver.stop();
    } else {
        simulationDriver.start();
    }
}

void SimulationViewWidget::SetTargetFramesPerSecond(unsigned targetFps)
{
    repaintDriver.setInterval(1000 / std::max(1u, targetFps));
}

void SimulationViewWidget::SetTargetTicksPerSecond(unsigned targetTps)
{
    simulationDriver.setInterval(1000 / std::max(1u, targetTps));
}

void SimulationViewWidget::SetUnlimitedTicksPerSecond()
{
    simulationDriver.setInterval(0);
}

void SimulationViewWidget::SetLeftMouseButtonAction(MouseButtonAction action)
{
    leftMouseButtonAction = action;
}

void SimulationViewWidget::SetRightMouseButtonAction(MouseButtonAction action)
{
    rightMouseButtonAction = action;
}

void SimulationViewWidget::mousePressEvent(QMouseEvent* event)
{
    if (sim) {
        if (event->button() == Qt::MouseButton::LeftButton) {
            PerformMouseButtonAction(leftMouseButtonAction, event->position());
        } else if (event->button() == Qt::MouseButton::RightButton) {
            PerformMouseButtonAction(rightMouseButtonAction, event->position());
        }
    }
}

void SimulationViewWidget::mouseMoveEvent(QMouseEvent* event)
{
    if (sim) {
        if (event->buttons() & Qt::MouseButton::LeftButton) {
            PerformMouseButtonAction(leftMouseButtonAction, event->position());
        } else if (event->buttons() & Qt::MouseButton::RightButton) {
            PerformMouseButtonAction(rightMouseButtonAction, event->position());
        }
    }
}

void SimulationViewWidget::wheelEvent(QWheelEvent* event)
{
    QCoreApplication::sendEvent(horizontalScrollBar(), event);
}

void SimulationViewWidget::paintEvent(QPaintEvent* /*event*/)
{
    QPainter paint(viewport());

    // Paint the sky
    paint.fillRect(viewport()->rect(), QGradient(QGradient::SkyGlider));

    paint.drawText(0, 20, QLocale::system().toString(sim->GetTickCount()));

    // Flip painter vertically so x=0 is at bottom of the screen
    paint.translate(0, viewport()->height());
    paint.scale(1, -1);

    // Paint the ground
    paint.fillRect(QRect(0, 0, viewport()->width(), groundHeght), Qt::green);

    // Move the painter so x=0 is at groundHeght
    paint.translate(0, groundHeght);

    QRect viewportArea = viewport()->rect().translated(horizontalScrollBar()->value(), verticalScrollBar()->value());
    paint.translate(-viewportArea.topLeft());

    if (viewLight) {
        paint.drawImage(viewportArea.topLeft(), sim->GetLightMap().GetLightImage(viewportArea));
    }

    // Paint the plants, shortest last so they aren't hidden by taller plant's stems
    std::vector<const Plant*> sortedPlants;
    for (const auto& plant : sim->GetPlants()) {
        if (viewportArea.intersects(plant->GetBounds().toRect())) {
            sortedPlants.push_back(plant.get());
        }
    }
    std::stable_sort(std::begin(sortedPlants), std::end(sortedPlants), [](const Plant* a, const Plant* b)
    {
        return a->GetBounds().height() > b->GetBounds().height();
    });

    paint.setRenderHint(QPainter::RenderHint::Antialiasing, true);

    for (const Plant* plantPtr : sortedPlants) {
        const Plant& plant = *plantPtr;
        PaintPlant(paint, plant, false);
    }

    if (selectedPlant && selectedPlant->IsAlive()) {
        PaintPlant(paint, *selectedPlant, true);
    }
}

void SimulationViewWidget::showEvent(QShowEvent*)
{
    simulationDriver.start();
    repaintDriver.start();
    UpdateScrollBars();
}

void SimulationViewWidget::resizeEvent(QResizeEvent* /*event*/)
{
    UpdateScrollBars();
}

void SimulationViewWidget::Tick()
{
    if (sim) {
        sim->Tick();
    }
}

void SimulationViewWidget::PaintPlant(QPainter& paint, const Plant& plant, bool selected)
{
    plant.ForEachStem([&](const QLineF& stem, double thickness, bool hasLeaf, double leafSize)
    {
        QPen pen(selected ? Qt::white : QColor::fromRgb(73, 39, 14));
        pen.setWidthF(std::max(1.0, selected ? (thickness * 1.5) : thickness));
        paint.setPen(pen);
        paint.drawLine(stem);

        if (hasLeaf) {
            paint.setPen(selected ? Qt::white : Qt::black);
            paint.setBrush(plant.GetLeafColour());
            double radius = (leafSize / 2) * plant.GetProportionGrown();
            paint.drawEllipse(stem.p2(), radius, radius * 0.66);
        }
    });
}

void SimulationViewWidget::PerformMouseButtonAction(MouseButtonAction action, const QPointF& location)
{
    switch (action) {
    case MouseButtonAction::SelectPlant:
        selectedPlant = sim->GetPlantAt(LocalToSimulation(location));
        break;
    case MouseButtonAction::AddPlantSeed:
        sim->AddPlant(Plant::Generate(GeneFactory::CreateDefaultGenome(), 40_j, LocalToSimulation(location).x()));
        break;
    case MouseButtonAction::RemovePlants:
        sim->RemovePlantsAt(LocalToSimulation(location));
        break;
    }
}

QPointF SimulationViewWidget::LocalToSimulation(const QPointF& localPoint) const
{
    QPointF simPoint = localPoint;
    simPoint.rx() += horizontalScrollBar()->value();
    simPoint.setY(viewport()->height() - simPoint.y() - groundHeght);
    return simPoint;
}
