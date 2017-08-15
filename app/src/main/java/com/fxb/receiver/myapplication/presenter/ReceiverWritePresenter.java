package com.fxb.receiver.myapplication.presenter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fxb.receiver.myapplication.application.App;
import com.fxb.receiver.myapplication.bean.Incidental;
import com.fxb.receiver.myapplication.config.RequestConfig;
import com.fxb.receiver.myapplication.util.Sp;
import com.fxb.receiver.myapplication.util.Util;
import com.fxb.receiver.myapplication.view.IReceiverWriteView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import hardware.print.BarcodeUtil;
import hardware.print.printer;

/**
 * Created by Administrator on 2017/8/9 0009.
 */

public class ReceiverWritePresenter extends Presenter {

    private IReceiverWriteView iReceiverView;

    private String[] strings;

    private Bitmap imageBitmap = null;

    private String receiverPI;

    private String receiverMao;

    private String receiverJing;

    private List<CheckBox> CheckBoxs;

    private List<EditText> EditTexts;

    private List<Incidental> incidentals;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10000) {
                CheckBoxs = new ArrayList<>();
                EditTexts = new ArrayList<>();

                for (int i = 0; i < incidentals.size(); i++) {
                    setView(i, incidentals.get(i));
                }
            }
        }
    };
    private String carnum = null;
    private String oradid = "";
    private printer mPrinter = new printer();
    private String cargo = "";


    public ReceiverWritePresenter(IReceiverWriteView iReceiverView) {
        super(iReceiverView);
        this.iReceiverView = iReceiverView;
        iReceiverView.setVisite(false);
        mPrinter.Open();
        getIncidental();
    }

    public void readCarNum() {
        readTrue(0, 32);
    }

    public void writeReveiver() {
        receiverPI = iReceiverView.getShipperPI();
        StringBuilder sb = new StringBuilder();
        if (receiverPI.equals("")) {
            iReceiverView.showToast("发货皮重不可以为空");
            return;
        }
        receiverMao = iReceiverView.getShipperMao();
        if (receiverMao.equals("")) {
            iReceiverView.showToast("发货毛重不可以为空");
            return;
        }
        if (strings == null || strings.length == 0) {
            iReceiverView.showToast("车牌号不能为空,请先读卡");
            return;
        }
        Double d_rough_weight = Double.parseDouble(receiverMao);
        Double d_tare = Double.parseDouble(receiverPI);
        double d_weight_empty = d_rough_weight - d_tare;
        DecimalFormat df = new DecimalFormat("#.00");
        receiverMao = df.format(d_rough_weight);
        receiverPI = df.format(d_tare);
        receiverJing = df.format(d_weight_empty);
        receiverJing = df.format(d_weight_empty);
        sb.append(strings[0]).append(",");
        sb.append(strings[1]).append(",");
        iReceiverView.setReceiverJingText(receiverJing);
        uploadMeasData(receiverMao, receiverPI, receiverJing, sb.toString());
    }

    @Override
    void readResponse(String str) {
        strings = str.split(",");
        iReceiverView.showToast("读取成功！");
        iReceiverView.setCarNumText(strings[1]);
        try {
            carnum = URLEncoder.encode(strings[1].substring(0, 1), "UTF-8") + strings[1].substring(1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int length = strings.length - 1;
        int msc = -1;
        if (length > 5) {
            msc = Integer.parseInt(strings[length]);
        }
        if (str.substring(18, 19).equals("0")) {
            iReceiverView.showToast("没有发货方写入数据");
            iReceiverView.finash();
            return;
        } else if (msc == 0) {
            iReceiverView.setShipperMaoText(strings[2]);
            iReceiverView.setShipperPiText(strings[3]);
            iReceiverView.setShipperJingText(strings[4]);
            iReceiverView.setResult(str.replace(strings[length], ""));
        } else {
            iReceiverView.setResult(str);
        }
        contrastReceiver();
    }

    @Override
    void writeResponse() {
        iReceiverView.setResult("写入成功");
    }

    private void getIncidental() {
        StringRequest getInidental = new StringRequest(RequestConfig.getIncidental, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (TextUtils.isEmpty(s)) {
                    iReceiverView.showToast("服务器数据异常");
                    return;
                }
                String name = null;
                try {
                    JSONObject o = new JSONObject(s);
                    incidentals = new ArrayList<>();
                    JSONArray ja = new JSONArray(o.getString("incidental"));
                    int j = ja.length();
                    for (int i = 0; i < j; i++) {
                        JSONObject jo = new JSONObject(String.valueOf(ja.get(i)));
                        Incidental inci = new Incidental();
                        inci.setIncidental(jo.getString("name"));
                        inci.setMoney(jo.getInt("money"));
                        inci.setStatus(jo.getInt("status"));
                        incidentals.add(inci);
                    }
                    handler.sendEmptyMessage(10000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                iReceiverView.showToast("网络异常，请稍后再试");
            }
        });
        getInidental.setTag(this);
        App.getRequestQueue().add(getInidental);
    }

    private void contrastReceiver() {
        if (strings == null) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder(RequestConfig.getRealordReceiver);
        stringBuilder.append("&CARNUM=").append(carnum);
        StringRequest getContactRequest = new StringRequest(stringBuilder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (TextUtils.isEmpty(s)) {
                    iReceiverView.showToast("服务器数据异常");
                    return;
                }
                try {
                    JSONObject o = new JSONObject(s);
                    if (o.getString("status").equals("0")) {
                        String localname = Sp.getStrings(App.mContext, "name");
                        String intentname = o.getString("name");
                        if (!localname.equals(intentname)) {
                            iReceiverView.showToast("所在收货商和订单收货商不符");
                            iReceiverView.finash();
                        } else {
                            iReceiverView.showToast("通过");
                        }
                    } else if (o.getString("status").equals("1")) {
                        iReceiverView.showToast("请先确认是否接取货物");
                        iReceiverView.finash();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                iReceiverView.showToast("网络异常，请稍后再试");
            }
        });
        getContactRequest.setTag(this);
        App.getRequestQueue().add(getContactRequest);
    }

    /*
    * 上传数据
    * */
    private void uploadMeasData(final String maoWeight, final String piWeight, String jingWeight, final String sb) {
        int c = CheckBoxs.size();
        final JSONArray ja = new JSONArray();
        for (int i = 0; i < c; i++) {
            String incidental = URLEncoder.encode(incidentals.get(i).getIncidental());
            JSONObject jo = new JSONObject();
            if (CheckBoxs.get(i).isChecked()) {
                try {
                    String money = EditTexts.get(i).getText().toString().trim();
                    jo.put("incidental", incidental);
                    jo.put("money", money);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    jo.put("incidental", incidental);
                    jo.put("money", "0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ja.put(jo);
        }

            /*
            * 参数说明
            * 139.224.0.153：IP
            * LYMistSystem：项目名
            * appPublishInformation：类名
            * uploadMeasData：action
            * CARDNUM：卡编号
            * CARNUM：车牌号
            * RECEIVERMAO：收货端毛重
            * RECEIVERPI：收货端皮重
            * RECEIVERJING：收货端净重
            * INCIDENTAL：杂费信息
            * */
        StringBuilder stringBuilder = new StringBuilder(RequestConfig.upReceiverMeasData);
        stringBuilder.append("CARDNUM=").append(strings[0]);
        stringBuilder.append("&CARNUM=").append(carnum);
        stringBuilder.append("&RECEIVERMAO=").append(maoWeight);
        stringBuilder.append("&RECEIVERPI=").append(piWeight);
        stringBuilder.append("&RECEIVERJING=").append(jingWeight);
        stringBuilder.append("&INCIDENTAL=").append(ja);
        Log.i("---- ", "uploadMeasData: " + stringBuilder.toString());
        StringRequest getContactRequest = new StringRequest(stringBuilder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (TextUtils.isEmpty(s)) {
                    iReceiverView.showToast("服务器数据异常");
                    return;
                }
                try {
                    JSONObject o = new JSONObject(s);
                    if (o.getString("Result").equals("0")) {
                        JSONArray ja = new JSONArray(o.getString("data"));
                        JSONObject jo = new JSONObject(String.valueOf(ja.getJSONObject(0)));
                        oradid = jo.getString("ordered");
                        cargo = jo.getString("name");
                        iReceiverView.setVisite(true);
                        imageBitmap = iReceiverView.getBitmap();
                        if (imageBitmap != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //上传发货方图片
                                    Log.i(" ------ ", "run:  start ");
                                    uploadReceiverServer(RequestConfig.uploadReceiverurl, oradid, getBitmapPath(), imageBitmap, sb);
                                }
                            }).start();
                        } else {
                            writeTrue(sb);
                        }

                    }
                    iReceiverView.showToast(o.getString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                iReceiverView.showToast("网络异常，请稍后再试");
            }
        });
        getContactRequest.setTag(this);
        App.getRequestQueue().add(getContactRequest);

    }


    /**
     * 生成图片名
     *
     * @return
     */
    private String getBitmapPath() {
        return "receiver.jpg";
    }

    /**
     * 上传图片
     *
     * @param targetUrl
     * @param ORDERNUM
     * @param fileName
     * @param bm
     * @return
     */
    private boolean uploadReceiverServer(String targetUrl, String ORDERNUM, String fileName, Bitmap bm, String sb) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "******";
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(targetUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"ORDERNUM\"" + end);
            dos.writeBytes(end);
            dos.writeBytes(ORDERNUM + end);

            dos.writeBytes(twoHyphens + boundary + end);
            dos.writeBytes("Content-Disposition: form-data; name=\"imagePath\"; filename=\"" + fileName + "\"" + end);
            dos.writeBytes(end);
            dos.write(Util.Bitmap2Bytes(bm));
            dos.writeBytes(end);

            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();

            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String result = br.readLine();
            JSONObject resultJson = new JSONObject(result);
            int i = resultJson.getInt("status");
            if (i == 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        iReceiverView.showToast("上传成功");
                    }
                });
                writeTrue(sb);
            }
            dos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
                httpURLConnection = null;
            }
        }
        return true;
    }

    private void setView(int position, Incidental incidental) {
        int i = incidental.getStatus();
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);

        LinearLayout la = new LinearLayout(App.mContext);
        la.setLayoutParams(llp);
        la.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
        vlp.weight = 1;
        CheckBox checkBox = new CheckBox(App.mContext);
        checkBox.setLayoutParams(vlp);
        checkBox.setTextColor(Color.parseColor("#000000"));
        checkBox.setText(incidental.getIncidental());
        CheckBoxs.add(checkBox);

        EditText textView = new EditText(App.mContext);
        textView.setLayoutParams(vlp);
        la.addView(checkBox);
        la.addView(textView);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setText("" + incidental.getMoney());
        if (i == 1) {
            textView.setEnabled(false);
        }
        EditTexts.add(textView);
        iReceiverView.getLinearLayout().addView(la);
    }

    public void printe() {
        if (oradid.equals("")) {
            iReceiverView.showToast("请先写卡");
            return;
        }
        if (cargo.equals("")) {
            iReceiverView.showToast("请先写卡");
            return;
        }
        if (strings == null) {
            iReceiverView.showToast("请先读卡");
            return;
        }
        if (receiverMao == null) {
            iReceiverView.showToast("请先读卡");
            return;
        }
        if (receiverPI == null) {
            iReceiverView.showToast("请先读卡");
            return;
        }
        if (receiverJing == null) {
            iReceiverView.showToast("请先读卡");
            return;
        }

        mPrinter.PrintStringEx("卡的信息平台单据", 40, false, true, printer.PrintType.Centering);
        mPrinter.PrintLineInit(20);
        mPrinter.PrintLineString("收货方单据", 20, 210, false);
        mPrinter.PrintLineEnd();
        String str = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
        mPrinter.PrintLineInit(18);
        mPrinter.PrintLineStringByType(str, 18, printer.PrintType.Centering, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("订单号：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(oradid, 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("发货方：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(iReceiverView.getLocalhostName(), 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("车牌号：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(strings[1], 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("货物名称：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(cargo, 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("毛重：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(receiverMao + "吨", 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("皮重：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(receiverPI + "吨", 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("净重：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(receiverJing + "吨", 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(25);
        mPrinter.PrintLineStringByType("打印时间：", 24, printer.PrintType.Left, true);
        mPrinter.PrintLineString(Util.getTime(), 20, 200, false);
        mPrinter.PrintLineEnd();
        mPrinter.PrintLineInit(18);
        mPrinter.PrintLineStringByType(str, 18, printer.PrintType.Centering, false);
        mPrinter.PrintLineEnd();
        Bitmap bm = null;
        try {
            bm = BarcodeUtil.encodeAsBitmap("Thanks for using our Android terminal",
                    BarcodeFormat.QR_CODE, 160, 160);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        if (bm != null) {
            mPrinter.PrintBitmap(bm);
        }
        mPrinter.PrintLineInit(40);
        mPrinter.PrintLineStringByType("", 24, printer.PrintType.Right, true);//160
        mPrinter.PrintLineEnd();
        mPrinter.printBlankLine(40);
        clear();
    }

    private void clear() {
        oradid = "";
        strings = null;
        cargo = "";
        receiverMao = null;
        receiverPI = null;
        receiverJing = null;
        abstractUHFModel.clear();
        iReceiverView.setEPCtext("");
        iReceiverView.setCarNumText("");
        iReceiverView.setShipperJingText("");
        iReceiverView.setShipperJingText("");
        iReceiverView.showToast("数据写入完成，可进行下一业务操作！");
        iReceiverView.setVisite(false);
    }


    public void Step() {
        if (mPrinter == null) {
            return;
        }
        mPrinter.Step((byte) 0x5f);
    }


    public void cancel() {
        App.getRequestQueue().cancelAll(this);
    }

}
