package com.fxb.receiver.myapplication.view;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public interface IShipperView extends IUHFViewBase {

    public String getShipperPI();

    public String getShipperMao();

    public void setCarNumText(String str);

    public void setShipperJingText(String str);

    public String getLocalhostName();

    public void finash();

}
