package controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("user")
public class LoginContrller {

    @Value("${server.port}")
    private String port;


    @PostMapping("/login")
    public Object login(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("code", "200");
        hashMap.put("msg", "登陆成功");
        hashMap.put("port", port);
        return hashMap;
    }

}
