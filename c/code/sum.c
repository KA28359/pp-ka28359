//
// Created by Kevin Aguilar
//

#include "ArrayList.h"
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>

void sumCommand(ArrayList tokens,char*argv[], bool header, char* outFile){ //done

    char* cols;
    if(header){
        cols = argv[3];
    }else{
        cols = argv[2];
    }

    int colNum = atoi(cols);
    FILE *f;
    f = fopen(outFile,"w");

    if(colNum >= tokens.size || colNum < 0){
        fprintf(f,"COL INDEX ERROR");
        fclose(f);
        //printf("COL INDEX ERROR");
        return;
    }

    ArrayList *currentList = &tokens;
    if(header){
        currentList = (currentList->next);
    }

    float sum = 0;

    while(currentList != NULL){ //traversing the 'arraylist'

        ArrayListElement thisElement = *(currentList->e);
        for(int j = 0; j < currentList->size; j++){

            if(j != colNum){
                if(thisElement.next != NULL){
                    thisElement = *thisElement.next;
                }

                continue;
            }



            if((strtof(thisElement.value,NULL) == 0 && strcmp(thisElement.value,"0") != 0)){ //incompatible type
                fprintf(f,"TYPE ERROR");
                fclose(f);
                //printf("TYPE ERROR");
                return;
            }

            sum = sum + strtof(thisElement.value,NULL); //adding to sum

            if(thisElement.next != NULL){
                thisElement = *thisElement.next;
            }

        }

        currentList = (currentList->next);

    }

    //printf("%g",sum);
    fprintf(f,"%g",sum);
    fclose(f);

}