//
// Created by ywl on 2017-12-3.
//

#ifndef WLPLAYER_QUEUE_H
#define WLPLAYER_QUEUE_H

#include "queue"
#include "WlPlayStatus.h"

extern "C"
{
#include <libavcodec/avcodec.h>
#include "pthread.h"
};

class WlQueue {

public:
    std::queue<AVPacket*> queuePacket;
    std::queue<AVFrame*> queueFrame;
    pthread_mutex_t mutexFrame;
    pthread_cond_t condFrame;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;
    WlPlayStatus *wlPlayStatus = NULL;

public:
    WlQueue(WlPlayStatus *playStatus);
    ~WlQueue();
    int putAvpacket(AVPacket *avPacket);
    int getAvpacket(AVPacket *avPacket);
    int clearAvpacket();
    int clearToKeyFrame();

    int putAvframe(AVFrame *avFrame);
    int getAvframe(AVFrame *avFrame);
    int clearAvFrame();

    void release();
    int getAvPacketSize();
    int getAvFrameSize();

    int noticeThread();
};


#endif //WLPLAYER_QUEUE_H
