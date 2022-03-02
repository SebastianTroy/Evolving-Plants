#ifndef SIMULATIONVIEWWIDGET_H
#define SIMULATIONVIEWWIDGET_H

#include "Simulation.h"

#include <QWidget>
#include <QTimer>

#include <memory>

class SimulationViewWidget : public QWidget {
    Q_OBJECT
public:
    explicit SimulationViewWidget(QWidget *parent = nullptr);

protected:
    virtual void mousePressEvent(QMouseEvent* event) override
    {
        if (simulationDriver.isActive()) {
            simulationDriver.stop();
            viewLight = true;
            update();
        } else {
            simulationDriver.start();
            viewLight = false;
            update();
        }
    }
    virtual void mouseMoveEvent(QMouseEvent* event) override { /* TODO */}
    virtual void wheelEvent(QWheelEvent* event) override { /* TODO */}
    virtual void paintEvent(QPaintEvent* event) override;
    virtual void showEvent(QShowEvent* /*event*/) override;

private:
    QTimer simulationDriver;
    Simulation sim;

    bool viewLight = false;

    qulonglong tickCount = 0;
};

#endif // SIMULATIONVIEWWIDGET_H
