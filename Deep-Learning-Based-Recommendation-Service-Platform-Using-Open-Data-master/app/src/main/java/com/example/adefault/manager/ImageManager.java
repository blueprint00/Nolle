package com.example.adefault.manager;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.adefault.R;

import java.io.IOException;

import static com.example.adefault.util.RestApi.BASE_URL;

public class ImageManager {

    public static final int PICK_FROM_ALBUM = 1;
    AppCompatDialog progressDialog;


    private static ImageManager instance;

    private ImageManager() {
    }

    public static ImageManager getInstance() {
        if (instance == null)
            return instance = new ImageManager();
        return instance;
    }

    //                .override(340,220)
    public void GlideWithView(View view, ImageView iv2, String url) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions
                .fitCenter()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo);

        Glide.with(view)
                .load(url)
                .apply(requestOptions)
                .into(iv2);

    }

//                .override(340,220)

    public void GlideWithContext(Context context, ImageView iv, String url) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions
                .fitCenter()
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo);

        Glide.with(context)
                .load(url)
                .apply(requestOptions)
                .into(iv);
    }

    public String getFullImageString(String img_url) {
        String url = BASE_URL;
        if(img_url == null) {
            return url;
        }
        if (img_url.charAt(0) == '/') {  // 서버에서 주는 imgUrl 의 첫 부분이 / 로 시작할 때
            url = BASE_URL.substring(0, BASE_URL.length()-1); // BASE_URL의 마지막 부분의 / 을 자른다.
        }
        url = url + img_url;
        Log.d("urllllllllllllllllllll", url);
        return url;
    }

    public Bitmap getRotatedBitmap(String imgPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int exifDegree = ImageManager.getInstance().exifOrientationToDegrees(exifOrientation);

        bitmap = ImageManager.getInstance().rotate(bitmap, exifDegree);

        return bitmap;
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        int column_index = 0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToFirst();
        column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        Log.d("sssong:getPathFromURI", cursor.getString(column_index));
        return cursor.getString(column_index);
    }

    public Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree); // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    // 이미지 각도 조절
    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public void progressON(Activity activity, String message) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.progress_loading);
            progressDialog.show();
        }
        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void progressSET(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
