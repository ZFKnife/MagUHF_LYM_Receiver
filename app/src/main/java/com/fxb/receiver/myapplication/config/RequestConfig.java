package com.fxb.receiver.myapplication.config;

/**
 *
 * Created by zf on 2017/8/10 0010.
 */

public class RequestConfig {

    private static String ip = "139.224.0.153";
//    private static String ip = "192.168.0.104";

    private static String backPackage = "LYMLogisticsSystem";

    private static String base = "http://" + ip + "/" + backPackage + "/";

    public static String upReceiverMeasData = base + "appPublishInformation?action=uploadMeasData&";

    public static String getRealordReceiver = base + "appPublishInformation?action=getRealordReceiver&";

    public static String uploadReceiverurl = base + "appUser?action=uploadReceiptorder";

    public static String getReceiverList = base + "appPublishInformation?action=getReceiverList";

    public static String getIncidental = base + "appIncidental?action=getIncidental";

}
