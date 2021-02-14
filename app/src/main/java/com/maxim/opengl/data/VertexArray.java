package com.maxim.opengl.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.maxim.opengl.Constants.BYTES_PER_FLOAT;

public class VertexArray {

    private FloatBuffer mVertexData;

    public VertexArray(float[] vertexData) {
        // Create native memory used by OpenGL.
        mVertexData = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mVertexData.put(vertexData);
    }

    public void setVertexAttributePointer(int dataOffset, int attributeLocation, int componentCount,
                                          int stride) {
        mVertexData.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
                false, stride, mVertexData);
        glEnableVertexAttribArray(attributeLocation);
        mVertexData.position(0);
    }
}
