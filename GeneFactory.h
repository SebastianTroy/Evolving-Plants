#ifndef GENEFACTORY_H
#define GENEFACTORY_H

#include "Gene.h"

#include <vector>
#include <memory>

class GeneFactory {
public:
    static std::vector<std::shared_ptr<Gene>> CreateDefaultGenome();
};

#endif // GENEFACTORY_H
