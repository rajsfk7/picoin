package com.r0pi.rajs.picoin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class PaymentSelection extends AppCompatActivity {

    Button btnBack;
    Button btnHome;
    Button btnNext;

    Integer intAmount = 1;

    ImageButton imgAmt1;
    ImageButton imgAmt2;
    ImageButton imgAmt3;
    ImageButton imgAmt4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        btnBack = (Button) findViewById(R.id.btnRetry);
        btnHome = (Button) findViewById(R.id.btnHome);
        btnNext = (Button) findViewById(R.id.btnNext);

        imgAmt1 = (ImageButton) findViewById(R.id.imgAmt1);
        imgAmt2 = (ImageButton) findViewById(R.id.imgAmt2);
        imgAmt3 = (ImageButton) findViewById(R.id.imgAmt3);
        imgAmt4 = (ImageButton) findViewById(R.id.imgAmt4);

        intAmount = Integer.parseInt(getIntent().getStringExtra("AMOUNT"));
        setActiveAmount();

        addListenersOnButton();
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
            Intent i=new Intent(PaymentSelection.this, HomeScreen.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i=new Intent(PaymentSelection.this, MyQRScreen.class);
            i.putExtra("AMOUNT", intAmount.toString());
            startActivity(i);
            finish();
            }
        });

        imgAmt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intAmount = 1;
                setActiveAmount();
            }
        });

        imgAmt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intAmount = 2;
                setActiveAmount();
            }
        });

        imgAmt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intAmount = 3;
                setActiveAmount();
            }
        });

        imgAmt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intAmount = 4;
                setActiveAmount();
            }
        });

    }

    public void deselectAllAmount(){
        imgAmt1.setImageResource(R.drawable.amt1);
        imgAmt2.setImageResource(R.drawable.amt2);
        imgAmt3.setImageResource(R.drawable.amt3);
        imgAmt4.setImageResource(R.drawable.amt4);
    }

    public void setActiveAmount(){
        deselectAllAmount();
        switch (intAmount){
            case 1:
                imgAmt1.setImageResource(R.drawable.amt1selected);
                break;
            case 2:
                imgAmt2.setImageResource(R.drawable.amt2selected);
                break;
            case 3:
                imgAmt3.setImageResource(R.drawable.amt3selected);
                break;
            case 4:
                imgAmt4.setImageResource(R.drawable.amt4selected);
                break;
            default:
                break;
        }
    }

}
