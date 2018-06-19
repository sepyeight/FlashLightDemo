package com.example.elaine.controllerviewdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

@SuppressLint("NewApi")
public class MainActivity extends AppCompatActivity {

    private final static String LOGTAG = "FlashLightActivity";

    private CameraManager manager = null;
    private Camera m_camera = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView imageView = findViewById(R.id.imageView);
        final ToggleButton button = findViewById(R.id.toggleButton);

        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{
            String[] cameraList = manager.getCameraIdList();
            for(String str: cameraList){

            }
        }catch (CameraAccessException e){
            Log.e(LOGTAG, e.getMessage());
        }
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                button.setChecked(isChecked);
                imageView.setImageResource(isChecked ? R.drawable.bulb_on : R.drawable.bulb_off);
                lightSwitch(isChecked);
            }
        });
    }

    private void lightSwitch(boolean lightStatus) {
        if(lightStatus){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true);
                } catch (Exception e) {
                    Log.e(LOGTAG, e.getMessage());
                }
            }else {
                final PackageManager packageManager = getPackageManager();
                final FeatureInfo[] featureInfos = packageManager.getSystemAvailableFeatures();
                for (final FeatureInfo f : featureInfos) {
                    if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                        if (null == m_camera) {
                            m_camera = Camera.open();
                        }
                        final Camera.Parameters parameters = m_camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        m_camera.setParameters(parameters);
                        m_camera.startPreview();
                    }
                }
            }
        }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                try{
                    manager.setTorchMode("0", false);
                }catch (Exception e){
                    Log.e(LOGTAG, e.getMessage());
                }
            }else {
                if(m_camera != null){
                    m_camera.stopPreview();
                    m_camera.release();
                    m_camera = null;
                }
            }
        }

    }
}
