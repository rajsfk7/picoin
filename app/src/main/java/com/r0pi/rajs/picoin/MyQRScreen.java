package com.r0pi.rajs.picoin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.mobapphome.mahencryptorlib.MAHEncryptor;
import com.r0pi.rajs.picoin.helpers.GlobalSettings;
import com.r0pi.rajs.picoin.helpers.PaymentManager;

import org.json.JSONObject;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class MyQRScreen extends AppCompatActivity {

    Button btnBack;
    Button btnHome;
    Button btnSent;

    String strAmount;
    PaymentManager payManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrscreen);

        strAmount = getIntent().getStringExtra("AMOUNT");

        btnBack = (Button) findViewById(R.id.btnRetry);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnSent = (Button) findViewById(R.id.btnSent);

        addListenersOnButton();

        payManager = new PaymentManager(MyQRScreen.this, (GlobalSettings) this.getApplication());
        try {



            String timeStamp = TimeUnit.MINUTES.toMinutes(System.currentTimeMillis()) + ""; // .MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";

            UUID strDeviceCode = ((GlobalSettings) this.getApplication()).getDEVICECODE();

            String SecretKey = "PI" + strDeviceCode + "ENC" + timeStamp + "COIN" + "VER1";

            MAHEncryptor mahEncryptor = MAHEncryptor.newInstance(SecretKey);
            JSONObject strData = new JSONObject();
            strData.put("Version", "VER1");
            strData.put("UserCode", strDeviceCode);
            strData.put("DateTime", timeStamp);
            strData.put("Amount", strAmount);
            //System.out.println(strData);

            String encrypted = mahEncryptor.encode(strData.toString());
            //System.out.println(encrypted);

            encrypted = encrypted.substring(0, 10) + String.format("%04d", strDeviceCode.toString().length()) + strDeviceCode + encrypted.substring(10, encrypted.length());
            //System.out.println(encrypted);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(encrypted, BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = (ImageView) findViewById(R.id.imgQrCode);
            imageViewQrCode.setImageBitmap(bitmap);

        } catch(Exception e) {

        }
    }

    public void addListenersOnButton(){

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MyQRScreen.this, PaymentSelection.class);
                i.putExtra("AMOUNT", strAmount);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i=new Intent(MyQRScreen.this, HomeScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            }
        });

        btnSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(MyQRScreen.this, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                if (payManager.RemoveBalance(Integer.parseInt(strAmount), "", "", "") == true)
                {
                    //TODO Disable Below Code
                    intent.putExtra("STATUS", "SUCCESS");
                    intent.putExtra("HEADER", "sent");
                    intent.putExtra("MESSAGE", "you have successfully sent");
                    intent.putExtra("FROMTO", "sent to");
                    intent.putExtra("USERNAME", "");
                    intent.putExtra("AMOUNT", strAmount );
                }
                else{
                    intent.putExtra("STATUS", "FAIL");
                    intent.putExtra("HEADER", "sent failed");
                    intent.putExtra("MESSAGE", "You have balance of 0picoin, please earn some picoins");
                    intent.putExtra("FROMTO", "You are out of balance");
                    intent.putExtra("USERNAME", "");
                    intent.putExtra("AMOUNT", strAmount );
                }


                startActivity(intent);
                finish();
            }
        });
    }

    //TODO Audio Listener if QRCode Scanned Perfectly.
    //Call RemoveAmount And Show result
}
