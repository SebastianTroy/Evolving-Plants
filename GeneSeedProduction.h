#ifndef GENESEEDPRODUCTION_H
#define GENESEEDPRODUCTION_H

#include "Gene.h"

#include <Energy.h>
#include <JsonSerialisationHelper.h>

class GeneSeedProduction : public Gene {
public:
    GeneSeedProduction(Energy seedSize);
    virtual ~GeneSeedProduction() {}

    static void ConfigureJsonSerialisationHelper(util::JsonSerialisationHelper<GeneSeedProduction>& helper);

    virtual std::string TypeName() const override;
    virtual QString ToString() const override;
    virtual QString Description() const override;

    virtual std::shared_ptr<Gene> Mutated() const override;
    virtual std::shared_ptr<Gene> Crossed(const std::shared_ptr<Gene>& other) const override;
    virtual double Similarity(const std::shared_ptr<Gene>& other) const override;
    virtual void Express(Phenotype& phenotype) const override;

private:
    Energy seedSize;
};

#endif // GENESEEDPRODUCTION_H
