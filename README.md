Evolving Plants Version 4.0
===========================
Developed with Qt 6.3.0 & MinGW 9.0.0

Built with CMake and Ninja in QtCreator

For a complete description of the project visit the [project's webpage](https://troydev.co.uk/evolving-plants).

TODO
----
 - [X] Basic port of Java simulation code to C++
 - [X] Tweaks to balance to create more than one plant niche, allowing multiple concurrent species
 - [X] Implement genes as seperate entities with a gene factory, a plant has one of every gene, genes modify a Phenotype and a plant constructs itself from the phenotype
 - [ ] GUI
 - - [X] Create new simulation of custom size
 - - [X] Scroll viewport so that the entire simulation can be seen by scrolling through it
 - - [X] Control simulation speed with fast-forward
 - - [X] Controlls to add/remove plants
 - - [X] Save/load plant genetics
 - - [X] Information panels for simulation and selected plants
 - [ ] Update website, then update this TODO to link to images
 - - [ ] Add a video walkthrough
 - [ ] Modding! It should be possible to make a custom Gene, and to enable/disable genes individually (including non-modded ones)
 - [ ] Implement all actual genes as modded genes and test performance difference, go full modded route if negligable
