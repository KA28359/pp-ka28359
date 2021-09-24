//
// Created by Kevin Aguilar
//

#include "String.h"
#include <string.h>
#include <stdlib.h>

String *allocString(char *chars){

    String *self = calloc(1,sizeof(String));
    self->content = calloc(strlen(chars) + 1, sizeof(char));
    strcpy(self->content,chars);
    return self;

}

String *concatString(char *input,char *newVal){
    String *self = calloc(1,sizeof(String));
    self->content = calloc(strlen(input)+2,sizeof(char));
    strcpy(self->content,input);
    strncat(self->content,newVal,1);
    free(input);
    return self;
}