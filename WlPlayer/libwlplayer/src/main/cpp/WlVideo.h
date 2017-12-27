//
// Created by ywl on 2017-12-3.
//

#ifndef WLPLAYER_WLVIDEO_H
#define WLPLAYER_WLVIDEO_H

#include "AndroidLog.h"
#include "WlBasePlayer.h"
#include "WlQueue.h"
#include "WlPlayStatus.h"
#include <android/native_window_jni.h>
extern "C"
{
#include "libswscale/swscale.h"
#include "libavutil/imgutils.h"
#include <libavutil/time.h>
};

class WlVideo : public WlBasePlayer{

public:
    pthread_t frameCodecThread;
    WlQueue *queue;
    WlPlayStatus *wlPlayStatus;
    std::deque<AVFrame*> queueFrame;
    pthread_mutex_t mutexFrame;
    pthread_cond_t condFrame;
    int ret;
    double framePts;
    double video_clock;
    double delayTime;
    int rate;

public:
    WlVideo(WlPlayStatus *playStatus);
    ~WlVideo();

    int putAvframe(AVFrame *avFrame);
    int getAvframe(AVFrame *avFrame);

    int playVideo();
    int decodeFrame();

    double synchronize(AVFrame *srcFrame, double pts);

    int clearAvFrame();
    void release();

};


#endif //WLPLAYER_WLVIDEO_H
