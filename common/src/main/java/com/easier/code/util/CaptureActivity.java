package com.easier.code.util;

import android.Manifest;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.clubank.common.R;
import com.clubank.device.BaseActivity;
import com.clubank.device.BaseCheckPermissionActivity;
import com.clubank.device.CamareAndCropActivity;
import com.clubank.domain.C;
import com.clubank.util.UI;
import com.easier.code.util.camera.CameraManager;
import com.easier.code.util.decoding.CaptureActivityHandler;
import com.easier.code.util.decoding.InactivityTimer;
import com.easier.code.util.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class CaptureActivity extends CamareAndCropActivity implements Callback {

    private CaptureActivityHandler handler;
    public ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    public InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private SurfaceHolder holder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.scan);
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        if (!getIntent().getBooleanExtra("showBottom", true)) {
            hide(R.id.bottom_layout);
        }
    }

    public void doWork(View view) {

        if (view.getId() == R.id.local_photos) {
            choosePicture(this, C.REQUEST_FROM_PICTURE);
        } else if (view.getId() == R.id.open_light) {
            flashHandler();
        }

    }

    /**
     * 是否打开闪光灯
     * 提供給其他activity調用
     */
    public void flashHandler() {
        //camera.startPreview();
        CameraManager cameraManager = CameraManager.get();
        if (cameraManager != null && cameraManager.camera != null) {
            Camera.Parameters parameters = cameraManager.camera.getParameters();

            // 判断闪光灯当前状态來修改
            if (Camera.Parameters.FLASH_MODE_OFF.equals(parameters.getFlashMode())) {
                turnOn(parameters);
            } else if (Camera.Parameters.FLASH_MODE_TORCH.equals(parameters.getFlashMode())) {
                turnOff(parameters);
            }
        }
    }

    /**
     * 打开闪光灯
     */
    private void turnOn(Camera.Parameters parameters) {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        CameraManager.get().camera.setParameters(parameters);
    }


    /**
     * 关闭闪光灯
     */
    private void turnOff(Camera.Parameters parameters) {
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        CameraManager.get().camera.setParameters(parameters);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String fname = settings.getString("fname", "");
        Bitmap large = null;
        Uri uri = null;
        if (requestCode == C.REQUEST_FROM_CAMERA) {// 照相后剪裁
            File file = new File(fname);
            if (file.exists()) {
                uri = Uri.fromFile(file);
            }
        } else if (requestCode == C.REQUEST_FROM_PICTURE) {// 选取图片后剪裁
            if (data != null) {
                uri = data.getData();
            }
        }
        if (uri != null) {
            try {
                large = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int[] intArray = new int[large.getWidth() * large.getHeight()];

            large.getPixels(intArray, 0, large.getWidth(), 0, 0, large.getWidth(), large.getHeight());
            LuminanceSource source = new RGBLuminanceSource(large.getWidth(), large.getHeight(), intArray);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));


            Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
            QRCodeReader reader = new QRCodeReader();

            try {
                Result re = reader.decode(bitmap, hints);
                handleDecode(re, large);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {

            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception ioe) {
            UI.showToast(this, "请开启拍照权限");
            Log.e("CAMERA", "initCamera: ", ioe);
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.holder = holder;
        if (BaseCheckPermissionActivity.isNeedCheckPermission) {//检测相机权限是否开启
            checkAllNeedPermissions();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        viewfinderView.drawResultBitmap(barcode);
        playBeepSoundAndVibrate();
        Intent intent = new Intent();
        intent.putExtra("RESULT", obj.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    public void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    protected String[] getNeedPermissions() {
        return new String[]{Manifest.permission.CAMERA};
    }

    @Override
    protected void permissionGrantedSuccess() {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    /**
     * 使Zxing能够继续扫描
     */
    public void continuePreview() {
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }
}