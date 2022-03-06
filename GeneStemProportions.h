#ifndef GENESTEMPROPORTIONS_H
#define GENESTEMPROPORTIONS_H

#include "Gene.h"

class GeneStemProportions : public Gene {
public:
    GeneStemProportions(double stemUnitLength, double stemRotationAngle);
    virtual ~GeneStemProportions() {}

    virtual std::shared_ptr<Gene> Mutated() const override;
    virtual std::shared_ptr<Gene> Crossed(const std::shared_ptr<Gene>& other) const override;
    virtual double Similarity(const std::shared_ptr<Gene>& other) const override;
    virtual void Express(Phenotype& phenotype) const override;

private:
    double stemUnitLength;
    double stemRotationAngle;
};

#endif // GENESTEMPROPORTIONS_H
