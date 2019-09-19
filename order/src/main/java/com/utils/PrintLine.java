package com.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @    POS小票打印机  网口无驱打印
 *
 *调用方法  new PrintLine().print(list,map,string);
 * @author Btboy1978 QQ: 138027869
 *
 */
public class PrintLine {
    static String PRINT_IP = "192.168.4.220";
    static int PRINT_PORT = 9100;
    public static final byte GS = 0x1D;
    public static final byte[] POS_CUT_MODE_PARTIAL = new byte[]{GS, 'V', 0x01};
    Socket socket = null;
    OutputStream socketOut = null;
    OutputStreamWriter writer = null;
    String[] Colum_Name = new String[] { "ID", "菜名", "份数", "小计金额" };// 设定小票列标题
    /**
     * @throws IOException
     *
     */
    public PrintLine() throws IOException {
        // TODO Auto-generated constructor stub
        // 建立打印机连接
        //
        socket = new Socket(PRINT_IP, PRINT_PORT);
        socketOut = socket.getOutputStream();
        writer = new OutputStreamWriter(socketOut, "GBK");
    }

    public static void main(String[] args) throws IOException {
        //小票页面 公司信息
        Map<String,String> GS_INFO=new HashMap<String,String>();
        GS_INFO.put("GS_Name", "盘古餐饮");
        GS_INFO.put("GS_Address", "上海市浦东新区xxxxxxx");
        GS_INFO.put("GS_Tel", "13507115045");
        GS_INFO.put("GS_Qq", "138027869");

        //菜品信息
        List<Map<String, Object>> PRINT_DATA = new ArrayList<Map<String, Object>>();
        Map<String,Object> data1 = new HashMap<String, Object>();
        data1.put("cai_name","小鸡炖蘑菇");
        data1.put("cai_price","36");
        PRINT_DATA.add(data1);


        PrintLine printLine = new PrintLine();

        printLine.print(PRINT_DATA,GS_INFO,"123456789");
    }

    /**
     * @param PRINT_DATA
     *            小票主要数据
     * @param GS_INFO
     *            小票附带信息
     * @param CAIDAN_SN
     *            小票号码
     */
    public void print(List<Map<String, Object>> PRINT_DATA,Map<String, String> GS_INFO, String CAIDAN_SN) {

        try {
            // 条码打印指令
            byte[] PRINT_CODE = new byte[9];
            PRINT_CODE[0] = 0x1d;
            PRINT_CODE[1] = 0x68;
            PRINT_CODE[2] = 120;
            PRINT_CODE[3] = 0x1d;
            PRINT_CODE[4] = 0x48;
            PRINT_CODE[5] = 0x10;
            PRINT_CODE[6] = 0x1d;
            PRINT_CODE[7] = 0x6B;
            PRINT_CODE[8] = 0x02;
            // 清除字体放大指令
            byte[] FD_FONT = new byte[3];
            FD_FONT[0] = 0x1c;
            FD_FONT[1] = 0x21;
            FD_FONT[2] = 4;
            // 字体加粗指令
            byte[] FONT_B = new byte[3];
            FONT_B[0] = 27;
            FONT_B[1] = 33;
            FONT_B[2] = 8;
            // 字体纵向放大一倍
            byte[] CLEAR_FONT = new byte[3];
            CLEAR_FONT[0] = 0x1c;
            CLEAR_FONT[1] = 0x21;
            CLEAR_FONT[2] = 0;
            // 计算合计金额
            int price = 0;
            socketOut.write(27);
            socketOut.write(64);
            socketOut.write(FD_FONT);// 字体放大
            socketOut.write(FONT_B);// 字体加粗
            socketOut.write(10);
            writer.write("  " + GS_INFO.get("GS_Name") + " \r\n");
            writer.flush();// 关键,很重要,不然指令一次性输出,后面指令覆盖前面指令,导致取消放大指令无效
            socketOut.write(CLEAR_FONT);
            socketOut.write(10);
            writer.write("NO:  " + CAIDAN_SN + " \r\n");
            writer.write("---------------------------------\r\n");
            writer.write("地址: " + GS_INFO.get("GS_Address") + "\r\n");
            writer.write("联系电话: " + GS_INFO.get("GS_Tel") + " \r\n");
            writer.write("---------------------------------\r\n");
            writer.write(Fix_String_Lenth(1,Colum_Name[0], 6)
                    + Fix_String_Lenth(0,Colum_Name[1], 14)
                    + Fix_String_Lenth(1,Colum_Name[2], 4)
                    + Fix_String_Lenth(1,Colum_Name[3], 6) + "\r\n");
            for (int i = 0; i < PRINT_DATA.size(); i++) {
                writer.write(Fix_String_Lenth(1,i + 1 + "", 6)
                        + Fix_String_Lenth(0,PRINT_DATA.get(i).get("cai_name")
                        .toString(), 14)
                        + Fix_String_Lenth(1,1 + "", 4)
                        + Fix_String_Lenth(1,PRINT_DATA.get(i).get("cai_price")
                        .toString(), 6) + "\r\n");
                price += Integer.parseInt(PRINT_DATA.get(i).get("cai_price")
                        .toString());
            }
            writer.write("---------------------------------\r\n");
            writer.write("本单共  " + PRINT_DATA.size() + " 道菜品,合计费用:  " + price
                    + "元\r\n");
            writer.write("---------------------------------\r\n");
            writer.write("    欢 迎 品 偿       谢 谢 惠 顾\r\n");
            // 下面指令为打印完成后自动走纸
            writer.write(27);
            writer.write(100);
            writer.write(4);
            writer.write(10);
            socketOut.write(Pos.POS_CUT_MODE_FULL);


            writer.close();
            socketOut.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 字符串长度补齐,方便打印时对齐,美化打印页面,不过中文计算好像有点问题
     *
     * @param strs
     *            源字符
     * @param len
     *            指定字符长度
     * @return
     * @throws UnsupportedEncodingException
     */
    public String Fix_String_Lenth(int type ,String strs, int len) {
        String strtemp = strs;
        int len1 = strs.length();
        switch (type) {
            case 0:
                while (strtemp.length() < len) {
                    strtemp += " ";}
                break;
            case 1:
                while (strtemp.length() < len) {
                    strtemp += " ";}
                break;
            default:

                break;
        }
        return strtemp;
    }
}
