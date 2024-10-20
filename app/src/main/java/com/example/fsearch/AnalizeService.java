package com.example.fsearch;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.example.fsearch.tools.Cam;
import com.example.fsearch.tools.GPS;
import com.example.fsearch.tools.MMS;
import com.example.fsearch.tools.SMS;
import com.example.fsearch.tools.server.Connect;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class AnalizeService extends Service {
    final Handler camHandler =new Handler();
    Runnable camRunnable =new Runnable() {
        @Override
        public void run() {
               cam.takePhoto();
        }
    };
    public static final int SLEEP = 5000;
    SMS smsSender;
    MMS mmsSender;
    int fireRate;
    Cam cam;
    GPS gps;
    private Preference preference;
    PhotoAnalize photoAnalize;
    private boolean running;

    public AnalizeService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        wl.acquire();
// screen and CPU will stay awake during this section
        wl.release();
        Log.d("fsearch", "Start AnalizeService");
        running=true;
        preference = new Preference(this);
        photoAnalize = new PhotoAnalize(this);
        preference.loadPreference();
        // создаем папку для фото если ее нет
        File photoDir=new File(preference.photoDir);
        if(!photoDir.exists() ) {
            Log.d("fsearch","Create photo dir: "+preference.photoDir);
            photoDir.mkdirs();
        }
        File demoPhotoDir;
        demoPhotoDir = new File(preference.photoDir + "_demo");
        if (!demoPhotoDir.exists()) {
            Log.d("fsearch", "Create photo dir: " + preference.photoDir + "_demo");
            demoPhotoDir.mkdirs();
        }
        File analizedDir=new File(preference.photoDir+"_analized");
        if(!analizedDir.exists() ) {
            Log.d("fsearch","Create photo dir: "+preference.photoDir+"_analized");
            analizedDir.mkdirs();
        }
        cam=new Cam(this);
        gps = new GPS(AnalizeService.this);
        // отправка координат
        Process process = new Process();
        process.start();
        // анализ фото и отправка пожаров
        Process1 process1=new Process1();
        process1.start();

    }

    @Override
    public void onDestroy() {
        running=false;
        super.onDestroy();
    }

    class Process1 extends Thread {
        @Override
        public void run() {
            while (running) {
                try {
                    Thread.currentThread().sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                 if(preference.flagBox){
                    Connect connect=new Connect(AnalizeService.this);
                    connect.sendCoordinate(gps.getLocation());
                }
            }
        }
    }


    class FileByDateComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            return (int) ( rhs.lastModified()-lhs.lastModified() );
        }
    };

    class Process extends Thread {
        @Override
        public void run() {
            File dir = new File(preference.photoDir);
            File[] files = dir.listFiles();
            Arrays.sort(files,new FileByDateComparator());
            int i = 0;
            while (running) {
                try {
                    Thread.currentThread().sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (preference.cameraBox) {
                    //Делаем фото
                    camHandler.post(camRunnable);
                    Log.d("Background camera","The photo was taken");
                }
                if(dir.listFiles().length!=files.length){
                    files = dir.listFiles();
                    Arrays.sort(files,new FileByDateComparator());
                }
                if (files == null || files.length == 0) {
                    try {
                        Thread.currentThread().sleep(SLEEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                if (i >= files.length) {
                    i = 0;
                    files = dir.listFiles();
                    Arrays.sort(files,new FileByDateComparator());
                }

                File demoDir=new File(preference.photoDir+"_demo");
                File demoFiles[]=demoDir.listFiles();
                File photoForAnalize;
                if(demoFiles!=null&&demoFiles.length!=0){
                    photoForAnalize=demoFiles[0];
                }
                else{
                    photoForAnalize=files[i];
                }
                preference.lastPhoto = photoForAnalize.getName();
                Log.d("fsearch", "Opened photo: " + photoForAnalize.getName());
                photoAnalize.loadPhoto(photoForAnalize);
                //Анализируем фото
                photoAnalize.analyze();
                //Принимаем решение о пожаре
                Log.d("fsearch","Percent of smoke "+ photoAnalize.smokePercent+" "+photoForAnalize.getName());
                fireRate=0;
                if (photoAnalize.smokePercent > 10) {
                    fireRate = 1;
                }
                if (photoAnalize.smokePercent > 15) {
                    fireRate = 2;
                }
                //Если нужно смс или ммс-отправляем
                if (preference.smsBox && fireRate>0) {
                    smsSender = new SMS(AnalizeService.this);

                    smsSender.sendSMS("Пожар (Баллов:" + fireRate + "/5) , Фото: " + photoForAnalize +
                            ", Координаты: " + gps.getLocation().getLatitude() + "," + gps.getLocation().getLongitude()+", высота полета: "+gps.getLocation().getAltitude()+", скорость полета: "+gps.getLocation().getSpeed());
                }
                if (preference.mmsBox && fireRate>0) {
                    mmsSender = new MMS(AnalizeService.this);
                    mmsSender.sendMMS(photoAnalize.bm,"Пожар (Баллов:" + fireRate + "/5) , Фото: " + photoForAnalize +
                            ", Координаты: " + gps.getLocation().getLatitude() + "," + gps.getLocation().getLongitude()+", высота полета: "+gps.getLocation().getAltitude()+", скорость полета: "+gps.getLocation().getSpeed());
                }
                //Отпровляем координаты на сервер
               if(preference.flagBox && fireRate > 0){
                    Connect connect=new Connect(AnalizeService.this);
                       connect.sendFire(gps.getLocation(), fireRate,photoAnalize.getPhoto());
                }
                preference.savePreference();
                photoAnalize.close();
                // переместить фото в другую папку!
                i++;
                if(demoFiles!=null&&demoFiles.length!=0){
                    demoFiles[0].delete();
                }
                try {
                    Thread.currentThread().sleep(SLEEP);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
