#ifndef GENETICS_H
#define GENETICS_H

#include <Algorithm.h>
#include <Random.h>

#include <QColor>

#include <ranges>
#include <vector>

class Genetics {
public:
    enum class Instruction : char {
        ADD_NODE = 'N',
        CLIMB_NODE_TREE = '+',
        DESCEND_NODE_TREE = '-',
        GROW_UP = '^',
        GROW_LEFT = '<',
        GROW_RIGHT = '>',
        SKIP = ' ',
        END_ALL = '|',
    };

    Genetics(std::string&& instructions, double seedEnergy, QColor colour);
    Genetics(std::vector<Instruction>&& instructions, double seedEnergy, QColor colour);
    Genetics(Genetics&& other);
    Genetics(const Genetics&& other);

    static Genetics Mutated(const Genetics& parent, unsigned mutationCount);
    static Genetics Mutated(const Genetics& parentOne, const Genetics& parentTwo, unsigned mutationCount);
    static bool AreRelated(std::vector<Instruction> parentOneCommands, std::vector<Instruction> parentTwoCommands, double similarityPercentLimit);

    const std::vector<Instruction>& GetInstructions() const;
    std::string GetInstructionsString() const;
    const QColor& GetLeafColour() const;
    double GetSeedEnergy() const;

    Genetics& operator=(const Genetics& other) = delete;
    Genetics& operator=(Genetics&& other) = default;

private:
    using Mutation = std::function<void(Genetics&)>;
    static Random::WeightedContainer<Mutation> CreateMutations();
    static inline Random::WeightedContainer<Mutation> mutations_ = CreateMutations();

    std::vector<Instruction> instructions;
    QColor leafColour;
    double seedEnergy;

    Genetics(const Genetics& other) = default;

    static QColor InterpolateColours(const QColor& a, const QColor& b);
    static Instruction RandomInstruction();
    static std::vector<Instruction> FromString(std::string&& instructionsString);

    void Mutate(unsigned mutationCount);
};

#endif // GENETICS_H
