//
// Created by ywl on 2017-12-1.
//

#include "WlListener.h"
#include "AndroidLog.h"

WlListener::WlListener(_JavaVM *vm, JNIEnv *env, jobject *obj) {
    javaVM = vm;
    jniEnv = env;
    jobj = *obj;
    jobj = env->NewGlobalRef(jobj);
    jclass  jlz = jniEnv->GetObjectClass(jobj);
    if(!jlz)
    {
        LOGE("find jclass faild");
        return;
    }
    jmid_prepared = jniEnv->GetMethodID(jlz, "onPrepared", "()V");
    jmid_error = jniEnv->GetMethodID(jlz, "onError", "(ILjava/lang/String;)V");
}

WlListener::~WlListener() {
    LOGE("~WlListener()");
}

void WlListener::onPreapared(int type) {
    if(type == 0)//子线程
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_prepared);
        javaVM->DetachCurrentThread();
    }
    else
    {
        jniEnv->CallVoidMethod(jobj, jmid_prepared);
    }
}

void WlListener::onError(int type, int code, const char *msg) {
    if(type == 0)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
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

void WlListener::release() {

}


