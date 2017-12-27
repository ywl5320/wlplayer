package com.ywl5320.libwlplayer;

import android.opengl.GLSurfaceView;
import android.view.Surface;

/**
 * Created by ywl on 2017-11-25.
 */

public class WlPlayer {

    /**
     * 视频路径或地址
     */
    private String url;

    private WlVideoView wlVideoView;
    private OnErrorListener onErrorListener;
    private OnPreparedListener onPreparedListener;
    public void setSource(String url)
    {
        this.url = url;
    }

    public void setWlVideoView(WlVideoView wlVideoView) {
        this.wlVideoView = wlVideoView;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void prepared()
    {
        prepared(url);
    }

    public void stop2()
    {
        wlVideoView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        stop();
    }



    /**
     * 准备
     */
    public native void prepared(String urlPath);

    public native void opengl_create(String vertex, String fragment);

    public native void opengl_change(int width, int height);

    public native void opengl_draw();

    public native void pause();

    public native void start();

    public native void play();

    public native void stop();

    public void onPrepared()
    {
        if(onPreparedListener != null)
        {
            onPreparedListener.onPrepared();
        }
    }

    //播放出错
    public void onError(int code, String msg)
    {
        if(onErrorListener != null)
        {
            onErrorListener.onError(code, msg);
        }
    }

    public interface OnPreparedListener
    {
        void onPrepared();
    }


    public interface OnErrorListener
    {
        void onError(int code, String msg);
    }

    static {
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avformat-57");
        System.loadLibrary("swscale-4");
        System.loadLibrary("postproc-54");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avdevice-57");
        System.loadLibrary("wlplayer");
    }
}
