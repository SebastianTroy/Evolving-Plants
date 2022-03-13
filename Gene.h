#ifndef GENE_H
#define GENE_H

#include <memory>

class Phenotype;

class Gene {
public:
    virtual ~Gene(){}
    virtual std::string TypeName() const = 0;
    virtual std::shared_ptr<Gene> Mutated() const = 0;
    virtual std::shared_ptr<Gene> Crossed(const std::shared_ptr<Gene>& other) const = 0;
    virtual double Similarity(const std::shared_ptr<Gene>& other) const = 0;
    virtual void Express(Phenotype& phenotype) const = 0;
};

#endif // GENE_H
