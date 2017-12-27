//
// Created by ywl on 2017-12-3.
//

#ifndef WLPLAYER_OPENSLES_H
#define WLPLAYER_OPENSLES_H

extern "C"
{
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
};
#include "WlAudio.h"

class WlOpenSLES {
public:

    WlAudio *wlAudio;
    void *buffer;
    int pcmsize;
    // 引擎接口
    SLObjectItf engineObject;
    SLEngineItf engineEngine;

    //混音器
    SLObjectItf outputMixObject;
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb;
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

    //pcm
    SLObjectItf pcmPlayerObject;
    SLPlayItf pcmPlayerPlay;
    SLVolumeItf pcmPlayerVolume;

    //缓冲器队列接口
    SLAndroidSimpleBufferQueueItf pcmBufferQueue;

public:

    WlOpenSLES(WlAudio *audio);
    ~WlOpenSLES();

    int initOpenSL();

    void release();

};


#endif //WLPLAYER_OPENSLES_H
