//
// Created by ywl on 2017-12-1.
//

#ifndef WLPLAYER_WLLISTENER_H
#define WLPLAYER_WLLISTENER_H

#include <jni.h>


class WlListener {

public:
    _JavaVM *javaVM;
    JNIEnv *jniEnv;
    jmethodID jmid_prepared;
    jmethodID jmid_error;
    jobject jobj;

public:
    WlListener(_JavaVM *javaVM, JNIEnv *env, jobject *jobj);
    ~WlListener();
    void onPreapared(int type);
    void onError(int type, int code, const char *msg);

    void release();

};


#endif //WLPLAYER_WLLISTENER_H
