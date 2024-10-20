package com.example.fsearch;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Map;

public class LogActivity extends AppCompatActivity {
    TextView AllConfig;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        AllConfig=(TextView)findViewById(R.id.allConging);
        loadConfig();
    }
    void loadConfig() {
        sp = getSharedPreferences("ConfigPreference", MODE_PRIVATE);
        Map<String,?> keys = sp.getAll();
        String str="";

        for(Map.Entry<String,?> entry : keys.entrySet()){
            str+=entry.getKey() + ": " + entry.getValue().toString()+"\n";
        }
        AllConfig.setText(str);
    }
}
