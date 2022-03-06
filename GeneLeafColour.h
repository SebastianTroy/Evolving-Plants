#ifndef GENELEAFCOLOUR_H
#define GENELEAFCOLOUR_H

#include "Gene.h"

#include <QColor>

class GeneLeafColour : public Gene {
public:
    GeneLeafColour(QRgb colour);
    GeneLeafColour(const QColor& colour);
    virtual ~GeneLeafColour() {}

    virtual std::shared_ptr<Gene> Mutated() const override;
    virtual std::shared_ptr<Gene> Crossed(const std::shared_ptr<Gene>& other) const override;
    virtual double Similarity(const std::shared_ptr<Gene>& other) const override;
    virtual void Express(Phenotype& phenotype) const override;

private:
    QColor colour;

    static QColor InterpolateColours(const QColor& a, const QColor& b);
};

#endif // GENELEAFCOLOUR_H
