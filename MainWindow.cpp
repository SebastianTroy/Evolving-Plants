#include "MainWindow.h"
#include "ui_MainWindow.h"

#include "GeneFactory.h"

MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);

    setWindowIcon(QIcon(":/Logo.png"));
    setWindowTitle("Evolving Plants v4.0");

    ///
    /// Simulation Settings
    ///
    connect(ui->showHideLightButton, &QPushButton::toggled, ui->simulationViewer, &SimulationViewWidget::SetShowLight);
    connect(ui->applyNewBoundsButton, &QPushButton::pressed, this, [this]()
    {
        if (sim) {
            sim->SetBounds(ui->widthSpinBox->value(), ui->heightSpinBox->value());
            ui->simulationViewer->UpdateScrollBars();
        }
    });
    connect(ui->resetButton, &QPushButton::pressed, this, &MainWindow::ResetSimulation);

    ///
    /// Play Speed Buttons
    ///
    connect(ui->pauseButton, &QPushButton::toggled, ui->simulationViewer, &SimulationViewWidget::SetPaused);
    connect(ui->speed1Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(10);
    });
    connect(ui->speed2Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(50);
    });
    connect(ui->speed3Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(200);
    });
    connect(ui->speedMaxButton, &QPushButton::pressed, ui->simulationViewer, &SimulationViewWidget::SetUnlimitedTicksPerSecond);

    ///
    /// Start
    ///
    ResetSimulation();
}

MainWindow::~MainWindow()
{
    delete ui;
}

void MainWindow::SetSimulationWidth(int width)
{
    sim->SetBounds(width, sim->GetLightMap().GetRect().height());
}

void MainWindow::SetSimulationHeight(int height)
{
    sim->SetBounds(sim->GetLightMap().GetRect().width(), height);
}

void MainWindow::ResetSimulation()
{
    sim = std::make_shared<Simulation>(ui->widthSpinBox->value(), ui->heightSpinBox->value());
    ui->simulationViewer->SetSimulation(sim);

    // TODO make this an optional part of the RESET process
    for (int x = 15; x < sim->GetLightMap().GetRect().width(); x += 30) {
        sim->AddPlant(*Plant::Generate(GeneFactory::CreateDefaultGenome(), 400_j, x));
    }
}
