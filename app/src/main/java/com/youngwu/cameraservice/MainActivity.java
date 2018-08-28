package com.youngwu.cameraservice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.faucamp.simplertmp.RtmpHandler;
import com.serenegiant.UVCCameraView;
import com.serenegiant.UVCPublisher;
import com.serenegiant.dialog.MessageDialogFragment;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.UVCCamera;
import com.serenegiant.utils.BuildCheck;
import com.serenegiant.utils.PermissionCheck;

import net.ossrs.yasea.SrsEncodeHandler;
import net.ossrs.yasea.SrsRecordHandler;

import java.io.IOException;
import java.net.SocketException;

public class MainActivity extends Activity implements MessageDialogFragment.MessageDialogListener, CameraDialog.CameraDialogParent {
    private UVCCameraView uvcCameraView;
    private Button btn_start;

    private USBMonitor usbMonitor;
    private UVCCamera uvcCamera;
    private UVCPublisher uvcPublisher;
    private static final String RTMP_URL = "这里换成自己的推流地址";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        uvcCameraView = (UVCCameraView) findViewById(R.id.uvcCameraView);
        btn_start = (Button) findViewById(R.id.btn_start);
    }

    private void initData() {
        btn_start.setOnClickListener(clickListener);

        usbMonitor = new USBMonitor(this, deviceConnectListener);

        uvcPublisher = new UVCPublisher(uvcCameraView);
        uvcPublisher.setEncodeHandler(new SrsEncodeHandler(srsEncodeListener));
        uvcPublisher.setRtmpHandler(new RtmpHandler(rtmpListener));
        uvcPublisher.setRecordHandler(new SrsRecordHandler(srsRecordListener));
        uvcPublisher.setPreviewResolution(UVCCamera.DEFAULT_PREVIEW_WIDTH, UVCCamera.DEFAULT_PREVIEW_HEIGHT);
        uvcPublisher.setScreenOrientation(Configuration.ORIENTATION_LANDSCAPE);
        uvcPublisher.setOutputResolution(UVCCamera.DEFAULT_PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_WIDTH);
        uvcPublisher.setVideoHDMode();
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == btn_start) {
                if (uvcCamera == null) {
                    CameraDialog.showDialog(MainActivity.this);
                } else {
                    uvcPublisher.stopCamera();
                }
            }
        }
    };

    private USBMonitor.OnDeviceConnectListener deviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "onAttach已关联", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDettach(UsbDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "onDettach失去关联", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "onConnect已连接", Toast.LENGTH_LONG).show();
                }
            });
            uvcPublisher.stopCamera();
            uvcCamera = uvcPublisher.startCamera(ctrlBlock);
            if (uvcCamera == null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "摄像头打开失败", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
            uvcPublisher.startPublish(RTMP_URL);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "摄像头打开成功，开始推流", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onDisconnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "onDisconnect断开连接", Toast.LENGTH_LONG).show();
                }
            });
            uvcPublisher.stopCamera();
        }

        @Override
        public void onCancel(UsbDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "onCancel已取消", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private RtmpHandler.RtmpListener rtmpListener = new RtmpHandler.RtmpListener() {
        @Override
        public void onRtmpConnecting(String msg) {

        }

        @Override
        public void onRtmpConnected(String msg) {

        }

        @Override
        public void onRtmpVideoStreaming() {

        }

        @Override
        public void onRtmpAudioStreaming() {

        }

        @Override
        public void onRtmpStopped() {

        }

        @Override
        public void onRtmpDisconnected() {

        }

        @Override
        public void onRtmpVideoFpsChanged(double fps) {

        }

        @Override
        public void onRtmpVideoBitrateChanged(double bitrate) {

        }

        @Override
        public void onRtmpAudioBitrateChanged(double bitrate) {

        }

        @Override
        public void onRtmpSocketException(SocketException e) {

        }

        @Override
        public void onRtmpIOException(IOException e) {

        }

        @Override
        public void onRtmpIllegalArgumentException(IllegalArgumentException e) {

        }

        @Override
        public void onRtmpIllegalStateException(IllegalStateException e) {

        }
    };

    private SrsRecordHandler.SrsRecordListener srsRecordListener = new SrsRecordHandler.SrsRecordListener() {
        @Override
        public void onRecordPause() {

        }

        @Override
        public void onRecordResume() {

        }

        @Override
        public void onRecordStarted(String msg) {

        }

        @Override
        public void onRecordFinished(String msg) {

        }

        @Override
        public void onRecordIllegalArgumentException(IllegalArgumentException e) {

        }

        @Override
        public void onRecordIOException(IOException e) {

        }
    };

    private SrsEncodeHandler.SrsEncodeListener srsEncodeListener = new SrsEncodeHandler.SrsEncodeListener() {
        @Override
        public void onNetworkWeak() {

        }

        @Override
        public void onNetworkResume() {

        }

        @Override
        public void onEncodeIllegalArgumentException(IllegalArgumentException e) {
            Log.e(MainActivity.class.getSimpleName(), e.toString());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        usbMonitor.register();
        uvcPublisher.startPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        uvcPublisher.resumeRecord();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uvcPublisher.pauseRecord();
    }

    @Override
    protected void onStop() {
        super.onStop();
        uvcPublisher.stopPreview();
        usbMonitor.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uvcPublisher.stopPublish();
        uvcPublisher.stopRecord();
        usbMonitor.unregister();
    }

    //================================================================================

    /**
     * MessageDialogFragmentメッセージダイアログからのコールバックリスナー
     *
     * @param dialog
     * @param requestCode
     * @param permissions
     * @param result
     */
    @SuppressLint("NewApi")
    @Override
    public void onMessageDialogResult(final MessageDialogFragment dialog, final int requestCode, final String[] permissions, final boolean result) {
        if (result) {
            // メッセージダイアログでOKを押された時はパーミッション要求する
            if (BuildCheck.isMarshmallow()) {
                requestPermissions(permissions, requestCode);
                return;
            }
        }
        // メッセージダイアログでキャンセルされた時とAndroid6でない時は自前でチェックして#checkPermissionResultを呼び出す
        for (final String permission : permissions) {
            checkPermissionResult(requestCode, permission, PermissionCheck.hasPermission(this, permission));
        }
    }

    /**
     * パーミッション要求結果を受け取るためのメソッド
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);    // 何もしてないけど一応呼んどく
        final int n = Math.min(permissions.length, grantResults.length);
        for (int i = 0; i < n; i++) {
            checkPermissionResult(requestCode, permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /**
     * パーミッション要求の結果をチェック
     * ここではパーミッションを取得できなかった時にToastでメッセージ表示するだけ
     *
     * @param requestCode
     * @param permission
     * @param result
     */
    protected void checkPermissionResult(final int requestCode, final String permission, final boolean result) {
        // パーミッションがないときにはメッセージを表示する
        if (!result && (permission != null)) {
            if (Manifest.permission.RECORD_AUDIO.equals(permission)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, R.string.permission_audio, Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, R.string.permission_ext_storage, Toast.LENGTH_LONG).show();
                    }
                });
            }
            if (Manifest.permission.INTERNET.equals(permission)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, R.string.permission_network, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    // 動的パーミッション要求時の要求コード
    protected static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x12345;
    protected static final int REQUEST_PERMISSION_AUDIO_RECORDING = 0x234567;
    protected static final int REQUEST_PERMISSION_NETWORK = 0x345678;
    protected static final int REQUEST_PERMISSION_CAMERA = 0x537642;

    /**
     * 外部ストレージへの書き込みパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true 外部ストレージへの書き込みパーミッションが有る
     */
    protected boolean checkPermissionWriteExternalStorage() {
        if (!PermissionCheck.hasWriteExternalStorage(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                    R.string.permission_title, R.string.permission_ext_storage_request,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
            return false;
        }
        return true;
    }

    /**
     * 録音のパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true 録音のパーミッションが有る
     */
    protected boolean checkPermissionAudio() {
        if (!PermissionCheck.hasAudio(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_AUDIO_RECORDING,
                    R.string.permission_title, R.string.permission_audio_recording_request,
                    new String[]{Manifest.permission.RECORD_AUDIO});
            return false;
        }
        return true;
    }

    /**
     * ネットワークアクセスのパーミッションが有るかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true ネットワークアクセスのパーミッションが有る
     */
    protected boolean checkPermissionNetwork() {
        if (!PermissionCheck.hasNetwork(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_NETWORK,
                    R.string.permission_title, R.string.permission_network_request,
                    new String[]{Manifest.permission.INTERNET});
            return false;
        }
        return true;
    }

    /**
     * カメラアクセスのパーミッションがあるかどうかをチェック
     * なければ説明ダイアログを表示する
     *
     * @return true カメラアクセスのパーミッションが有る
     */
    protected boolean checkPermissionCamera() {
        if (!PermissionCheck.hasCamera(this)) {
            MessageDialogFragment.showDialog(this, REQUEST_PERMISSION_CAMERA,
                    R.string.permission_title, R.string.permission_camera_request,
                    new String[]{Manifest.permission.CAMERA});
            return false;
        }
        return true;
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return usbMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {

    }
}
