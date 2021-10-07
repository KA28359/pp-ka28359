#include <stdio.h>

void print_something(char *str){
    if(*str == "\0"){
        return;
    }else{
        print_something(str+1);
        printf("%c\n",*str);
    }
}

void append(int *a1, int len1, int *a2, int len2){
    if(len2 == 0){
        return;
    }else{
        *(a1+len1) = *a2;
        append(a1,len1+1,a2+1,len2-1);
    }
}

void merge(int*a1, int len1, int *a2, int len2, int *a3, int len){

    if(len == len1+len2){
        return;
    }else{
        if(len1 != 0 && len2 != 0){
            if(*a1 < *a2){
                a3[len] = *a1;
                merge(a1+1,len1-1,a2,len2,a3,len+1);
            }else{
                a3[len] = *a2;
                merge(a1,len1,a2+1,len2-1,a3,len+1);
            }
        }else if(len1 != 0){
            a3[len] = *a1;
            merge(a1+1,len1-1,a2,len2,a3,len+1);
        }else if(len2 != 0){
            a3[len] = *a2;
            merge(a1,len1,a2+1,len2-1,a3,len+1);
        }
    }

}

int main() {
    //print_something("something");
    int a1[100] = {1,5,22};
    int  a2[] = {2,17};
    int a3[100]={};
    //append(a1,3,a2,2);
    merge(a1,3,a2,2,a3,0);
    printf("HI");

}
