package com.maxim.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.maxim.opengl.bean.Mallet;
import com.maxim.opengl.bean.Table;
import com.maxim.opengl.program.ColorShaderProgram;
import com.maxim.opengl.program.TextureShaderProgram;
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
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;
    private int texture;

    public AirHockeyRender(Context context) {
        mContext = context;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(mContext);
        colorProgram = new ColorShaderProgram(mContext);

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectRatio = height > width ? height / width : width / height;
//        if (height > width) {
//            // Portrait
//            Matrix.orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);
//        } else {
//            // Landscape
//            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio,-1, 1,-1, 1);
//        }
        // The matrix operation order matters.
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0, 0, -2.5f);
        Matrix.rotateM(modelMatrix, 0, -60, 1, 0, 0);
        // near is exclude
        Matrix.perspectiveM(projectionMatrix, 0, 45, aspectRatio, 1f, 10f);
//        Matrix.setIdentityM(projectionMatrix, 0);
        final float[] transformMatrix = new float[16];
        Matrix.multiplyMM(transformMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(transformMatrix, 0, projectionMatrix, 0, transformMatrix.length);
        // TODO what does count parameter means?
//        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL10.GL_COLOR_BUFFER_BIT);

        textureProgram.useProgram();
        textureProgram.setUniform(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();

    }
}
