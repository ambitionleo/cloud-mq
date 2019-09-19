package com.rabbitmq;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Component
public class FirstConsumer {


   @Resource
    private RestTemplate restTemplate;

    @RabbitListener(queues = {"first-queue","second-queue"}, containerFactory = "singleListenerContainer")
    public void handleMessage(Channel channel, Message message) throws Exception {
        String str = new String(message.getBody());
        System.out.printf("str:" + str);
        JSONObject parse = JSON.parseObject(str);
        //jsonObject.p
        //手动消费
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, false);
        System.out.println("myQueue1:" + new String(message.getBody()));
        Thread.sleep(1000);
        String s = new String(message.getBody());
        //String clusterId = message.getMessageProperties().getClusterId();
        //System.out.println("FirstConsumer {} handleMessage :"+message);
    }


}
