package com.utils;

import android.graphics.Bitmap;
import com.bean.Image;
import com.bean.QrCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.Socket;


/**
 * @Author songxiao
 * @Description //封装Pos机打印工具类
 * @Date 2019/9/11
 * @Param
 * @return
 **/
public class Pos {

    protected final String LEFT = "LEFT";
    protected final String CENTER = "CENTER";
    protected final String RIGHT = "RIGHT";
    public static final byte HT = 0x9;
    public static final byte LF = 0x0A;
    public static final byte CR = 0x0D;
    public static final byte ESC = 0x1B;
    public static final byte DLE = 0x10;
    public static final byte GS = 0x1D;
    public static final byte FS = 0x1C;
    public static final byte STX = 0x02;
    public static final byte US = 0x1F;
    public static final byte CAN = 0x18;
    public static final byte CLR = 0x0C;
    public static final byte EOT = 0x04;
    /* 初始化打印机 */
    public static final byte[] ESC_INIT = new byte[] {ESC, '@'};
    /* 设置标准模式 */
    public static final byte[] ESC_STANDARD = new byte[] {ESC, 'S'};
    /* 设置汉字打印模式 */
    public static final byte[] ESC_CN_FONT = new byte[] {FS, '&'};
    /* 选择字符集 */
    public static final byte[] ESC_SELECT_CHARACTER = new byte[] {ESC, 'R', 9};
    /* 设置用户自定义汉字字体 焗7118 */
    public static final byte[] ESC_FS_2 = new byte[] {FS, 0x32, 0x71, 0x18};
    /* 取消用户自定义字体 */
    public static final byte[] ESC_CANCEL_DEFINE_FONT = new byte[]{ESC, '%', 0};
    /* 打开钱箱指令 */
    public static final byte[] ESC_OPEN_DRAWER = new byte[]{ESC, 'p', 0x00, 0x10, (byte) 0xff};
    /* 切纸指令GS V m
     * m  0,48 Executes a full cut(cuts the paper completely)
     *    1,49 Excutes a partilal cut(one point left uncut)
     */
    public static final byte[] POS_CUT_MODE_FULL = new byte[]{GS, 'V', 0x00};
    //public static final byte[] POS_CUT_MODE_PARTIAL = new byte[]{GS, 'V', 0x01};
    /* 西文字符 （半宽）字体A (6 ×12)，汉字字符 （全宽）字体A （12×12） */
    public static final byte[] ESC_FONT_A = new byte[]{ESC, '!', 0};
    /* 西文字符 （半宽）字体B (8×16)，汉字字符 （全宽）字体B （16×16） */
    public static final byte[] ESC_FONT_B = new byte[]{ESC, '!', 1};
    /* 12*24   0/48*/
    public static final byte[] ESC_FONTA= new byte[]{ESC, 'M', 48};
    /* 9*17    1/49*/
    public static final byte[] ESC_FONTB= new byte[]{ESC, 'M', 1};
    /* 默认颜色字体指令 */
    public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[] {ESC, 'r', 0x00};
    /* 红色字体指令 */
    public static final byte[] ESC_FONT_COLOR_RED = new byte[] {ESC, 'r', 0x01 };
    /* 标准大小 */
    public static final byte[] FS_FONT_ALIGN = new byte[]{FS, 0x21, 1, ESC, 0x21, 1};
    /* 横向放大一倍 */
    public static final byte[] FS_FONT_ALIGN_DOUBLE = new byte[]{FS, 0x21, 4, ESC, 0x21, 4};
    /* 纵向放大一倍 */
    public static final byte[] FS_FONT_VERTICAL_DOUBLE = new byte[]{FS, 0x21, 8, ESC, 0x21, 8, GS, '!', 0x01};
    /* 横向纵向都放大一倍 */
    public static final byte[] FS_FONT_DOUBLE = new byte[]{FS, 0x21, 12, ESC, 0x21, 48};
    /* 靠左打印命令 */
    public static final byte[] ESC_ALIGN_LEFT = new byte[]{0x1b,'a', 0x00};
    /* 居中打印命令 */
    public static final byte[] ESC_ALIGN_CENTER = new byte[]{0x1b,'a', 0x01};
    /* 靠右打印命令 */
    public static final byte[] ESC_ALIGN_RIGHT = new byte[]{0x1b,'a', 0x02};
    /* 字体加粗 */
    public static final byte[] ESC_SETTING_BOLD = new byte[]{ESC, 0x45, 1};
    /* 取消字体加粗 */
    public static final byte[] ESC_CANCEL_BOLD = new byte[]{ESC, 0x45, 0};

