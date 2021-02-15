package com.maxim.opengl.bean;

import android.opengl.GLES20;

import java.util.ArrayList;
import java.util.List;

public class GeometryBuilder {

    // x, y, z three components
    private static final int FLOATS_PER_VERTEX = 3;

    interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawCommands;

        GeneratedData(float[] vertexData, List<DrawCommand> drawCommands) {
            this.vertexData = vertexData;
            this.drawCommands = drawCommands;
        }
    }

    public static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);
        GeometryBuilder geometryBuilder = new GeometryBuilder(size);
        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f),
                puck.radius);
        geometryBuilder.appendCircle(puckTop, numPoints);
        geometryBuilder.appendCylinder(puck, numPoints);
        return geometryBuilder.build();
    }

    public static GeneratedData createMallet(Geometry.Point center, float radius, float height,
                                             int numPoints) {
        int sizeOfVertex = sizeOfCircleInVertices(numPoints) *2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;
        GeometryBuilder geometryBuilder = new GeometryBuilder(sizeOfVertex);

        // First, create base cylinder
        float baseHeight = height * 0.25f;
        Geometry.Circle baseCircle = new Geometry.Circle(
                center.translateY(/*-height + baseHeight*/-baseHeight), radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);

        geometryBuilder.appendCircle(baseCircle, numPoints);
        geometryBuilder.appendCylinder(baseCylinder, numPoints);

        // Second, create handle
        float handleHeight = height - baseHeight;
        float handleRadius = radius / 3f;
        Geometry.Circle handleCircle = new Geometry.Circle(center.translateY(height * 0.5f), handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);
        geometryBuilder.appendCircle(handleCircle, numPoints);
        geometryBuilder.appendCylinder(handleCylinder, numPoints);

        return geometryBuilder.build();
    }

    private final float[] vertexData;
    private int offset;

    private final List<DrawCommand> drawCommands = new ArrayList<DrawCommand>();

    public GeometryBuilder(int sizeOfVertices) {
        vertexData = new float[sizeOfVertices * FLOATS_PER_VERTEX];
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        // A cylinder top is a circle built out of a triangle fan; it has one vertex in the center,
        // one vertex for each point around the circle, and the first vertex around the circle is
        // repeated twice so that we can close the circle off.
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        // A cylinder side is a rolled-up rectangle built out of a triangle strip, with two vertices
        // for each point around the circle, and with the first two vertices repeated twice so that
        // we can close off the tube.
        return (numPoints + 1) * 2;
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angle = (float) Math.PI * 2f * (float) i / (float) numPoints;
            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angle);
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angle);
        }
        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float startY = cylinder.center.y - cylinder.height / 2;
        final float endY = cylinder.center.y + cylinder.height / 2;

        for (int i = 0; i <= numPoints; i++) {
            float angle = (float) Math.PI * 2f * (float) i / (float) numPoints;
            float x = cylinder.center.x + cylinder.radius * (float) Math.cos(angle);
            float z = cylinder.center.z + cylinder.radius * (float) Math.sin(angle);
            vertexData[offset++] = x;
            vertexData[offset++] = startY;
            vertexData[offset++] = z;

            vertexData[offset++] = x;
            vertexData[offset++] = endY;
            vertexData[offset++] = z;
        }

        drawCommands.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawCommands);
    }
}
