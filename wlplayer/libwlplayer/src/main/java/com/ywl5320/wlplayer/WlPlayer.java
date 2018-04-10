package com.ywl5320.wlplayer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.ywl5320.listener.WlOnCompleteListener;
import com.ywl5320.listener.WlOnCutVideoImgListener;
import com.ywl5320.listener.WlOnGlSurfaceViewOncreateListener;
import com.ywl5320.listener.WlOnInfoListener;
import com.ywl5320.listener.WlOnLoadListener;
import com.ywl5320.listener.WlOnErrorListener;
import com.ywl5320.listener.WlOnPreparedListener;
import com.ywl5320.listener.WlOnStopListener;
import com.ywl5320.listener.WlStatus;
import com.ywl5320.opengles.WlGlSurfaceView;
import com.ywl5320.util.MyLog;

import java.nio.ByteBuffer;

/**
 * Created by ywl on 2017-12-13.
 */

public class WlPlayer {

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

    /**
     * 播放文件路径
     */
    private String dataSource;
    /**
     * 硬解码mime
     */
    private MediaFormat mediaFormat;
    /**
     * 视频硬解码器
     */
    private MediaCodec mediaCodec;
    /**
     * 渲染surface
     */
    private Surface surface;
    /**
     * opengl surfaceview
     */
    private WlGlSurfaceView wlGlSurfaceView;
    /**
     * 视频解码器info
     */
    private MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

    /**
     * 准备好时的回调
     */
    private WlOnPreparedListener wlOnPreparedListener;
    /**
     * 错误时的回调
     */
    private WlOnErrorListener wlOnErrorListener;
    /**
     * 加载回调
     */
    private WlOnLoadListener wlOnLoadListener;
    /**
     * 更新时间回调
     */
    private WlOnInfoListener wlOnInfoListener;
    /**
     * 播放完成回调
     */
    private WlOnCompleteListener wlOnCompleteListener;
    /**
     * 视频截图回调
     */
    private WlOnCutVideoImgListener wlOnCutVideoImgListener;
    /**
     * 停止完成回调
     */
    private WlOnStopListener wlOnStopListener;
    /**
     * 是否已经准备好
     */
    private boolean parpared = false;
    /**
     * 时长实体类
     */
    private WlTimeBean wlTimeBean;
    /**
     * 上一次播放时间
     */
    private int lastCurrTime = 0;

    /**
     * 是否只有音频（只播放音频流）
     */
    private boolean isOnlyMusic = false;

    private boolean isOnlySoft = false;

