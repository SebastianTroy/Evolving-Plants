#include "GeneFactory.h"

#include "GenePlantStructure.h"
#include "GeneLeafColour.h"
#include "GeneSeedProduction.h"
#include "GeneStemProportions.h"

#include <Random.h>
#include <MathConstants.h>

std::vector<std::shared_ptr<Gene>> GeneFactory::CreateDefaultGenome()
{
    return {
        std::make_shared<GenePlantStructure>("     "),
        std::make_shared<GeneLeafColour>(Random::Number(0xFF000000, 0xFFFFFFFF)),
        std::make_shared<GeneSeedProduction>(40_j),
        std::make_shared<GeneStemProportions>(25, util::Tau / 9),
    };
}
