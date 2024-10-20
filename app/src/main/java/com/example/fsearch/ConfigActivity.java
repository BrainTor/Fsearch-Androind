package com.example.fsearch;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ConfigActivity extends AppCompatActivity {
    EditText tuneRed;
    EditText tuneGreen;
    EditText tuneBlue;
    EditText deviation;
    EditText phone;
    EditText port;
    EditText MMSC;
    EditText proxy;
    CheckBox smsBox;
    CheckBox cameraBox;
    CheckBox mmsBox;
    CheckBox flagBox;
    CheckBox savedAnalizedPhotoBox;
    EditText MMSnumber;
    EditText photoDir;
    EditText SMSC;
    EditText droneId;
    EditText baseURL;
    EditText cameraNumber;
    Preference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);
        preference = new Preference(this);
        cameraNumber = (EditText) findViewById(R.id.cameraNumber);
        cameraBox = (CheckBox) findViewById(R.id.cameraBox);
        droneId = (EditText) findViewById(R.id.droneId);
        baseURL = (EditText) findViewById(R.id.baseURL);
        phone = (EditText) findViewById(R.id.phone);
        SMSC = (EditText) findViewById(R.id.SMSC);
        port = (EditText) findViewById(R.id.port);
        MMSC = (EditText) findViewById(R.id.MMSC);
        proxy = (EditText) findViewById(R.id.proxy);
        MMSnumber = (EditText) findViewById(R.id.MMSnumber);
        savedAnalizedPhotoBox=(CheckBox)findViewById(R.id.savedAnalizedPhotoBox) ;
        smsBox = (CheckBox) findViewById(R.id.smsBox);
        mmsBox = (CheckBox) findViewById(R.id.mmsBox);
        flagBox = (CheckBox) findViewById(R.id.flagBox);
        tuneRed = (EditText) findViewById(R.id.tun_red);
        tuneBlue = (EditText) findViewById(R.id.tun_blue);
        tuneGreen = (EditText) findViewById(R.id.tun_green);
        deviation = (EditText) findViewById(R.id.deviation);
        photoDir = (EditText) findViewById(R.id.photoDir);

        loadConfig();
    }

    public void save(View view) {
        saveConfig();
    }

    void saveConfig() {
        preference.cameraBox = cameraBox.isChecked();
        preference.droneId = droneId.getText().toString();
        preference.baseURL = baseURL.getText().toString();
        preference.SMSnumber = phone.getText().toString();
        preference.SMSC = SMSC.getText().toString();
        preference.MMSC = MMSC.getText().toString();
        preference.MMSport = port.getText().toString();
        preference.MMSproxy = proxy.getText().toString();
        preference.MMSnumber = MMSnumber.getText().toString();
        try {
            preference.cameraNumber = Integer.parseInt(cameraNumber.getText().toString());
            preference.tuneRed = Integer.parseInt(tuneRed.getText().toString());
            preference.tuneBlue = Integer.parseInt(tuneBlue.getText().toString());
            preference.tuneGreen = Integer.parseInt(tuneGreen.getText().toString());
            preference.deviation = Integer.parseInt(deviation.getText().toString());
        } catch (Exception e) {
            Log.e("Fsearch", "Incorrect tune Paramaters");
        }
        preference.savedAnalizedPhotoBox =savedAnalizedPhotoBox.isChecked();
        preference.smsBox = smsBox.isChecked();
        preference.mmsBox = mmsBox.isChecked();
        preference.flagBox = flagBox.isChecked();
        preference.photoDir = photoDir.getText().toString();
        preference.savePreference();
        Toast.makeText(this, getResources().getText(R.string.config_saved), Toast.LENGTH_SHORT).show();
    }

    void loadConfig() {
        preference.loadPreference();
        cameraBox.setChecked(preference.cameraBox);
        cameraNumber.setText(Integer.toString(preference.cameraNumber));
        droneId.setText(preference.droneId);
        baseURL.setText(preference.baseURL);
        phone.setText(preference.SMSnumber);
        SMSC.setText(preference.SMSC);
        MMSC.setText(preference.MMSC);
        port.setText(preference.MMSport);
        proxy.setText(preference.MMSproxy);
        MMSnumber.setText(preference.MMSnumber);
        tuneRed.setText(Integer.toString(preference.tuneRed));
        tuneBlue.setText(Integer.toString(preference.tuneBlue));
        tuneGreen.setText(Integer.toString(preference.tuneGreen));
        deviation.setText(Integer.toString(preference.deviation));
        savedAnalizedPhotoBox.setChecked(preference.savedAnalizedPhotoBox);
        smsBox.setChecked(preference.smsBox);
        mmsBox.setChecked(preference.mmsBox);
        flagBox.setChecked(preference.flagBox);
        photoDir.setText(preference.photoDir);
        Toast.makeText(ConfigActivity.this, "Настройки Загружены", Toast.LENGTH_SHORT).show();
    }

    public void startService(View view) {
        Intent i = new Intent(this, AnalizeService.class);
        startService(i);
        Log.d("fsearch", "Service started");
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    public void stopService(View view) {
        Intent i = new Intent(this, AnalizeService.class);
        stopService(i);
        Log.d("fsearch", "Service stoped");
        Toast.makeText(this, "Service stoped", Toast.LENGTH_SHORT).show();
    }

    public void goStatistic(View view) {
        Intent i = new Intent(this, LogActivity.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && data != null) {
                Uri selectedImage = data.getData();
                String fileName=preference.photoDir+"_demo/";
                saveFile(selectedImage,fileName);
            }
        }
    }


    void saveFile(Uri sourceuri, String destinationFile)
    {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(sourceuri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        destinationFile=destinationFile+"/"+new File(filePath).getName();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            Log.d("fsearch",filePath+" " +destinationFile+" ");
            bis = new BufferedInputStream(new FileInputStream(filePath));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFile));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {

            }
        }
    }


    public void openDemoPhoto(View view) {
        File demoPhotoDir;
        demoPhotoDir = new File(preference.photoDir + "_demo");
        if (!demoPhotoDir.exists()) {
            Log.d("fsearch", "Create photo dir: " + preference.photoDir + "_demo");
            demoPhotoDir.mkdirs();
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);

    }
}
