package com.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;

/**
* @Author songxiao
* @Description //本地驱动打印方法
* @Date  2019/9/11
* @Param
* @return
**/
public class LocalPrint implements Printable,ImageObserver {


    public static void main(String[] Args){
        printWordDayInfo();
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }
        Graphics2D graphics2d = (Graphics2D) graphics;
        //设置字体
        graphics2d.setFont(new Font("宋体", Font.PLAIN,12));
        graphics2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        //打印格式
        graphics2d.drawString("收银员：宋骁", 25, 45);
        graphics2d.drawString("收银员编号：1136", 25, 60);
/*        graphics2d.drawString("------------------------------------------", 15, 75);
        graphics2d.drawString("总单数：100", 25, 90);
        graphics2d.drawString("当班总收入(元)：35698", 25, 105);
        graphics2d.drawString("聚合支付单数：100" , 25, 125);
        graphics2d.drawString("聚合支付收款(元)：15623", 25, 140);
        graphics2d.drawString("微信单数：56" , 25, 160);
        graphics2d.drawString("微信收款(元)：5677", 25, 175);
        graphics2d.drawString("------------------------------------------", 25, 190);*/
        try{
            Image image= ImageIO.read(new File("C:\\Users\\ambit\\Desktop\\20190912162300.png"));
            graphics2d.drawImage(image, 0, 278, 80, 80, Color.LIGHT_GRAY, this);
            //销毁画笔
            graphics2d.dispose();
        }catch(IOException e){
            e.printStackTrace();
        }
/*       graphics2d.drawString("支付宝单数：56", 15, 195);
        graphics2d.drawString("支付宝收款(元)：6754", 15, 210);
        graphics2d.drawString("POS机单数：8", 15, 230);
        graphics2d.drawString("POS机收款(元)：4689" , 15, 245);
        graphics2d.drawString("加油卡单数：70" , 15, 265);
        graphics2d.drawString("加油卡收款(元):4342", 15, 280);
        graphics2d.drawString("------------------------------------------", 15, 295);
                */

        return PAGE_EXISTS;
    }


    public static void printWordDayInfo() {
        try {
            Book book = new Book();
            PageFormat pf = new PageFormat();
            //竖向打印
            pf.setOrientation(PageFormat.PORTRAIT);
            Paper p = new Paper();
            //设置打印纸的大小一般是158，10000随便设的因为这是一卷纸不像A4纸是规定的
            p.setSize(158,10000);
            //打印区域
            p.setImageableArea(0,0, 158,10000);
            pf.setPaper(p);
            book.append(new LocalPrint(), pf);
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPageable(book);
            job.print();
        } catch (Exception e) {
            e.printStackTrace();
            //log.error("打印异常："+e.getMessage());
        }
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        return false;
    }
}
