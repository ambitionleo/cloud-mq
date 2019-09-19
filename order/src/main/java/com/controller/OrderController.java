package com.controller;

import com.alibaba.fastjson.JSONObject;
import com.bean.ActivityIInfoDto;
import com.bean.LoginInfo;
import com.bean.ServerMsgPo;
import com.config.RabbitMqConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*",maxAge = 3600)
@RestController
@RequestMapping("order")
public class OrderController {

    @Resource
    private RestTemplate restTemplate;


    @Autowired
    private RabbitTemplate rabbitTemplate;


    public static void  main(String[] Args){
        List<String> list = new ArrayList<>();
        ArrayList<ServerMsgPo> serverMsgPos = new ArrayList<>();
        serverMsgPos.add(new ServerMsgPo("msg","1"));
        serverMsgPos.add(new ServerMsgPo("msgg","2"));
        serverMsgPos.add(new ServerMsgPo("msgs","3"));
        serverMsgPos.add(new ServerMsgPo("msgs","3"));
        List<String> msg = serverMsgPos.stream()
                .filter(item -> {
                    if(StringUtils.equals(item.getMsg(), "msg")){
                        return true;
                    }else if(StringUtils.equals(item.getMsg(), "msgs")){
                        return true;
                    }else {
                        return false;
                    }
                })
                .map(one -> one.getUserId())
                .distinct()
                .collect(Collectors.toList());
        msg.stream().forEach(System.out::printf);
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("No use lambda.");
            }
        });
        //serverMsgPo = null;
        //boolean present = Optional.ofNullable(serverMsgPo).map(one -> one.getMsg()).isPresent();
        //System.out.printf(present+"");
    }



    @PostMapping("info2")
    public Object getOrder(){
        Object o = restTemplate.postForObject("http://service-user/user/login", null, Object.class);
        return o;
    }

   @PostMapping("send-msg")
    public void sendMsg(){
        String stringObjectHashMap = "test-json";
        String uuId = UUID.randomUUID().toString();
        send(uuId,stringObjectHashMap);
    }

    @PostMapping("fit")
    public JSONObject fit(@RequestBody HashMap<String, Object> paramMap){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        jsonObject.put("msg", "操作成功");
        return jsonObject;
    }

    @PostMapping("info")
    public JSONObject info(@RequestBody ActivityIInfoDto activityIInfoDto){
        String s = JSONObject.toJSONString(activityIInfoDto);
        rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTINGKEY2,
                s, new CorrelationData(UUID.randomUUID().toString()));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        jsonObject.put("msg", "操作成功");
        return jsonObject;
    }


    public void send(String uuid,Object message) {
        String msg = UUID.randomUUID().toString();
        ServerMsgPo serverMsgPo = new ServerMsgPo();
        serverMsgPo.setMsg("消息對象字符");
        serverMsgPo.setUserId("用户id");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        jsonObject.put("data",serverMsgPo );
        CorrelationData correlationId = new CorrelationData(uuid);
        Message build = MessageBuilder.withBody(jsonObject.toString().getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
                .build();
        for(int i=0;i<100;i++){
            System.out.printf(i + "\n");
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE, RabbitMqConfig.ROUTINGKEY2,
                    build, correlationId);
        }

    }

}
