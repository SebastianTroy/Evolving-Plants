#ifndef SIMULATIONVIEWWIDGET_H
#define SIMULATIONVIEWWIDGET_H

#include "Simulation.h"

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

    // FIXME this feels a lil hacky, as the click to plant a seed is detected here, but the MainWindow ui has the selected filename...
    void SetCurrentGenomeSaveFileName(const QString& saveFileName);

    std::shared_ptr<Plant> GetSelectedPlant() const;

protected:
    virtual void mousePressEvent(QMouseEvent* event) override;
    virtual void mouseMoveEvent(QMouseEvent* event) override;
    virtual void wheelEvent(QWheelEvent* event) override;
    virtual void paintEvent(QPaintEvent* event) override;
    virtual void showEvent(QShowEvent* /*event*/) override;
    virtual void resizeEvent(QResizeEvent* event) override;

private:
    static inline constexpr int groundHeght = 5;

    QTimer simulationDriver;
    QTimer repaintDriver;
    std::shared_ptr<Simulation> sim;

    bool viewLight;

    QString saveFileName;

    MouseButtonAction leftMouseButtonAction;
    MouseButtonAction rightMouseButtonAction;
    std::shared_ptr<Plant> selectedPlant;

    void Tick();
    void Reset();
    void PaintPlant(QPainter& paint, const Plant& plant, bool selected);

    void PerformMouseButtonAction(MouseButtonAction action, const QPointF& location);

    QPointF LocalToSimulation(const QPointF& localPoint) const;
};

#endif // SIMULATIONVIEWWIDGET_H
