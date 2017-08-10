package com.fxb.receiver.myapplication.model;

/**
 * Created by zf on 2017/8/5 0005.
 */

public interface IResponse {
    public void Error(int code, String error);

    public void Response(byte[] date);

}
