#include "Genetics.h"


Genetics::Genetics(std::string&& instructions, double seedEnergy, QColor colour)
    : Genetics(FromString(std::move(instructions)), seedEnergy, colour)
{
}

Genetics::Genetics(std::vector<Instruction>&& instructions, double seedEnergy, QColor colour)
    : instructions(std::move(instructions))
    , leafColour(colour)
    , seedEnergy(seedEnergy)
{
}

Genetics::Genetics(Genetics&& other)
    : instructions(std::move(other.instructions))
    , leafColour(other.leafColour)
    , seedEnergy(other.seedEnergy)
{
}

Genetics::Genetics(const Genetics&& other)
    : instructions(std::move(other.instructions))
    , leafColour(other.leafColour)
    , seedEnergy(other.seedEnergy)
{
}

Genetics Genetics::Mutated(const Genetics& parent, unsigned mutationCount)
{
    Genetics copy = parent;
    copy.Mutate(mutationCount);
    return copy;
}

Genetics Genetics::Mutated(const Genetics& parentOne, const Genetics& parentTwo, unsigned mutationCount)
{
    double seedEnergy = (parentOne.seedEnergy + parentTwo.seedEnergy) / 2;

    QColor leafColour;
    leafColour.setRedF((parentOne.leafColour.redF() + parentTwo.leafColour.redF()) / 2.0f);
    leafColour.setRedF((parentOne.leafColour.redF() + parentTwo.leafColour.redF()) / 2.0f);
    leafColour.setRedF((parentOne.leafColour.redF() + parentTwo.leafColour.redF()) / 2.0f);

    Genetics child(Random::Merge(parentOne.instructions, parentTwo.instructions), seedEnergy, leafColour);
    child.Mutate(mutationCount);
    return child;
}

bool Genetics::AreRelated(std::vector<Instruction> parentOneCommands, std::vector<Instruction> parentTwoCommands, double similarityPercentLimit)
{
    // Unrelated if too many differences in gene sequence
    unsigned differences = 0;
    unsigned acceptableDifference = std::round((similarityPercentLimit / 100.0) * std::min(parentOneCommands.size(), parentTwoCommands.size()));

    // FIXME need to do a better comparison here to account for insertions & deletions etc
    util::IterateBoth(parentOneCommands, parentTwoCommands, [&differences](const Instruction& a, const Instruction& b)
    {
        if (a != b) {
            ++differences;
        }
    });

    return differences < acceptableDifference;
}

const std::vector<Genetics::Instruction>& Genetics::GetInstructions() const
{
    return instructions;
}

std::string Genetics::GetInstructionsString() const
{
    std::string instructionsString;
    for (const Instruction& i : instructions) {
        instructionsString.push_back(static_cast<std::underlying_type_t<Instruction>>(i));
    }
    return instructionsString;
}

const QColor& Genetics::GetLeafColour() const
{
    return leafColour;
}

double Genetics::GetSeedEnergy() const
{
    return seedEnergy;
}

Random::WeightedContainer<Genetics::Mutation> Genetics::CreateMutations()
{
    Random::WeightedContainer<Genetics::Mutation> mutations;

    // Leaf Colour
    mutations.PushBack([](Genetics& g)
    {
        g.leafColour.setRed(std::clamp(g.leafColour.red() + Random::Number(-10, 10), 0, 255));
    }, 1.0);
    mutations.PushBack([](Genetics& g)
    {
        g.leafColour.setGreen(std::clamp(g.leafColour.green() + Random::Number(-10, 10), 0, 255));
    }, 1.0);
    mutations.PushBack([](Genetics& g)
    {
        g.leafColour.setBlue(std::clamp(g.leafColour.blue() + Random::Number(-10, 10), 0, 255));
    }, 1.0);

    // Seed Size
    mutations.PushBack([](Genetics& g)
    {
        g.seedEnergy += Random::Number(-10, 10);
    }, 1.0);

    // Deletion
    mutations.PushBack([](Genetics& g)
    {
        auto eraseIter = std::begin(g.instructions);
        std::advance(eraseIter, Random::Number(size_t{ 0 }, g.instructions.size()));
        if (eraseIter != std::end(g.instructions)) {
            g.instructions.erase(eraseIter);
        }
    }, 1.0);

    // Insertion
    mutations.PushBack([](Genetics& g)
    {
        auto insertIter = std::begin(g.instructions);
        std::advance(insertIter, Random::Number(size_t{ 0 }, g.instructions.size()));
        g.instructions.insert(insertIter, RandomInstruction());
    }, 1.0);

    // Mutation

    return mutations;
}

QColor Genetics::InterpolateColours(const QColor& a, const QColor& b)
{
    return QColor::fromRgbF((a.redF() + b.redF()) / 2.0, (a.greenF() + b.greenF()) / 2.0, (a.blueF() + b.blueF()) / 2.0);
}

Genetics::Instruction Genetics::RandomInstruction()
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
        return Instruction::GROW_LEFT;
    case 5:
        return Instruction::GROW_RIGHT;
    default:
        return Instruction::SKIP;
    }
}

std::vector<Genetics::Instruction> Genetics::FromString(std::string&& instructionsString)
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
            instructions.push_back(Instruction::GROW_LEFT);
            break;
        case '>':
            instructions.push_back(Instruction::GROW_RIGHT);
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

void Genetics::Mutate(unsigned mutationCount)
{
    for (unsigned i = 0; i < mutationCount; ++i) {
        Genetics::mutations_.RandomItem()(*this);
    }
}
