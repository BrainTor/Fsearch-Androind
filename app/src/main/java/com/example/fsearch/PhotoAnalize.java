package com.example.fsearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PhotoAnalize {
    private Preference preference;
    private String fileName;
    public Bitmap bm;
    private int se[][];
    private static byte arr[][];
    private Context context;
    public double smokePercent;
    public int allPixels, smokePixels;

    public void loadPhoto(File file) {
        fileName = file.getName();
        preference = new Preference(context);
        preference.loadPreference();
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inMutable = true;
        try {
            bm = BitmapFactory.decodeStream(new FileInputStream(file), null, o2);
            o2.inBitmap = bm;
        } catch (Exception e) {
            Log.d("fsearch", "Photo is not correct:" + file.getName());
            return;
        }
        // cоздаем структурный элемент для морф открытия
        int w = bm.getWidth() / 300;
        int h = bm.getHeight() / 300;
        if (w <= 0 || h <= 0) {
            w = 3;
            h = 3;
        }
        se = new int[h][w];
        if (arr == null || arr.length != bm.getHeight() || arr[0].length != bm.getWidth())
            arr = new byte[bm.getHeight()][bm.getWidth()];
        else for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                arr[i][j] = 0;
            }
        }
    }

    public byte[] getPhoto() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 30, bos);
        byte[] photo;
        photo = bos.toByteArray();
        return photo;
    }

    public void erosion() {
        for (int i = se.length / 2; i < arr.length - se.length / 2; i++) {
            for (int j = se[0].length / 2; j < arr[i].length - se[0].length / 2; j++) {
                if (arr[i][j] == 0 || arr[i][j] == 3) {
                    for (int k = i - se.length / 2; k < i + se.length / 2; k++) {
                        for (int l = j - se[0].length / 2; l < j + se[0].length / 2; l++) {
                            if (arr[k][l] == 1 || arr[k][l] == 0)
                                arr[k][l] = (byte) (3 - arr[k][l]);
                        }
                    }
                }
            }
        }
        for (int i = se.length / 2; i < arr.length - se.length / 2; i++) {
            for (int j = se[0].length / 2; j < arr[i].length - se[0].length / 2; j++) {
                if (arr[i][j] == 3 || arr[i][j] == 2) {
                    arr[i][j] = 0;
                }
            }
        }
    }

    public void dilation() {
        for (int i = se.length / 2; i < arr.length - se.length / 2; i++) {
            for (int j = se[0].length / 2; j < arr[i].length - se[0].length / 2; j++) {
                if (arr[i][j] == 1) {
                    for (int k = i - se.length / 2; k < i + se.length / 2; k++) {
                        for (int l = j - se[0].length / 2; l < j + se[0].length / 2; l++) {
                            if (arr[k][l] == 0)
                                arr[k][l] = 2;
                        }
                    }
                }
            }
        }
    }

    public void saveAnalizedPhoto() {
        for (int i = 0; i < arr.length; i++)
            for (int j = 0; j < arr[0].length; j++)
                if (arr[i][j] > 0) bm.setPixel(j, i, 0);
        FileOutputStream out = null;
        OutputStream fOut = null;
        File file = new File(preference.photoDir + "_analized", fileName); // the File to save to
        try {
            out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public PhotoAnalize(Context context) {
        this.context = context;
    }

    public void analyze() {
        int sum = 0;
        int deviation = preference.deviation;
        for (int i = 0; i < bm.getHeight(); i++) {
            for (int j = 0; j < bm.getWidth(); j++) {
                int c = bm.getPixel(j, i);
                int r = Color.red(c) + preference.tuneRed;
                int g = Color.green(c) + preference.tuneGreen;
                int b = Color.blue(c) + preference.tuneBlue;
                r = r < 0 ? 0 : r;
                g = g < 0 ? 0 : g;
                b = b < 0 ? 0 : b;
                int avg = (r + g + b) / 3;
                if (80 <= avg && avg <= 220) {
                    if (Math.abs(r - g) < deviation && Math.abs(r - b) < deviation && Math.abs(g - b) < deviation) {
                        arr[i][j] = 1;
                    }
                }
            }
        }
        for (int a = 0; a < se.length; a++) {
            for (int b = 0; b < se[a].length; b++) {
                se[a][b] = 2;
            }
        }
        erosion();
        dilation();
        //считаем количество пикселей с дымом
        for (int i = 0; i < arr.length; i++)
            for (int j = 0; j < arr[i].length; j++)
                if (arr[i][j] > 0) sum++;

        if (preference.savedAnalizedPhotoBox) {
            saveAnalizedPhoto();
        }

        allPixels = bm.getHeight() * bm.getWidth();
        smokePixels = sum;
        smokePercent = 1.0 * sum * 100 / (bm.getHeight() * bm.getWidth());
    }

    public void close() {
        //здесь очищаем все
        bm.recycle();
        se = null;
        System.gc();

    }
}
