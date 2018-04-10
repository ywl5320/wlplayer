//
// Created by ywl on 2017-12-17.
//

#ifndef WLPLAYER_WLVIDEO_H
#define WLPLAYER_WLVIDEO_H


#include "WlBasePlayer.h"
#include "WlQueue.h"
#include "WlJavaCall.h"
#include "AndroidLog.h"
#include "WlAudio.h"

extern "C"
{
    #include <libavutil/time.h>
};

class WlVideo : public WlBasePlayer{

public:
    WlQueue *queue = NULL;
    WlAudio *wlAudio = NULL;
    WlPlayStatus *wlPlayStatus = NULL;
    pthread_t videoThread;
    pthread_t decFrame;
    WlJavaCall *wljavaCall = NULL;

    double delayTime = 0;
    int rate = 0;
    bool isExit = true;
    bool isExit2 = true;
    int codecType = -1;
    double video_clock = 0;
    double framePts = 0;
    bool frameratebig = false;
    int playcount = -1;

public:
    WlVideo(WlJavaCall *javaCall, WlAudio *audio, WlPlayStatus *playStatus);
    ~WlVideo();

    void playVideo(int codecType);
    void decodVideo();
    void release();
    double synchronize(AVFrame *srcFrame, double pts);

    double getDelayTime(double diff);

    void setClock(int secds);

};


#endif //WLPLAYER_WLVIDEO_H
