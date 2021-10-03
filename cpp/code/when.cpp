//
// Created by Kevin Aguilar on 9/27/21.
//

#include "when.h"
#include <regex>
#include <sstream>
#include <fstream>

bool isInt(std::string value){

    for(int i = 0; i < value.size(); i++){
        if(i == 0){
            if(value.at(i) == '-')continue;
        }
        if(std::isdigit(value.at(i))){
            continue;
        }else{
            return false;
        }
    }
    return true;

}

bool isFloat(std::string value){

    bool decimalFound = false;
    for(int i = 0; i < value.size(); i++){
        if(i == 0){
            if(value.at(i) == '-')continue;
        }
        if(std::isdigit(value.at(i))){
            continue;
        }else if(!decimalFound && value.at(i) == '.'){
            decimalFound = true;
            continue;
        }else{
            return false;
        }
    }
    return true;

}

void DoWhen(std::vector<Column> cols, bool header, char *argv[]){ //error printing empty list

    std::string cond;
    if(header){
        cond = argv[3];
    }else{
        cond = argv[2];
    }

    std::fstream f;
    std::string outFile;
    if(header){
        outFile = argv[5];
    }else{
        outFile = argv[4];
    }
    f.open(outFile, std::fstream::out | std::fstream::trunc);

    bool eq = false;
    bool dne = false;
    bool gt = false;
    bool lt = false;

    std::regex b("((<|==|>|<>)[$@]?[-_.a-zA-Z0-9]+,[$@]?[-_.a-zA-Z0-9]+)");
    if (!regex_match(cond, b)){
        //printf("COND ERROR");
        std::cout << "COND ERROR" << std::endl;
        f << "COND ERROR";
        f.close();
        return;
    }
    std::regex gtTest("((>)(.*))");
    std::regex ltTest("((<)(.*))");
    std::regex eqTest("((==)(.*))");
    std::regex dneTest("((<>)(.*))");
    if (regex_match(cond, dneTest)){
        //printf("dne");
        dne = true;
    }else if (regex_match(cond, ltTest)){
        //printf("lt");
        lt = true;
    }else if (regex_match(cond, eqTest)){
        //printf("eq");
        eq = true;
    }else if (regex_match(cond, gtTest)){
        //printf("gt");
        gt = true;
    }

    bool containsColNum = false;
    bool containsColHeader = false;
    if(cond.find('$') < cond.length()){
        containsColNum = true;
    }else if(cond.find('@') < cond.length()){
        containsColHeader = true;
    }

    bool leftSide = false;
    if(containsColNum){
        if(cond.find('$') < cond.find(',')){
            leftSide = true;
        }
    }else if(containsColHeader){
        if(cond.find('@') < cond.find(',')){
            leftSide = true;
        }
    }
    std::vector<int> rows;

    if(!containsColHeader && !containsColNum){

        std::string constVal1;
        std::string constVal2;

        if(eq || dne){
            constVal1 = cond.substr(2,cond.find(',')-2);
        }else{
            constVal1 = cond.substr(1,cond.find(',')-1);
        }

        constVal2 = cond.substr(cond.find(',')+1,cond.size()-cond.find(','));

        bool all = false;

        if(eq){
            if(constVal1 == constVal2){
                all = true;
            }
        }else if(dne){
            if(constVal1 != constVal2){
                all = true;
            }
        }else if (gt){
            if(isInt(constVal1)){
                if(isInt(constVal2)){
                    std::stringstream ss;
                    std::stringstream ss2;
                    int constIntVal1;
                    int constIntVal2;
                    ss << constVal1;
                    ss >> constIntVal1;
                    ss2 << constVal2;
                    ss2 >> constIntVal2;
                    if(constIntVal1 > constIntVal2){
                        all = true;
                    }
                }else if(isFloat(constVal2)){
                    std::stringstream ss;
                    int constIntVal1;
                    float constFloatVal2 = std::stof(constVal2);
                    ss << constVal1;
                    ss >> constIntVal1;
                    if((float) constIntVal1 > constFloatVal2){
                        all = true;
                    }
                }
            }else if(isFloat(constVal1)){
                if(isInt(constVal2)){
                    float constFloatVal1 = std::stof(constVal1);
                    std::stringstream ss;
                    int constIntVal2;
                    ss << constVal2;
                    ss >> constIntVal2;
                    if(constFloatVal1 > (float) constIntVal2){
                        all = true;
                    }
                }else if(isFloat(constVal2)){
                    float constFloatVal1 = std::stof(constVal1);
                    float constFloatVal2 = std::stof(constVal2);
                    if(constFloatVal1 > constFloatVal2){
                        all = true;
                    }
                }
            }
        }else if (lt){
            if(isInt(constVal1)){
                if(isInt(constVal2)){
                    std::stringstream ss;
                    std::stringstream ss2;
                    int constIntVal1;
                    int constIntVal2;
                    ss << constVal1;
                    ss >> constIntVal1;
                    ss2 << constVal2;
                    ss2 >> constIntVal2;
                    if(constIntVal1 < constIntVal2){
                        all = true;
                    }
                }else if(isFloat(constVal2)){
                    std::stringstream ss;
                    int constIntVal1;
                    float constFloatVal2 = std::stof(constVal2);
                    ss << constVal1;
                    ss >> constIntVal1;
                    if((float) constIntVal1 < constFloatVal2){
                        all = true;
                    }
                }
            }else if(isFloat(constVal1)){
                if(isInt(constVal2)){
                    float constFloatVal1 = std::stof(constVal1);
                    std::stringstream ss;
                    int constIntVal2;
                    ss << constVal2;
                    ss >> constIntVal2;
                    if(constFloatVal1 < (float) constIntVal2){
                        all = true;
                    }
                }else if(isFloat(constVal2)){
                    float constFloatVal1 = std::stof(constVal1);
                    float constFloatVal2 = std::stof(constVal2);
                    if(constFloatVal1 < constFloatVal2){
                        all = true;
                    }
                }
            }
        }

        if(all){
            for(int i = 0; i < cols.at(0).size; i++){
                rows.push_back(i);
            }
        }

    }else if(leftSide){

        std::string colVal;
        std::string constVal;

        if(containsColNum){
            colVal = cond.substr(cond.find('$')+1,cond.find(',')-cond.find('$')-1);
            //printf("%s",colVal.c_str());
            if(header){
                std::stringstream ss;
                ss << colVal;
                int index;
                ss >> index;
                if(index >= cols.size() || index < 0){
                    //printf("COL INDEX ERROR");
                    std::cout << "COL INDEX ERROR" << std::endl;
                    f << "COL INDEX ERROR";
                    f.close();
                    return;
                }
                colVal = cols.at(index).name;
            }
        }else{
            colVal = cond.substr(cond.find('@')+1,cond.find(',')-cond.find('@')-1);
            //printf("%s",colVal.c_str());
            bool found = false;
            for(const Column& c : cols){
                if(c.name == colVal){
                    found = true;
                    break;
                }
            }
            if(!found){
                //printf("COL INDEX ERROR");
                std::cout << "COL INDEX ERROR" << std::endl;
                f << "COL INDEX ERROR";
                f.close();
                return;
            }
        }

        //printf("\n");
        constVal = cond.substr(cond.find(',')+1,cond.size()-cond.find(','));
        //printf("%s",constVal.c_str());

        if(eq){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(c.allEntries.at(i) == constVal){
                        rows.push_back(i);
                    }
                }
            }
        }else if(dne){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(c.allEntries.at(i) != constVal){
                        rows.push_back(i);
                    }
                }
            }
        }else if(gt){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(isInt(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            std::stringstream ss;
                            std::stringstream ss2;
                            int colIntVal;
                            int constIntVal;
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            ss2 << constVal;
                            ss2 >> constIntVal;
                            if(colIntVal > constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            std::stringstream ss;
                            int colIntVal;
                            float constFloatVal = std::stof(constVal);
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            if((float) colIntVal > constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }else if(isFloat(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            std::stringstream ss;
                            int constIntVal;
                            ss << constVal;
                            ss >> constIntVal;
                            if(colFloatVal > (float) constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            float constFloatVal = std::stof(constVal);
                            if(colFloatVal > constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }
                }
            }
        }else if (lt){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(isInt(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            std::stringstream ss;
                            std::stringstream ss2;
                            int colIntVal;
                            int constIntVal;
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            ss2 << constVal;
                            ss2 >> constIntVal;
                            if(colIntVal < constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            std::stringstream ss;
                            int colIntVal;
                            float constFloatVal = std::stof(constVal);
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            if((float) colIntVal < constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }else if(isFloat(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            std::stringstream ss;
                            int constIntVal;
                            ss << constVal;
                            ss >> constIntVal;
                            if(colFloatVal < (float) constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            float constFloatVal = std::stof(constVal);
                            if(colFloatVal < constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }
                }
            }
        }

    }else{

        std::string colVal;
        std::string constVal;

        if(containsColNum){
            colVal = cond.substr(cond.find('$')+1,cond.size()-cond.find('$'));
            //printf("%s",colVal.c_str());
            if(header){
                std::stringstream ss;
                ss << colVal;
                int index;
                ss >> index;
                if(index >= cols.size() || index < 0){
                    //printf("COL INDEX ERROR");
                    std::cout << "COL INDEX ERROR" << std::endl;
                    f << "COL INDEX ERROR";
                    f.close();
                    return;
                }
                colVal = cols.at(index).name;
            }
        }else{
            colVal = cond.substr(cond.find('@')+1,cond.size()-cond.find('@'));
            //printf("%s",colVal.c_str());
            bool found = false;
            for(const Column& c : cols){
                if(c.name == colVal){
                    found = true;
                    break;
                }
            }
            if(!found){
                //printf("COL INDEX ERROR");
                std::cout << "COL INDEX ERROR" << std::endl;
                f << "COL INDEX ERROR";
                f.close();
                return;
            }
        }

       // printf("\n");
        if(eq || dne){
            constVal = cond.substr(2,cond.find(',')-2);
        }else{
            constVal = cond.substr(1,cond.find(',')-1);
        }
       // printf("%s",constVal.c_str());

        if(eq){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(c.allEntries.at(i) == constVal){
                        rows.push_back(i);
                    }
                }
            }
        }else if(dne){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(c.allEntries.at(i) != constVal){
                        rows.push_back(i);
                    }
                }
            }
        }else if(gt){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(isInt(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            std::stringstream ss;
                            std::stringstream ss2;
                            int colIntVal;
                            int constIntVal;
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            ss2 << constVal;
                            ss2 >> constIntVal;
                            if(colIntVal < constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            std::stringstream ss;
                            int colIntVal;
                            float constFloatVal = std::stof(constVal);
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            if((float) colIntVal < constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }else if(isFloat(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            std::stringstream ss;
                            int constIntVal;
                            ss << constVal;
                            ss >> constIntVal;
                            if(colFloatVal < (float) constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            float constFloatVal = std::stof(constVal);
                            if(colFloatVal < constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }
                }
            }
        }else if (lt){
            for(Column c : cols){
                if(c.name != colVal)continue;
                for(int i = 0; i < c.allEntries.size();i++){
                    if(isInt(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            std::stringstream ss;
                            std::stringstream ss2;
                            int colIntVal;
                            int constIntVal;
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            ss2 << constVal;
                            ss2 >> constIntVal;
                            if(colIntVal > constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            std::stringstream ss;
                            int colIntVal;
                            float constFloatVal = std::stof(constVal);
                            ss << c.allEntries.at(i);
                            ss >> colIntVal;
                            if((float) colIntVal > constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }else if(isFloat(c.allEntries.at(i))){
                        if(isInt(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            std::stringstream ss;
                            int constIntVal;
                            ss << constVal;
                            ss >> constIntVal;
                            if(colFloatVal > (float) constIntVal){
                                rows.push_back(i);
                            }
                        }else if(isFloat(constVal)){
                            float colFloatVal = std::stof(c.allEntries.at(i));
                            float constFloatVal = std::stof(constVal);
                            if(colFloatVal > constFloatVal){
                                rows.push_back(i);
                            }
                        }
                    }
                }
            }
        }

    }


    if(header){
        for(const Column& c : cols){
            //printf("%s ", c.name.c_str());
            f << c.name + " ";
        }
        //printf("\n");
        f << "\n";
    }

    for(int r : rows){

        for(Column c : cols){
            //printf("%s ", c.allEntries.at(r).c_str());
            f << c.allEntries.at(r) + " ";
        }
        //printf("\n");
        f << "\n";

    }

    f.close();


}