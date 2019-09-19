package com.leo.controller;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class testController {

    @PostMapping("/login")
    public Object login(){
        //BeanFactory
        //int i = 1/0;
        return "success";
    }

}
