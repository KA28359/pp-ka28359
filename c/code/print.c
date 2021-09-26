//
// Created by Kevin Aguilar
//

#include "ArrayList.h"
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>

void printCommand(ArrayList tokens, char*argv[], bool header, char* outFile){ //done

    char* cols;
    if(header){
        cols = argv[3];
    }else{
        cols = argv[2];
    }

    int numOfCols = 1;
    for(int i = 0; i < strlen(cols); i++){

        if(cols[i] == ','){
            numOfCols++;
        }

    }

    int colNums[numOfCols];
    FILE *f;
    f = fopen(outFile, "w");

    int currentIndex = 0;
    char* num = strtok(cols,",");
    if(atoi(num) < 0 || atoi(num) >= tokens.size){
        fprintf(f,"COL INDEX ERROR");
        fclose(f);
        //printf("COL INDEX ERROR");
        return;
    }
    colNums[currentIndex] = atoi(num);
    currentIndex++;
    while(num!=NULL){ //getting all col vals
        num = strtok(NULL,",");
        if(num == NULL)continue;
        if(atoi(num) < 0 || atoi(num) >= tokens.size){
            fprintf(f,"COL INDEX ERROR");
            fclose(f);
            //printf("COL INDEX ERROR");
            return;
        }
        colNums[currentIndex] = atoi(num);
        currentIndex++;
    }

    ArrayList *currentList = &tokens;
    bool flag = false;
    while(currentList != NULL){ //traversing the 'arraylist'

        if(flag){
            fprintf(f,"\n");
            //printf("\n");
        }
        flag = true;

        ArrayListElement thisElement = *(currentList->e);
        int counter = 0;
        for(int j = 0; j < currentList->size; j++){

            if(!ListContains(j,colNums, numOfCols)){
                if(thisElement.next != NULL){
                    thisElement = *thisElement.next;
                }

                continue;
            }
            counter++;
            if(counter == numOfCols){
                fprintf(f,"%s",thisElement.value);
                //printf("%s",thisElement.value);
            }else{
                fprintf(f,"%s ",thisElement.value);
                //printf("%s ",thisElement.value);
            }
            if(thisElement.next != NULL){
                thisElement = *thisElement.next;
            }

        }

        currentList = (currentList->next);
    }

    fclose(f);


}