package com.maodq.imguploaddemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.maodq.imguploaddemo.utils.BitmapHelper;

import java.io.File;
import java.io.IOException;


/**
 * 上传头像功能封装
 * 必须在使用此adapter页面监听欧尼ActivityResult方法，执行adapter.onActivityResult(requestCode, resultCode, data)
 * Created by maodq on 16/4/29.
 */
public class UploadImgAdapter {
    // 可自定义文件目录名
    private static final String ROOT_NAME = "UPLOAD_CACHE";
    private final String TAG = "UploadImgAdapter";

    private Activity mActivity;
    private File mTempCameraFile;
    private File mTempCropFile;
    public static final int MODE_HEAD = 0;
    public static final int MODE_IMG = 1;


    private static final int ALBUM_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;
    private static final int CROP_REQUEST_CODE = 4;
    Action2 mOnImageUpdateListener;
    private ProgressDialog mProgressDialog;
    private int mMode = -1;
    private QuickOptionDialog mDialog;

    public UploadImgAdapter(Activity activity, Action2 l) {
        mActivity = activity;
        mOnImageUpdateListener = l;
        mDialog = new QuickOptionDialog(mActivity);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnQuickOptionformClickListener(new QuickOptionDialog.OnQuickOptionFormClick() {
            @Override
            public void onQuickOptionClick(View v) {
                clickQuickOption(v);
            }
        });
    }

    public UploadImgAdapter showDialog() {
        if (mMode == -1)
            throw new IllegalStateException("请通过withMode(mode)方法设置执行图片模式");
        mDialog.show();
        return this;
    }

    public UploadImgAdapter withMode(int mode) {
        mMode = mode;
        return this;
    }

    // 头像dialog点击监听器
    private void clickQuickOption(View v) {
        switch (v.getId()) {
            case R.id.tv_photograph:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempCameraFile()));
                mActivity.startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                break;
            case R.id.tv_select:
//                Intent albumIntent = new Intent(Intent.ACTION_PICK); // 老版本的相册
//                albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
                Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                mActivity.startActivityForResult(albumIntent, ALBUM_REQUEST_CODE);
                break;
            case R.id.tv_cancel:
                // 默认不做处理也会自动关闭dialog
            default:
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:       // 照相机返回结果
                    if (mMode == MODE_IMG) {
                        File file = composBitmap(mTempCameraFile);
                        sendImage(file);
                    } else if (mMode == MODE_HEAD) {
                        // 头像宽高为65dp,对应xxxhdpi为260px
                        startCrop(Uri.fromFile(mTempCameraFile), 260, 260);
                    }
                    break;
                case ALBUM_REQUEST_CODE:        // 相册返回结果
                    Uri uri = data.getData();
                    if (mMode == MODE_IMG) {
                        File file = BitmapHelper.decodeUriAsFile(mActivity, uri);
                        file = composBitmap(file);
                        sendImage(file);
                    } else if (mMode == MODE_HEAD) {
                        startCrop(uri, 260, 260);
                    }
                    break;
                case CROP_REQUEST_CODE:         // 裁剪返回结果
                    sendImage(getTempCropFile());
                    break;
            }
        }
    }

    /**
     * 开始裁剪
     * 附加选项	   数据类型	    描述
     * crop	        String	    发送裁剪信号
     * aspectX	    int	        X方向上的比例
     * aspectY	    int	        Y方向上的比例
     * outputX	    int	        裁剪区的宽
     * outputY	    int	        裁剪区的高
     * scale	    boolean	    是否保留比例
     * return-data	boolean	    是否将数据保留在Bitmap中返回
     * data	        Parcelable	相应的Bitmap数据
     * circleCrop	String	    圆形裁剪区域？
     * MediaStore.EXTRA_OUTPUT ("output")	URI	将URI指向相应的file:///...
     *
     * @param uri uri
     */
    private void startCrop(Uri uri, int outputX, int outputY) {
        Bitmap bitmap = null;
        String str = "";

        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            str = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null || TextUtils.isEmpty(str)) {
            Toast.makeText(mActivity, "找不到此图片", Toast.LENGTH_SHORT).show();
            return;
        }
        uri = Uri.parse(str);

        Intent intent = new Intent("com.android.camera.action.CROP"); //调用Android系统自带的一个图片剪裁页面
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");//进行修剪
        // aspectX aspectY 是宽高的比例
        if (outputX == outputY) {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        } else {
            intent.putExtra("scale", true);
        }
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempCropFile()));
        mActivity.startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    private ContentResolver getContentResolver() {
        return mActivity.getContentResolver();
    }

    private File getTempCropFile() {
        if (mTempCropFile == null) {
            mTempCropFile = getTempMediaFile();
        }
        return mTempCropFile;
    }

    private File getTempCameraFile() {
        if (mTempCameraFile == null)
            mTempCameraFile = getTempMediaFile();
        return mTempCameraFile;
    }

    private File composBitmap(File file) {
        Bitmap bitmap = BitmapHelper.revisionImageSize(file);
        return BitmapHelper.saveBitmap2file(mActivity, bitmap);
    }

    private void sendImage(final File file) {
        if (file == null) {
            Toast.makeText(mActivity, "找不到此图片", Toast.LENGTH_SHORT).show();
            return;
        }
        // 可以在这里统一进行联网上传操作，把上传结果传回，也可以直接传file交给原页面处理
        mOnImageUpdateListener.call(file, mMode);
    }

    /**
     * 原项目用的RxJava的Action2
     */
    public interface Action2 {
        void call(File file, int mode);
    }

    /**
     * 获取相机的file
     */
    public File getTempMediaFile() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String fileName = getTempMediaFileName();
            file = new File(fileName);
        }
        return file;
    }

    public String getTempMediaFileName() {
        return getParentPath() + "image" + System.currentTimeMillis() + ".jpg";
    }

    private String getParentPath() {
        String parent = Environment.getExternalStorageDirectory()
                + File.separator + ROOT_NAME + File.separator;
        mkdirs(parent);
        return parent;
    }

    public boolean mkdirs(String path) {
        File file = new File(path);
        return !file.exists() && file.mkdirs();
    }
}
