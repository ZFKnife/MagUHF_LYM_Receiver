package com.fxb.receiver.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fxb.receiver.myapplication.presenter.ReceiverWritePresenter;
import com.fxb.receiver.myapplication.util.Sp;
import com.fxb.receiver.myapplication.view.IReceiverWriteView;

/**
 * Created by dxl on 2017-06-26.
 */

public class ReceiverWriteActivity extends Activity implements IReceiverWriteView {


    private TextView tv_receiverEpc;
    private TextView tv_receiverCarNum;
    private TextView tv_shipperMao;
    private TextView tv_shipperPi;
    private TextView tv_shipperJing;

    private EditText et_maoZhong;
    private EditText et_piZhong;
    private TextView tv_jingZhong;
    private TextView tv_resultView;

    private Button btn_receiverReadEpc;

    private Button btn_receiverReading;

    private Button btn_receiverWritting;

    private TextView tvShipper;
    private TextView tvShipperCargo;


    private Button btnPrinter;
    private Button btnZouzhi;


    private ImageView imageView;


    private LinearLayout linearLayout;


    private ReceiverWritePresenter presenter = null;
    private Bitmap imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_receiver_write);

        initView();

        presenter = new ReceiverWritePresenter(this);

        presenter.setEPCtext();

        onClick();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            imageBitmap = (Bitmap) bundle.get("data");
            assert imageBitmap != null;
            imageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void initView() {

        tv_receiverEpc = (TextView) findViewById(R.id.tv_receiver_epc);
        tv_receiverCarNum = (TextView) findViewById(R.id.tv_receiver_carnum);
        tv_shipperMao = (TextView) findViewById(R.id.tv_shipper_famao);
        tv_shipperPi = (TextView) findViewById(R.id.tv_shipper_fapi);
        tv_shipperJing = (TextView) findViewById(R.id.tv_shipper_fajing);
        tvShipper = (TextView) findViewById(R.id.tv_shipper);
        tvShipperCargo = (TextView) findViewById(R.id.tv_shipper_cargo);

        et_maoZhong = (EditText) findViewById(R.id.et_maozhong);
        et_piZhong = (EditText) findViewById(R.id.et_pizhong);
        tv_jingZhong = (TextView) findViewById(R.id.tv_jingzhong);
        tv_resultView = (TextView) findViewById(R.id.tv_resultView);

        imageView = (ImageView) findViewById(R.id.receiver_image);

        btn_receiverReadEpc = (Button) findViewById(R.id.btn_receiver_readepc);
        btn_receiverReading = (Button) findViewById(R.id.btn_receiver_reading);
        btn_receiverWritting = (Button) findViewById(R.id.btn_receiver_writting);

        linearLayout = (LinearLayout) findViewById(R.id.reveiver_linear);

        btnPrinter = (Button) findViewById(R.id.btn_printer);
        btnZouzhi = (Button) findViewById(R.id.btn_zouzhi);

    }


    private void onClick() {
        btn_receiverReadEpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.setEPCtext();
            }
        });
        btn_receiverReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.readCarNum();
            }
        });
        btn_receiverWritting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.writeReveiver();
            }
        });
        btnPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.printe();
            }
        });
        btnZouzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.Step();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
            }
        });
    }


    @Override
    public void setEPCtext(String EPC) {
        tv_receiverEpc.setText(EPC);
    }

    @Override
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setDialog(String str) {

    }

    @Override
    public String getShipperPI() {
        return et_piZhong.getText().toString().trim();
    }

    @Override
    public String getShipperMao() {
        return et_maoZhong.getText().toString().trim();
    }

    @Override
    public void setCarNumText(String str) {
        tv_receiverCarNum.setText(str);
    }

    @Override
    public void setShipperJingText(String str) {
        tv_shipperJing.setText(str);
    }

    @Override
    public String getLocalhostName() {
        return Sp.getStrings(this, "name");
    }

    @Override
    public void finash() {
        finish();
    }

    @Override
    public void setResult(String str) {
        tv_resultView.setText(str);
    }

    @Override
    public LinearLayout getLinearLayout() {
        return linearLayout;
    }

    @Override
    public void setShipperMaoText(String str) {
        tv_shipperMao.setText(str);
    }

    @Override
    public void setShipperPiText(String str) {
        tv_shipperPi.setText(str);
    }

    @Override
    public void setReceiverJingText(String str) {
        tv_jingZhong.setText(str);
    }

    @Override
    public void setVisite(boolean b) {
        btnPrinter.setEnabled(b);
    }

    @Override
    public Bitmap getBitmap() {
        if (imageBitmap == null) {
            return null;
        }
        return imageBitmap;
    }

    @Override
    public void setShipper(String str) {
        tvShipper.setText(str);
    }

    @Override
    public void setCargo(String str) {
        tvShipperCargo.setText(str);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.cancel();
    }
}
