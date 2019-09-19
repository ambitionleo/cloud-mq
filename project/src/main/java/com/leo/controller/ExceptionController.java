package com.leo.controller;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Object result(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("code", "500");
        hashMap.put("msg", "系统错误");
        return hashMap;
    }


}
