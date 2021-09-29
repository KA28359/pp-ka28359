//
// Created by Kevin Aguilar on 9/27/21.
//

#include "Column.h"

float Column::sumOfElements() {
    float sum = 0;
    for(int iEntries : this->intEntries){
        sum = sum + iEntries;
    }

    for(float fEntries : this->floatEntries){
        sum = sum + fEntries;
    }
    return sum;
}
