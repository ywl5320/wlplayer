//
// Created by ywl on 2017-12-3.
//

#include "WlQueue.h"
#include "AndroidLog.h"

WlQueue::WlQueue() {
    pthread_mutex_init(&mutexPacket, NULL);
    pthread_cond_init(&condPacket, NULL);
}

WlQueue::~WlQueue() {
    pthread_mutex_destroy(&mutexPacket);
    pthread_cond_destroy(&condPacket);
    LOGE("~WlQueue() 释放完了");
}

void WlQueue::release() {
    LOGE("WlQueue::release");
    clearAvpacket();
    LOGE("WlQueue::release success");
}

int WlQueue::putAvpacket(AVPacket *avPacket) {

    pthread_mutex_lock(&mutexPacket);
    queuePacket.push_back(avPacket);
    pthread_cond_signal(&condPacket);
    pthread_mutex_unlock(&mutexPacket);

    return 0;
}

int WlQueue::getAvpacket(AVPacket *avPacket) {

    pthread_mutex_lock(&mutexPacket);

    while(1)
    {
        if(queuePacket.size() > 0)
        {
            AVPacket *pkt = queuePacket.front();
            if(av_packet_ref(avPacket, pkt) == 0)
            {
                queuePacket.pop_front();
            }
            av_packet_free(&pkt);
            av_free(pkt);
            pkt = NULL;
            break;
        } else{
            pthread_cond_wait(&condPacket, &mutexPacket);
        }
    }
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

int WlQueue::clearAvpacket() {

    pthread_mutex_lock(&mutexPacket);
    while (!queuePacket.empty())
    {
        AVPacket *pkt = queuePacket.front();
        queuePacket.pop_front();
        av_packet_free(&pkt);
        av_free(pkt);
        pkt = NULL;
    }
    queuePacket.clear();
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

