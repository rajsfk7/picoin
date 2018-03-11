package com.r0pi.rajs.picoin;

import android.content.Intent;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.r0pi.rajs.picoin.helpers.DatabaseHelper;
import com.r0pi.rajs.picoin.helpers.GlobalSettings;

import java.io.File;
import java.util.UUID;




public class HomeScreen extends AppCompatActivity {

    String strUsername = "";
    String strDeviceCode = "";
    UUID uuidDeviceID = null;

    ImageButton imgBtnTransaction;
    ImageButton imgBtnQrCode;
    ImageButton imgBtnSettings;
    ImageButton imgBtnHistory;
    TextView txtUsername;
    TextView txtBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((GlobalSettings) this.getApplication()).setDEVICECODE();
        uuidDeviceID = ((GlobalSettings) this.getApplication()).getDEVICECODE();
        strUsername = uuidDeviceID.toString();

        imgBtnTransaction = (ImageButton) findViewById(R.id.transaction);
        imgBtnQrCode = (ImageButton) findViewById(R.id.qrcode);
        imgBtnSettings = (ImageButton) findViewById(R.id.settings);
        imgBtnHistory = (ImageButton) findViewById(R.id.history);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtBalance = (TextView) findViewById(R.id.txtAmount);

        if(strUsername == ""){
            Toast.makeText(this, "Invalid Wallet", Toast.LENGTH_LONG);
            imgBtnTransaction.setEnabled(false);
            imgBtnQrCode.setEnabled(false);
            imgBtnSettings.setEnabled(false);
            imgBtnHistory.setEnabled(false);
        }
        else{
            txtUsername.setText(strUsername);
        }

        addListenersOnButton();

        InitializeSQLCipher();

    }

    public void addListenersOnButton(){
        imgBtnTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeScreen.this, ReceivePayment.class);
                startActivity(i);
            }
        });

        imgBtnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeScreen.this, PaymentSelection.class);
                i.putExtra("AMOUNT", "1");
                startActivity(i);
            }
        });

        imgBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeScreen.this, Settings.class);
                startActivity(i);
            }
        });

        imgBtnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeScreen.this, History.class);
                startActivity(i);
            }
        });

    }

    private void InitializeSQLCipher(){
        SQLiteDatabase db;
        DatabaseHelper myDbHelper = new DatabaseHelper(HomeScreen.this, strUsername);
        db=myDbHelper.open(strUsername);

        Cursor cursor=db.rawQuery("select * from user", null);

        strUsername = cursor.getString(cursor.getColumnIndex("username"));
        strDeviceCode = cursor.getString(cursor.getColumnIndex("deviceid"));
        int balanceAmount = 0;
        try{
            balanceAmount= Integer.parseInt(cursor.getString(cursor.getColumnIndex("balance")));
        }
        catch (Exception e){
        }

        ((GlobalSettings) this.getApplication()).setBALANCE(balanceAmount);

    }

    /*
    private void InitializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
        File databaseFile = getDatabasePath("pc" + strUsername + ".db");
        databaseFile.mkdirs();
        databaseFile.delete();
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(databaseFile, "pI8234" + strUsername + "CoiN", null);

        database.execSQL("create table user(username, deviceid, balance)");
        database.execSQL("insert into t1( username, deviceid, balance) values(?, ?, ?)", new Object[]{"  + strUsername + " , " + strUsername  + ", 50});
    }
    */
}
