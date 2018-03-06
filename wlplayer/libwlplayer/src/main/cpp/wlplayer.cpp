#include <jni.h>
#include <stddef.h>
#include "AndroidLog.h"
#include "WlJavaCall.h"
#include "WlFFmpeg.h"

_JavaVM *javaVM = NULL;
WlJavaCall *wlJavaCall = NULL;
WlFFmpeg *wlFFmpeg = NULL;

extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlPrepared(JNIEnv *env, jobject instance, jstring url_, jboolean isOnlyMusic) {
    const char *url = env->GetStringUTFChars(url_, 0);
    // TODO
    if(wlJavaCall == NULL)
    {
        wlJavaCall = new WlJavaCall(javaVM, env, &instance);
    }
    if(wlFFmpeg == NULL)
    {
        wlFFmpeg = new WlFFmpeg(wlJavaCall, url, isOnlyMusic);
        wlJavaCall->onLoad(WL_THREAD_MAIN, true);
        wlFFmpeg->preparedFFmpeg();
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
        if(LOG_SHOW)
        {
            LOGE("GetEnv failed!");
        }
        return result;
    }
    return JNI_VERSION_1_4;
}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlStart(JNIEnv *env, jobject instance) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->start();
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlStop(JNIEnv *env, jobject instance, bool exit) {
    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->exitByUser = true;
        wlFFmpeg->release();
        delete(wlFFmpeg);
        wlFFmpeg = NULL;
        if(wlJavaCall != NULL)
        {
            wlJavaCall->release();
            wlJavaCall = NULL;
        }
        if(!exit)
        {
            jclass jlz = env->GetObjectClass(instance);
            jmethodID jmid_stop = env->GetMethodID(jlz, "onStopComplete", "()V");
            env->CallVoidMethod(instance, jmid_stop);
        }
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlPause(JNIEnv *env, jobject instance) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->pause();
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlResume(JNIEnv *env, jobject instance) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->resume();
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlSeek(JNIEnv *env, jobject instance, jint secds) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->seek(secds);
    }

}extern "C"
JNIEXPORT jint JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlGetDuration(JNIEnv *env, jobject instance) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        return wlFFmpeg->getDuration();
    }
    return 0;

}extern "C"
JNIEXPORT jint JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlGetAudioChannels(JNIEnv *env, jobject instance) {

    if(wlFFmpeg != NULL)
    {
        return wlFFmpeg->getAudioChannels();
    }
    return 0;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlGetVideoWidth(JNIEnv *env, jobject instance) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->getVideoWidth();
    }

}extern "C"
JNIEXPORT jint JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlGetVideoHeidht(JNIEnv *env, jobject instance) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->getVideoHeight();
    }

}extern "C"
JNIEXPORT void JNICALL
Java_com_ywl5320_wlplayer_WlPlayer_wlSetAudioChannels(JNIEnv *env, jobject instance, jint index) {

    // TODO
    if(wlFFmpeg != NULL)
    {
        wlFFmpeg->setAudioChannel(index);
    }

}