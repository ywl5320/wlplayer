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

extern "C"
{
#include "libswresample/swresample.h"
};
class WlOpenSLES;
class WlAudio : public WlBasePlayer{

public:
    WlOpenSLES *wlOpenSLES;
    WlQueue *queue;
    WlPlayStatus *wlPlayStatus;
    pthread_t audioThread;

    int ret = 0;//函数调用返回结果
    int64_t dst_layout;//重采样为立体声
    int dst_nb_samples = 0;// 计算转换后的sample个数 a * b / c
    int nb = 0;//转换，返回值为转换后的sample个数
    uint8_t *out_buffer;//buffer 内存区域
    int out_channels = 0;//输出声道数
    int data_size = 0;//buffer大小
    double clock;//时钟
public:
    WlAudio(WlPlayStatus *playStatus);
    ~WlAudio();

    void playAudio();
    int getPcmData(void **pcm);

    void realease();

};


#endif //WLPLAYER_WLAUDIO_H
