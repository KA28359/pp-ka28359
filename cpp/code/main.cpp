#include <iostream>
#include <fstream>
#include <vector>
#include "print.h"
#include "sum.h"
#include "Column.h"
#include <sstream>
#include "when.h"

int main(int argc, char *argv[]) {
    //std::cout << "Hello, World!" << std::endl;

    bool header = false;
    if(argc == 6){ //checking if header argument present
        header = true;
    }
    std::string filePath;
    if(header){
        filePath = argv[4];
    }else{
        filePath = argv[3];
    }

    std::fstream file(filePath);
    int numOfCols = 0;
    std::vector<std::vector<std::string>> tokens;
    if(file.is_open()){
        std::string line;
        while(std::getline(file,line)){ //tokenizing
            int colCounter = 0;
            std::vector<std::string> newRow;
            std::string currenString;
            for(int i = 0; i < line.size(); i++){ //checking the size of each col
                if((line.at(i) != ' ') && (line.at(i) != '\n') && (line.at(i) != '\t')){//not in the set
                    currenString.push_back(line.at(i));
                    if(i == line.size()-1 ||(line.at(i+1) == ' ') || (line.at(i+1) == '\n') || (line.at(i+1) == '\t') || (line.at(i+1) == '\0')){//is in the set
                        newRow.emplace_back(currenString); //placing the string in the new row and resetting current string
                        colCounter++;
                        currenString = "";
                    }
                }
            }
            tokens.push_back(newRow);
            //printf("%s \n",line.c_str());
            if(numOfCols == 0){ //entering size of first col
                numOfCols = colCounter;
            }else{ //checking size of first col with respect to the rest
                if(numOfCols != colCounter){ //if one col doesnt match the first col then error and return
                    std::cout << "NUM COLS ERROR" << std::endl;
                    return 0;
                }
            }
        }

        file.close();

        std::vector<Column> cols;
        for(int i = 0; i < numOfCols; i++){
            Column newColumn;
            if(header){
                newColumn.name = tokens[0][i];
                newColumn.size = tokens.size()-1;

            }else{
                newColumn.name = std::to_string(i);
                newColumn.size = tokens.size();
            }
            for(int j = 0; j < tokens.size(); j++){
                std::vector<std::string> v = tokens.at(j);
                if(j == 0 && header)continue;
                newColumn.allEntries.push_back(v.at(i));
                if(isInt(v.at(i))){
                    std::stringstream ss;
                    int currentInt;
                    ss << v.at(i);
                    ss >> currentInt;
                    newColumn.intEntries.push_back(currentInt);
                }else if(isFloat(v.at(i))){
                    float currentFloat = std::stof(v.at(i));
                    newColumn.floatEntries.push_back(currentFloat);
                }else{
                    newColumn.stringEntries.push_back(v.at(i));
                }
            }
            newColumn.index = i;
            cols.push_back(newColumn);

        }

        std::string prog;

        if(header){ //geting program name
            prog = argv[2];
        }else{
            prog = argv[1];
        }

        if(prog == "-print"){
            DoPrint(cols,header, argv);
        }else if(prog == "-sum"){
            DoSum(cols,header,argv);
        }else if(prog == "-when"){
            DoWhen(cols,header,argv);
        }

        cols.clear();

    }

    return 0;
}