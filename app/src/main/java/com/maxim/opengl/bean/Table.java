package com.maxim.opengl.bean;

import com.maxim.opengl.data.VertexArray;
import com.maxim.opengl.program.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.maxim.opengl.Constants.BYTES_PER_FLOAT;

public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private VertexArray vertexArray;

    // Triangle Fan
    private static final float[] VERTEX_DATA = new float[] {
            // Order of coordinates: X, Y, S, T
            // that the T component is running in the opposite direction of the y component.
            // This is so that the image is oriented with the right side up.
            // To avoid squashing the texture, we use the range 0.1 to 0.9 instead of 0.0 to 1.0
            // to clip the edges and just draw the center portion.
               0f,    0f, 0.5f,  0.5f,
            -0.5f, -0.8f,   0f,  0.9f,
             0.5f, -0.8f,   1f,  0.9f,
             0.5f,  0.8f,   1f,  0.1f,
            -0.5f,  0.8f,   0f,  0.1f,
            -0.5f, -0.8f,   0f,  0.9f

    };

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttributePointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);
        vertexArray.setVertexAttributePointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
