//
// Created by ywl on 2017-11-25.
//
#include <jni.h>
#include "AndroidLog.h"
#include "WlListener.h"
#include "WlFFmpeg.h"
#include "WlAudio.h"
#include "WlVideo.h"
#include "WlOpenGLES.h"
#include "unistd.h"
#include "WlPlayStatus.h"

_JavaVM *javaVM;
WlListener *wlListener;
WlFFmpeg *wlFFmpeg;
WlAudio *wlAudio;
WlVideo *wlVideo;
WlOpenGLES *wlOpenGLES;
WlPlayStatus *wlPlayStatus;

extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_prepared(JNIEnv *env, jobject instance, jstring jurlpath) {
    const char * urlpath = env->GetStringUTFChars(jurlpath, 0);

    if(wlPlayStatus == NULL)
    {
        wlPlayStatus = new WlPlayStatus();
    }
    if(wlVideo == NULL)
    {
        wlVideo = new WlVideo(wlPlayStatus);
    }
    if(wlOpenGLES == NULL)
    {
        wlOpenGLES = new WlOpenGLES();
    }
    if(wlListener == NULL)
    {
        wlListener = new WlListener(javaVM, env, &instance);
    }
    if(wlAudio == NULL)
    {
        wlAudio = new WlAudio(wlPlayStatus);
    }
    if(wlFFmpeg == NULL)
    {
        wlFFmpeg = new WlFFmpeg(wlListener, wlAudio, wlVideo, wlPlayStatus, urlpath);
    }
}

extern "C"
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved)
{
    jint result = -1;
    javaVM = vm;
    JNIEnv* env;

    if (vm->GetEnv((void**)&env, JNI_VERSION_1_4) != JNI_OK)
    {
        LOGE("GetEnv failed!");
        return result;
    }
    return JNI_VERSION_1_4;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_opengl_1create(JNIEnv *env, jobject instance, jstring vertex_,
                                                     jstring fragment_) {
    const char *vertex = env->GetStringUTFChars(vertex_, 0);
    const char *fragment = env->GetStringUTFChars(fragment_, 0);
    if(wlOpenGLES != NULL)
    {
        wlOpenGLES->setRenderGLSL(vertex, fragment);
    }
    env->ReleaseStringUTFChars(vertex_, vertex);
    env->ReleaseStringUTFChars(fragment_, fragment);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_opengl_1change(JNIEnv *env, jobject instance, jint width,
                                                     jint height) {
    if(wlOpenGLES != NULL)
    {
        wlOpenGLES->setWinSize(width, height);
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_opengl_1draw(JNIEnv *env, jobject instance) {



    if(wlPlayStatus == NULL)
    {
        return;
    }

    if(wlPlayStatus->play_status == 1)
    {
        return;
    }
    if(wlPlayStatus->play_status == 2)
    {
        return;
    }

    if(wlVideo != NULL && wlAudio != NULL && wlVideo->queueFrame.size() > 0)
    {
        AVFrame *frame = av_frame_alloc();
        if(wlVideo->getAvframe(frame) == 0)
        {
            //获取frame的pts
            if ((wlVideo->framePts = av_frame_get_best_effort_timestamp(frame)) == AV_NOPTS_VALUE)
            {
                wlVideo->framePts = 0;
            }
            wlVideo->framePts *= av_q2d(wlVideo->time_base);
            wlVideo->framePts = wlVideo->synchronize(frame, wlVideo->framePts);
            LOGE("frame pts is %f, video clock is %f", wlVideo->framePts, wlVideo->video_clock);
            double framt_pts = wlVideo->framePts;
            double diff = wlAudio->clock - framt_pts;
            LOGE("diff is %f", diff);
            if(diff > 0.003)
            {
                LOGD("视频慢了");
                wlVideo->delayTime = wlVideo->delayTime / 3 * 2;
                if(wlVideo->delayTime < wlVideo->rate / 2)
                {
                    wlVideo->delayTime = wlVideo->rate / 3 * 2;
                }
                else if(wlVideo->delayTime > wlVideo->rate * 2)
                {
                    wlVideo->delayTime = wlVideo->rate * 2;
                }

            }
            else if(diff < -0.003)
            {
                LOGD("视频快了");
                wlVideo->delayTime = wlVideo->delayTime * 3 / 2;
                if(wlVideo->delayTime < wlVideo->rate / 2)
                {
                    wlVideo->delayTime = wlVideo->rate / 3 * 2;
                }
                else if(wlVideo->delayTime > wlVideo->rate * 2)
                {
                    wlVideo->delayTime = wlVideo->rate * 2;
                }
            } else{
                LOGE("正常播放");
//                wlVideo->delayTime = wlVideo->rate - 0.002;
            }
            LOGE("delay time is %f", wlVideo->delayTime);
            av_usleep(wlVideo->delayTime * 1000);
            wlOpenGLES->draw(frame->width, frame->height, frame->data[0], frame->data[1], frame->data[2]);
        }
        av_frame_free(&frame);
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_pause(JNIEnv *env, jobject instance) {

    // TODO
    if(wlPlayStatus)
    {
        wlPlayStatus->play_status = 1;
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_play(JNIEnv *env, jobject instance) {

    // TODO
    if(wlPlayStatus)
    {
        wlPlayStatus->play_status = 0;
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_stop(JNIEnv *env, jobject instance) {

    // TODO
    if(wlPlayStatus)
    {
        wlPlayStatus->play_status = 2;
        if(wlFFmpeg != NULL)
        {
            wlFFmpeg->release();
            delete(wlFFmpeg);
            wlFFmpeg = NULL;

            wlOpenGLES->release();
            delete(wlOpenGLES);
            wlOpenGLES = NULL;
        }
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_libwlplayer_WlPlayer_start(JNIEnv *env, jobject instance) {

    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->initFFmpeg();
    }

}