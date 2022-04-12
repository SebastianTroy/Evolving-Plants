#include "MainWindow.h"
#include "ui_MainWindow.h"

#include "GeneFactory.h"
#include "GeneLeafColour.h"

#include <Random.h>

#include <QDir>
#include <QFileDialog>
#include <QDesktopServices>

MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
    , ui(new Ui::MainWindow)
    , saveGameMonitor(this)
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
    /// Genetics Settings
    ///
    connect(ui->saveGeneticsButton, &QPushButton::pressed, this, [&]()
    {
        auto plant = ui->simulationViewer->GetSelectedPlant();
        if (plant) {
            QString saveFileName = QFileDialog::getSaveFileName(this, "Load Genome", "./SavedGenomes/", "Genome (*.genome)");
            GeneFactory::SaveGenome(plant->GetGenetics(), saveFileName);
        }
    });

    saveGameMonitor.addPath("./SavedGenomes/");
    connect(&saveGameMonitor, &QFileSystemWatcher::directoryChanged, this, &MainWindow::UpdateSavedGenomeNames);

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
        ui->simulationViewer->SetTargetTicksPerSecond(250);
        ui->simulationViewer->SetTargetFramesPerSecond(60);
    });
    connect(ui->speed3Button, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetTicksPerSecond(1000);
        ui->simulationViewer->SetTargetFramesPerSecond(40);
    });
    connect(ui->speedMaxButton, &QPushButton::pressed, ui->simulationViewer, &SimulationViewWidget::SetUnlimitedTicksPerSecond);
    connect(ui->speedMaxButton, &QPushButton::pressed, ui->simulationViewer, [&]()
    {
        ui->simulationViewer->SetTargetFramesPerSecond(4);
    });

    // Link to website
    connect(ui->moreByTroyDevButton, &QPushButton::pressed, this, []()
    {
        QDesktopServices::openUrl(QUrl("http://www.TroyDev.co.uk"));
    });

    ///
    /// Start
    ///

    ui->splitter->setStretchFactor(0, 1);
    ui->splitter->setStretchFactor(1, 9);
    ui->splitter->setStretchFactor(2, 1);

    ui->leftSelectPlant->setChecked(true);
    ui->rightRemovePlants->setChecked(true);
    emit ui->leftSelectPlant->pressed();
    emit ui->rightRemovePlants->pressed();

    emit ui->speed1Button->pressed();

    connect(ui->simulationViewer, &SimulationViewWidget::onPlacePlantSeedRequested, this, &MainWindow::AddPlant);

    ui->simulationInformationTable->setModel(&ui->simulationViewer->GetSimulationInfoModel());

    GeneFactory::CreateDefaultGenome();

    UpdateSavedGenomeNames();
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

void MainWindow::UpdateSavedGenomeNames()
{
    QString currentFile = ui->geneticsSelectionCombobox->currentText();
    ui->geneticsSelectionCombobox->clear();
    for (const QFileInfo& file : QDir("./SavedGenomes/").entryInfoList(QDir::Filter::Files | QDir::Filter::NoDotAndDotDot)) {
        ui->geneticsSelectionCombobox->addItem(file.baseName());
    }
    ui->geneticsSelectionCombobox->setCurrentText(currentFile);
}

void MainWindow::ResetSimulation()
{
    sim = std::make_shared<Simulation>(ui->widthSpinBox->value(), ui->heightSpinBox->value());
    ui->simulationViewer->SetSimulation(sim);

    if (ui->autoPopulateGroup->isChecked()) {
        for (int x = ui->autoPopulateSpacingSpinBox->value() / 2; x < sim->GetLightMap().GetRect().width(); x += ui->autoPopulateSpacingSpinBox->value()) {
            AddPlant(x);
        }
    }
}

void MainWindow::AddPlant(double x)
{
    auto genome = GeneFactory::LoadGenome(ui->geneticsSelectionCombobox->currentText());
    if (ui->geneticsRandomiseLeafColourCheckbox->isChecked()) {
        for (auto& gene : genome) {
            if (auto colourGene = dynamic_pointer_cast<GeneLeafColour>(gene)) {
                gene.reset(new GeneLeafColour(Random::Number<QRgb>(0xFF000000, 0xFFFFFFFF)));
            }
        }
    }
    sim->AddPlant(Plant::Generate(std::move(genome), ui->autoPopulateEnergySpinBox->value() * 1_j, x));
}
