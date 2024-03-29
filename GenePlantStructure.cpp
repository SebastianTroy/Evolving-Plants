#include "GenePlantStructure.h"

#include "Phenotype.h"

#include <Algorithm.h>
#include <MathConstants.h>
#include <Random.h>

#include <QLocale>

GenePlantStructure::GenePlantStructure(const std::string& instructions)
    : GenePlantStructure(instructions, 25, util::Tau / 9)
{
}

GenePlantStructure::GenePlantStructure(const std::string& instructions, double stemUnitLength, double stemRotationAngle)
    : GenePlantStructure(FromString(instructions), stemUnitLength, stemRotationAngle)
{
}

GenePlantStructure::GenePlantStructure(const std::vector<Instruction>& instructions, double stemUnitLength, double stemRotationAngle)
    : stemUnitLength(stemUnitLength)
    , stemRotationAngle(stemRotationAngle)
    , instructions(instructions)
{
}

void GenePlantStructure::ConfigureJsonSerialisationHelper(util::JsonSerialisationHelper<GenePlantStructure>& helper)
{
    helper.RegisterConstructor(
                helper.CreateParameter<std::string>("Instructions", [](const GenePlantStructure& g){ return g.ToString().toStdString(); }),
                helper.CreateParameter<double>("StemLength", &GenePlantStructure::stemUnitLength),
                helper.CreateParameter<double>("RotationAngleRadians", &GenePlantStructure::stemRotationAngle)
                );
}

std::string GenePlantStructure::TypeName() const
{
    return std::string(util::TypeName<GenePlantStructure>());
}

QString GenePlantStructure::ToString() const
{
    QString instructionString;

    instructionString += QLocale::system().toString(stemUnitLength, 'f', 0);
    instructionString += ", ";
    instructionString += QLocale::system().toString(stemRotationAngle, 'f', 0);
    instructionString += "°, ";

    instructionString.reserve(instructionString.size() + instructions.size());
    for (const auto& instruction : instructions) {
        instructionString.push_back(static_cast<std::underlying_type_t<Instruction>>(instruction));
    }
    return instructionString;
}

QString GenePlantStructure::Description() const
{
    return "<p>A Length, an angle & a series of characters.</p>"
           "<p>A node can be different lengths, one or more of the specified value long.</p>"
           "<p>A node can be rotated either clockwise or anti-clockwise in increments of the specified angle.</p>"
           "<p>The series of instructions defines the shape of the plant.</p>"
           "<p>The plant can be thought of as a tree of nodes, each plant begins as a single node and the instructons modify the structure one at a time.</p>"
           "<ul>"
           "<li>'N': Adds a new node to the structure, and selects it.</li>"
           "<li>'^': If the currently selected node has any children, selects the most recently added one.</li>"
           "<li>'v': If the currently selected node has a parent, selects the parent (only the very first  node, the root node, has no parent).</li>"
           "<li>'>': Selects the next node in the list (as if iterating from tip to root).</li>"
           "<li>'<': Selects the previous node in the list (as if iterating from tip to root).</li>"
           "<li>'|': Increases the length of the current node by the first gene value.</li>"
           "<li>'\\': Rotates the current node anti-clockwise by the second gene value.</li>"
           "<li>'/': Rotates the current node clockwise by the second gene value.</li>"
           "<li>' ': Does nothing, ignored.</li>"
           "<li>'#': Causes the instructions to stop being processed, like an early stop.</li>"
           "</ul>";
}

std::shared_ptr<Gene> GenePlantStructure::Mutated() const
{
    auto copy = std::make_shared<GenePlantStructure>(instructions, stemUnitLength, stemRotationAngle);

    switch (Random::Number(1, 5)) {
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
        case 4: {
            copy->stemUnitLength += Random::Gaussian(0.0, 1.0);
        }
        case 5: {
            copy->stemRotationAngle += Random::Gaussian(0.0, util::Tau / 19);
        }
    }

    return copy;
}

