//
// Created by ywl on 2017-12-1.
//

#include <libavutil/mem.h>
#include "WlJavaCall.h"
#include "AndroidLog.h"

WlJavaCall::WlJavaCall(_JavaVM *vm, JNIEnv *env, jobject *obj) {
    javaVM = vm;
    jniEnv = env;
    jobj = *obj;
    jobj = env->NewGlobalRef(jobj);
    jclass  jlz = jniEnv->GetObjectClass(jobj);
    if(!jlz)
    {
//        LOGE("find jclass faild");
        return;
    }
    jmid_error = jniEnv->GetMethodID(jlz, "onError", "(ILjava/lang/String;)V");
    jmid_load = jniEnv->GetMethodID(jlz, "onLoad", "(Z)V");
    jmid_parpared = jniEnv->GetMethodID(jlz, "onParpared", "()V");
    jmid_init_mediacodec = jniEnv->GetMethodID(jlz, "mediacodecInit", "(III[B[B)V");
    jmid_dec_mediacodec = jniEnv->GetMethodID(jlz, "mediacodecDecode", "([BII)V");
    jmid_gl_yuv = jniEnv->GetMethodID(jlz, "setFrameData", "(II[B[B[B)V");
    jmid_info = jniEnv->GetMethodID(jlz, "setVideoInfo", "(II)V");
    jmid_complete = jniEnv->GetMethodID(jlz, "videoComplete", "()V");
    jmid_onlysoft = jniEnv->GetMethodID(jlz, "isOnlySoft", "()Z");
}

WlJavaCall::~WlJavaCall() {
//    LOGE("WlJavaCall()");
}

void WlJavaCall::onError(int type, int code, const char *msg) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_error, code, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jstring jmsg = jniEnv->NewStringUTF(msg);
        jniEnv->CallVoidMethod(jobj, jmid_error, code, jmsg);
        jniEnv->DeleteLocalRef(jmsg);
    }
}

void WlJavaCall::release() {
    if(javaVM != NULL)
    {
        javaVM = NULL;
    }
    if(jniEnv != NULL)
    {
        jniEnv = NULL;
    }
}

void WlJavaCall::onLoad(int type, bool load) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jniEnv->CallVoidMethod(jobj, jmid_load, load);
    }
}

void WlJavaCall::onParpared(int type) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jniEnv->CallVoidMethod(jobj, jmid_parpared);
    }
}

void WlJavaCall::onInitMediacodec(int type, int mimetype, int width, int height, int csd_0_size, int csd_1_size, uint8_t *csd_0, uint8_t *csd_1) {

    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jbyteArray csd0 = jniEnv->NewByteArray(csd_0_size);
        jniEnv->SetByteArrayRegion(csd0, 0, csd_0_size, (jbyte*)csd_0);
        jbyteArray csd1 = jniEnv->NewByteArray(csd_1_size);
        jniEnv->SetByteArrayRegion(csd1, 0, csd_1_size, (jbyte*)csd_1);

        jniEnv->CallVoidMethod(jobj, jmid_init_mediacodec, mimetype, width, height, csd0, csd1);

        jniEnv->DeleteLocalRef(csd0);
        jniEnv->DeleteLocalRef(csd1);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jbyteArray csd0 = jniEnv->NewByteArray(csd_0_size);
        jniEnv->SetByteArrayRegion(csd0, 0, csd_0_size, (jbyte*)csd_0);
        jbyteArray csd1 = jniEnv->NewByteArray(csd_1_size);
        jniEnv->SetByteArrayRegion(csd1, 0, csd_1_size, (jbyte*)csd_1);

        jniEnv->CallVoidMethod(jobj, jmid_init_mediacodec, mimetype, width, height, csd0, csd1);

        jniEnv->DeleteLocalRef(csd0);
        jniEnv->DeleteLocalRef(csd1);
    }

}

void WlJavaCall::onDecMediacodec(int type, int size, uint8_t *packet_data, int pts) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jbyteArray data = jniEnv->NewByteArray(size);
        jniEnv->SetByteArrayRegion(data, 0, size, (jbyte*)packet_data);
        jniEnv->CallVoidMethod(jobj, jmid_dec_mediacodec, data, size, pts);
        jniEnv->DeleteLocalRef(data);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jbyteArray data = jniEnv->NewByteArray(size);
        jniEnv->SetByteArrayRegion(data, 0, size, (jbyte*)data);
        jniEnv->CallVoidMethod(jobj, jmid_dec_mediacodec, data, size, pts);
        jniEnv->DeleteLocalRef(data);
    }
}

void
WlJavaCall::onGlRenderYuv(int type, int width, int height, uint8_t *fy, uint8_t *fu, uint8_t *fv) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }

        jbyteArray y = jniEnv->NewByteArray(width * height);
        jniEnv->SetByteArrayRegion(y, 0, width * height, (jbyte*)fy);

        jbyteArray u = jniEnv->NewByteArray(width * height / 4);
        jniEnv->SetByteArrayRegion(u, 0, width * height / 4, (jbyte*)fu);

        jbyteArray v = jniEnv->NewByteArray(width * height / 4);
        jniEnv->SetByteArrayRegion(v, 0, width * height / 4, (jbyte*)fv);

        jniEnv->CallVoidMethod(jobj, jmid_gl_yuv, width, height, y, u, v);
        jniEnv->DeleteLocalRef(y);
        jniEnv->DeleteLocalRef(u);
        jniEnv->DeleteLocalRef(v);

        javaVM->DetachCurrentThread();
    }
    else
    {
        jbyteArray y = jniEnv->NewByteArray(width * height);
        jniEnv->SetByteArrayRegion(y, 0, width * height, (jbyte*)y);

        jbyteArray u = jniEnv->NewByteArray(width * height / 4);
        jniEnv->SetByteArrayRegion(u, 0, width * height / 4, (jbyte*)u);

        jbyteArray v = jniEnv->NewByteArray(width * height / 4);
        jniEnv->SetByteArrayRegion(v, 0, width * height / 4, (jbyte*)v);

        jniEnv->CallVoidMethod(jobj, jmid_gl_yuv, width, height, y, u, v);
        jniEnv->DeleteLocalRef(y);
        jniEnv->DeleteLocalRef(u);
        jniEnv->DeleteLocalRef(v);
    }
}

void WlJavaCall::onVideoInfo(int type, int currt_secd, int total_secd) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_info, currt_secd, total_secd);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jniEnv->CallVoidMethod(jobj, jmid_info, currt_secd, total_secd);
    }
}

void WlJavaCall::onComplete(int type) {
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
//            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_complete);
//        javaVM->DetachCurrentThread();
    }
    else
    {
        jniEnv->CallVoidMethod(jobj, jmid_complete);
    }
}

bool WlJavaCall::isOnlySoft(int type) {
    bool soft = false;
    if(type == WL_THREAD_CHILD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
        }
        soft = jniEnv->CallBooleanMethod(jobj, jmid_onlysoft);
        javaVM->DetachCurrentThread();
    }
    else
    {
        soft = jniEnv->CallBooleanMethod(jobj, jmid_onlysoft);
    }
    return soft;
}


