package com.maxim.opengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.maxim.opengl.utils.ShaderHelper;
import com.maxim.opengl.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final int BYTES_PER_FLOAT = 4;

    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
            * BYTES_PER_FLOAT;

    private Context mContext;

    private final FloatBuffer mVertexData;
    private int mProgram;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;
    private static final String A_COLOR = "a_Color";
    private int aColorLocation;
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;
    private final float[] projectionMatrix = new float[16];

    public AirHockeyRender(Context context) {
        mContext = context;
        // Vertex attribute array.
        float[] tableVertices = new float[]{
                // Order of coordinates: X, Y, Z, W, R, G, B
                // Triangle Fan
                   0f,    0f, 0f, 1.5f,   1f,  1f,    1f,
                -0.5f, -0.5f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                 0.5f, -0.5f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                 0.5f,  0.5f, 0f,   2f, 0.7f, 0.7f, 0.7f,
                -0.5f,  0.5f, 0f,   2f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.5f, 0f,   1f, 0.7f, 0.7f, 0.7f,
                // Line 1
                -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                 0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
                // Mallets
                0f, -0.25f, 0f, 1.5f, 0f, 0f, 1f,
                0f,  0.25f, 0f, 1.5f, 1f, 0f, 0f
        };

        // Create native memory used by OpenGL.
        mVertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        // Copy heap memory into native memory.
        mVertexData.put(tableVertices);
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext,
                R.raw.vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext,
                R.raw.fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(mProgram);
        glUseProgram(mProgram);

        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        mVertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, mVertexData);
        glEnableVertexAttribArray(aPositionLocation);

        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        mVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, mVertexData);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        final float aspectRatio = height > width ? height / width : width / height;
        if (height > width) {
            // Portrait
            Matrix.orthoM(projectionMatrix, 0, -1, 1, -aspectRatio, aspectRatio, -1, 1);
        } else {
            // Landscape
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio,-1, 1,-1, 1);
        }
        // TODO what does count parameter means?
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        // Draw two triangles.
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        glDrawArrays(GL_LINES, 6, 2);

        glDrawArrays(GL_POINTS, 8, 1);

        glDrawArrays(GL_POINTS, 9,1);

    }
}