    //定义编码方式
    private String encoding = null;

    private Socket sock = null;
    // 通过socket流进行读写
    private OutputStream socketOut = null;

    private OutputStreamWriter writer = null;

    BufferedImage bi = null;
    /**
     * 初始化Pos实例
     *
     * @param ip       打印机IP
     * @param port     打印机端口号
     * @param encoding 编码
     * @throws IOException
     */
    public Pos(String ip, int port, String encoding) throws IOException {
        sock = new Socket(ip, port);
        socketOut = new DataOutputStream(sock.getOutputStream());
        this.encoding = encoding;
        writer = new OutputStreamWriter(socketOut, encoding);
    }

    public Pos() {

    }


    public OutputStream getSocketOut(){
        return socketOut;
    }

    public OutputStreamWriter getWriter(){
        return writer;
    }

    /**
     * 关闭IO流和Socket
     *
     * @throws IOException
     */
    public void closeIOAndSocket() throws IOException {
        writer.close();
        socketOut.close();
        sock.close();
    }



    /**
     * 打印换行
     *
     * @return length 需要打印的空行数
     * @throws IOException
     */
    public void printLine(int lineNum) throws IOException {
        for (int i = 0; i < lineNum; i++) {
            writer.write("\n");
        }
        writer.flush();
    }

    /**
     * 打印换行(只换一行)
     *
     * @throws IOException
     */
    public void printLine() throws IOException {
        writer.write("\n");
        writer.flush();
    }

    /**
     * 打印空白(一个Tab的位置，约4个汉字)
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printTabSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("\t");
        }
        writer.flush();
    }

    /**
     * 打印空白（一个汉字的位置）
     *
     * @param length 需要打印空白的长度,
     * @throws IOException
     */
    public void printWordSpace(int length) throws IOException {
        for (int i = 0; i < length; i++) {
            writer.write("  ");
        }
        writer.flush();
    }

