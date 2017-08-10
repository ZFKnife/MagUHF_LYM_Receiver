package com.fxb.receiver.myapplication.presenter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fxb.receiver.myapplication.application.App;
import com.fxb.receiver.myapplication.bean.Incidental;
import com.fxb.receiver.myapplication.util.Sp;
import com.fxb.receiver.myapplication.util.Util;
import com.fxb.receiver.myapplication.view.IReceiverWriteView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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


    public ReceiverWritePresenter(IReceiverWriteView iReceiverView) {
        super(iReceiverView);
        this.iReceiverView = iReceiverView;
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
        carnum = URLEncoder.encode(strings[1].substring(0, 1)) + strings[1].substring(1);
        iReceiverView.setShipperMaoText(strings[2]);
        iReceiverView.setShipperPiText(strings[3]);
        iReceiverView.setShipperJingText(strings[4]);
        iReceiverView.setResult(str);
        contrastReceiver();
    }

    @Override
    void writeResponse() {
        iReceiverView.setResult("写入成功");
    }

    private void getIncidental() {
        String url = "http://39.108.0.144/YJYNLogisticsSystem/appIncidental?action=getIncidental";
        StringRequest getInidental = new StringRequest(url, new Response.Listener<String>() {
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
        String url = "http://39.108.0.144/YJYNLogisticsSystem/appPublishInformation?action=getRealordReceiver&";
        StringBuilder stringBuilder = new StringBuilder(url);
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
    private void uploadMeasData(String maoWeight, String piWeight, String jingWeight, final String sb) {
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
        String url = "http://39.108.0.144/YJYNLogisticsSystem/appPublishInformation?action=uploadMeasData&";
        StringBuilder stringBuilder = new StringBuilder(url);
        stringBuilder.append("CARDNUM=").append(strings[0]);
        stringBuilder.append("&CARNUM=").append(carnum);
        stringBuilder.append("&RECEIVERMAO=").append(maoWeight);
        stringBuilder.append("&RECEIVERPI=").append(piWeight);
        stringBuilder.append("&RECEIVERJING=").append(jingWeight);
        stringBuilder.append("&INCIDENTAL=").append(ja);
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
                        //上传成功后重新写卡
                        writeTrue(sb);
                        if (imageBitmap != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //上传发货方图片
                                    String uploadShipperurl = "";
                                    uploadReceiverServer(uploadShipperurl, carnum, getBitmapPath(), imageBitmap);
                                }
                            }).start();
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
        return "shipper" + System.currentTimeMillis() + ".jpg";
    }

    /**
     * 上传图片
     *
     * @param targetUrl
     * @param carnum
     * @param fileName
     * @param bm
     * @return
     */
    private boolean uploadReceiverServer(String targetUrl, String carnum, String fileName, Bitmap bm) {
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
            dos.writeBytes("Content-Disposition: form-data; name=\"CARNUM\"" + end);
            dos.writeBytes(end);
            dos.writeBytes(carnum + end);

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
        iReceiverView.getLinearLayout().addView(la);
        if (i == 1) {
            textView.setEnabled(false);
        }
        EditTexts.add(textView);
    }

    public void cancel() {
        App.getRequestQueue().cancelAll(this);
    }

}
