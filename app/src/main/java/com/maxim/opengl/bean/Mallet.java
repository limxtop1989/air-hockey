package com.maxim.opengl.bean;

import com.maxim.opengl.data.VertexArray;
import com.maxim.opengl.program.ColorShaderProgram;

import java.util.List;

import static com.maxim.opengl.Constants.BYTES_PER_FLOAT;

public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private final VertexArray vertexArray;
    private List<GeometryBuilder.DrawCommand> drawCommands;

    public final float height;

    public Mallet(float radius, float height, int numPoints) {
        GeometryBuilder.GeneratedData generatedData = GeometryBuilder.createMallet(
                new Geometry.Point(0, 0, 0), radius, height, numPoints);

        vertexArray = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

        this.height = height;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttributePointer(
                0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0
        );
    }

    public void draw() {
        for (GeometryBuilder.DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }

}
