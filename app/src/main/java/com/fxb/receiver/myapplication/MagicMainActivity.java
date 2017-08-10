package com.fxb.receiver.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MagicMainActivity extends Activity {
    public Button m_btnread;
    public Button m_set_receiver;
    public TextView text_versionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_main);
        Initview();
        m_btnread.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intentTo = new Intent();
                intentTo.setClass(MagicMainActivity.this, ReceiverWriteActivity.class);
                startActivity(intentTo);
            }
        });  m_set_receiver.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MagicMainActivity.this, SelectReceiverActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Initview() {
        m_btnread = (Button) findViewById(R.id.ReadWrite);
        m_set_receiver = (Button) findViewById(R.id.set_receiver);
        text_versionName = (TextView) findViewById(R.id.text_versionName);
        try {
            text_versionName.setText(this.getString(R.string.Version) + getLocalVersionCode(this));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the software version number ：
     *
     * @param context
     * @return
     * @throws NameNotFoundException
     */
    public static String getLocalVersionCode(Context context)
            throws NameNotFoundException {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo(
                context.getPackageName(), 0);
        return packageInfo.versionName;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
