#include "SimulationInfoTableModel.h"


SimulationInfoTableModel::SimulationInfoTableModel(QObject* parent)
    : QAbstractTableModel(parent)
{
}

int SimulationInfoTableModel::rowCount(const QModelIndex&) const
{
    return displayData.size();
}

int SimulationInfoTableModel::columnCount(const QModelIndex&) const
{
    return 2;
}

QVariant SimulationInfoTableModel::data(const QModelIndex& index, int role) const
{
    switch (role) {
    case Qt::ItemDataRole::ToolTipRole:
        return displayData.at(index.row()).description;
    case Qt::ItemDataRole::DisplayRole:
        if (index.column() == NAME_COLUMN) {
            return displayData.at(index.row()).name;
        } else if (index.column() == VALUE_COLUMN) {
            return displayData.at(index.row()).value;
        }
        break;
    }
    return {};
}

void SimulationInfoTableModel::UpdateAll(QVector<TableRow>&& newData)
{
    beginResetModel();
    displayData = std::move(newData);
    endResetModel();
}
