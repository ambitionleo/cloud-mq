package com.leo.controller;

public class Son1 extends Parent1 {

    int j = 1;
    Son1(){
        j=2;
    }
    {j=3;}

    @Override
    protected int getValue(){return i;}

}
