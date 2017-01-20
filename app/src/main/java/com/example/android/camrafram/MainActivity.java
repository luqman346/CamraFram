package com.example.android.camrafram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "error";
        ImageView imageView;
        static final int CAM_REQUEST = 1;
        Date curentTime;

        // Storage Permissions
        private static final int REQUEST_EXTERNAL_STORAGE = 1;
        private static String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            verifyStoragePermissions(this);

            imageView = (ImageView)findViewById(R.id.imageView2);
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = getFile();
            camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(camera_intent,CAM_REQUEST);

        }

    private File getFile()
    {
        curentTime = new Date();
        String root = Environment.getExternalStorageDirectory().toString();
        File folder = new File(root + "/Blue_&_Gold_Perade");
        if (!folder.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }

        File image_file = new File(folder,"selfie_cam"+curentTime+".jpg");
        return image_file;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        setAndSaveImageWithOverlay(getBitmapOfSnappedImage());
    }

    public Bitmap getBitmapOfSnappedImage(){


        String root = Environment.getExternalStorageDirectory().toString();
        String path = root + "/Blue_&_Gold_Perade"+     "/selfie_cam"+curentTime+".jpg";

        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap =     BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
        return bitmap;
    }

    public void setAndSaveImageWithOverlay(Bitmap snappedImage){
        Bitmap b = Bitmap.createBitmap(snappedImage.getWidth(),     snappedImage.getHeight(), Bitmap.Config.ARGB_8888);

        //the overlay png file from drawable folder
        Bitmap overlay = BitmapFactory.decodeResource(getResources(),     R.mipmap.ic_launcher);
        overlay =     Bitmap.createScaledBitmap(overlay,snappedImage.getWidth(),snappedImage.getHeight    (),false);

        //create canvas with a clean bitmap
        Canvas canvas = new Canvas(b);
        //draw the snappedImage on the canvas
        canvas.drawBitmap(snappedImage, 0, 0, new Paint());
        //draw the overlay on the canvas
        canvas.drawBitmap(overlay, 0, 0, new Paint());

        imageView.setImageBitmap(b);

        SaveImage(b);
    }


    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Blue_&_Gold_Perade");
        myDir.mkdirs();
        String fname = "selfie_cam"+curentTime+".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}