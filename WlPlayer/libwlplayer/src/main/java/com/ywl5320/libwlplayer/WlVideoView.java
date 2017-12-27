package com.ywl5320.libwlplayer;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ywl on 2017-12-3.
 */

public class WlVideoView extends GLSurfaceView implements GLSurfaceView.Renderer{

    private String vertex;
    private String fragment;
    private WlPlayer wlPlayer;

    public WlVideoView(Context context) {
        this(context, null);
    }

    public WlVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //设置egl版本为2.0
        setEGLContextClientVersion(2);
        //设置render
        setRenderer(this);
        //设置为手动刷新模式
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //加载顶点着色器程序代码
        vertex = Utils.loadStringFromAssets(context, "vertex_yuv.glsl");
        //加载片元着色器程序代码
        fragment = Utils.loadStringFromAssets(context, "fragment_yuv.glsl");
    }

    public void setWlPlayer(WlPlayer wlPlayer) {
        this.wlPlayer = wlPlayer;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        wlPlayer.opengl_create(vertex, fragment);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int w, int h) {
        wlPlayer.opengl_change(w, h);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        wlPlayer.opengl_draw();
    }
}
