//
// Created by Kevin Aguilar
//

#include "ArrayList.h"
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>

ArrayList* allocArrayList(){

    ArrayList *self = calloc(1,sizeof(ArrayList));
    self->size = 0;
    self->e = NULL;
    self->next = NULL;
    self->flag = false;
    return self;

}

bool ListContains(int val, const int array[], int size){

    for(int i = 0; i < size; i++){
        if(array[i] == val){
            return true;
        }
    }
    return false;

}