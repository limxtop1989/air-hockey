package com.maxim.opengl.utils;

import android.os.Debug;

import com.maxim.opengl.BuildConfig;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

public class ShaderHelper {

    private static final String TAG = ShaderHelper.class.getSimpleName();

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GL_FRAGMENT_SHADER, shaderCode);

    }

    private static int compileShader(int type, String shaderCode) {
        // Set OpenGL version to 2 in advance, otherwise it will crash here.
        final int shaderObjectId = glCreateShader(type);
        if (shaderObjectId == 0) {
            LogWrapper.w(TAG, "compileShader", "Could not create shader");
            return shaderObjectId;
        }

        glShaderSource(shaderObjectId, shaderCode);
        glCompileShader(shaderObjectId);

        final int[] compileStatus = new int[1];
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId);
            LogWrapper.d("Compile shader failed" + shaderCode);
            return shaderObjectId;
        }

        LogWrapper.d(new String[] {"shaderCode", "GLLogInfo"}, new String[]{shaderCode, glGetShaderInfoLog(shaderObjectId)});;

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programObjectId = glCreateProgram();
        if (programObjectId == 0) {
            LogWrapper.w(TAG, "linkProgram", "Could not create new program");
            return  programObjectId;
        }

        glAttachShader(programObjectId, vertexShaderId);
        glAttachShader(programObjectId, fragmentShaderId);

        glLinkProgram(programObjectId);
        final int[] linkStatus = new int[1];
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId);
            LogWrapper.w(TAG, "linkProgram", glGetProgramInfoLog(programObjectId));
            return programObjectId;
        }
        return programObjectId;
    }

    public static boolean validateProgram(int programObjectId) {
        if (!BuildConfig.DEBUG) {
            return true;
        }
        glValidateProgram(programObjectId);

        final int[] validateStatus = new int[1];
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validateStatus, 0);
        LogWrapper.d("Results of validating program: ", new String[] {"validateStatus", "ProgramInfo"},
                new String[]{ String.valueOf(validateStatus[0]), glGetProgramInfoLog(programObjectId)});

        return validateStatus[0] != 0;
    }
}
