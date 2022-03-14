#ifndef GENEFACTORY_H
#define GENEFACTORY_H

#include "Gene.h"

#include <QString>

#include <vector>
#include <memory>

class GeneFactory {
public:
    static void CreateDefaultGenome();
    static void SaveGenome(const std::vector<std::shared_ptr<Gene>>& genes, const QString& saveName);
    static std::vector<std::shared_ptr<Gene>> LoadGenome(const QString& saveName);
    static std::vector<std::string> GetSavedGenomes();

private:
    static inline bool initialised = false;

    static void Initialise();
};

#endif // GENEFACTORY_H
