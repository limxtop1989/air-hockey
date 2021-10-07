package com.maxim.opengl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.maxim.opengl.utils.LogWrapper;

public class AirHockeyActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = AirHockeyActivity.class.getSimpleName();

    private GLSurfaceView mGlSurfaceView;
    private final AirHockeyRender mRenderer = new AirHockeyRender(this);
    private boolean mRendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlSurfaceView = new GLSurfaceView(this);
        if (checkSupport()) {
            mGlSurfaceView.setEGLContextClientVersion(2);
            mGlSurfaceView.setRenderer(mRenderer);
            mRendererSet = true;
        } else {
            LogWrapper.w(TAG, "onCreate", "This device does not support OpenGL ES 2.0.");
        }
        mGlSurfaceView.setOnTouchListener(this);
        setContentView(mGlSurfaceView);

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
        if (mRendererSet) {
            mGlSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRendererSet) {
            mGlSurfaceView.onResume();
        }
    }

    /**
     * In perspective projection, a 3D point in a truncated pyramid frustum (eye coordinates) is
     * mapped to a cube (NDC); the x-coordinate from [l, r] to [-1, 1], the y-coordinate from [b, t]
     * to [-1, 1] and the z-coordinate from [n, f] to [-1, 1].
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null == event) {
            return false;
        }
        final float normalX = (event.getX() / v.getWidth()) * 2 - 1;
        final float normalY = (event.getY() / v.getHeight()) * 2 - 1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mRenderer.handleActionDown(normalX, normalY);
                    }
                });
                break;
            case MotionEvent.ACTION_MOVE:
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mRenderer.handleActionMove(normalX, normalY);
                    }
                });
                break;
            default:
                return false;
        }
        return true;
    }
}