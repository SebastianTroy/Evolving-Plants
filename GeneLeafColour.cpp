#include "GeneLeafColour.h"

#include "Phenotype.h"

#include <Random.h>

GeneLeafColour::GeneLeafColour(QRgb colour)
    : GeneLeafColour(QColor::fromRgb(colour))
{
}

GeneLeafColour::GeneLeafColour(const QColor& colour)
    : colour(colour)
{
}

std::shared_ptr<Gene> GeneLeafColour::Mutated() const
{
    auto copy = std::make_shared<GeneLeafColour>(colour);
    switch (Random::Number(1, 3)) {
    case 1:
        copy->colour.setRed(std::clamp(copy->colour.red() + Random::Number(-10, 10), 0, 255));
        break;
    case 2:
        copy->colour.setGreen(std::clamp(copy->colour.green() + Random::Number(-10, 10), 0, 255));
        break;
    case 3:
        copy->colour.setBlue(std::clamp(copy->colour.blue() + Random::Number(-10, 10), 0, 255));
        break;
    };
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
        double proportionRedSimilarity = 1.0 - std::abs(colour.redF() - otherLeafColourGene->colour.redF());
        double proportionGreenSimilarity = 1.0 - std::abs(colour.greenF() - otherLeafColourGene->colour.greenF());
        double proportionBlueSimilarity = 1.0 - std::abs(colour.blueF() - otherLeafColourGene->colour.blueF());
        percentSimilarity = ((proportionRedSimilarity + proportionGreenSimilarity + proportionBlueSimilarity) / 3.0) * 100;
    }
    return percentSimilarity;
}

void GeneLeafColour::Express(Phenotype& phenotype) const
{
    phenotype.leafColour = colour;
}

QColor GeneLeafColour::InterpolateColours(const QColor& a, const QColor& b) {
    return QColor::fromRgbF((a.redF() + b.redF()) / 2.0, (a.greenF() + b.greenF()) / 2.0, (a.blueF() + b.blueF()) / 2.0);
}
