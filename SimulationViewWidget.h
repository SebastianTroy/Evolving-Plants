#ifndef SIMULATIONVIEWWIDGET_H
#define SIMULATIONVIEWWIDGET_H

#include "Simulation.h"

#include "SimulationInfoTableModel.h"

#include <QWidget>
#include <QTimer>
#include <QAbstractScrollArea>

#include <memory>

class SimulationViewWidget : public QAbstractScrollArea {
    Q_OBJECT
public:
    enum class MouseButtonAction {
        SelectPlant,
        AddPlantSeed,
        RemovePlants,
    };

    explicit SimulationViewWidget(QWidget *parent = nullptr);

    void SetSimulation(std::shared_ptr<Simulation> sim);

    void UpdateScrollBars();

    void SetShowLight(bool showLight);

    void SetPaused(bool paused);
    void SetTargetFramesPerSecond(unsigned targetFps);
    void SetTargetTicksPerSecond(unsigned targetTps);
    void SetUnlimitedTicksPerSecond();

    void SetLeftMouseButtonAction(MouseButtonAction action);
    void SetRightMouseButtonAction(MouseButtonAction action);

    SimulationInfoTableModel& GetSimulationInfoModel();

    std::shared_ptr<Plant> GetSelectedPlant() const;

signals:
    void onPlacePlantSeedRequested(double x);

protected:
    virtual void mousePressEvent(QMouseEvent* event) override;
    virtual void mouseMoveEvent(QMouseEvent* event) override;
    virtual void wheelEvent(QWheelEvent* event) override;
    virtual void paintEvent(QPaintEvent* event) override;
    virtual void showEvent(QShowEvent* /*event*/) override;
    virtual void resizeEvent(QResizeEvent* event) override;

private:
    static inline constexpr int groundHeight = 5;

    QTimer simulationDriver;
    QTimer repaintDriver;
    QTimer infoUpdateDriver;
    std::shared_ptr<Simulation> sim;

    SimulationInfoTableModel infoModel;

    bool viewLight;

    MouseButtonAction leftMouseButtonAction;
    MouseButtonAction rightMouseButtonAction;
    std::shared_ptr<Plant> selectedPlant;

    static QString ElapsedTimeString(qint64 elapsedMsecs);

    void Tick();
    void Reset();
    void PaintPlant(QPainter& paint, const Plant& plant, bool selected);
    void UpdateInfoModel();

    void PerformMouseButtonAction(MouseButtonAction action, const QPointF& location);

    QPointF LocalToSimulation(const QPointF& localPoint) const;
};

#endif // SIMULATIONVIEWWIDGET_H
