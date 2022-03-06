#include "GenePlantStructure.h"

#include "Phenotype.h"

#include <Algorithm.h>
#include <Random.h>

GenePlantStructure::GenePlantStructure(const std::string& instructions)
    : GenePlantStructure(FromString(instructions))
{
}

GenePlantStructure::GenePlantStructure(const std::vector<Instruction>& instructions)
    : instructions(instructions)
{
}

std::shared_ptr<Gene> GenePlantStructure::Mutated() const
{
    auto copy = std::make_shared<GenePlantStructure>(instructions);

    switch (Random::Number(1, 3)) {
        case 1: {
            // Deletion
            auto eraseIter = std::begin(copy->instructions);
            std::advance(eraseIter, Random::Number(size_t{ 0 }, copy->instructions.size()));
            if (eraseIter != std::end(copy->instructions)) {
                copy->instructions.erase(eraseIter);
            }
            break;
        }
        case 2: {
            // Insertion
            auto insertIter = std::begin(copy->instructions);
            std::advance(insertIter, Random::Number(size_t{ 0 }, copy->instructions.size()));
            copy->instructions.insert(insertIter, RandomInstruction());
            break;
        }
        case 3: {
            // Mutation
            auto modifyIter = std::begin(copy->instructions);
            std::advance(modifyIter, Random::Number(size_t{ 0 }, copy->instructions.size()));
            if (modifyIter != std::end(copy->instructions)) {
                *modifyIter = RandomInstruction();
            }
            break;
        }
    }

    return copy;
}

std::shared_ptr<Gene> GenePlantStructure::Crossed(const std::shared_ptr<Gene>& other) const
{
    std::shared_ptr<GenePlantStructure> otherPlantStructureGene = std::dynamic_pointer_cast<GenePlantStructure>(other);
    if (otherPlantStructureGene) {
        return std::make_shared<GenePlantStructure>(Random::Merge(instructions, otherPlantStructureGene->instructions));
    }
    return nullptr;
}

double GenePlantStructure::Similarity(const std::shared_ptr<Gene>& other) const
{
    double percentSimilarity = 0;

    std::shared_ptr<GenePlantStructure> otherPlantStructureGene = std::dynamic_pointer_cast<GenePlantStructure>(other);
    if (otherPlantStructureGene) {
        // FIXME need to do a better comparison here to account for insertions & deletions etc
        unsigned differences = 0;
        util::IterateBoth(instructions, otherPlantStructureGene->instructions, [&differences](const Instruction& a, const Instruction& b)
        {
            if (a != b) {
                ++differences;
            }
        });

        // Unrelated if too many differences in gene sequence
        percentSimilarity = differences / std::max(instructions.size(), otherPlantStructureGene->instructions.size());
    }

    return percentSimilarity;
}

void GenePlantStructure::Express(Phenotype& phenotype) const
{
    for (const Instruction& instruction : instructions) {
        // Every instruction ups metabolism
        phenotype.metabolism += 5.0_j;
        switch (instruction) {
        case Instruction::ADD_NODE:
            phenotype.AddNode();
            [[ fallthrough ]];
        case Instruction::CLIMB_NODE_TREE:
            phenotype.AscendNodeTree();
            break;
        case Instruction::DESCEND_NODE_TREE:
            phenotype.DescendNodeTree();
            break;
        case Instruction::GROW_UP:
            phenotype.IncreaseCurrentNodesLength(1);
            break;
        case Instruction::ROTATE_LEFT:
            phenotype.RotateCurrentNode(-1);
            break;
        case Instruction::ROTATE_RIGHT:
            phenotype.RotateCurrentNode(+1);
            break;
        case Instruction::SKIP:
            // (disincentivise long empty genomes)
            phenotype.metabolism += 0.5_j;
            break;
        case Instruction::END_ALL:
            break;
        }
    }
}

GenePlantStructure::Instruction GenePlantStructure::RandomInstruction()
{
    switch (Random::Number(0, 20)) {
    case 0:
        return Instruction::ADD_NODE;
    case 1:
        return Instruction::CLIMB_NODE_TREE;
    case 2:
        return Instruction::DESCEND_NODE_TREE;
    case 3:
        return Instruction::GROW_UP;
    case 4:
        return Instruction::ROTATE_LEFT;
    case 5:
        return Instruction::ROTATE_RIGHT;
    case 6:
        return Instruction::END_ALL;
    default:
        return Instruction::SKIP;
    }
    return Instruction::SKIP;
}

std::vector<GenePlantStructure::Instruction> GenePlantStructure::FromString(const std::string& instructionsString)
{
    std::vector<Instruction> instructions;
    for (const char& characterInstruction : instructionsString) {
        switch(characterInstruction) {
        case 'N':
            instructions.push_back(Instruction::ADD_NODE);
            break;
        case '+':
            instructions.push_back(Instruction::CLIMB_NODE_TREE);
            break;
        case '-':
            instructions.push_back(Instruction::DESCEND_NODE_TREE);
            break;
        case '^':
            instructions.push_back(Instruction::GROW_UP);
            break;
        case '<':
            instructions.push_back(Instruction::ROTATE_LEFT);
            break;
        case '>':
            instructions.push_back(Instruction::ROTATE_RIGHT);
            break;
        case ' ':
            instructions.push_back(Instruction::SKIP);
            break;
        case '|':
            instructions.push_back(Instruction::END_ALL);
            break;
        }
    }
    return instructions;
}
