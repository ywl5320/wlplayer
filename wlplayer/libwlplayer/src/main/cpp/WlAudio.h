//
// Created by ywl on 2017-12-3.
//
#pragma once
#ifndef WLPLAYER_WLAUDIO_H
#define WLPLAYER_WLAUDIO_H


#include "WlBasePlayer.h"
#include "WlQueue.h"
#include "AndroidLog.h"
#include "WlPlayStatus.h"
#include "WlJavaCall.h"

extern "C"
{
#include "libswresample/swresample.h"
#include "libavutil/time.h"
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
};
class WlOpenSLES;
class WlAudio : public WlBasePlayer{

public:
    WlQueue *queue = NULL;
    WlPlayStatus *wlPlayStatus = NULL;
    WlJavaCall *wljavaCall = NULL;
    pthread_t audioThread;

    int ret = 0;//函数调用返回结果
    int64_t dst_layout = 0;//重采样为立体声
    int dst_nb_samples = 0;// 计算转换后的sample个数 a * b / c
    int nb = 0;//转换，返回值为转换后的sample个数
    uint8_t *out_buffer = NULL;//buffer 内存区域
    int out_channels = 0;//输出声道数
    int data_size = 0;//buffer大小
    enum AVSampleFormat dst_format;
    //opensl es

    void *buffer = NULL;
    int pcmsize = 0;
    int sample_rate = 44100;
    bool isExit = false;
    bool isVideo = false;

    bool isReadPacketFinish = true;
    AVPacket *packet;

    // 引擎接口
    SLObjectItf engineObject = NULL;
    SLEngineItf engineEngine = NULL;

    //混音器
    SLObjectItf outputMixObject = NULL;
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

    //pcm
    SLObjectItf pcmPlayerObject = NULL;
    SLPlayItf pcmPlayerPlay = NULL;
    SLVolumeItf pcmPlayerVolume = NULL;

    //缓冲器队列接口
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = NULL;
public:
    WlAudio(WlPlayStatus *playStatus, WlJavaCall *javaCall);
    ~WlAudio();

    void setVideo(bool video);

    void playAudio();
    int getPcmData(void **pcm);
    int initOpenSL();
    void pause();
    void resume();
    void realease();
    int getSLSampleRate();
    void setClock(int secds);


};


#endif //WLPLAYER_WLAUDIO_H
