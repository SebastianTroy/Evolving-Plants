#include "MainWindow.h"

#include <Random.h>

#include <fmt/core.h>

#include <QApplication>

#include <time.h>

int main(int argc, char *argv[])
{
    auto seed = static_cast<unsigned long>(time(nullptr));
    Random::Seed(seed);
    fmt::print("Seed: {}\n", seed);

    QApplication a(argc, argv);
    MainWindow m;
    m.show();
    return a.exec();
}
