package com.leo.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ExcelUtils {


    public static void main(String[] args) throws IOException {
        ExcelUtils em = new ExcelUtils();
        em.test();
    }

    public void test() throws IOException {
        int a1 = 0;
        int a2 = 0;
        int b = 0;
        int c = 0;
        int d = 0;
        int e = 0;
        int f = 0;
        int g = 0;

        //存储微信官方数据excel的list
        List<ExcelBean> excelBeans = new ArrayList<>();
        StopWatch sw = new StopWatch();
        sw.start("读取基础数据");
        a1 = readBaseData("E:\\22-25-03.xls", excelBeans, a1);
        sw.stop();
        sw.start("读取基础数据第二部分");
        //a2 = readBaseData("E:\\total03-2.xls", excelBeans, a2);
        sw.stop();

        //存储真实数据excel的list
        List<ExcelBean> excelBeansOther = new ArrayList<>();
        sw.start("读取真实数据excel的list");
        //b = readExcel("E:\\8-19.xls", excelBeansOther, b);
        //c = readExcel("E:\\8-20.xls", excelBeansOther, c);
        d = readExcel("E:\\8-22.xls", excelBeansOther, d);
        e = readExcel("E:\\8-23.xls", excelBeansOther, e);
        f = readExcel("E:\\8-24.xls", excelBeansOther, f);
        g = readExcel("E:\\8-25.xls", excelBeansOther, g);
        sw.stop();
        System.out.printf("\na1:" + a1);
        System.out.printf("\na2:" + a2);
        System.out.printf("\na1+a2:" + (a1+a2));
        System.out.printf("\nb:" + b );
        System.out.printf("\nc:" + c );
        System.out.printf("\nd:" + d );
        System.out.printf("\ne:" + e );
        System.out.printf("\nf:" + f );
        System.out.printf("\ng:" + g );
        System.out.printf("\nexcelBeans.size():" + excelBeans.size());
        System.out.printf("\nexcelBeansOther.size():" + excelBeansOther.size() + "\n");

        //excelBeans = excelBeans.stream().filter(distinctByKey(ExcelBean::getId)).collect(Collectors.toList());
        //excelBeansOther = excelBeansOther.stream().filter(distinctByKey(ExcelBean::getId)).collect(Collectors.toList());

        sw.start("比对");
        ArrayList<ExcelBean> result = new ArrayList<>();

        //循环微信官方数据
        loop:for(Iterator<ExcelBean> iterator = excelBeans.iterator(); iterator.hasNext(); ){
            ExcelBean one = iterator.next();
            String id = one.getId();
            String money = one.getMoney();
            //如果金额为0  移除并且跳出去 （当金额为0时，还会有一条orderId相同并且金额不为0的数据）
            if(StringUtils.equals("0",money )){
                iterator.remove();
                continue;
            }
            //循环门店数据
            local:for (ExcelBean two : excelBeansOther) {
                String twoId = two.getId();
                String twoMoney = two.getMoney();
                //如果是退单直接跳过，！！此操作可能会导致 -> 漏单结果集中，存在实际上存在的数据
                if(twoMoney.contains("-")){
                    continue local;
                }
                //如果官方数据 与 门店数据相等
                if (StringUtils.equals(id, twoId)) {
                    //如果对象中的金额不一样，并且金额不相等
                    if ((!(StringUtils.equals(money, twoMoney)))) {
                        ExcelBean bean = new ExcelBean();
                        bean.setId(id);
                        result.add(bean);
                        System.out.printf("Go!");
                    }
                    iterator.remove();
                    continue loop;
                }
            }
        }
        //金额不符合的id
        for (ExcelBean item : result) {
            System.out.printf("\nitem.getId():" + item.getId());
        }
        System.out.printf("\nresult.size():" + result.size());
        //多余的id
        for (ExcelBean im : excelBeans) {
            System.out.printf("\n缺失订单id：" + im.getId());
        }
        System.out.printf("\nexcelBeans.size():" + excelBeans.size());
        sw.stop();

        System.out.println("\n"+sw.prettyPrint());
    }

    /**
    * @Author songxiao
    * @Description //去重
    * @Date  2019/8/28
    * @Param [keyExtractor]
    * @return java.util.function.Predicate<T>
    **/
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        ConcurrentHashMap<Object, Boolean> map = new ConcurrentHashMap<>(16);
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
    * @Author songxiao
    * @Description //读取微信官方数据
    * @Date  2019/8/28
    * @Param [path, excelBeans, flag]
    * @return int
    **/
    public int readBaseData(String path,List<ExcelBean> excelBeans,int flag) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        HSSFWorkbook book = new HSSFWorkbook(fis);
        HSSFSheet sheet = book.getSheetAt(0);
        int rows = 1000000;
        System.out.printf("rows:" + rows);
        for (int i = 1; i < rows; i++) {
            HSSFRow row = sheet.getRow(i);
            if (row == null) {
                break;
            }
            HSSFCell cell = row.getCell(6);
            HSSFCell cell12 = row.getCell(12);
            HSSFCell cell13 = row.getCell(13);

            String id = cell.getStringCellValue();
            System.out.println("商户订单号截取前：" + id);
            id = StringUtils.substring(id, 1, 17);
            System.out.println("商户订单号：" + id);

            String money = cell12.getStringCellValue();
            System.out.println("应结订单金额截取前" + money);
            money = StringUtils.substringBetween(money, "`", ".");
            System.out.println("应结订单金额" + money);

            String tickets = cell13.getStringCellValue();
            tickets = StringUtils.substringBetween(tickets, "`", ".");
            BigDecimal moneybd = new BigDecimal(money);
            BigDecimal ticketsbd = new BigDecimal(tickets);
            //实际消费金额减去代金券
            BigDecimal divide = moneybd.subtract(ticketsbd);
            money = divide.toString();
            System.out.printf("moneyafter:" + money);
            ExcelBean excelBean = new ExcelBean();
            excelBean.setId(id);
            excelBean.setMoney(money);
            //excelBean.setTickets(tickets);
            excelBeans.add(excelBean);
            System.out.printf("i" + i);
            flag = i;
        }
        return flag;
    }

    /**
    * @Author songxiao
    * @Description //读取门店数据
    * @Date  2019/8/28
    * @Param [path, excelBeansOther, flag]
    * @return int
    **/
    public int readExcel(String path, List<ExcelBean> excelBeansOther,int flag) throws IOException {
        File fileOther = new File(path);
        FileInputStream fisOther = new FileInputStream(fileOther);
        HSSFWorkbook bookOther = new HSSFWorkbook(fisOther);
        HSSFSheet sheetOther = bookOther.getSheetAt(0);
        int rowsOther = sheetOther.getLastRowNum();
        for (int j = 1; j < rowsOther; j++) {
            HSSFRow row = sheetOther.getRow(j);
            HSSFCell cell = row.getCell(3);
            HSSFCell cell5 = row.getCell(5);
            HSSFCell cell10 = row.getCell(10);
            String id = cell.getStringCellValue();
            System.out.println("商户订单号：" + id);
            String money = cell5.getStringCellValue();
            System.out.println("应结订单金额截取前" + money);
            money = StringUtils.substringBeforeLast(money, ".");
            System.out.println("应结订单金额" + money);
            String type = cell10.getStringCellValue();
            //if (StringUtils.equals("微信支付", type) || StringUtils.equals("微信平台补贴", type)) {
            if (StringUtils.equals("微信支付", type)) {
                ExcelBean excelBean = new ExcelBean();
                excelBean.setId(id);
                excelBean.setMoney(money);
                excelBeansOther.add(excelBean);
                flag++;
            }
        }
        return flag;
    }
}
