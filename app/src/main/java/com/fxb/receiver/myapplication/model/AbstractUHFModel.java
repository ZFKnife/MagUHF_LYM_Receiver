package com.fxb.receiver.myapplication.model;

import android.util.Log;

import com.fxb.receiver.myapplication.application.App;
import com.fxb.receiver.myapplication.util.DevBeep;
import com.olc.uhf.tech.ISO1800_6C;


/**
 * zf 2033152950
 * Created by zf on 2017/8/4 0004.
 */

public abstract class AbstractUHFModel {

    protected final static String TAG = AbstractUHFModel.class.getSimpleName();
    /**
     * 默认密码
     */
    private final static String pass = "00000000";
    /**
     * 协议
     */
    protected ISO1800_6C uhf_6c;

    protected byte[] byteEpc = null;
    /**
     * 操作区域 默认user区
     */
    protected byte MemBank = 0x03;

    public final static byte RFU = 0x00;

    public final static byte EPC = 0x01;

    public final static byte TID = 0x02;

    public final static byte USER = 0x03;
    /**
     * 默认密码 转byte【】
     */
    protected byte[] mimaStr;
    /**
     * 读取起始位置 word类型
     */
    protected int nSA = 2;
    /**
     * 读取位数 word类型
     */
    protected int nDL = 12;

    /**
     * 初始化
     */
    public AbstractUHFModel() {
        uhf_6c = App.mService.getISO1800_6C();
        mimaStr = stringToBytes(pass);
        DevBeep.init(App.mContext);
    }

    /**
     * 获取EPC标签
     *
     * @return EPC
     */
    public abstract String inventory();

    /**
     *
     */
    public abstract void ReadDate(IResponse iResponse);

    /**
     * @param date
     */
    public abstract void WriteDate(byte[] date, IResponse iResponse);

    /**
     * 转化字节数组
     *
     * @param hexString
     * @return
     */
    public byte[] stringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public String BytesToString(byte[] b, int nS, int ncount) {
        String ret = "";
        int nMax = ncount > (b.length - nS) ? b.length - nS : ncount;
        for (int i = 0; i < nMax; i++) {
            String hex = Integer.toHexString(b[i + nS] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 选择存储区
     *
     * @param memBank
     */
    public void setMemBank(byte memBank) {
        MemBank = memBank;
    }

    /**
     * 自定义密码
     *
     * @param mimaStr
     */
    public void setMimaStr(String mimaStr) {
        if (mimaStr == null) {
            throw new NullPointerException("mimaStr is null");
        }
        if (mimaStr.length() != 8) {
            Log.e(TAG, "mimaStr is eight length");
            return;
        }
        this.mimaStr = stringToBytes(mimaStr);
    }

    /**
     * 自定义读取起始位置
     *
     * @param nSA
     */
    public void setnSA(int nSA) {
        this.nSA = nSA;
    }

    /**
     * 自定义读取位数
     *
     * @param nDL
     */
    public void setnDL(int nDL) {
        this.nDL = nDL;
    }

    /**
     * 根据错误码显示详细错误信息
     */
    public String getErrorDescription(int code) {
        return uhf_6c.getErrorDescription(code);
    }


    public void clear() {
        byteEpc = null;
    }
}
