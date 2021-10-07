package com.maxim.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.maxim.opengl.bean.Geometry;
import com.maxim.opengl.bean.Mallet;
import com.maxim.opengl.bean.Puck;
import com.maxim.opengl.bean.Table;
import com.maxim.opengl.program.ColorShaderProgram;
import com.maxim.opengl.program.TextureShaderProgram;
import com.maxim.opengl.utils.LogWrapper;
import com.maxim.opengl.utils.ShaderHelper;
import com.maxim.opengl.utils.TextResourceReader;
import com.maxim.opengl.utils.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private Context mContext;
    private final float[] modelMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];

    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] invertedViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    // Reset onSurfaceCreated.
    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

    public AirHockeyRender(Context context) {
        mContext = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        table = new Table();
        mallet = new Mallet(0.08f, 0.12f, 64);
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        puck = new Puck(0.06f, 0.02f, 64);
        textureProgram = new TextureShaderProgram(mContext);
        colorProgram = new ColorShaderProgram(mContext);

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        // TODO How to explain aspectRatio clearly?
        final float aspectRatio = height > width ? height / width : width / height;
        Matrix.perspectiveM(projectionMatrix, 0, 45, aspectRatio, 1f, 10f);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f,
                0f, 0f, 0f, 0f, 1f, 0f);

//        if (height > width) {
//            // Portrait
//            Matrix.orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);
//        } else {
//            // Landscape
//            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio,-1, 1,-1, 1);
//        }
        // The matrix operation order matters.
//        Matrix.setIdentityM(modelMatrix, 0);
//        Matrix.translateM(modelMatrix, 0, 0, 0, -2.5f);
//        Matrix.rotateM(modelMatrix, 0, -60, 1, 0, 0);
//        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
//        // near is exclude
//        Matrix.perspectiveM(projectionMatrix, 0, 45, aspectRatio, 1f, 10f);
//        final float[] transformMatrix = new float[16];
//        Matrix.multiplyMM(transformMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(transformMatrix, 0, projectionMatrix, 0, transformMatrix.length);
        // TODO what does count parameter means?
//        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL10.GL_COLOR_BUFFER_BIT);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
                viewMatrix, 0);
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
        
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniform(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1, 0, 0);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        // Note that we don't have to define the object data twice -- we just draw the same mallet
        // again but in a different position and with a different color.
        mallet.draw();

        // Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        Matrix.setIdentityM(modelMatrix, 0);
        // TODO: Does it rotate along the direction that the fingers points to when make a fist and the
        // thumb points to positive x pivot?
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, x, y, z);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    void handleActionDown(float normalX, float normalY) {
        LogWrapper.d(normalX + ", " + normalY);
        Geometry.Ray ray = convertNormalized2DPointToRay(normalX, normalY);
        Geometry.Sphere malletSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z),
                mallet.height / 2);
        malletPressed = Geometry.intersects(malletSphere, ray);
        LogWrapper.d(String.valueOf(malletPressed));
    }

    private Geometry.Ray convertNormalized2DPointToRay(float normalX, float normalY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        final float[] nearPointNdc = { normalX, normalY, -1, 1 };
        final float[] farPointNdc =  { normalX, normalY,  1, 1 };

        final float[] nearPointWord = new float[4];
        final float[] farPointWord = new float[4];

        Matrix.multiplyMV(nearPointWord, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWord, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWord);
        divideByW(farPointWord);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWord[0], nearPointWord[1], nearPointWord[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWord[0], farPointWord[1], farPointWord[2]);
        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    void handleActionMove(float normalX, float normalY) {
        LogWrapper.d(normalX + ", " + normalY);
    }
}
