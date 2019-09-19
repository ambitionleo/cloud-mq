package com.leo.controller;

public class Parent1 {

    int i = 1;
    Parent1(){
        System.out.printf(i+"");
        int x = getValue();
    }

    {i=2;}

    protected int getValue(){
        return i;
    }

}
