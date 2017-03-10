package com.sdsmdg.harjot.MusicDNA.utilities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Harjot on 18-Jan-17.
 */

public class FileUtils {

    public static void shareLocalSong(String path, Context ctx) {
        Uri contentUri = Uri.parse("file:///" + path);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("audio/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            ctx.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    public static void saveBitmapAsImage(Bitmap bmp, String fileName) {
        String path = Environment.getExternalStorageDirectory().toString() + "/SavedDNAs/";
        File f = new File(path);
        f.mkdirs();
        OutputStream fOut = null;
        File file = new File(path, fileName + ".png");
        try {
            fOut = new FileOutputStream(file);
            Bitmap pictureBitmap = bmp;
            pictureBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        } catch (FileNotFoundException e) {
            Log.e("DNA Save ERROR", e.getMessage());
            e.printStackTrace();
        }
        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void shareBitmapAsImage(Bitmap bmp, String fileName, Context ctx) {
        try {
            File cachePath = new File(ctx.getCacheDir(), "images");
            if (cachePath.exists())
                deleteRecursive(cachePath);
            cachePath.mkdirs(); // don'timer forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/" + fileName + ".png"); // overwrites this image every time
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(ctx.getCacheDir(), "images");
        File newFile = new File(imagePath, fileName + ".png");
        Uri contentUri = FileProvider.getUriForFile(ctx, "com.sdsmdg.harjot.MusicDNA.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, ctx.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            ctx.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }

    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

}
