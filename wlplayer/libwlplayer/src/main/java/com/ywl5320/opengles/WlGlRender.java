package com.ywl5320.opengles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLSurfaceView;
import android.view.Surface;

import com.ywl5320.listener.WlOnGlSurfaceViewOncreateListener;
import com.ywl5320.listener.WlOnRenderRefreshListener;
import com.ywl5320.util.MyLog;
import com.ywl5320.wlplayer.R;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by hlwky001 on 2017/12/15.
 */

public class WlGlRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{

    private Context context;
    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f,1f,0f,
            -1f,1f,0f,
            1f,-1f,0f,
            -1f,-1f,0f
    };

    private FloatBuffer textureBuffer;
    private final float[] textureVertexData = {
            1f,0f,
            0f,0f,
            1f,1f,
            0f,1f
    };

    /**
     * mediacodec
     */

    private int programId_mediacodec;
    private int aPositionHandle_mediacodec;
    private int textureid_mediacodec;
    private int uTextureSamplerHandle_mediacodec;
    private int aTextureCoordHandle_mediacodec;

    private SurfaceTexture surfaceTexture;
    private Surface surface;

    /**
     * yuv
     */
    private int programId_yuv;
    private int aPositionHandle_yuv;
    private int aTextureCoordHandle_yuv;
    private int sampler_y;
    private int sampler_u;
    private int sampler_v;
    private int [] textureid_yuv;

    int w;
    int h;

    Buffer y;
    Buffer u;
    Buffer v;

    /**
     * stop
     */
    private int programId_stop;
    private int aPositionHandle_stop;
    private int aTextureCoordHandle_stop;


    int codecType = -1;
    private boolean cutimg = false;
    private int sWidth = 0;
    private int sHeight = 0;



    private WlOnGlSurfaceViewOncreateListener wlOnGlSurfaceViewOncreateListener;
    private WlOnRenderRefreshListener wlOnRenderRefreshListener;

    public WlGlRender(Context context) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureBuffer.position(0);

    }

    public void setFrameData(int w, int h, byte[] by, byte[] bu, byte[] bv)
    {
        this.w = w;
        this.h = h;
        this.y = ByteBuffer.wrap(by);
        this.u = ByteBuffer.wrap(bu);
        this.v = ByteBuffer.wrap(bv);

    }

    public void setCodecType(int codecType) {
        this.codecType = codecType;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        MyLog.d("onSurfaceCreated");
        initMediacodecShader();
        initYuvShader();
        initStop();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        MyLog.d("onSurfaceChanged, width:" + width + ",height :" + height);
        sWidth = width;
        sHeight = height;
        GLES20.glViewport(0,0,width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        if(codecType == 1)
        {
            renderMediacodec();
            MyLog.d("mediaocdec.......");
        }
        else if(codecType == 0)
        {
            renderYuv();
            MyLog.d("yuv.......");
        }
        else
        {
            renderStop();
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        if(cutimg)
        {
            cutimg = false;
            Bitmap bitmap = cutBitmap(0, 0, sWidth, sHeight);
            if(wlOnGlSurfaceViewOncreateListener != null)
            {
                wlOnGlSurfaceViewOncreateListener.onCutVideoImg(bitmap);
            }
        }

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        MyLog.d("updateSurface");
        if(wlOnRenderRefreshListener != null)
        {
            wlOnRenderRefreshListener.onRefresh();
        }
    }

    public void setWlOnGlSurfaceViewOncreateListener(WlOnGlSurfaceViewOncreateListener wlOnGlSurfaceViewOncreateListener) {
        this.wlOnGlSurfaceViewOncreateListener = wlOnGlSurfaceViewOncreateListener;
    }

    public void setWlOnRenderRefreshListener(WlOnRenderRefreshListener wlOnRenderRefreshListener) {
        this.wlOnRenderRefreshListener = wlOnRenderRefreshListener;
    }

    /**
     * 初始化硬件解码shader
     */
    private void initMediacodecShader()
    {
        String vertexShader = WlShaderUtils.readRawTextFile(context, R.raw.vertex_base);
        String fragmentShader = WlShaderUtils.readRawTextFile(context, R.raw.fragment_mediacodec);
        programId_mediacodec = WlShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle_mediacodec= GLES20.glGetAttribLocation(programId_mediacodec,"av_Position");
        aTextureCoordHandle_mediacodec =GLES20.glGetAttribLocation(programId_mediacodec,"af_Position");
        uTextureSamplerHandle_mediacodec =GLES20.glGetUniformLocation(programId_mediacodec,"sTexture");

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureid_mediacodec = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureid_mediacodec);
        WlShaderUtils.checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        surfaceTexture = new SurfaceTexture(textureid_mediacodec);
        surfaceTexture.setOnFrameAvailableListener(this);
        surface = new Surface(surfaceTexture);
        if(wlOnGlSurfaceViewOncreateListener != null)
        {
            wlOnGlSurfaceViewOncreateListener.onGlSurfaceViewOncreate(surface);
        }
    }

    /**
     * 使用硬件解码shader
     */
    private void renderMediacodec()
    {
        GLES20.glUseProgram(programId_mediacodec);
        surfaceTexture.updateTexImage();
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionHandle_mediacodec);
        GLES20.glVertexAttribPointer(aPositionHandle_mediacodec, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle_mediacodec);
        GLES20.glVertexAttribPointer(aTextureCoordHandle_mediacodec,2,GLES20.GL_FLOAT,false,8, textureBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureid_mediacodec);
        GLES20.glUniform1i(uTextureSamplerHandle_mediacodec,0);
    }

    private void initYuvShader()
    {
        String vertexShader = WlShaderUtils.readRawTextFile(context, R.raw.vertex_base);
        String fragmentShader = WlShaderUtils.readRawTextFile(context, R.raw.fragment_yuv);
        programId_yuv = WlShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle_yuv= GLES20.glGetAttribLocation(programId_yuv,"av_Position");
        aTextureCoordHandle_yuv =GLES20.glGetAttribLocation(programId_yuv,"af_Position");

        sampler_y = GLES20.glGetUniformLocation(programId_yuv, "sampler_y");
        sampler_u = GLES20.glGetUniformLocation(programId_yuv, "sampler_u");
        sampler_v = GLES20.glGetUniformLocation(programId_yuv, "sampler_v");

        textureid_yuv = new int[3];
        GLES20.glGenTextures(3, textureid_yuv, 0);
        for (int i = 0; i < 3; i++) {
            // 绑定纹理空间
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[i]);
            //设置属性 当显示的纹理比加载的纹理大时 使用纹理坐标中最接近的若干个颜色 通过加权算法获得绘制颜色
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // 比加载的小
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            // 如果纹理坐标超出范围 0,0-1,1 坐标会被截断在范围内
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        }
    }

    private void renderYuv()
    {
        if(w > 0 && h > 0 && y != null && u != null && v != null)
        {
            GLES20.glUseProgram(programId_yuv);
            GLES20.glEnableVertexAttribArray(aPositionHandle_yuv);
            GLES20.glVertexAttribPointer(aPositionHandle_yuv, 3, GLES20.GL_FLOAT, false,
                    12, vertexBuffer);
            textureBuffer.position(0);
            GLES20.glEnableVertexAttribArray(aTextureCoordHandle_yuv);
            GLES20.glVertexAttribPointer(aTextureCoordHandle_yuv,2,GLES20.GL_FLOAT,false,8, textureBuffer);

            MyLog.d("renderFFmcodec");
            //使 GL_TEXTURE0 单元 活跃 opengl最多支持16个纹理
            //纹理单元是显卡中所有的可用于在shader中进行纹理采样的显存 数量与显卡类型相关，至少16个
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            //绑定纹理空间 下面的操作就会作用在这个空间中
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[0]);
            //创建一个2d纹理 使用亮度颜色模型并且纹理数据也是亮度颜色模型
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w, h, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, y);
            //绑定采样器与纹理单元
            GLES20.glUniform1i(sampler_y, 0);


            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[1]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    u);
            GLES20.glUniform1i(sampler_u, 1);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureid_yuv[2]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, w / 2, h / 2, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE,
                    v);
            GLES20.glUniform1i(sampler_v, 2);
            y.clear();
            u.clear();
            v.clear();
            y = null;
            u = null;
            v = null;
        }
    }

    private void initStop()
    {
        String vertexShader = WlShaderUtils.readRawTextFile(context, R.raw.vertex_base);
        String fragmentShader = WlShaderUtils.readRawTextFile(context, R.raw.fragment_no);
        programId_stop = WlShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle_stop= GLES20.glGetAttribLocation(programId_stop,"av_Position");
        aTextureCoordHandle_stop =GLES20.glGetAttribLocation(programId_stop,"af_Position");
    }

    private void renderStop()
    {
        GLES20.glUseProgram(programId_stop);
        vertexBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aPositionHandle_stop);
        GLES20.glVertexAttribPointer(aPositionHandle_stop, 3, GLES20.GL_FLOAT, false,
                12, vertexBuffer);
        textureBuffer.position(0);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle_stop);
        GLES20.glVertexAttribPointer(aTextureCoordHandle_stop,2,GLES20.GL_FLOAT,false,8, textureBuffer);
    }

    private Bitmap cutBitmap(int x, int y, int w, int h) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            GLES20.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
        intBuffer.clear();
        return bitmap;
    }

    public void cutVideoImg()
    {
        cutimg = true;
    }

}
