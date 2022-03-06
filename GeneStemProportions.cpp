#include "GeneStemProportions.h"

#include "Phenotype.h"

#include <Random.h>
#include <MathConstants.h>

GeneStemProportions::GeneStemProportions(double stemUnitLength, double stemRotationAngle)
    : stemUnitLength(stemUnitLength)
    , stemRotationAngle(stemRotationAngle)
{
}

std::shared_ptr<Gene> GeneStemProportions::Mutated() const
{
    return std::make_shared<GeneStemProportions>(stemUnitLength + Random::Number(-1, 1), stemRotationAngle + Random::Number(-util::Tau / 100, util::Tau / 100));
}

std::shared_ptr<Gene> GeneStemProportions::Crossed(const std::shared_ptr<Gene>& other) const
{
    auto otherStemProportionGene = std::dynamic_pointer_cast<GeneStemProportions>(other);
    if (otherStemProportionGene) {
        return std::make_shared<GeneStemProportions>((stemUnitLength + otherStemProportionGene->stemUnitLength) / 2, (stemRotationAngle + otherStemProportionGene->stemRotationAngle) / 2);
    }
    return nullptr;
}

double GeneStemProportions::Similarity(const std::shared_ptr<Gene>& other) const
{
    double percentSimilarity = 0;
    auto otherStemProportionGene = std::dynamic_pointer_cast<GeneStemProportions>(other);
    if (otherStemProportionGene) {
        double proportionLengthDifference = std::abs(stemUnitLength - otherStemProportionGene->stemUnitLength) / stemUnitLength;
        double proportionAngleDifference = std::abs(stemRotationAngle - otherStemProportionGene->stemRotationAngle) / stemRotationAngle;
        percentSimilarity = ((proportionLengthDifference + proportionAngleDifference) / 2) * 100;
    }
    return std::clamp(percentSimilarity, 0.0, 100.0);
}

void GeneStemProportions::Express(Phenotype& phenotype) const
{
    phenotype.stemUnitLength = stemUnitLength;
    phenotype.stemRotationAngle = stemRotationAngle;
    phenotype.leafRadius = 7.5;
}
