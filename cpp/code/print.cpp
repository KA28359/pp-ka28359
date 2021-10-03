//
// Created by Kevin Aguilar on 9/27/21.
//

#include "print.h"
#include <iostream>
#include <vector>
#include <sstream>
#include <algorithm>
#include <fstream>

void DoPrint(std::vector<Column> cols, bool header, char *argv[]){ //fix space in print

    std::string colVals;
    if(header){
        colVals = argv[3];
    }else{
        colVals = argv[2];
    }

    std::fstream f;
    std::string outFile;
    if(header){
        outFile = argv[5];
    }else{
        outFile = argv[4];
    }
    f.open(outFile, std::fstream::out | std::fstream::trunc);

    std::vector<int> colVector; //vector that holds the cols to print
    std::string currentVal;
    bool colError = false;
    for(int i = 0; i < colVals.size(); i++){ //reading the input string and getting the cols to print into the vector
        if((colVals.at(i) != ',')){
            if(std::isdigit(colVals.at(i))){ //only allow numbers
                currentVal.push_back(colVals.at(i));
                if(i == colVals.size()-1 ||(colVals.at(i+1) == ',')){ //if we reach end of string or a comma is next place number into vector
                    std::stringstream ss;
                    int colInt;
                    ss << currentVal;
                    ss >> colInt;
                    if(colInt < 0 || colInt >= cols.size()){
                        colError = true;
                        break;
                    }
                    colVector.push_back(colInt);
                    currentVal = "";
                }
            }else{
                colError = true;
                break;
            }

        }

    }

    if(colError){
        //printf("COL INDEX ERROR");
        std::cout << "COL INDEX ERROR" << std::endl;
        f << "COL INDEX ERROR";
        f.close();
        return;
    }

    sort(colVector.begin(), colVector.end());

    if(header){
        for(const Column& c : cols){
            if(std::count(colVector.begin(), colVector.end(), c.index)){
                //printf("%s ",c.name.c_str());
                f << c.name + " ";
            }
        }
        //printf("\n");
        f << "\n";
    }

    for(int i = 0; i < cols.at(0).size; i++){
        for(Column c : cols){
            if(std::count(colVector.begin(), colVector.end(), c.index)){
                //printf("%s ",c.allEntries.at(i).c_str());
                f << c.allEntries.at(i) + " ";
            }
        }
        //printf("\n");
        f << "\n";
    }


}
