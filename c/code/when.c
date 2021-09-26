//
// Created by Kevin Aguilar
//

#include "ArrayList.h"
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "when.h"

void whenCommand(ArrayList tokens,char*argv[], bool header, char* outFile) {

    char *cond;
    if (header) {
        cond = argv[3];
    } else {
        cond = argv[2];
    }

    FILE *f = fopen(outFile,"w");

    bool formatGood = true;
    bool symLeftFound = false;
    bool symRightFound = false;
    int checkedIndex = -1;
    bool commaFound = false;
    for(int i = 0; i < strlen(cond); i++){ //checking the format

        char currentChar = cond[i];
        if(i == 0){
            if(currentChar == '>' && (cond[i+1] == '$' || cond[i+1] == '@' || isalnum(cond[i+1]) || cond[i+1] == '-')){ //checking the comp symbol used
                if(cond[i+1] == '$' || cond[i+1] == '@'){
                    symLeftFound = true;
                }
                checkedIndex = i+2;
                continue;
            }else if(currentChar == '<' && (cond[i+1] == '$' || cond[i+1] == '@' || isalnum(cond[i+1]) || cond[i+1] == '-')){
                if(cond[i+1] == '$' || cond[i+1] == '@'){
                    symLeftFound = true;
                }
                checkedIndex = i+2;
                continue;
            }else if((currentChar == '<' && cond[i+1] == '>') && (cond[i+2] == '$' || cond[i+2] == '@' || isalnum(cond[i+2]) || cond[i+2] == '-')){
                if(cond[i+2] == '$' || cond[i+2] == '@'){
                    symLeftFound = true;
                    checkedIndex = i+3;
                    continue;
                }
                checkedIndex = i+2;
                continue;
            }else if((currentChar == '=' && cond[i+1] == '=') && (cond[i+2] == '$' || cond[i+2] == '@' || isalnum(cond[i+2]) || cond[i+2] == '-')){
                if(cond[i+2] == '$' || cond[i+2] == '@'){
                    symLeftFound = true;
                    checkedIndex = i+3;
                    continue;
                }
                checkedIndex = i+2;
                continue;
            }else{
                formatGood = false;
                break;
            }
        }
        if(i>=checkedIndex){ //checking the rest of the input
            if(isalnum(cond[i]) || cond[i] == '-'){
                continue;
            }else if(commaFound && (cond[i] == '@' || cond[i] == '$')){
                symRightFound = true;
                continue;
            }else if(symRightFound && (cond[i] == '@' || cond[i] == '$')){
                formatGood = false;
                break;
            }else if(commaFound && cond[i] == ','){
                formatGood = false;
                break;
            }else if(symLeftFound && !commaFound && (cond[i] == '@' || cond[i] == '$')){
                formatGood = false;
                break;
            }else if(cond[i] == ','){
                commaFound = true;
                continue;
            }
        }

    }

    if(!formatGood || !commaFound || (commaFound && !symRightFound && !isalnum(cond[strlen(cond)-1]))){ //not formatted good
        fprintf(f,"FORMAT ERROR");
        fclose(f);
        //printf("FORMAT ERROR");
        return;
    }

    bool eq = false;
    bool dne = false;
    bool gt = false;
    bool lt = false;

    bool leftSide = false;

    bool numGiven = false;

    bool noneGiven = false;

    if (cond[0] == '=' && cond[1] == '=') {
        eq = true;
    } else if (cond[0] == '<' && cond[1] == '>') {
        dne = true;
    } else if (cond[0] == '>') {
        gt = true;
    } else if (cond[0] == '<' && cond[1] != '>') {
        lt = true;
    }

    if (eq && (cond[2] == '@' || cond[2] == '$')) {
        leftSide = true;
        if (cond[2] == '$') {
            numGiven = true;
        }
    } else if (dne && (cond[2] == '@' || cond[2] == '$')) {
        leftSide = true;
        if (cond[2] == '$') {
            numGiven = true;
        }
    } else if (lt && (cond[1] == '@' || cond[1] == '$')) {
        leftSide = true;
        if (cond[1] == '$') {
            numGiven = true;
        }
    } else if (gt && (cond[1] == '@' || cond[1] == '$')) {
        leftSide = true;
        if (cond[1] == '$') {
            numGiven = true;
        }
    }

    int commaIndex = -1;
    for (int i = 0; i < strlen(cond); i++) {
        if (cond[i] == ',') {
            commaIndex = i;
            break;
        }
    }

    if (cond[commaIndex + 1] == '$') {
        numGiven = true;
    } else {
        if (!leftSide && cond[commaIndex + 1] != '@') {
            noneGiven = true;
        }
    }
    bool headerGiven = false;
    if(!noneGiven && !numGiven){
        headerGiven = true;
    }


    if (noneGiven) { //no col val was given

        char *constVal1 = NULL;
        char *constVal2 = NULL;
        if (eq || dne) {
            char left[commaIndex - 1];
            memcpy(left, &cond[2], commaIndex - 2);
            left[commaIndex - 2] = '\0';
            constVal1 = calloc(1, sizeof(left));
            strcpy(constVal1, left);
        } else if (gt || lt) {
            char left[commaIndex];
            memcpy(left, &cond[1], commaIndex - 1);
            left[commaIndex - 1] = '\0';
            constVal1 = calloc(1, sizeof(left));
            strcpy(constVal1, left);
        }
        if (eq || dne) {
            int constLen = strlen(cond) - commaIndex;
            char right[constLen];
            memcpy(right, &cond[commaIndex + 1], constLen - 1);
            right[constLen - 1] = '\0';
            constVal2 = calloc(1, sizeof(right));
            strcpy(constVal2, right);
        } else if (gt || lt) {
            int constLen = strlen(cond) - commaIndex;
            char right[constLen];
            memcpy(right, &cond[commaIndex + 1], constLen - 1);
            right[constLen - 1] = '\0';
            constVal2 = calloc(1, sizeof(right));
            strcpy(constVal2, right);
        }

        bool allVals = false;

        if (eq) {
            if (strcmp(constVal1, constVal2) == 0) {
                allVals = true;
            } else {
                allVals = false;
            }
        } else if (dne) {
            if (strcmp(constVal1, constVal2) == 0) {
                allVals = false;
            } else {
                allVals = true;
            }
        } else if (gt) {
            if ((strtof(constVal1, NULL) == 0 && strcmp(constVal1, "0") != 0) &&
                !isInt(constVal1)) { //neither a float or int
                allVals = false;
            } else if ((strtof(constVal2, NULL) == 0 && strcmp(constVal2, "0") != 0) && !isInt(constVal2)) {
                allVals = false;
            } else {
                if (isInt(constVal1)) {
                    if (isInt(constVal2)) {//both ints
                        if (atoi(constVal1) > atoi(constVal2)) {
                            allVals = true;
                        }
                    } else { //thisEle is int const is float
                        if (atoi(constVal1) > strtof(constVal2, NULL)) {
                            allVals = true;
                        }
                    }
                } else {
                    if (isInt(constVal2)) {//thisEle is float const is int
                        if (strtof(constVal1, NULL) > atoi(constVal2)) {
                            allVals = true;
                        }
                    } else {//both floats
                        if (strtof(constVal1, NULL) > strtof(constVal2, NULL)) {
                            allVals = true;
                        }
                    }

                }
            }
        } else if (lt) {
            if ((strtof(constVal1, NULL) == 0 && strcmp(constVal1, "0") != 0) &&
                !isInt(constVal1)) { //neither a float or int
                allVals = false;
            } else if ((strtof(constVal2, NULL) == 0 && strcmp(constVal2, "0") != 0) && !isInt(constVal2)) {
                allVals = false;
            } else {
                if (isInt(constVal1)) {
                    if (isInt(constVal2)) {//both ints
                        if (atoi(constVal1) < atoi(constVal2)) {
                            allVals = true;
                        }
                    } else { //thisEle is int const is float
                        if (atoi(constVal1) < strtof(constVal2, NULL)) {
                            allVals = true;
                        }
                    }
                } else {
                    if (isInt(constVal2)) {//thisEle is float const is int
                        if (strtof(constVal1, NULL) < atoi(constVal2)) {
                            allVals = true;
                        }
                    } else {//both floats
                        if (strtof(constVal1, NULL) < strtof(constVal2, NULL)) {
                            allVals = true;
                        }
                    }

                }
            }
        }

        if (allVals) {
            ArrayList *currentListPrint = &tokens;
            while (currentListPrint != NULL) {

                currentListPrint->flag = true;
                currentListPrint = (currentListPrint->next);
            }
        }

        free(constVal1);
        free(constVal2);


    } else if (leftSide) { //col val is on the left side

        char *colVal = NULL;
        char *constVal = NULL;
        if (eq || dne) {
            char left[commaIndex - 2];
            memcpy(left, &cond[3], commaIndex - 1);
            left[commaIndex - 3] = '\0';
            colVal = calloc(1, sizeof(left));
            strcpy(colVal, left);
        } else if (gt || lt) {
            char left[commaIndex - 1];
            memcpy(left, &cond[2], commaIndex - 1);
            left[commaIndex - 2] = '\0';
            colVal = calloc(1, sizeof(left));
            strcpy(colVal, left);
        }
        if (eq || dne) {
            int constLen = strlen(cond) - commaIndex;
            char right[constLen];
            memcpy(right, &cond[commaIndex + 1], constLen - 1);
            right[constLen - 1] = '\0';
            constVal = calloc(1, sizeof(right));
            strcpy(constVal, right);
        } else if (gt || lt) {
            int constLen = strlen(cond) - commaIndex;
            char right[constLen];
            memcpy(right, &cond[commaIndex + 1], constLen - 1);
            right[constLen - 1] = '\0';
            constVal = calloc(1, sizeof(right));
            strcpy(constVal, right);
        }

        int colNum = -1;
        if (numGiven) {
            colNum = atoi(colVal);
        }else if(headerGiven){
            ArrayList *headerList = &tokens;
            ArrayListElement headerElement = *(headerList->e);
            for (int j = 0; j < headerList->size; j++) {
                if(strcmp(headerElement.value,colVal) == 0){
                    colNum = j;
                    break;
                }
                if (headerElement.next != NULL) {
                    headerElement = *headerElement.next;
                }
            }
        }

        if(colNum < 0 || colNum >= tokens.size){
            fprintf(f,"COL INDEX ERROR");
            fclose(f);
            //printf("OTHER ERROR (INDEX)");
            return;
        }

        ArrayList *currentList = &tokens;
        if(header){
            currentList = (currentList->next);
        }
        while (currentList != NULL) {

            ArrayListElement thisElement = *(currentList->e);
            for (int j = 0; j < currentList->size; j++) {

                if (j != colNum) {
                    if (thisElement.next != NULL) {
                        thisElement = *thisElement.next;
                    }

                    continue;
                }

                if (eq) {
                    if (strcmp(thisElement.value, constVal) == 0) {
                        currentList->flag = true;
                    }
                } else if (dne) {
                    if (strcmp(thisElement.value, constVal) != 0) {
                        currentList->flag = true;
                    }
                } else if (gt) {

                    if ((strtof(thisElement.value, NULL) == 0 && strcmp(thisElement.value, "0") != 0) &&
                        !isInt(thisElement.value)) {
                        if (thisElement.next != NULL) {
                            thisElement = *thisElement.next;
                        }
                        continue;
                    }

                    if (isInt(thisElement.value)) {
                        if (isInt(constVal)) {//both ints
                            if (atoi(thisElement.value) > atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else { //thisEle is int const is float
                            if (atoi(thisElement.value) > strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }
                    } else {
                        if (isInt(constVal)) {//thisEle is float const is int
                            if (strtof(thisElement.value, NULL) > atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else {//both floats
                            if (strtof(thisElement.value, NULL) > strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }

                    }

                } else if (lt) {

                    if ((strtof(thisElement.value, NULL) == 0 && strcmp(thisElement.value, "0") != 0) &&
                        !isInt(thisElement.value)) {
                        if (thisElement.next != NULL) {
                            thisElement = *thisElement.next;
                        }
                        continue;
                    }

                    if (isInt(thisElement.value)) {
                        if (isInt(constVal)) {//both ints
                            if (atoi(thisElement.value) < atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else { //thisEle is int const is float
                            if (atoi(thisElement.value) < strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }
                    } else {
                        if (isInt(constVal)) {//thisEle is float const is int
                            if (strtof(thisElement.value, NULL) < atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else {//both floats
                            if (strtof(thisElement.value, NULL) < strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }

                    }

                }

                if (thisElement.next != NULL) {
                    thisElement = *thisElement.next;
                }

            }
            currentList = (currentList->next);
        }

        free(colVal);
        free(constVal);

    } else { //col val is on the right side

        char *colVal = NULL;
        char *constVal = NULL;
        if (eq || dne) {
            char left[commaIndex - 1];
            memcpy(left, &cond[2], commaIndex - 2);
            left[commaIndex - 2] = '\0';
            constVal = calloc(1, sizeof(left));
            strcpy(constVal, left);
        } else if (gt || lt) {
            char left[commaIndex];
            memcpy(left, &cond[1], commaIndex - 1);
            left[commaIndex - 1] = '\0';
            constVal = calloc(1, sizeof(left));
            strcpy(constVal, left);
        }
        if (eq || dne) {
            int constLen = strlen(cond) - commaIndex - 1;
            char right[constLen];
            memcpy(right, &cond[commaIndex + 2], constLen - 1);
            right[constLen - 1] = '\0';
            colVal = calloc(1, sizeof(right));
            strcpy(colVal, right);
        } else if (gt || lt) {
            int constLen = strlen(cond) - commaIndex - 1;
            char right[constLen];
            memcpy(right, &cond[commaIndex + 2], constLen - 1);
            right[constLen - 1] = '\0';
            colVal = calloc(1, sizeof(right));
            strcpy(colVal, right);
        }

        int colNum = -1;
        if (numGiven) {
            colNum = atoi(colVal);
        }else if(headerGiven){
            ArrayList *headerList = &tokens;
            ArrayListElement headerElement = *(headerList->e);
            for (int j = 0; j < headerList->size; j++) {
                if(strcmp(headerElement.value,colVal) == 0){
                    colNum = j;
                    break;
                }
                if (headerElement.next != NULL) {
                    headerElement = *headerElement.next;
                }
            }
        }

        if(colNum < 0 || colNum >= tokens.size){
            fprintf(f,"COL INDEX ERROR");
            fclose(f);
            //printf("OTHER ERROR (INDEX)");
            return;
        }

        ArrayList *currentList = &tokens;
        if(header){
            currentList = (currentList->next);
        }
        while (currentList != NULL) {

            ArrayListElement thisElement = *(currentList->e);
            for (int j = 0; j < currentList->size; j++) {

                if (j != colNum) {
                    if (thisElement.next != NULL) {
                        thisElement = *thisElement.next;
                    }

                    continue;
                }

                if (eq) {
                    if (strcmp(thisElement.value, constVal) == 0) {
                        currentList->flag = true;
                    }
                } else if (dne) {
                    if (strcmp(thisElement.value, constVal) != 0) {
                        currentList->flag = true;
                    }
                } else if (gt) {

                    if ((strtof(thisElement.value, NULL) == 0 && strcmp(thisElement.value, "0") != 0) &&
                        !isInt(thisElement.value)) {
                        if (thisElement.next != NULL) {
                            thisElement = *thisElement.next;
                        }
                        continue;
                    }

                    if (isInt(thisElement.value)) {
                        if (isInt(constVal)) {//both ints
                            if (atoi(thisElement.value) < atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else { //thisEle is int const is float
                            if (atoi(thisElement.value) < strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }
                    } else {
                        if (isInt(constVal)) {//thisEle is float const is int
                            if (strtof(thisElement.value, NULL) < atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else {//both floats
                            if (strtof(thisElement.value, NULL) < strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }

                    }

                } else if (lt) {

                    if ((strtof(thisElement.value, NULL) == 0 && strcmp(thisElement.value, "0") != 0) &&
                        !isInt(thisElement.value)) {
                        if (thisElement.next != NULL) {
                            thisElement = *thisElement.next;
                        }
                        continue;
                    }

                    if (isInt(thisElement.value)) {
                        if (isInt(constVal)) {//both ints
                            if (atoi(thisElement.value) > atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else { //thisEle is int const is float
                            if (atoi(thisElement.value) > strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }
                    } else {
                        if (isInt(constVal)) {//thisEle is float const is int
                            if (strtof(thisElement.value, NULL) > atoi(constVal)) {
                                currentList->flag = true;
                            }
                        } else {//both floats
                            if (strtof(thisElement.value, NULL) > strtof(constVal, NULL)) {
                                currentList->flag = true;
                            }
                        }

                    }

                }

                if (thisElement.next != NULL) {
                    thisElement = *thisElement.next;
                }

            }
            currentList = (currentList->next);
        }

        free(constVal);
        free(colVal);

    }

    ArrayList *currentListPrint = &tokens;
    if(header){
        currentListPrint->flag = true;
    }
    int counter = 0;
    bool flag = false;
    while (currentListPrint != NULL) { //print out the list
        if (currentListPrint->flag) {

            if(flag){
                fprintf(f,"\n");
                //printf("\n");
            }
            flag = true;

            ArrayListElement thisElement = *(currentListPrint->e);
            for (int j = 0; j < currentListPrint->size; j++) {

                if(j == tokens.size-1){
                    fprintf(f,"%s", thisElement.value);
                    //printf("%s", thisElement.value);
                }else{
                    fprintf(f,"%s ", thisElement.value);
                    //printf("%s ", thisElement.value);
                }
                if (thisElement.next != NULL) {
                    thisElement = *thisElement.next;
                }
            }
        }
        currentListPrint = (currentListPrint->next);
        counter++;

    }

    fclose(f);

}


bool isInt(char*value){ //checking if value is an integer

    for(int i = 0; i < strlen(value); i++){
        if(i == 0){
            if(value[i] == '-'){
                continue;
            }
        }
        if(value[i] >= '0' && value[i] <= '9'){
            continue;
        }else{
            return false;
        }
    }
    return true;

}