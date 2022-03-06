#include "GeneSeedProduction.h"

#include "Phenotype.h"

#include <Random.h>

GeneSeedProduction::GeneSeedProduction(Energy seedSize)
    : seedSize(seedSize)
{
}

std::shared_ptr<Gene> GeneSeedProduction::Mutated() const
{
    return std::make_shared<GeneSeedProduction>(seedSize + Random::Number(-10_j, 10_j));
}

std::shared_ptr<Gene> GeneSeedProduction::Crossed(const std::shared_ptr<Gene>& other) const
{
    std::shared_ptr<GeneSeedProduction> otherSeedProductionGene = std::dynamic_pointer_cast<GeneSeedProduction>(other);
    if (otherSeedProductionGene) {
        return std::make_shared<GeneSeedProduction>(seedSize + Random::Number(-10_j, 10_j));
    }
    return nullptr;
}

double GeneSeedProduction::Similarity(const std::shared_ptr<Gene>& other) const
{
    double percentSimilarity = 0;
    std::shared_ptr<GeneSeedProduction> otherSeedProductionGene = std::dynamic_pointer_cast<GeneSeedProduction>(other);
    if (otherSeedProductionGene) {
        double proportionalDifference = std::abs(seedSize - otherSeedProductionGene->seedSize) / seedSize;
        percentSimilarity = (1.0 - proportionalDifference) * 100;
    }
    return std::clamp(percentSimilarity, 0.0, 100.0);
}

void GeneSeedProduction::Express(Phenotype& phenotype) const
{
    phenotype.seedSize = seedSize;
}