//
// Created by Kevin Aguilar
//

typedef struct myString{
    char *content;

} String;

String *allocString(char *chars);

String *concatString(char *input,char *newVal);