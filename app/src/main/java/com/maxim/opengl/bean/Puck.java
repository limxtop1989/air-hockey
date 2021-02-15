package com.maxim.opengl.bean;

import com.maxim.opengl.data.VertexArray;
import com.maxim.opengl.program.ColorShaderProgram;

import java.util.List;

public class Puck {

    private static final int POSITION_COMPONENT_COUNT = 3;

    private final VertexArray vertexArray;
    private final List<GeometryBuilder.DrawCommand> drawCommands;

    public final float height;

    public Puck(float radius, float height, int numPoints) {

        GeometryBuilder.GeneratedData generatedData = GeometryBuilder.createPuck(
                new Geometry.Cylinder(new Geometry.Point(0, 0, 0), radius, height),
                numPoints);

        vertexArray = new VertexArray(generatedData.vertexData);
        drawCommands = generatedData.drawCommands;

        this.height = height;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttributePointer(0,
                colorShaderProgram.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (GeometryBuilder.DrawCommand drawCommand : drawCommands) {
            drawCommand.draw();
        }
    }
}
