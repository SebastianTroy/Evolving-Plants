#ifndef GENEPLANTSTRUCTURE_H
#define GENEPLANTSTRUCTURE_H

#include "Gene.h"

#include <vector>

class GenePlantStructure : public Gene {
public:
    enum class Instruction : char {
        ADD_NODE = 'N',
        CLIMB_NODE_TREE = '+',
        DESCEND_NODE_TREE = '-',
        GROW_UP = '^',
        ROTATE_LEFT = '<',
        ROTATE_RIGHT = '>',
        SKIP = ' ',
        END_ALL = '|',
    };

    GenePlantStructure(const std::string& instructions);
    GenePlantStructure(const std::vector<Instruction>& instructions);
    virtual ~GenePlantStructure() {}

    virtual std::shared_ptr<Gene> Mutated() const override;
    virtual std::shared_ptr<Gene> Crossed(const std::shared_ptr<Gene>& other) const override;
    virtual double Similarity(const std::shared_ptr<Gene>& other) const override;
    virtual void Express(Phenotype& phenotype) const override;

private:
    std::vector<Instruction> instructions;

    static Instruction RandomInstruction();
    static std::vector<Instruction> FromString(const std::string& instructionsString);
};

#endif // GENEPLANTSTRUCTURE_H
