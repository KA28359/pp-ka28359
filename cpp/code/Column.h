//
// Created by Kevin Aguilar on 9/27/21.
//

#ifndef MYHEADEFILE_H
#define MYHEADEFILE_H

#include <iostream>
#include <vector>
class Column {
public:
    int size;
    int index;
    std::string name;
    std::vector<std::string> allEntries;
    std::vector<std::string> stringEntries;
    std::vector<int> intEntries;
    std::vector<float> floatEntries;
    float sumOfElements();

};

#endif
