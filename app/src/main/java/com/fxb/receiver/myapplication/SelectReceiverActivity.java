package com.fxb.receiver.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fxb.receiver.myapplication.application.App;
import com.fxb.receiver.myapplication.config.RequestConfig;
import com.fxb.receiver.myapplication.util.Sp;
import com.fxb.receiver.myapplication.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18 0018.
 */

public class SelectReceiverActivity extends Activity {
    private String TAG = SelectReceiverActivity.class.getSimpleName();

    private Spinner selectActivityReceiver;

    private Button selectActivityBtn;

    private List<String> list;

    private int size = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_receiver);
        init();
        getReceiver();
    }

    private void init() {
        selectActivityReceiver = (Spinner) findViewById(R.id.select_activity_receiver);
        selectActivityBtn = (Button) findViewById(R.id.select_activity_btn);
        selectActivityReceiver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                size = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        selectActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list == null) {
                    return;
                }
                if (size == -1) {
                    return;
                }
                Sp.putString(SelectReceiverActivity.this, "name", list.get(size));
                Toast.makeText(SelectReceiverActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void getReceiver() {
        StringRequest getContactRequest = new StringRequest(RequestConfig.getReceiverList, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (TextUtils.isEmpty(s)) {
                    ToastUtil.getShortToastByString(SelectReceiverActivity.this, "服务器数据异常");
                    return;
                }
                try {
                    JSONObject o = new JSONObject(s);
                    JSONArray ja = new JSONArray(o.getString("namelist"));
                    int w = ja.length();
                    list = new ArrayList<>();
                    for (int i = 0; i < w; i++) {
                        list.add(ja.getString(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(SelectReceiverActivity.this,
                            android.R.layout.simple_expandable_list_item_1, list);
                    selectActivityReceiver.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ToastUtil.getShortToastByString(SelectReceiverActivity.this, "网络异常，请稍后再试");
            }
        });
        getContactRequest.setTag(this);
        App.getRequestQueue().add(getContactRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getRequestQueue().cancelAll(this);
    }
}