    /**
     * 打印位置调整
     *
     * @param position 打印位置  0：居左(默认) 1：居中 2：居右
     * @throws IOException
     */
    public void printLocation(int position) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        writer.write(position);
        writer.flush();
    }

    /**
     * 绝对打印位置
     *
     * @throws IOException
     */
    public void printLocation(int light, int weight) throws IOException {
        writer.write(0x1B);
        writer.write(0x24);
        writer.write(light);
        writer.write(weight);
        writer.flush();
    }

    /**
     * 打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printText(String text) throws IOException {
        String str = text;
        byte[] content = str.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }

    public Pos feedAndCutAll() throws IOException {
        //换行
        writer.write("\n");
        //切纸
        writer.write(0x1D);
        writer.write(86);
        writer.write(65);
        writer.write(0);
        writer.flush();
        return this;
    }

    /**
     * 新起一行，打印文字
     *
     * @param text
     * @throws IOException
     */
    public void printTextNewLine(String text) throws IOException {
        //换行
        writer.write("\n");
        writer.flush();
        String s = text;
        byte[] content = s.getBytes("gbk");
        socketOut.write(content);
        socketOut.flush();
    }


    public Pos tmCodePrint(String tmCode) throws IOException {
        writer.write(0x1D);
        writer.write(107);
        writer.write(67);
        writer.write(tmCode.length());
        writer.write(tmCode);
        writer.flush();
        return this;
    }


    /**
     * 初始化打印机
     *
     * @throws IOException
     */
    public void initPos() throws IOException {
        writer.write(0x1B);
        writer.write(0x40);
        writer.flush();
    }

    /**
     * 加粗
     *
     * @param flag false为不加粗
     * @return
     * @throws IOException
     */
    public void bold(boolean flag) throws IOException {
        if (flag) {
            //常规粗细
            writer.write(0x1B);
            writer.write(69);
            writer.write(0xF);
            writer.flush();
        } else {
            //加粗
            writer.write(0x1B);
            writer.write(69);
            writer.write(0);
            writer.flush();
        }
    }



    /**
     * 加宽
     *
     * @throws IOException
     */
    public void fsFontAlignDouble() throws IOException {
        //FS, 0x21, 4, ESC, 0x21, 4
        socketOut.write(FS_FONT_ALIGN_DOUBLE);
        socketOut.flush();
    }


    public void qrCode(int position, String qrData) throws IOException {
        int moduleSize = 0;
        int length = qrData.getBytes(encoding).length;
        int l = (int) (Math.ceil(1.5*length) * 8);
        if (l<200) {
            moduleSize = 1;
        } else if (l<429) {
            moduleSize = 2;
        } else if (l<641) {
            moduleSize = 3;
        } else if (l<885) {
            moduleSize = 4;
        } else if (l<1161) {
            moduleSize = 5;
        } else if (l<1469) {
            moduleSize = 6;
        }

        alignQr(position, moduleSize);

        writer.write(0x1D);// init
        writer.write("(k");// adjust height of barcode
        writer.write(length + 3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(80); // fn
        writer.write(48); //
        writer.write(qrData);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(69);
        writer.write(48);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3);
        writer.write(0);
        writer.write(49);
        writer.write(67);
        writer.write(moduleSize);

        writer.write(0x1D);
        writer.write("(k");
        writer.write(3); // pl
        writer.write(0); // ph
        writer.write(49); // cn
        writer.write(81); // fn
        writer.write(48); // m

        writer.flush();

    }


    /**
    * @Author songxiao
    * @Description // 0居左  1居中  2居右
    * @Date  2019/9/12
    * @Param [position, moduleSize]
    * @return void
    **/
    private void alignQr(int position, int moduleSize) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        if (position == 1) {
            writer.write(1);
            centerQr(moduleSize);
        } else if (position == 2){
            writer.write(2);
            rightQr(moduleSize);
        } else {
            writer.write(0);
        }
    }


    private void rightQr(int moduleSize) throws IOException {
        switch (moduleSize) {
            case 1 :
                printSpace(14);
                break;
            case 2 :
                printSpace(17);
                break;
            case 3 :
                printSpace(20);
                break;
            case 4 :
                printSpace(23);
                break;
            case 5 :
                printSpace(26);
                break;
            case 6 :
                printSpace(28);
                break;
            default:
                break;
        }
    }



    private void centerQr(int moduleSize) throws IOException {
        switch (moduleSize) {
            case 1 :{
                printSpace(16);
                break;
            }
            case 2 : {
                printSpace(18);
                break;
            }
            case 3 :{
                printSpace(20);
                break;
            }
            case 4 : {
                printSpace(22);
                break;
            }
            case 5 : {
                printSpace(24);
                break;
            }
            case 6 : {
                printSpace(26);
                break;
            }
            default:
                break;
        }
    }


    /**
     * 打印空白
     *
     * @param length  需要打印空白的长度
     * @throws IOException
     **/
    private void printSpace(int length) throws IOException {
        for (int i=0; i<length; i++) {
            writer.write(" ");
        }
        writer.flush();
    }


    /**
    * @Author songxiao
    * @Description //二维码
    * @Date  2019/9/12
    * @Param [qrCode]
    * @return void
    **/
    public void printQrCode(QrCode qrCode) throws IOException {
        qrCode(qrCode.getFormat(), qrCode.getText());
    }


    /**
     * 打印条形码
     *
     * @param value
     * @return
     * @throws IOException
     */
    public void barCode(String value) throws IOException {
        writer.write(0x1D);
        writer.write(107);
        writer.write(67);
        writer.write(value.length());
        writer.write(value);
        writer.flush();
    }


    /**
     * 打印图片
     *
     * @param image  图片内容
     * @throws IOException
     */
    public void printImage(Image image) throws IOException {
        align(image.getFormat())
                .image(image.getPath())
                .line(image.getLine());
        initPos();
    }

    /**
     * 排版
     *
     * @param position 0：居左(默认) 1：居中 2：居右
     * @return
     * @throws IOException
     */
    public Pos align(int position) throws IOException {
        writer.write(0x1B);
        writer.write(97);
        writer.write(position);
        return this;
    }


    private Pos image(String path) throws IOException {
        // trans to byte array
        //Bitmap bmp  = BitmapFactory.decodeFile(path);
        File file = new File(path);
        bi = ImageIO.read(file);
        byte[] data = new byte[] { 0x1B, 0x33, 0x00 };
        write(data);
        data[0] = (byte)0x00;
        data[1] = (byte)0x00;
        data[2] = (byte)0x00;    //重置参数

        int pixelColor;

        // ESC * m nL nH 点阵图
        byte[] escBmp = new byte[] { 0x1B, 0x2A, 0x00, 0x00, 0x00 };

        escBmp[2] = (byte)0x21;

        //nL, nH
        escBmp[3] = (byte)(bi.getWidth() % 256);
        escBmp[4] = (byte)(bi.getWidth() / 256);

        System.out.printf("bi.getWidth():90" + bi.getWidth());
        System.out.printf("bi.getHeight():110" + bi.getHeight());
        // 每行进行打印
        for (int i = 0; i < bi.getHeight()  / 24 + 1; i++){
            write(escBmp);
            for (int j = 0; j < bi.getWidth(); j++){
                for (int k = 0; k < 24; k++){
                    if (((i * 24) + k) < bi.getHeight()){
                        pixelColor = (bi.getRGB(j, (i * 24) + k)) + 16777216 ;
                        System.out.printf("pixelColor:" + pixelColor);
                        if (pixelColor != -1){
                            //10000000右移动 2^8 -> 2^7 ->...2^N
                            data[k / 8] += (byte)(128 >> (k % 8));

                        }
                    }
                }
                write(data);
                // 重置参数
                data[0] = (byte)0x00;
                data[1] = (byte)0x00;
                data[2] = (byte)0x00;
            }
            //换行
            byte[] byte_send1 = new byte[2];
            byte_send1[0] = 0x0d;
            byte_send1[1] = 0x0a;
            write(byte_send1);
        }
        return this;
    }



    private void write(byte ...data) throws IOException {
        socketOut.write(data);
        socketOut.flush();
    }


    /**
     * 换行
     *
     * @param lineNum 换行数，0为不换行
     * @return
     * @throws IOException
     */
    private Pos line(int lineNum) throws IOException {
        for (int i=0; i<lineNum; i++) {
            writer.write("\n");
            writer.flush();
        }
        return this;
    }


    /*************************************************************************
     * 假设一个240*240的图片，分辨率设为24, 共分10行打印
     * 每一行,是一个 240*24 的点阵, 每一列有24个点,存储在3个byte里面。
     * 每个byte存储8个像素点信息。因为只有黑白两色，所以对应为1的位是黑色，对应为0的位是白色
     **************************************************************************/
    /**
     * 把一张Bitmap图片转化为打印机可以打印的字节流
     *
     * @param bmp
     * @return
     */
    public byte[] draw2PxPoint(BufferedImage bmp) {
        //用来存储转换后的 bitmap 数据。为什么要再加1000，这是为了应对当图片高度无法
        //整除24时的情况。比如bitmap 分辨率为 240 * 250，占用 7500 byte，
        //但是实际上要存储11行数据，每一行需要 24 * 240 / 8 =720byte 的空间。再加上一些指令存储的开销，
        //所以多申请 1000byte 的空间是稳妥的，不然运行时会抛出数组访问越界的异常。
        int size = bmp.getWidth() * bmp.getHeight() / 8 + 1000;
        byte[] data = new byte[size];
        int k = 0;
        //设置行距为0的指令
        data[k++] = 0x1B;
        data[k++] = 0x33;
        data[k++] = 0x00;
        // 逐行打印
        for (int j = 0; j < bmp.getHeight() / 24f; j++) {
            //打印图片的指令
            data[k++] = 0x1B;
            data[k++] = 0x2A;
            data[k++] = 33;
            data[k++] = (byte) (bmp.getWidth() % 256); //nL
            data[k++] = (byte) (bmp.getWidth() / 256); //nH
            //对于每一行，逐列打印
            for (int i = 0; i < bmp.getWidth(); i++) {
                //每一列24个像素点，分为3个字节存储
                for (int m = 0; m < 3; m++) {
                    //每个字节表示8个像素点，0表示白色，1表示黑色
                    for (int n = 0; n < 8; n++) {
                        byte b = 1;
                        //byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
                        data[k] += data[k] + b;
                    }
                    k++;
                }
            }
            data[k++] = 10;//换行
        }
        return data;
    }



    /**
     * 灰度图片黑白化，黑色是1，白色是0
     *
     * @param x   横坐标
     * @param y   纵坐标
     * @param img 位图
     * @return
     */
    public static byte px2Byte(int x, int y, BufferedImage img) {
        if (x < img.getWidth() && y < img.getHeight()) {
            byte b;
            int pixel = img.getRGB(x, y) + 16777216;
            int red = (pixel & 0x00ff0000) >> 16; // 取高两位
            int green = (pixel & 0x0000ff00) >> 8; // 取中两位
            int blue = pixel & 0x000000ff; // 取低两位
            int gray = RGB2Gray(red, green, blue);
            if (gray < 128) {
                b = 1;
            } else {
                b = 0;
            }
            return b;
        }
        return 0;
    }


    /**
     * 图片灰度的转化
     */
    private static int RGB2Gray(int r, int g, int b) {
        //灰度转化公式
        int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);
        return gray;
    }


    /**解析图片 获取打印数据**/
    public byte[] getReadBitMapBytes(BufferedImage bitmap) {
        /***图片添加水印**/
        //bitmap = createBitmap(bitmap);
        byte[] bytes = null;  //打印数据
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        System.out.println("width=" + width + ", height=" + height);
        int heightbyte = (height - 1) / 8 + 1;
        int bufsize = width * heightbyte;
        int m1, n1;
        byte[] maparray = new byte[bufsize];

        byte[] rgb = new byte[3];

        int []pixels = new int[width * height]; //通过位图的大小创建像素点数组

        //bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        /**解析图片 获取位图数据**/
        for (int j = 0;j < height; j++) {
            for (int i = 0; i < width; i++) {
                int pixel = bitmap.getRGB(i,j ) + 16777216;

                int r = (pixel & 0x00ff0000) >> 16; // 取高两位
                int g = (pixel & 0x0000ff00) >> 8; // 取中两位
                int b = pixel & 0x000000ff; // 取低两位


                /*int pixel = pixels[width * j + i]; *//**获取ＲＧＢ值**//*
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);*/
                System.out.println("i=" + i + ",j=" + j + ":(" + r + ","+ g+ "," + b + ")");
                rgb[0] = (byte)r;
                rgb[1] = (byte)g;
                rgb[2] = (byte)b;
                if (r != 255 || g != 255 || b != 255){//如果不是空白的话用黑色填充
                    m1 = (j / 8) * width + i;
                    n1 = j - (j / 8) * 8;
                    maparray[m1] |= (byte)(1 << 7 - ((byte)n1));
                }
            }
        }
        byte[] b = new byte[322];
        int line = 0;
        int j = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        /**对位图数据进行处理**/
        for (int i = 0; i < maparray.length; i++) {
            b[j] = maparray[i];
            j++;
            if (j == 322) {  /**  322图片的宽 **/
                if (line < ((322 - 1) / 8)) {
                    byte[] lineByte = new byte[329];
                    byte nL = (byte) 322;
                    byte nH = (byte) (322 >> 8);
                    int index = 5;
                    /**添加打印图片前导字符**/
                    lineByte[0] = 0x1B;
                    lineByte[1] = 0x2A;
                    lineByte[2] = 1;
                    lineByte[3] = nL;
                    lineByte[4] = nH;
                    /**copy 数组数据**/
                    System.arraycopy(b, 0, lineByte, index, b.length);

                    lineByte[lineByte.length - 2] = 0x0D;
                    lineByte[lineByte.length - 1] = 0x0A;
                    baos.write(lineByte, 0, lineByte.length);
                    try {
                        baos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    line++;
                }
                j = 0;
            }
        }
        bytes = baos.toByteArray();
        return bytes;
    }
}
