package com.example.fsearch.tools;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.fsearch.AnalizeService;
import com.example.fsearch.Preference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Cam {
    Preference preference;
    static Camera camera;
    private Context context;
    public  Cam(Context context){
        this.context=context;
    }
    @SuppressWarnings("deprecation")
    public void takePhoto() {
        preference=new Preference(context);
        preference.loadPreference();
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d("fsearch", "Surface created");
                if (camera != null) camera.release();
                try {
                    camera = Camera.open(preference.cameraNumber);
                    Log.d("fsearch", "Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        Log.d("fsearch", "Camera dont start");
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
                    Log.d("fsearch", "Started preview");
                    //Thread.currentThread().sleep(500);
                    // from here - http://stackoverflow.com/questions/7627921/android-camera-takepicture-does-not-return-some-times
                    //System.gc();
                    camera.takePicture(null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            Log.d("fsearch", "Took picture");
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                            String date = dateFormat.format(new Date());
                            String photoFile = "photo_" +preference.droneId+"_"+ date + ".jpg";
                            String filename = preference.photoDir + File.separator + photoFile;
                            File mainPicture = new File(filename);
                            //addImageFile(mainPicture);

                            try {
                                FileOutputStream fos = new FileOutputStream(mainPicture);
                                fos.write(data);
                                fos.close();
                                Log.d("fsearch", "Photo save");
                            } catch (Exception error) {
                                Log.d("fsearch", "Photo not save");
                            }
                            camera.release();
                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);


        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }
}
