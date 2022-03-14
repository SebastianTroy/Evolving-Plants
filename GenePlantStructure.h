#ifndef GENEPLANTSTRUCTURE_H
#define GENEPLANTSTRUCTURE_H

#include "Gene.h"

#include <MathConstants.h>
#include <JsonSerialisationHelper.h>
#include <TypeName.h>

#include <vector>

class GenePlantStructure : public Gene {
public:
    enum class Instruction : char {
        ADD_NODE = 'N',
        CLIMB_NODE_TREE = '^',
        DESCEND_NODE_TREE = 'v',
        NEXT_NODE = '>',
        PREVIOUS_NODE = '<',
        GROW_UP = '|',
        ROTATE_LEFT = '\\',
        ROTATE_RIGHT = '/',
        SKIP = ' ',
        END_ALL = '#',
    };

    GenePlantStructure(const std::string& instructions);
    GenePlantStructure(const std::string& instructions, double stemUnitLength, double stemRotationAngle);
    GenePlantStructure(const std::vector<Instruction>& instructions, double stemUnitLength, double stemRotationAngle);
    virtual ~GenePlantStructure() {}

    static void ConfigureJsonSerialisationHelper(util::JsonSerialisationHelper<GenePlantStructure>& helper);

    virtual std::string TypeName() const override;
    virtual QString ToString() const override;
    virtual QString Description() const override;

    virtual std::shared_ptr<Gene> Mutated() const override;
    virtual std::shared_ptr<Gene> Crossed(const std::shared_ptr<Gene>& other) const override;
    virtual double Similarity(const std::shared_ptr<Gene>& other) const override;
    virtual void Express(Phenotype& phenotype) const override;

private:
    double stemUnitLength;
    double stemRotationAngle;
    std::vector<Instruction> instructions;

    static Instruction RandomInstruction();
    static std::vector<Instruction> FromString(const std::string& instructionsString);
};

#endif // GENEPLANTSTRUCTURE_H
