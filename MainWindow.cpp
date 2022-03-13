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
    /// Plant Settings
    ///
    connect(ui->leftSelectPlant, &QRadioButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetLeftMouseButtonAction(SimulationViewWidget::MouseButtonAction::SelectPlant);
    });
    connect(ui->leftRemovePlants, &QRadioButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetLeftMouseButtonAction(SimulationViewWidget::MouseButtonAction::RemovePlants);
    });
    connect(ui->leftAddSeed, &QRadioButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetLeftMouseButtonAction(SimulationViewWidget::MouseButtonAction::AddPlantSeed);
    });

    connect(ui->rightSelectPlant, &QRadioButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetRightMouseButtonAction(SimulationViewWidget::MouseButtonAction::SelectPlant);
    });
    connect(ui->rightRemovePlants, &QRadioButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetRightMouseButtonAction(SimulationViewWidget::MouseButtonAction::RemovePlants);
    });
    connect(ui->rightAddSeed, &QRadioButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetRightMouseButtonAction(SimulationViewWidget::MouseButtonAction::AddPlantSeed);
    });

    ///
    /// Play Speed Buttons
    ///
    connect(ui->pauseButton, &QPushButton::toggled, ui->simulationViewer, &SimulationViewWidget::SetPaused);
    connect(ui->speed1Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(10);
        ui->simulationViewer->SetTargetFramesPerSecond(60);
    });
    connect(ui->speed2Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(50);
        ui->simulationViewer->SetTargetFramesPerSecond(60);
    });
    connect(ui->speed3Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(200);
        ui->simulationViewer->SetTargetFramesPerSecond(40);
    });
    connect(ui->speedMaxButton, &QPushButton::pressed, ui->simulationViewer, &SimulationViewWidget::SetUnlimitedTicksPerSecond);
    connect(ui->speedMaxButton, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetFramesPerSecond(4);
    });


    ///
    /// Start
    ///

    ui->leftSelectPlant->setChecked(true);
    ui->rightRemovePlants->setChecked(true);
    emit(ui->leftSelectPlant->pressed());
    emit(ui->rightRemovePlants->pressed());

    emit(ui->speed1Button->pressed());

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

    if (ui->autoPopulateGroup->isChecked()) {
        for (int x = ui->autoPopulateSpacingSpinBox->value() / 2; x < sim->GetLightMap().GetRect().width(); x += ui->autoPopulateSpacingSpinBox->value()) {
            // TODO allow for non-default genome to be selected
            sim->AddPlant(Plant::Generate(GeneFactory::CreateDefaultGenome(), ui->autoPopulateEnergySpinBox->value() * 1_j, x));
        }
    }
}
