package com.example.fsearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

public class Preference {

    Context context;

    private SharedPreferences sp;
    public String SMSnumber, MMSC, MMSport, MMSproxy, MMSnumber,lastPhoto,photoDir,SMSC,droneId,baseURL;
    public int tuneRed, tuneBlue, tuneGreen, deviation,cameraNumber;
    public boolean smsBox, mmsBox, flagBox, cameraBox, savedAnalizedPhotoBox;

    public Preference(Context context) {
        this.context = context;
    }

    public void savePreference() {
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("droneId",droneId);
        ed.putString("baseURL",baseURL);
        ed.putString("SMSnumber", SMSnumber);
        ed.putString("MMSc", MMSC);
        ed.putString("MMSport", MMSport);
        ed.putString("MMSproxy", MMSproxy);
        ed.putString("MMSnumber", MMSnumber);
        ed.putString("lastPhoto",lastPhoto);
        ed.putString("SMSC",SMSC);
        ed.putString("photoDir",lastPhoto);
        try {
            ed.putInt("cameraNumber",cameraNumber);
            ed.putInt("tuneRed", tuneRed);
            ed.putInt("tuneBlue", tuneBlue);
            ed.putInt("tuneGreen", tuneGreen);
            ed.putInt("deviation", deviation);
        } catch (Exception e) {
            Log.e("Fsearch", "Incorrect tune Paramaters");
        }
        ed.putBoolean("savedAnalizedPhotoBox", savedAnalizedPhotoBox);
       ed.putBoolean("cameraBox", cameraBox);
        ed.putBoolean("smsBox", smsBox);
        ed.putBoolean("mmsBox", mmsBox);
        ed.putBoolean("flagBox", flagBox);
        ed.putString("photoDir",photoDir);
        ed.commit();
    }

    public void loadPreference() {
        sp = context.getSharedPreferences("ConfigPreference", context.MODE_PRIVATE);
        cameraBox =sp.getBoolean("cameraBox",true);
        cameraNumber=sp.getInt("cameraNumber",0);
        baseURL=sp.getString("baseURL","https://dry-beach-13530.herokuapp.com/");
        droneId=sp.getString("droneId","");
        SMSnumber = sp.getString("SMSnumber", "");
        SMSC=sp.getString("SMSC","+79282000002");
        MMSC = sp.getString("MMSC", "");
        MMSport = sp.getString("MMSport", "");
        MMSproxy = sp.getString("MMSproxy", "");
        MMSnumber = sp.getString("MMSnumber", "");
        tuneRed = sp.getInt("tuneRed", 0);
        tuneBlue = sp.getInt("tuneBlue", -25);
        tuneGreen = sp.getInt("tuneGreen", 0);
        deviation = sp.getInt("deviation", 20);
        savedAnalizedPhotoBox =sp.getBoolean("savedAnalizedPhotoBox",false);
        flagBox = sp.getBoolean("flagBox", false);
        smsBox = sp.getBoolean("smsBox", false);
        mmsBox = sp.getBoolean("mmsBox", false);
        lastPhoto=sp.getString("lastPhoto","");
        String DownloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        photoDir=sp.getString("photoDir",DownloadDirectory + "/photos");
    }
}
