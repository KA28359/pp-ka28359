//
// Created by Kevin Aguilar on 9/22/21.
//

#ifndef UNTITLED_ARRAYLIST_H
#define UNTITLED_ARRAYLIST_H

#endif //UNTITLED_ARRAYLIST_H

#include <stdbool.h>

typedef struct arrayListElement{

    char *value;
    struct arrayListElement *next;

} ArrayListElement;

typedef struct arrayList{

    int size;
    bool flag;
    ArrayListElement *e;
    struct arrayList *next;

} ArrayList;

ArrayList* allocArrayList();

bool ListContains(int val, const int array[], int size);