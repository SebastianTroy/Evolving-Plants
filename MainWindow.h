#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

#include "Simulation.h"

#include <QFileSystemWatcher>

#include <memory>

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

    QFileSystemWatcher saveGameMonitor;

    std::shared_ptr<Simulation> sim; // MAYBE this should not live here, go back to SimulationViewWidget being the only owner?

    void SetSimulationWidth(int width);
    void SetSimulationHeight(int height);
    void UpdateSavedGenomeNames();
    void ResetSimulation();
};

#endif // MAINWINDOW_H
