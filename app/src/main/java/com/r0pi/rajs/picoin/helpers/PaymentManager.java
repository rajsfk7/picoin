package com.r0pi.rajs.picoin.helpers;


import android.content.Context;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by user on 3/11/2018.
 */

public class PaymentManager {

    Context myContext;
    String deviceCode;
    SQLiteDatabase db;
    DatabaseHelper myDbHelper;

    public PaymentManager(Context context, GlobalSettings gSetting){
        this.myContext = context;
        this.deviceCode = gSetting.getDEVICECODE().toString();

        myDbHelper = new DatabaseHelper(myContext, deviceCode);
        db=myDbHelper.open(deviceCode);
    }

    public boolean AddBalance( int amount, String otherDeviceID, String strHash, String strAddress){
        Cursor cursor=db.rawQuery("select balance from tbluser where deviceid = '" + this.deviceCode + "'", null);
        cursor.moveToFirst();
        int balanceAmount = amount + Integer.parseInt(cursor.getString(cursor.getColumnIndex("balance")));
        cursor.close();
        if(balanceAmount > 50){
            return false;
        }
        else{
            db.execSQL("insert into tblTxn( txnid, mydeviceid, otherdeviceid, amt, txntype, txnhash, txnaddress ) values(?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{0, deviceCode, otherDeviceID, amount, "RECEIVED", strHash, strAddress });

             db.rawQuery("UPDATE tbluser SET balance = ? WHERE deviceid = ?", new Object[]{balanceAmount, this.deviceCode});
            return true;
        }
    }

    public boolean RemoveBalance(int amount, String otherDeviceID, String strHash, String strAddress){
        Cursor cursor=db.rawQuery("select balance from tbluser where deviceid = '" + this.deviceCode + "'", null);
        cursor.moveToFirst();
        int balanceAmount = Integer.parseInt(cursor.getString(cursor.getColumnIndex("balance"))) - amount;
        cursor.close();

        if(balanceAmount < 0){
            return false;
        }
        else{
            db.execSQL("insert into tblTxn( txnid, mydeviceid, otherdeviceid, amt, txntype, txnhash, txnaddress ) values(?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{0, deviceCode, otherDeviceID, amount, "PAID", strHash, strAddress });

            db.rawQuery("UPDATE tbluser SET balance = ? WHERE deviceid = ?", new Object[]{balanceAmount, this.deviceCode});
            return true;
        }
    }

    public int getBalance( int balance){
        Cursor cursor = db.rawQuery("select balance from tbluser where deviceid = '" + this.deviceCode + "'", null);
        cursor.moveToFirst();
        int balanceAmount = Integer.parseInt(cursor.getString(cursor.getColumnIndex("balance")));
        cursor.close();
        return balanceAmount;
    }
}
