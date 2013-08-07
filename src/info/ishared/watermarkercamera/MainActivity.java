package info.ishared.watermarkercamera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.*;

public class MainActivity extends Activity {
    private Preview mPreview;
    private FrameLayout frame;
    SurfaceHolder mHolder;
    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new Preview(this);
        frame = new FrameLayout(this);
        frame.addView(mPreview);
        TextView text = new TextView(this);
        text.setTextSize(20);
        text.setText("lovehui");
        text.setHeight(30);
        text.setWidth(100);
        frame.addView(text);


        Button button =new Button(this);
        button.setText("Take");
        button.setLayoutParams(new LinearLayout.LayoutParams(140, 60));
        button.setPadding(100, 20, 0, 0);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(mPreview, null, null, mPreview);
            }
        });
        frame.addView(button);

        setContentView(frame);

    }
    class Preview extends SurfaceView implements SurfaceHolder.Callback ,Camera.PictureCallback,Camera.ShutterCallback{


        Preview(Context context) {
            super(context);
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException exception) {
                mCamera.release();
                mCamera = null;
                // TODO: add more exception handling logic here
            }

        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = mCamera.getParameters();
//	        parameters.setPreviewSize(w, h);
            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // data是一个原始的JPEG图像数据，
            //在这里我们可以存储图片，很显然可以采用MediaStore
            //注意保存图片后，再次调用startPreview()回到预览
            Uri imageUri =getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
            try {
                OutputStream os = getContentResolver().openOutputStream(imageUri);



                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
                Bitmap bitmap  = BitmapFactory.decodeByteArray(data, 0,data.length,opts);
                Bitmap newb = Bitmap.createBitmap(bitmap.getWidth(), 605, Bitmap.Config.ARGB_8888 );
                Canvas canvasTemp = new Canvas(newb);

                Paint p = new Paint();
                String familyName ="宋体";
                Typeface font = Typeface.create(familyName,Typeface.BOLD);
                p.setColor(Color.WHITE);
                p.setTypeface(font);

                p.setTextSize(48);

                Date dt=new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置显示格式
                String nowTime="";
                nowTime= df.format(dt);//
                canvasTemp.drawText(nowTime,10,20,p);
                canvasTemp.drawText("who am i", 10, 80, p);

                Bitmap newPic=combineBitmap(bitmap, newb);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                newPic.compress(Bitmap.CompressFormat.JPEG, 100, baos);



                os.write(baos.toByteArray());
                os.flush();
                os.close();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            camera.startPreview();
        }


        @Override
        public void onShutter() {
            Toast.makeText(MainActivity.this, "Click!", Toast.LENGTH_SHORT).show();
        }


        /**
         * 合并两张bitmap为一张
         * @param background
         * @param foreground
         * @return Bitmap
         */
        public  Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
            if (background == null) {
                return null;
            }
            int bgWidth = background.getWidth();
            int bgHeight = background.getHeight();
            int fgWidth = foreground.getWidth();
            int fgHeight = foreground.getHeight();
            Bitmap newmap = Bitmap .createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newmap);
            canvas.drawBitmap(background, 0, 0, null);
            canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2,
                    (bgHeight - fgHeight) / 2, null);
            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();
            return newmap;
        }
    }

}

// ----------------------------------------------------------------------

