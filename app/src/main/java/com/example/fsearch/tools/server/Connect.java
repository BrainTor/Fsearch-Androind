package com.example.fsearch.tools.server;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.example.fsearch.Preference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import retrofit2.Call;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Connect {
    Coordinates coordinates;
    Fire fire;
    int fireRate;
    Location location;
    byte[] photo;
    Preference preference;
    Boolean answer;
    Context context;
    CoordinateService serviceCoordinate;
    FireService serviceFire;

    public Connect(Context context) {
        this.context = context;
        GsonBuilder builder = new GsonBuilder();
// Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        Gson gson = builder.create();

        preference=new Preference(context);
        preference.loadPreference();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(preference.baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        serviceCoordinate = retrofit.create(CoordinateService.class);
        serviceFire = retrofit.create(FireService.class);
    }

    public void sendCoordinate(Location location) {
        CoordinateThread coordinateThread = new CoordinateThread();
        this.location=location;
        coordinateThread.start();
        Log.d("fsearch","send coordinates to server");
    }
    public void sendFire(Location location,int fireRate,byte[] photo) {
        this.location=location;
        this.fireRate=fireRate;
        this.photo=photo;
        FireThread fireAsyncTask=new FireThread();
        fireAsyncTask.start();
        Log.d("fsearch","send fire to server");
    }
    public class CoordinateThread extends Thread {

        public void run() {
            if (location==null) {
                Log.d("fsearch","Into CoordinateThread location is null! ");
                return;
            }
            preference = new Preference(context);
            preference.loadPreference();
            coordinates = new Coordinates(0, 0, new Date(), location.getLatitude(),
                    location.getLongitude(), location.getAltitude(),Double.valueOf(location.getSpeed()));
            Call<Boolean> call = serviceCoordinate.sendCoordinate(coordinates,preference.droneId);
            try {
                Response<Boolean> userResponse = call.execute();
//                Log.w("fsearch","Retrofit@Response"+ userResponse.body().toString());
//                answer = userResponse.body();
            } catch (Exception e) {
                // e.printStackTrace();
                Log.d("fsearch","Problem with send coordinates or result parse :"+ e.getMessage());
            }
        }



    }
    public class FireThread extends Thread {
        @Override
        public void run() {
            if (location==null) {
                Log.d("fsearch","Into FireThread location is null! :");
                return;
            }
            preference = new Preference(context);
            preference.loadPreference();
            fire = new Fire(0, 0, fireRate,location.getLatitude(),
                    location.getLongitude(), new Date(),photo);
            Call<Boolean> call = serviceFire.sendFire(fire,preference.droneId);
            try {
                Response<Boolean> userResponse = call.execute();
//                answer = userResponse.body();
            } catch (Exception e) {
//                e.printStackTrace();
                Log.d("fsearch","Problem with send fire or result parse:"+ e.getMessage());
            }
        }



    }
}