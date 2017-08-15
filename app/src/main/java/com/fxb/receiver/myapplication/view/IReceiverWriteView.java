package com.fxb.receiver.myapplication.view;

import android.graphics.Bitmap;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public interface IReceiverWriteView extends IUHFViewBase {
    /**
     * 获取皮重
     *
     * @return
     */
    public String getShipperPI();

    /**
     * 获取毛重
     *
     * @return
     */
    public String getShipperMao();

    /**
     * 显示车牌号
     *
     * @param str
     */
    public void setCarNumText(String str);

    /**
     * 显示净重
     *
     * @param str
     */
    public void setShipperJingText(String str);

    /**
     * 获取公司名称 本地存储
     *
     * @return
     */
    public String getLocalhostName();

    /**
     * 结束activity
     */
    public void finash();

    /**
     * 显示反馈信息
     */
    public void setResult(String str);

    /**
     * 获取线性布局 动态添加控件
     */

    public LinearLayout getLinearLayout();

    /**
     * 显示发货毛重
     */
    public void setShipperMaoText(String str);

    /**
     * 显示发货皮重
     * @param str
     */
    public void setShipperPiText(String str);

    /**
     * 显示收货净重
     * @param str
     */
    public void setReceiverJingText(String str);

    public void setVisite(boolean b);

    public Bitmap getBitmap();


}
