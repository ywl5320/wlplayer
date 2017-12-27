//
// Created by ywl on 2017-12-3.
//

#ifndef WLPLAYER_QUEUE_H
#define WLPLAYER_QUEUE_H

#include "deque"
#include "WlPlayStatus.h"

extern "C"
{
#include <libavcodec/avcodec.h>
#include "pthread.h"
};

class WlQueue {

public:
    std::deque<AVPacket*> queuePacket;
    pthread_mutex_t mutexPacket;
    pthread_cond_t condPacket;


public:
    WlQueue();
    ~WlQueue();
    int putAvpacket(AVPacket *avPacket);
    int getAvpacket(AVPacket *avPacket);
    int clearAvpacket();

    void release();
};


#endif //WLPLAYER_QUEUE_H
