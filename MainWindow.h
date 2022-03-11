#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

#include <memory>

#include "Simulation.h"

namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow {
    Q_OBJECT
public:
    explicit MainWindow(QWidget *parent = nullptr);
    ~MainWindow();

private:
    Ui::MainWindow *ui;
    std::shared_ptr<Simulation> sim; // MAYBE this should not live here, go back to SimulationViewWidget being the only owner?

    void SetSimulationWidth(int width);
    void SetSimulationHeight(int height);
    void ResetSimulation();
};

#endif // MAINWINDOW_H
