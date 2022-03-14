#include "GeneLeafColour.h"

#include "Phenotype.h"

#include <Random.h>

GeneLeafColour::GeneLeafColour(QRgb colour)
    : colour(colour)
{
}

GeneLeafColour::GeneLeafColour(const QColor& colour)
    : GeneLeafColour(colour.rgb())
{
}

void GeneLeafColour::ConfigureJsonSerialisationHelper(util::JsonSerialisationHelper<GeneLeafColour>& helper)
{
    helper.RegisterConstructor(
                helper.CreateParameter<QRgb>("Colour", &GeneLeafColour::colour)
                );
}

std::string GeneLeafColour::TypeName() const
{
    return std::string(util::TypeName<GeneLeafColour>());
}

QString GeneLeafColour::ToString() const
{
    QColor colour(this->colour);
    return QString("%1, %2, %3").arg(colour.red()).arg(colour.green()).arg(colour.blue());
}

QString GeneLeafColour::Description() const
{
    return "R, G, B. The leaf colour represents the light NOT absorbed by the leaf, a darker leaf colour absorbs more light. Each of the Red, Green and Blue channels is a value between 0 and 255.";
}

std::shared_ptr<Gene> GeneLeafColour::Mutated() const
{
    auto copy = std::make_shared<GeneLeafColour>(colour);
    QColor colour = QColor::fromRgb(copy->colour);
    switch (Random::Number(1, 3)) {
    case 1:
        colour.setRed(std::clamp(colour.red() + Random::Number(-10, 10), 0, 255));
        break;
    case 2:
        colour.setGreen(std::clamp(colour.green() + Random::Number(-10, 10), 0, 255));
        break;
    case 3:
        colour.setBlue(std::clamp(colour.blue() + Random::Number(-10, 10), 0, 255));
        break;
    };
    copy->colour = colour.rgb();
    return copy;
}

std::shared_ptr<Gene> GeneLeafColour::Crossed(const std::shared_ptr<Gene>& other) const
{
    std::shared_ptr<GeneLeafColour> otherLeafColourGene = std::dynamic_pointer_cast<GeneLeafColour>(other);
    if (otherLeafColourGene) {
        return std::make_shared<GeneLeafColour>(InterpolateColours(colour, otherLeafColourGene->colour));
    }
    return nullptr;
}

double GeneLeafColour::Similarity(const std::shared_ptr<Gene>& other) const
{
    double percentSimilarity = 0;
    std::shared_ptr<GeneLeafColour> otherLeafColourGene = std::dynamic_pointer_cast<GeneLeafColour>(other);
    if (otherLeafColourGene) {
        QColor colour = QColor::fromRgb(this->colour);
        QColor otherColour = QColor::fromRgb(otherLeafColourGene->colour);
        double proportionRedSimilarity = 1.0 - std::abs(colour.redF() - otherColour.redF());
        double proportionGreenSimilarity = 1.0 - std::abs(colour.greenF() - otherColour.greenF());
        double proportionBlueSimilarity = 1.0 - std::abs(colour.blueF() - otherColour.blueF());
        percentSimilarity = ((proportionRedSimilarity + proportionGreenSimilarity + proportionBlueSimilarity) / 3.0) * 100;
    }
    return percentSimilarity;
}

void GeneLeafColour::Express(Phenotype& phenotype) const
{
    phenotype.leafColour = colour;
    QColor colour = QColor::fromRgb(this->colour);
    phenotype.metabolism += (std::pow(2.0 - colour.valueF(), 2.0) - 1.0) * 100_j;
}

QColor GeneLeafColour::InterpolateColours(const QColor& a, const QColor& b) {
    return QColor::fromRgbF((a.redF() + b.redF()) / 2.0, (a.greenF() + b.greenF()) / 2.0, (a.blueF() + b.blueF()) / 2.0);
}
