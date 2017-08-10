package com.fxb.receiver.myapplication.config;

/**
 *
 * Created by zf on 2017/8/10 0010.
 */

public class RequestConfig {

    private static String ip = "39.108.0.144";

    private static String backPackage = "YJYNLogisticsSystem";

    private static String base = "http://" + ip + "/" + backPackage + "/";

    public static String upReceiverMeasData = base + "appPublishInformation?action=uploadMeasData&";

    public static String getRealordReceiver = base + "appPublishInformation?action=getRealordReceiver&";

    public static String uploadReceiverurl = base + "";

    public static String getReceiverList = base + "appPublishInformation?action=getReceiverList";

    public static String getIncidental = base + "appIncidental?action=getIncidental";




}
