cmake_minimum_required(VERSION 3.21)

project(Evolving-Plants VERSION 4 LANGUAGES CXX)

set(CMAKE_INCLUDE_CURRENT_DIR ON)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(QT NAMES Qt6 COMPONENTS Widgets REQUIRED)
find_package(Qt${QT_VERSION_MAJOR} COMPONENTS Widgets REQUIRED)

set(PROJECT_SOURCES
    Gene.h
    GeneFactory.h
    GeneFactory.cpp
    GeneLeafColour.h
    GeneLeafColour.cpp
    GenePlantStructure.h
    GenePlantStructure.cpp
    GeneSeedProduction.h
    GeneSeedProduction.cpp
    GeneStemProportions.h
    GeneStemProportions.cpp
    LightMap.h
    LightMap.cpp
    main.cpp
    MainWindow.h
    MainWindow.cpp
    MainWindow.ui
    Phenotype.h
    Phenotype.cpp
    Plant.h
    Plant.cpp
    Resources.qrc
    Simulation.h
    Simulation.cpp
    SimulationViewWidget.h
    SimulationViewWidget.cpp
)

if(${QT_VERSION_MAJOR} GREATER_EQUAL 6)
    qt_add_executable(Evolving-Plants
        MANUAL_FINALIZATION
        ${PROJECT_SOURCES}
    )
# Define target properties for Android with Qt 6 as:
#    set_property(TARGET Evolving-Plants APPEND PROPERTY QT_ANDROID_PACKAGE_SOURCE_DIR
#                 ${CMAKE_CURRENT_SOURCE_DIR}/android)
# For more information, see https://doc.qt.io/qt-6/qt-add-executable.html#target-creation
else()
    if(ANDROID)
        add_library(Evolving-Plants SHARED
            ${PROJECT_SOURCES}
        )
# Define properties for Android with Qt 5 after find_package() calls as:
#    set(ANDROID_PACKAGE_SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/android")
    else()
        add_executable(Evolving-Plants
            ${PROJECT_SOURCES}
        )
    endif()
endif()

include(FetchContent)

list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR})
include(json)
include(fmt)
include(utility)

target_link_libraries(Evolving-Plants
    PRIVATE
    Qt6::Widgets
    nlohmann_json::nlohmann_json
    fmt::fmt
    Utility
)

# Make additional files appear in QtCreator
FILE(GLOB SAVD_GENOMES SavedGenomes/*)
add_custom_target(Other
    SOURCES
    README.md
    ${SAVD_GENOMES}
    ${JAVA_CODE}
)

set_target_properties(Evolving-Plants PROPERTIES
    MACOSX_BUNDLE_GUI_IDENTIFIER my.example.com
    MACOSX_BUNDLE_BUNDLE_VERSION ${PROJECT_VERSION}
    MACOSX_BUNDLE_SHORT_VERSION_STRING ${PROJECT_VERSION_MAJOR}.${PROJECT_VERSION_MINOR}
    MACOSX_BUNDLE TRUE
    WIN32_EXECUTABLE TRUE
)

if(QT_VERSION_MAJOR EQUAL 6)
    qt_finalize_executable(Evolving-Plants)
endif()

# Make deploying/distributing this executable much easier!
if (WIN32)
    # Retrieve the absolute path to qmake and then use that path to find
    # the windeployqt and qtenv2 executables
    get_target_property(QMAKE_EXE Qt6::qmake IMPORTED_LOCATION)
    get_filename_component(QT_BIN_DIR "${QMAKE_EXE}" DIRECTORY)
    find_program(WINDEPLOYQT_ENV_SETUP qtenv2.bat HINTS "${QT_BIN_DIR}")
    find_program(WINDEPLOYQT_EXECUTABLE windeployqt HINTS "${QT_BIN_DIR}")

    add_custom_command(TARGET Evolving-Plants
        POST_BUILD
        COMMAND "${WINDEPLOYQT_ENV_SETUP}" && "${WINDEPLOYQT_EXECUTABLE}" \"$<TARGET_FILE:Evolving-Plants>\" --no-translations
        COMMENT "Packaging dependancies..."
    )
endif()
