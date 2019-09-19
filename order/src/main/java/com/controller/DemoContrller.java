package com.controller;

import com.bean.Image;
import com.bean.QrCode;
import com.utils.Pos;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class DemoContrller implements  Runnable{

    //打印机IP地址
    private final String IP = "192.168.4.220";
    //打印机端口
    private final int PORT = 9100;
    //字符集格式
    private final String ENCODING = "GBK";

    private String dateType = "yyyy-MM-dd HH:mm:ss";

    private static int count = 100;

    public static void main(String[] Args) throws IOException {
        /*DemoContrller demoContrller = new DemoContrller();
        DemoContrller demoContrller2 = new DemoContrller();
        DemoContrller demoContrller3 = new DemoContrller();
        DemoContrller demoContrller4 = new DemoContrller();
        Thread thread = new Thread(demoContrller);
        Thread thread2 = new Thread(demoContrller2);
        Thread thread3 = new Thread(demoContrller3);
        Thread thread4 = new Thread(demoContrller4);
        thread.start();
        System.out.printf("毫秒数1："+System.currentTimeMillis() + "\n");
        thread2.start();
        System.out.printf("毫秒数2："+System.currentTimeMillis() + "\n");
        thread3.start();
        System.out.printf("毫秒数3："+System.currentTimeMillis() + "\n");
        thread4.start();
        System.out.printf("毫秒数4："+System.currentTimeMillis() + "\n");*/

        //DemoContrller demoContrller = new DemoContrller();
        //demoContrller.printOut();

    }




    public void printOut() throws IOException {
        /*SimpleDateFormat formatter = new SimpleDateFormat(dateType);
        Date date = new Date();
        String strDate = formatter.format(date);
        //新建POS连接
        Pos pos = new Pos(IP,PORT,ENCODING);
        //初始化
        pos.initPos();
        //打印居左
        pos.printLocation(0);
        //加粗
        pos.bold(true);
        //加宽
        pos.fsFontAlignDouble();
        pos.printText("打印时间:" + strDate);
        //取消加粗
        pos.bold(false);
        pos.printTextNewLine("操作人:" + "宋骁");
        pos.printLine(2);
        Pos pos = new Pos(IP,PORT,ENCODING);
        Image image = new Image();
        image.setFormat(0);
        image.setLine(1);
        image.setPath("C:\\Users\\ambit\\Desktop\\20190912162300.png");
        pos.printImage(image);

        pos.printLine(2);
        QrCode qrCode = new QrCode();
        qrCode.setFormat(1);
        qrCode.setText("https://www.baidu.com");
        pos.printQrCode(qrCode);

        OutputStreamWriter writer = pos.getWriter();
        writer.write("NO:  " + "197817652" + " \r\n");
        writer.write("---------------------------------\r\n");
        writer.write("地址: " + "上海市浦东新区xxxx" + "\r\n");
        writer.write("联系电话: " + "18201922999" + " \r\n");
        writer.write("---------------------------------\r\n");
        writer.flush();
        pos.feedAndCutAll();
        //关闭POS连接
        pos.closeIOAndSocket();*/


/*
        Pos pos = new Pos(IP,PORT,ENCODING);
        File file = new File("C:\\Users\\ambit\\Desktop\\20190912162300.png");
        BufferedImage bi = ImageIO.read(file);
        byte[] bytes = pos.draw2PxPoint(bi);
        OutputStream socketOut = pos.getSocketOut();
        socketOut.write(bytes);
        pos.feedAndCutAll();
        //关闭POS连接
        pos.closeIOAndSocket();
*/
        /*Pos pos = new Pos(IP,PORT,ENCODING);
        File file = new File("C:\\Users\\ambit\\Desktop\\20190912162299.png");
        BufferedImage bi = ImageIO.read(file);

        byte[] bytes = pos.getReadBitMapBytes(bi);
        byte[] start = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1B, 0x40, 0x1B, 0x33, 0x00 };
        byte[] end = { 0x1d, 0x4c, 0x1f, 0x00 };

        for(byte one : bytes){
            System.out.printf("one:" + one + "|");
        }
        OutputStream socketOut = pos.getSocketOut();

        // 发送打印图片前导指令
        socketOut.write(start);
        socketOut.write(bytes);
        // 发送结束指令
        socketOut.write(end);

        pos.feedAndCutAll();
        pos.closeIOAndSocket();*/
    }


    @Override
    public void run() {
       /* while (count !=0){
            count--;
            System.out.printf(count+ "\n");
        }*/
        DemoContrller demoContrller = new DemoContrller();
        try {
            demoContrller.printOut();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
