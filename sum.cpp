//
// Created by Kevin Aguilar on 9/27/21.
//

#include "sum.h"
#include <sstream>
#include <fstream>

void DoSum(std::vector<Column> cols, bool header, char *argv[]){
    std::string colVal;
    if(header){
        colVal = argv[3];
    }else{
        colVal = argv[2];
    }

    std::fstream f;
    std::string outFile;
    if(header){
        outFile = argv[5];
    }else{
        outFile = argv[4];
    }
    f.open(outFile, std::fstream::out | std::fstream::trunc);

    int colInt;
    std::string currentVal;
    bool colError = false;
    for(int i = 0; i < colVal.size(); i++){ //reading the input string and getting the cols to print into the vector
            if(std::isdigit(colVal.at(i))){ //only allow numbers
                currentVal.push_back(colVal.at(i));
                if(i == colVal.size()-1){ //if we reach end of string or a comma is next place number into vector
                    std::stringstream ss;
                    ss << currentVal;
                    ss >> colInt;
                    if(colInt < 0 || colInt >= cols.size()){
                        colError = true;
                        break;
                    }
                    currentVal = "";
                }
            }else{
                colError = true;
                break;
            }

    }

    if(colError){
        //printf("COL INDEX ERROR");
        f << "COL INDEX ERROR";
        f.close();
        return;
    }

    if(!cols.at(colInt).stringEntries.empty()){
        //printf("TYPE ERROR");
        f << "TYPE ERROR";
        f.close();
        return;
    }

    float sum = cols.at(colInt).sumOfElements();

    //printf("%g",sum);
    f << sum;
    f.close();
}