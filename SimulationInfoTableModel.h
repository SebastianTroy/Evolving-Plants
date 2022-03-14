#ifndef SIMULATIONINFOTABLEMODEL_H
#define SIMULATIONINFOTABLEMODEL_H

#include <QAbstractTableModel>

class SimulationInfoTableModel : public QAbstractTableModel {
    Q_OBJECT
public:
    struct TableRow {
        QString name;
        QString value;
        QString description;
    };

    explicit SimulationInfoTableModel(QObject *parent = nullptr);

    virtual int rowCount(const QModelIndex& parent) const override;
    virtual int columnCount(const QModelIndex& parent) const override;
    virtual QVariant data(const QModelIndex& index, int role) const override;

    void UpdateAll(QVector<TableRow>&& data);
private:
    static constexpr inline int NAME_COLUMN = 0;
    static constexpr inline int VALUE_COLUMN = 1;

    QVector<TableRow> displayData;
};

#endif // SIMULATIONINFOTABLEMODEL_H
