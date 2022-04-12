package com.kiven.sample.service;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.kiven.sample.service.gl.EglConfigChooser;
import com.kiven.sample.service.gl.GLWallpaperService;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * @author chenhang
 */
public class MyGLWallpaperService extends GLWallpaperService {

    @Override
    protected GLEngine createGLEngine() {
        return new GLEngine() {
            @Override
            protected void setupGLSurfaceView(boolean isPreview) {
                setEGLContextClientVersion(2);
                setEGLConfigChooser(new EglConfigChooser(8, 8, 8, 0, 0, 0, 0));
                setRenderer(new MyRenderer());
                setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }
        };
    }

    public static class MyRenderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        }
    }
}
