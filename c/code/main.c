#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "print.h"
#include "String.h"
#include "sum.h"
#include "when.h"


int main(int argc, char *argv[]) {

    bool header = false;
    if(strcmp(argv[1],"-header") == 0){
        header = true;
    }
    char * filePath;
    if(header){
        filePath = argv[4];
    }else{
        filePath = argv[3];
    }
    FILE *file;
    char buff[255];
    file = fopen( filePath, "r" );
    int numberOfRows = 0;
    int numberOfColumns = 0;

    while(fgets(buff,225,file)){
        numberOfRows++;
        int colCounter = 0;
        int currentChar = 0;
        while(buff[currentChar] != '\0'){

            if((buff[currentChar] != ' ') && (buff[currentChar] != '\n') && (buff[currentChar] != '\t')){//not in the set
                if((buff[currentChar+1] == ' ') || (buff[currentChar+1] == '\n') || (buff[currentChar+1] == '\t') || (buff[currentChar+1] == '\0')){//is in the set
                    colCounter++;
                }
            }
            currentChar++;
        }
        if(numberOfColumns == 0){
            numberOfColumns = colCounter;
        }else{
            if(numberOfColumns != colCounter){
                printf("COLS DONT MATCH");
                return 0;
            }
        }

    }
    fclose(file);

    file = fopen( filePath, "r" );
    int currentRow = -1;
    ArrayList *tokens = NULL;
    while(fgets(buff,255,file)){ //creating an 'arraylist' of the tokens

        currentRow++;
        ArrayList *newRow = allocArrayList();
        ArrayListElement* newElement = calloc(1,sizeof(ArrayListElement));

        int currentChar = 0;
        int currentCol = 0;
        char *newString = calloc(1,sizeof(char));
        String currentString = *allocString(newString);
        int sizeCounter = 0;

        while(buff[currentChar] != '\0'){

            if((buff[currentChar] != ' ') && (buff[currentChar] != '\n') && (buff[currentChar] != '\t')){
                currentString = *concatString(currentString.content,&buff[currentChar]);
                if((buff[currentChar+1] == ' ') || (buff[currentChar+1] == '\n') || (buff[currentChar+1] == '\t') || (buff[currentChar+1] == '\0')){
                    newElement->value = currentString.content;
                    if(newRow->e == NULL){
                        newRow->e = newElement;
                    }else{
                        ArrayListElement *findLastElement = newRow->e;
                        while(findLastElement->next != NULL){
                            findLastElement = findLastElement->next;
                        }
                        findLastElement->next = newElement;
                        //printf("HI");
                    }
                    sizeCounter++;
                    newRow->size = sizeCounter;
                }

            }else if((buff[currentChar] == ' ') || (buff[currentChar] == '\n') || (buff[currentChar] == '\t') || (buff[currentChar] == '\0')){
                if((buff[currentChar+1] != ' ') && (buff[currentChar+1] != '\n') && (buff[currentChar+1] != '\t')){
                    currentCol++;
                    newString = calloc(1,sizeof(char));
                    currentString = *allocString(newString);
                    newElement = calloc(1,sizeof(ArrayListElement));
                }
            }
            currentChar++;
        }

        ArrayList *findLast = tokens;
        if(findLast == NULL){
            tokens = newRow;
        }else{
            while(findLast->next != NULL){
                findLast = findLast->next;
            }
            findLast->next = newRow;
        }

    }
    fclose(file);

    char* command;
    char* outFile;
    if(header){
        command = argv[2];
        outFile = argv[5];
    }else{
        command = argv[1];
        outFile = argv[4];
    }

    if(strcmp(command,"-print") == 0){
        printCommand(*tokens,argv, header, outFile);
    }else if(strcmp(command,"-sum") == 0){
        sumCommand(*tokens,argv,header, outFile);
    }else if(strcmp(command,"-when") == 0){
        whenCommand(*tokens,argv,header, outFile);
    }


    ArrayList *currentList = tokens;
    ArrayList *previousTokens;
    while(currentList != NULL){

        ArrayListElement *thisElement = currentList->e;
        ArrayListElement *previousElement;
        for(int j = 0; j < currentList->size; j++){

            if(thisElement->next != NULL){
                previousElement = thisElement;
                thisElement = thisElement->next;
                free(previousElement);
                continue;
            }else{
                previousElement = thisElement;
                thisElement = NULL;
                free(previousElement);
            }

        }
        previousTokens = currentList;
        currentList = (currentList->next);
        free(previousTokens);

    }
    //free(tokens);


    return 0;
}
