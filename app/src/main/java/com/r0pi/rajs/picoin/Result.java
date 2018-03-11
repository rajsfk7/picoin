package com.r0pi.rajs.picoin;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Result extends AppCompatActivity {

    TextView txtHeader;
    TextView txtMessage;
    TextView txtFromTo;
    TextView txtUsername;
    TextView txtAmount;

    Button btnRetry;
    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        String strStatus = getIntent().getStringExtra("STATUS");
        String strHeader = getIntent().getStringExtra("HEADER");
        String strMessage = getIntent().getStringExtra("MESSAGE");
        String strFromTo = getIntent().getStringExtra("FROMTO");
        String strUsername = getIntent().getStringExtra("USERNAME");
        String strAmount = getIntent().getStringExtra("AMOUNT");

        txtHeader = (TextView) findViewById(R.id.txtHeader);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtFromTo = (TextView) findViewById(R.id.txtFromTo);
        txtUsername = (TextView) findViewById(R.id.txtUsername);
        txtAmount = (TextView) findViewById(R.id.txtAmount);

        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnHome = (Button) findViewById(R.id.btnHome);

        btnRetry.setVisibility(View.INVISIBLE);
        btnHome.setVisibility(View.INVISIBLE);

        txtHeader.setText(strHeader);
        txtMessage.setText(strMessage);
        txtFromTo.setText(strFromTo);
        txtUsername.setText(strUsername);
        txtAmount.setText(strAmount);

        if(strStatus.toUpperCase().equals("SUCCESS"))
        {
            btnHome.setVisibility(View.VISIBLE);
        }
        else if(strStatus.toUpperCase().equals("FAIL"))
        {
            btnRetry.setVisibility(View.VISIBLE);

            txtHeader.setTextColor(Color.RED);
            txtMessage.setTextColor(Color.RED);
        }

        addListenersOnButton();
    }

    public void addListenersOnButton(){

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Result.this, ReceivePayment.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Result.this, HomeScreen.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

}
