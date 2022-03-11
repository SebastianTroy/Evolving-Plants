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
    explicit SimulationViewWidget(QWidget *parent = nullptr);

    void SetSimulation(std::shared_ptr<Simulation> sim);

    void UpdateScrollBars();

    void SetShowLight(bool showLight);

    void SetPaused(bool paused);
    void SetTargetFramesPerSecond(unsigned targetFps);
    void SetTargetTicksPerSecond(unsigned targetTps);
    void SetUnlimitedTicksPerSecond();

protected:
    virtual void mousePressEvent(QMouseEvent* event) override { /* TODO */}
    virtual void mouseMoveEvent(QMouseEvent* event) override { /* TODO */}
    virtual void wheelEvent(QWheelEvent* event) override;
    virtual void paintEvent(QPaintEvent* event) override;
    virtual void showEvent(QShowEvent* /*event*/) override;
    virtual void resizeEvent(QResizeEvent* event) override;

private:
    QTimer simulationDriver;
    QTimer repaintDriver;
    std::shared_ptr<Simulation> sim;

    bool viewLight = false;

    qulonglong tickCount = 0;

    void Tick();
    void Reset();
};

#endif // SIMULATIONVIEWWIDGET_H