    public WlPlayer()
    {
        wlTimeBean = new WlTimeBean();
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public void setOnlyMusic(boolean onlyMusic) {
        isOnlyMusic = onlyMusic;
    }

    private void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void setWlGlSurfaceView(WlGlSurfaceView wlGlSurfaceView) {
        this.wlGlSurfaceView = wlGlSurfaceView;
        wlGlSurfaceView.setOnGlSurfaceViewOncreateListener(new WlOnGlSurfaceViewOncreateListener() {
            @Override
            public void onGlSurfaceViewOncreate(Surface s) {
                if(surface == null)
                {
                    setSurface(s);
                }
                if(parpared && !TextUtils.isDigitsOnly(dataSource))
                {
                    wlPrepared(dataSource, isOnlyMusic);
                }
            }

            @Override
            public void onCutVideoImg(Bitmap bitmap) {
                if(wlOnCutVideoImgListener != null)
                {
                    wlOnCutVideoImgListener.onCutVideoImg(bitmap);
                }
            }
        });
    }


    /**
     * 准备
     * @param url
     */
    private native void wlPrepared(String url, boolean isOnlyMusic);

    /**
     * 开始
     */
    private native void wlStart();

    /**
     * 停止并释放资源
     */
    private native void wlStop(boolean exit);

    /**
     * 暂停
     */
    private native void wlPause();

    /**
     * 播放 对应暂停
     */
    private native void wlResume();

    /**
     * seek
     * @param secds
     */
    private native void wlSeek(int secds);

    /**
     * 设置音轨 根据获取的音轨数 排序
     * @param index
     */
    private native void wlSetAudioChannels(int index);

    /**
     * 获取总时长
     * @return
     */
    private native int wlGetDuration();

    /**
     * 获取音轨数
     * @return
     */
    private native int wlGetAudioChannels();

    /**
     * 获取视频宽度
     * @return
     */
    private native int wlGetVideoWidth();

    /**
     * 获取视频长度
     * @return
     */
    private native int wlGetVideoHeidht();

    public int getDuration()
    {
        return wlGetDuration();
    }

    public int getAudioChannels()
    {
        return wlGetAudioChannels();
    }

    public int getVideoWidth()
    {
        return wlGetVideoWidth();
    }

    public int getVideoHeight()
    {
        return wlGetVideoHeidht();
    }

    public void setAudioChannels(int index)
    {
        wlSetAudioChannels(index);
    }



    public void setWlOnPreparedListener(WlOnPreparedListener wlOnPreparedListener) {
        this.wlOnPreparedListener = wlOnPreparedListener;
    }



    public void setWlOnErrorListener(WlOnErrorListener wlOnErrorListener) {
        this.wlOnErrorListener = wlOnErrorListener;
    }

    public void prepared()
    {
        if(TextUtils.isEmpty(dataSource))
        {
            onError(WlStatus.WL_STATUS_DATASOURCE_NULL, "datasource is null");
            return;
        }
        parpared = true;
        if(isOnlyMusic)
        {
            wlPrepared(dataSource, isOnlyMusic);
        }
        else
        {
            if(surface != null)
            {
                wlPrepared(dataSource, isOnlyMusic);
            }
        }
    }

    public void start()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(TextUtils.isEmpty(dataSource))
                {
                    onError(WlStatus.WL_STATUS_DATASOURCE_NULL, "datasource is null");
                    return;
                }
                if(!isOnlyMusic)
                {
                    if(surface == null)
                    {
                        onError(WlStatus.WL_STATUS_SURFACE_NULL, "surface is null");
                        return;
                    }
                }

                if(wlTimeBean == null)
                {
                    wlTimeBean = new WlTimeBean();
                }
                wlStart();
            }
        }).start();
    }

    public void stop(final boolean exit)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                wlStop(exit);
                if(mediaCodec != null)
                {
                    try
                    {
                        mediaCodec.flush();
                        mediaCodec.stop();
                        mediaCodec.release();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    mediaCodec = null;
                    mediaFormat = null;
                }
                if(wlGlSurfaceView != null)
                {
                    wlGlSurfaceView.setCodecType(-1);
                    wlGlSurfaceView.requestRender();
                }

            }
        }).start();
    }

    public void pause()
    {
        wlPause();

    }

    public void resume()
    {
        wlResume();
    }

    public void seek(final int secds)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                wlSeek(secds);
                lastCurrTime = secds;
            }
        }).start();
    }

    public void setOnlySoft(boolean soft)
    {
        this.isOnlySoft = soft;
    }

    public boolean isOnlySoft()
    {
        return isOnlySoft;
    }



    private void onLoad(boolean load)
    {
        if(wlOnLoadListener != null)
        {
            wlOnLoadListener.onLoad(load);
        }
    }

    private void onError(int code, String msg)
    {
        if(wlOnErrorListener != null)
        {
            wlOnErrorListener.onError(code, msg);
        }
        stop(true);
    }

    private void onParpared()
    {
        if(wlOnPreparedListener != null)
        {
            wlOnPreparedListener.onPrepared();
        }
    }

    public void mediacodecInit(int mimetype, int width, int height, byte[] csd0, byte[] csd1)
    {
        if(surface != null)
        {
            try {
                wlGlSurfaceView.setCodecType(1);
                String mtype = getMimeType(mimetype);
                mediaFormat = MediaFormat.createVideoFormat(mtype, width, height);
                mediaFormat.setInteger(MediaFormat.KEY_WIDTH, width);
                mediaFormat.setInteger(MediaFormat.KEY_HEIGHT, height);
                mediaFormat.setLong(MediaFormat.KEY_MAX_INPUT_SIZE, width * height);
                mediaFormat.setByteBuffer("csd-0", ByteBuffer.wrap(csd0));
                mediaFormat.setByteBuffer("csd-1", ByteBuffer.wrap(csd1));
                Log.d("ywl5320", mediaFormat.toString());
                mediaCodec = MediaCodec.createDecoderByType(mtype);
                if(surface != null)
                {
                    mediaCodec.configure(mediaFormat, surface, null, 0);
                    mediaCodec.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            if(wlOnErrorListener != null)
            {
                wlOnErrorListener.onError(WlStatus.WL_STATUS_SURFACE_NULL, "surface is null");
            }
        }
    }

    public void mediacodecDecode(byte[] bytes, int size, int pts)
    {
        if(bytes != null && mediaCodec != null && info != null)
        {
            try
            {
                int inputBufferIndex = mediaCodec.dequeueInputBuffer(10);
                if(inputBufferIndex >= 0)
                {
                    ByteBuffer byteBuffer = mediaCodec.getInputBuffers()[inputBufferIndex];
                    byteBuffer.clear();
                    byteBuffer.put(bytes);
                    mediaCodec.queueInputBuffer(inputBufferIndex, 0, size, pts, 0);
                }
                int index = mediaCodec.dequeueOutputBuffer(info, 10);
                while (index >= 0) {
                    mediaCodec.releaseOutputBuffer(index, true);
                    index = mediaCodec.dequeueOutputBuffer(info, 10);
                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void setWlOnLoadListener(WlOnLoadListener wlOnLoadListener) {
        this.wlOnLoadListener = wlOnLoadListener;
    }

    private String getMimeType(int type)
    {
        if(type == 1)
        {
            return "video/avc";
        }
        else if(type == 2)
        {
            return "video/hevc";
        }
        else if(type == 3)
        {
            return "video/mp4v-es";
        }
        else if(type == 4)
        {
            return "video/x-ms-wmv";
        }
        return "";
    }

    public void setFrameData(int w, int h, byte[] y, byte[] u, byte[] v)
    {
        if(wlGlSurfaceView != null)
        {
            MyLog.d("setFrameData");
            wlGlSurfaceView.setCodecType(0);
            wlGlSurfaceView.setFrameData(w, h, y, u, v);
        }
    }

    public void setWlOnInfoListener(WlOnInfoListener wlOnInfoListener) {
        this.wlOnInfoListener = wlOnInfoListener;
    }

    public void setVideoInfo(int currt_secd, int total_secd)
    {
        if(wlOnInfoListener != null && wlTimeBean != null)
        {
            if(currt_secd < lastCurrTime)
            {
                currt_secd = lastCurrTime;
            }
            wlTimeBean.setCurrt_secds(currt_secd);
            wlTimeBean.setTotal_secds(total_secd);
            wlOnInfoListener.onInfo(wlTimeBean);
            lastCurrTime = currt_secd;
        }
    }

    public void setWlOnCompleteListener(WlOnCompleteListener wlOnCompleteListener) {
        this.wlOnCompleteListener = wlOnCompleteListener;
    }

    public void videoComplete()
    {
        if(wlOnCompleteListener != null)
        {
            setVideoInfo(wlGetDuration(), wlGetDuration());
            wlTimeBean = null;
            wlOnCompleteListener.onComplete();
        }
    }

    public void setWlOnCutVideoImgListener(WlOnCutVideoImgListener wlOnCutVideoImgListener) {
        this.wlOnCutVideoImgListener = wlOnCutVideoImgListener;
    }

    public void cutVideoImg()
    {
        if(wlGlSurfaceView != null)
        {
            wlGlSurfaceView.cutVideoImg();
        }
    }

    public void setWlOnStopListener(WlOnStopListener wlOnStopListener) {
        this.wlOnStopListener = wlOnStopListener;
    }

    public void onStopComplete()
    {
        if(wlOnStopListener != null)
        {
            wlOnStopListener.onStop();
        }
    }
}