std::shared_ptr<Gene> GenePlantStructure::Crossed(const std::shared_ptr<Gene>& other) const
{
    std::shared_ptr<GenePlantStructure> otherPlantStructureGene = std::dynamic_pointer_cast<GenePlantStructure>(other);
    if (otherPlantStructureGene) {
        double averageStemUnitLength = (stemUnitLength + otherPlantStructureGene->stemUnitLength) / 2.0;
        double averageStemRotationAngle = (stemRotationAngle + otherPlantStructureGene->stemRotationAngle) / 2.0;
        return std::make_shared<GenePlantStructure>(Random::Merge(instructions, otherPlantStructureGene->instructions), averageStemUnitLength, averageStemRotationAngle);
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
    const int LENGTH_KEY = -5432;
    const int ROTATION_KEY = -5431;

    const double stemThickness = 1.0;
    const double leafRadius = 7.5;

    for (const Instruction& instruction : instructions) {
        // Every instruction ups metabolism
        phenotype.metabolism += 5.0_j;
        switch (instruction) {
        case Instruction::ADD_NODE:
            phenotype.AddNode(Vec2{ 0, stemUnitLength }, stemThickness, leafRadius);
            phenotype.AscendNodeTree();
            break;
        case Instruction::CLIMB_NODE_TREE:
            phenotype.AscendNodeTree();
            break;
        case Instruction::DESCEND_NODE_TREE:
            phenotype.DescendNodeTree();
            break;
        case Instruction::PREVIOUS_NODE:
            phenotype.SelectPreviousNode();
            break;
        case Instruction::NEXT_NODE:
            phenotype.SelectNextNode();
            break;
        case Instruction::GROW_UP:
            phenotype.GetCurrentNodesParameters()[LENGTH_KEY] += stemUnitLength;
            break;
        case Instruction::ROTATE_LEFT:
            phenotype.GetCurrentNodesParameters()[ROTATION_KEY] -= stemRotationAngle;
            break;
        case Instruction::ROTATE_RIGHT:
            phenotype.GetCurrentNodesParameters()[ROTATION_KEY] += stemRotationAngle;
            break;
        case Instruction::SKIP:
            // (disincentivise long empty genomes)
            phenotype.metabolism += 0.5_j;
            break;
        case Instruction::END_ALL:
            goto exit_loop_early;
        }
    }
    exit_loop_early:

    phenotype.ForEachNode([&](std::map<int, double>& parameters, Vec2& stemVector, double& nodeStemThickness, double& nodeLeafRadius)
    {
        Point relativeTip = ApplyOffset({ 0, 0 }, parameters[ROTATION_KEY], parameters[LENGTH_KEY]);
        stemVector.x += relativeTip.x;
        stemVector.y += relativeTip.y;

        stemVector.x += Random::Gaussian(0.0, 1.0);
        stemVector.y += Random::Gaussian(0.0, 1.0);

        nodeStemThickness = stemThickness;
        nodeLeafRadius = leafRadius;
    });

    auto height = phenotype.GetBounds(0).height();
    phenotype.metabolism += std::pow(height, 2.0) * 0.5_j;
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
        return Instruction::NEXT_NODE;
    case 4:
        return Instruction::PREVIOUS_NODE;
    case 5:
        return Instruction::GROW_UP;
    case 6:
        return Instruction::ROTATE_LEFT;
    case 7:
        return Instruction::ROTATE_RIGHT;
    case 8:
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
        switch(static_cast<Instruction>(characterInstruction)) {
        case Instruction::ADD_NODE:
            [[fallthrough]];
        case Instruction::CLIMB_NODE_TREE:
            [[fallthrough]];
        case Instruction::DESCEND_NODE_TREE:
            [[fallthrough]];
        case Instruction::GROW_UP:
            [[fallthrough]];
        case Instruction::NEXT_NODE:
            [[fallthrough]];
        case Instruction::PREVIOUS_NODE:
            [[fallthrough]];
        case Instruction::ROTATE_LEFT:
            [[fallthrough]];
        case Instruction::ROTATE_RIGHT:
            [[fallthrough]];
        case Instruction::SKIP:
            [[fallthrough]];
        case Instruction::END_ALL:
            instructions.push_back(static_cast<Instruction>(characterInstruction));
        }
    }
    return instructions;
}
