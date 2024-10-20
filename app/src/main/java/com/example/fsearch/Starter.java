package com.example.fsearch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Starter extends BroadcastReceiver {
    public Starter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
     Intent i=new Intent(context,AnalizeService.class);
        context.startService(i);
    }
}
