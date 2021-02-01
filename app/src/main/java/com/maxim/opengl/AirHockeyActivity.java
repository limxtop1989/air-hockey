package com.maxim.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.maxim.opengl.utils.LogWrapper;

public class AirHockeyActivity extends AppCompatActivity {

    private static final String TAG = AirHockeyActivity.class.getSimpleName();

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        if (checkSupport()) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(new AirHockeyRender(this));
            rendererSet = true;
        } else {
            LogWrapper.w(TAG, "onCreate", "This device does not support OpenGL ES 2.0.");
        }
        setContentView(glSurfaceView);

    }

    private boolean checkSupport() {
        final ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        LogWrapper.d(Boolean.toString(supportsEs2));
        return supportsEs2;
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}