package com.maxim.opengl.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGenerateMipmap;

public class TextureHelper {

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        GLES20.glGenTextures(1, textureObjectIds, 0);
        if (textureObjectIds[0] == 0) {
            LogWrapper.d("Could not generate a new OpenGL texture object.");
            return 0;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId,
                options);
        if (null == bitmap) {
            LogWrapper.d("Can't decode resource id " + resourceId);
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }
        // Tell OpenGL that future texture calls should be applied to this texture object.
        GLES20.glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // Read in the bitmap defined by bitmap and copy it over into the texture object that is
        // currently bound.
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        // The bitmap data have been loaded into OpenGL, there is no longer need to keep it around.
        bitmap.recycle();

        GLES20.glGenerateMipmap(GL_TEXTURE_2D);
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);

        return textureObjectIds[0];
    }
}
