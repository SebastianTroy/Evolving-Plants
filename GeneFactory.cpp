#include "GeneFactory.h"

#include "GenePlantStructure.h"
#include "GeneLeafColour.h"
#include "GeneSeedProduction.h"

#include <JsonPolymorphicSerialisationHelper.h>

#include <QDir>
#include <QFile>
#include <QFileInfoList>

#include <Random.h>

std::vector<std::shared_ptr<Gene>> GeneFactory::CreateDefaultGenome()
{
    return {
        std::make_shared<GenePlantStructure>("  |  "),
        std::make_shared<GeneLeafColour>(Random::Number(0xFF000000, 0xFFFFFFFF)),
        std::make_shared<GeneSeedProduction>(40_j),
    };
}

void GeneFactory::SaveGenome(const std::vector<std::shared_ptr<Gene> >& genes, const QString& saveName)
{
    Initialise();

    std::string saveData = JsonHelpers::Serialise(genes).dump(4);
    QFile file(saveName);
    file.open(QIODeviceBase::OpenModeFlag::WriteOnly);
    file.write(QByteArray::fromStdString(saveData));
}

std::vector<std::shared_ptr<Gene>> GeneFactory::LoadGenome(const QString& saveName)
{
    Initialise();

    QFile file(QString("./SavedGenomes/%1.genome").arg(saveName));
    file.open(QIODeviceBase::OpenModeFlag::ReadOnly);
    nlohmann::json saveData = nlohmann::json::parse(file.readAll().toStdString());
    if (JsonHelpers::Validate<std::vector<std::shared_ptr<Gene>>>(saveData)) {
        return JsonHelpers::Deserialise<std::vector<std::shared_ptr<Gene>>>(saveData);
    }
    return {};
}

std::vector<std::string> GeneFactory::GetSavedGenomes()
{
    std::vector<std::string> files;
    for (const QFileInfo& fileInfo : QDir("./SavedGenomes").entryInfoList(QDir::Filter::Files | QDir::Filter::NoDotAndDotDot)) {

        files.push_back(fileInfo.baseName().toStdString());
    }
    return files;
}

void GeneFactory::Initialise()
{
    if (!initialised) {
        util::JsonPolymorphicSerialisationHelper<Gene>::template RegisterChildType<GenePlantStructure>();
        util::JsonPolymorphicSerialisationHelper<Gene>::template RegisterChildType<GeneLeafColour>();
        util::JsonPolymorphicSerialisationHelper<Gene>::template RegisterChildType<GeneSeedProduction>();
        initialised = true;
    }
}
