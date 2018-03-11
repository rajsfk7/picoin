package com.r0pi.rajs.picoin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.BeepManager;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.mobapphome.mahencryptorlib.MAHEncryptor;
import com.r0pi.rajs.picoin.helpers.GlobalSettings;
import com.r0pi.rajs.picoin.helpers.PaymentManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ReceivePayment extends AppCompatActivity {
    private static final String TAG = ReceivePayment.class.getSimpleName();
    private DecoratedBarcodeView barcodeView;
    private BeepManager beepManager;
    private String qrCodeToken;

    Button btnBack;
    Button btnHome;
    Button btnNext;

    ImageView imgQrView;

    PaymentManager payManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        btnBack = (Button) findViewById(R.id.btnRetry);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnNext = (Button) findViewById(R.id.btnNext);

        barcodeView = (DecoratedBarcodeView) findViewById(R.id.barcode_scanner);
        Collection<BarcodeFormat> formats = Arrays.asList(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_39);
        barcodeView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        barcodeView.decodeContinuous(callback);
        barcodeView.setStatusText("");
        beepManager = new BeepManager(this);

        addListenersOnButton();


        payManager = new PaymentManager(ReceivePayment.this, (GlobalSettings) this.getApplication());

    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if(result.getText() == null || result.getText().equals(qrCodeToken)) {
                // Prevent duplicate scans
                return;
            }

            barcodeView.setVisibility(View.INVISIBLE);
            qrCodeToken = result.getText();
            //barcodeView.setStatusText(result.getText());

            beepManager.playBeepSoundAndVibrate();

            //Added preview of scanned barcode
            ImageView imageView = (ImageView) findViewById(R.id.imgQrCode);
            imageView.setImageBitmap(result.getBitmapWithResultPoints(Color.YELLOW));

            gotoResult();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }


    private void gotoResult(){
        //TODO Save To transaction


        try {



            Integer deviceCodeLength = Integer.parseInt(qrCodeToken.substring(10, 14));
            String sendingDeviceCode = qrCodeToken.substring(14, 14 + deviceCodeLength);

            String timeStamp = TimeUnit.MINUTES.toMinutes(System.currentTimeMillis()) + "";
            String recivedEncrypted = qrCodeToken.substring(0, 10) + qrCodeToken.substring(14 + deviceCodeLength, qrCodeToken.length());

            String SecretKey = "PI" + sendingDeviceCode + "ENC" + timeStamp + "COIN" + "VER1";

            MAHEncryptor mahEncryptor = null;

            mahEncryptor = MAHEncryptor.newInstance(SecretKey);
            String decrypted = mahEncryptor.decode(recivedEncrypted);

            JSONObject jsonData = new JSONObject(decrypted);

            String strPackageSentTime = jsonData.get("DateTime").toString();

            Double TimeDiff  = Double.parseDouble(timeStamp) - Double.parseDouble(strPackageSentTime);
            Intent intent=new Intent(ReceivePayment.this, Result.class);

            int intAmount  = Integer.parseInt(jsonData.get("Amount").toString());

            if(TimeDiff > (3600 * 60) || TimeDiff < 0){
                Toast.makeText(this, "Invalid Transaction", Toast.LENGTH_LONG).show();

                intent.putExtra("STATUS", "FAIL");
                intent.putExtra("HEADER", "received failed");
                intent.putExtra("MESSAGE", "Please Try Again, Refresh sender QR Code");
                intent.putExtra("FROMTO", "Invalid Transaction received from");
                intent.putExtra("USERNAME", sendingDeviceCode);
                intent.putExtra("AMOUNT", String.valueOf(intAmount) );
            }
            else{
                if (payManager.AddBalance(intAmount, sendingDeviceCode, qrCodeToken, strPackageSentTime ) == true)
                {
                    //TODO Disable Below Code
                    intent.putExtra("STATUS", "SUCCESS");
                    intent.putExtra("HEADER", "received");
                    intent.putExtra("MESSAGE", "you have successfully received");
                    intent.putExtra("FROMTO", "received from");
                    intent.putExtra("USERNAME", sendingDeviceCode);
                    intent.putExtra("AMOUNT", String.valueOf(intAmount) );
                }
                else{
                    intent.putExtra("STATUS", "FAIL");
                    intent.putExtra("HEADER", "received failed");
                    intent.putExtra("MESSAGE", "You have exceeded limit of 50picoin, please donate some picoins");
                    intent.putExtra("FROMTO", "You have reached 50picoin limit");
                    intent.putExtra("USERNAME", sendingDeviceCode);
                    intent.putExtra("AMOUNT", String.valueOf(intAmount) );
                }

            }
            startActivity(intent);
            finish();


        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void addListenersOnButton(){

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ReceivePayment.this, HomeScreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

}
