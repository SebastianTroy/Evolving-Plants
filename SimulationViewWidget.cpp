#include "SimulationViewWidget.h"

#include "GeneFactory.h"

#include <RollingStatistics.h>

#include <QPainter>
#include <QLocale>
#include <QScrollBar>
#include <QCoreApplication>
#include <QWheelEvent>
#include <QTime>

SimulationViewWidget::SimulationViewWidget(QWidget* parent)
    : QAbstractScrollArea(parent)
    , simulationDriver(this)
    , repaintDriver(this)
    , infoModel(this)
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

    infoUpdateDriver.setSingleShot(false);
    infoUpdateDriver.setInterval(1000 / 1);
    connect(&infoUpdateDriver, &QTimer::timeout, this, &SimulationViewWidget::UpdateInfoModel);
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

SimulationInfoTableModel& SimulationViewWidget::GetSimulationInfoModel()
{
    return infoModel;
}

std::shared_ptr<Plant> SimulationViewWidget::GetSelectedPlant() const
{
    return selectedPlant;
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

    // Flip painter vertically so x=0 is at bottom of the screen
    paint.translate(0, viewport()->height());
    paint.scale(1, -1);

    // Paint the ground
    paint.fillRect(QRect(0, 0, viewport()->width(), groundHeight), Qt::green);

    // Move the painter so x=0 is at groundHeght
    paint.translate(0, groundHeight);

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
    infoUpdateDriver.start();
    UpdateScrollBars();
}

void SimulationViewWidget::resizeEvent(QResizeEvent* /*event*/)
{
    UpdateScrollBars();
}

QString SimulationViewWidget::ElapsedTimeString(qint64 elapsedMsecs)
{
    constexpr qint64 milliSecondsInDay = 1000 * 60 * 60 * 24;
    qint64 elapsedDays = elapsedMsecs / milliSecondsInDay;
    QTime elapsedTime = QTime::fromMSecsSinceStartOfDay(elapsedMsecs % milliSecondsInDay);
    if (elapsedDays > 1) {
        return QString("%1 days, %2:%3:%4").arg(elapsedDays).arg(elapsedTime.hour(), 2, 10, QLatin1Char('0')).arg(elapsedTime.minute(), 2, 10, QLatin1Char('0')).arg(elapsedTime.second(), 2, 10, QLatin1Char('0'));
    } else if (elapsedDays == 1) {
        return QString("%1 day, %2:%3:%4").arg(elapsedDays).arg(elapsedTime.hour(), 2, 10, QLatin1Char('0')).arg(elapsedTime.minute(), 2, 10, QLatin1Char('0')).arg(elapsedTime.second(), 2, 10, QLatin1Char('0'));
    } else {
        return QString("%1:%2:%3").arg(elapsedTime.hour(), 2, 10, QLatin1Char('0')).arg(elapsedTime.minute(), 2, 10, QLatin1Char('0')).arg(elapsedTime.second(), 2, 10, QLatin1Char('0'));
    }
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

void SimulationViewWidget::UpdateInfoModel()
{
    QVector<SimulationInfoTableModel::TableRow> items;
    if (sim) {
        items.push_back({ "Simulation:",
                          "",
                          "",
                        });
        items.push_back({ "Run Time",
                          ElapsedTimeString(sim->GetRuntime().elapsed()),
                          "The number of times the simulation loop has been run since it was created or reset.",
                        });
        items.push_back({ "Tick Count",
                          QLocale::system().toString(sim->GetTickCount()),
                          "The number of times the simulation loop has been run since it was created or reset.",
                        });
        items.push_back({ "Living Plants",
                          QLocale::system().toString(sim->GetLivingPlantCount()),
                          "The number of living plants currently being siumlated.",
                        });
        items.push_back({ "Total Plants",
                          QLocale::system().toString(sim->GetTotalPlantCount()),
                          "The total number of plants to have lived in this simulation.",
                        });

        util::RollingStatistics ageStats;
        util::RollingStatistics heightStats;
        for (const auto& plant : sim->GetPlants()) {
            ageStats.AddValue(plant->GetAge());
            heightStats.AddValue(plant->GetBounds().height());
        }

        items.push_back({ "Average Age",
                          QLocale::system().toString(ageStats.Mean(), 'f', 2),
                          "The average age of all plants in the simulation, where age is counted in simulation ticks.",
                        });
        items.push_back({ "Average Height",
                          QLocale::system().toString(heightStats.Mean(), 'f', 2),
                          "The average height of all plants in the simulation.",
                        });
    }
    if (selectedPlant) {
        if (sim) {
            items.push_back({ "",
                              "",
                              "",
                            });
        }
        items.push_back({ "Selected Plant:",
                          "",
                          "",
                        });
        items.push_back({ "Age",
                          QLocale::system().toString(selectedPlant->GetAge()),
                          "The number of simulation ticks this plant has lived for.",
                        });
        items.push_back({ "Height",
                          QLocale::system().toString(selectedPlant->GetBounds().height(), 'f', 2),
                          "The height of the plant in the simulation.",
                        });
        items.push_back({ "Energy",
                          QLocale::system().toString(selectedPlant->GetEnergy(), 'f', 2) + "j",
                          "The ammount of energy the plant currently has, it will die if this falls to zero.",
                        });
        items.push_back({ "Metabolism",
                          QLocale::system().toString(selectedPlant->GetMetabolism(), 'f', 2) + "j",
                          "The ammount of energy the plant spends each tick to survive. A larger and more complex plant will have a larger metabolism when it germinates, and it will increase each tick.",
                        });
        items.push_back({ "Percent Grown",
                          QLocale::system().toString(selectedPlant->GetProportionGrown() * 100, 'f', 1) + "%",
                          "When a plant germinates it is 0% grown, and will reach its full size at 100% grown.",
                        });

        for (auto& gene : selectedPlant->GetGenetics()) {
            items.push_back({ QString::fromStdString(gene->TypeName()),
                              gene->ToString(),
                              gene->Description(),
                            });
        }
    }
    infoModel.UpdateAll(std::move(items));
}

void SimulationViewWidget::PerformMouseButtonAction(MouseButtonAction action, const QPointF& location)
{
    switch (action) {
    case MouseButtonAction::SelectPlant:
        selectedPlant = sim->GetPlantAt(LocalToSimulation(location));
        break;
    case MouseButtonAction::AddPlantSeed:
        emit onPlacePlantSeedRequested(LocalToSimulation(location).x());
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
    simPoint.setY(viewport()->height() - simPoint.y() - groundHeight);
    return simPoint;
}
