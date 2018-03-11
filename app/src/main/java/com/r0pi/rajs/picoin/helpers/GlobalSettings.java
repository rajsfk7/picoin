package com.r0pi.rajs.picoin.helpers;

import android.app.Application;

import java.util.UUID;

/**
 * Created by user on 3/10/2018.
 */

public class GlobalSettings extends Application {
    private UUID DEVICECODE;
    private int BALANCE;

    public UUID getDEVICECODE() {
        return DEVICECODE;
    }

    public void setDEVICECODE(){
        DeviceUuidFactory duFactory =  new DeviceUuidFactory(getBaseContext());
        DEVICECODE = duFactory.getDeviceUuid();
    }

    public int getBALANCE() {
        return BALANCE;
    }

    public void setBALANCE(int balanceAmount){
        this.BALANCE = balanceAmount;
    }
}
