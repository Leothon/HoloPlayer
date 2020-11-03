package com.holo.holoplayer.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.holo.holoplayer.databinding.ActivityCameraBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * NV21:
 * camera通过setPreviewCallBack回调的数据格式事NV21，而x264编码输入格式是I420
 * camera采集的数据需要进行转化，从NV21到I420
 * YUV，由三个分量组成的视频格式，Y表示视频的亮度，UV表示色度，浓度
 */
public class CameraActivity extends AppCompatActivity {


    public static void start(Context context) {
        context.startActivity(new Intent(context,CameraActivity.class));
    }

    private ActivityCameraBinding mBinding;

    private String[] permissions = {Manifest.permission.CAMERA};

    private static final int BACK_CAMERA = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int Front_CAMERA = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Camera camera;

    private Camera.Parameters parameters;

    private SurfaceHolder surfaceHolder;

    private int picWidth = 2160;
    private int picHeight = 3840;
    private Camera.CameraInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        checkPermission(permissions);
    }

    private void openCamera(int cameraFacing) {
        info = new Camera.CameraInfo();
        if (checkCameraHardware(this)) {
            int cameraCount = Camera.getNumberOfCameras();
            if (cameraCount < 2) {
                // 单个摄像头
                //  切换摄像头按钮隐藏
                mBinding.switchCamera.setVisibility(View.INVISIBLE);
            }
            camera = Camera.open(cameraFacing);
            initParameter(camera);
        } else {
            Toast.makeText(this,"未找到相机",Toast.LENGTH_SHORT).show();
        }
    }

    private void initParameter(Camera camera) {
        parameters = camera.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21); // 设置预览数据的格式
        Camera.Size previewSize = getPreviewSize(mBinding.previewSurface.getWidth(),mBinding.previewSurface.getHeight(),parameters.getSupportedPreviewSizes());
        if (previewSize != null) {
            parameters.setPreviewSize(previewSize.width,previewSize.height);
        }
        Camera.Size saveSize = getPreviewSize(picWidth,picHeight,parameters.getSupportedVideoSizes());
        if (saveSize != null) {
            parameters.setPictureSize(saveSize.width,saveSize.height);
        }

        camera.setParameters(parameters);
    }

    private Camera.Size getPreviewSize(int surfaceWidth, int surfaceHeight, List<Camera.Size> sizeList) {
        Camera.Size previewSize = null;
        float previewRatio = (surfaceHeight / surfaceWidth);
        float minDiff = previewRatio;

        for (Camera.Size size : sizeList) {
            if (size.width == surfaceHeight && size.height == surfaceWidth) {
                previewSize = size;
                break;
            }
            float supportRatio = size.width / size.height;
            if (Math.abs(supportRatio - previewRatio) < minDiff) {
                minDiff = Math.abs(supportRatio - previewRatio);
                previewSize = size;
            }
        }
        return previewSize;
    }

    private void startPreview() throws IOException {
        if (camera != null) {
            //camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewTexture(mBinding.previewTextureView.getSurfaceTexture());
            camera.startPreview();
        }
    }

    private void initCamera() {

        mBinding.previewTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull final SurfaceTexture surfaceTexture, int i, int i1) {
                if (camera == null) {
                    openCamera(BACK_CAMERA);
                }
                try {
                    startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                camera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, Camera camera) {
                        Log.e("==相机==","change " + bytes);
                    }
                });

            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                releaseCamera();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });



//        surfaceHolder = mBinding.previewSurface.getHolder();
//        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
//                if (camera == null) {
//                    openCamera(BACK_CAMERA);
//                }
//                try {
//                    startPreview();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//
//                camera.setPreviewCallback(new Camera.PreviewCallback() {
//                    @Override
//                    public void onPreviewFrame(byte[] bytes, Camera camera) {
//                        Log.e("相机","change " + bytes);
//                    }
//                });
//            }
//
//            @Override
//            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
//                releaseCamera();
//            }
//        });


    }



    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void checkPermission(String[] permissions) {

        List<String> mPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissions.add(permission);
            }
        }
        if (!mPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions, 100);
        } else {
            permissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            permissionGranted();
        }
    }

    private void permissionGranted() {
        initCamera();
//        mBinding.switchCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (info.facing == Front_CAMERA && camera != null) {
//                    releaseCamera();
//                    openCamera(BACK_CAMERA);
//                } else if (info.facing == BACK_CAMERA && camera != null){
//                    releaseCamera();
//                    openCamera(Front_CAMERA);
//                }
//            }
//        });
    }

}