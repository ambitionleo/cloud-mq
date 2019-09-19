package com.bean;

import java.io.Serializable;

/**
* @Author songxiao
* @Description //TODO
* @Date  2019/9/5
* @Param
* @return
**/
public class ServerMsgPo implements Serializable {

    private String msg;
    private String userId;

    public ServerMsgPo(){

    }

    public ServerMsgPo(String msg,String userId){
        this.msg = msg;
        this.userId = userId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
