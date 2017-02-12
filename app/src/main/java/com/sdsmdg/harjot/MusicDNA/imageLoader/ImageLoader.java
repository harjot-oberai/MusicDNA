package com.sdsmdg.harjot.MusicDNA.imageloader;

/**
 * Created by Harjot on 06-Apr-16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;

import com.sdsmdg.harjot.MusicDNA.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ImageLoader {

    MemoryCache memoryCache = new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Context ctx;
    public String type = "none";
    Random random;

    public ImageLoader(Context context) {
        ctx = context;
        fileCache = new FileCache(context);
        executorService = Executors.newFixedThreadPool(5);
        random = new Random();
    }

    final int stub_id = R.drawable.ic_default;

    public void DisplayImage(String url, ImageView imageView) {
        imageViews.put(imageView, url);
        Bitmap bitmap = null;
        if (type.equals("none"))
            bitmap = memoryCache.get(url);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            queuePhoto(url, imageView);
            if (type.equals("none"))
                imageView.setImageResource(stub_id);
        }
    }

    private void queuePhoto(String url, ImageView imageView) {
        PhotoToLoad p = new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }

    private Bitmap getBitmap(String url) {
        if (url == null) {
            return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_default);
        } else if (url.contains("all_playlist") || url.contains("folder")) {
            return null;
        } else if (url.contains("https")) {
            File f = fileCache.getFile(url);

            //from SD cache
            Bitmap b = decodeFile(f);
            if (b != null)
                return b;

            //from web
            try {
                Bitmap bitmap = null;
                URL imageUrl = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setInstanceFollowRedirects(true);
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(f);
                Utils.CopyStream(is, os);
                os.close();
                bitmap = decodeFile(f);
                return bitmap;
            } catch (Throwable ex) {
                ex.printStackTrace();
                if (ex instanceof OutOfMemoryError)
                    memoryCache.clear();
                return null;
            }
        } else {

            File f = fileCache.getFile(url);
            Bitmap b = decodeFile(f);
            if (b != null)
                return b;

            try {
                android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(url);
                Bitmap bitmap = null;

                byte[] data = mmr.getEmbeddedPicture();

                if (data != null) {
                    OutputStream os = new FileOutputStream(f);
                    os.write(data);
                    os.close();
//                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bitmap = decodeFile(f);
                    return bitmap;
                } else {
                    return null;
                }
            } catch (Throwable ex) {
                ex.printStackTrace();
                if (ex instanceof OutOfMemoryError)
                    memoryCache.clear();
                return null;
            }

        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 128;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    //Task for the queue
    private class PhotoToLoad {
        public String url;
        public ImageView imageView;

        public PhotoToLoad(String u, ImageView i) {
            url = u;
            imageView = i;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad) {
            this.photoToLoad = photoToLoad;
        }

        @Override
        public void run() {
            if (imageViewReused(photoToLoad))
                return;
            final Bitmap bmp = getBitmap(photoToLoad.url);
            if (type.contains("none") && photoToLoad.url != null)
                memoryCache.put(photoToLoad.url, bmp);
            if (imageViewReused(photoToLoad)) {
                return;
            }
            Activity a = (Activity) ctx;
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (imageViewReused(photoToLoad)) {
                        return;
                    }
                    if (bmp != null) {
                        photoToLoad.imageView.setImageBitmap(bmp);
                    } else {
                        if (type.equals("none"))
                            photoToLoad.imageView.setImageResource(stub_id);
                        else {
                            int r = random.nextInt(210) + 45;
                            int g = random.nextInt(210) + 45;
                            int b = random.nextInt(210) + 45;
                            photoToLoad.imageView.setImageResource(R.drawable.ic_record_3);
                            photoToLoad.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            photoToLoad.imageView.setImageTintList(ColorStateList.valueOf(Color.rgb(r, g, b)));
                        }
                    }
                }
            });
        }
    }

    boolean imageViewReused(PhotoToLoad photoToLoad) {
        String tag = imageViews.get(photoToLoad.imageView);
        if (tag == null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
