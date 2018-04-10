//
// Created by ywl on 2017-12-3.
//

#include "WlQueue.h"
#include "AndroidLog.h"

WlQueue::WlQueue(WlPlayStatus *playStatus) {
    wlPlayStatus = playStatus;
    pthread_mutex_init(&mutexPacket, NULL);
    pthread_cond_init(&condPacket, NULL);
    pthread_mutex_init(&mutexFrame, NULL);
    pthread_cond_init(&condFrame, NULL);
}

WlQueue::~WlQueue() {
    wlPlayStatus = NULL;
    pthread_mutex_destroy(&mutexPacket);
    pthread_cond_destroy(&condPacket);
    pthread_mutex_destroy(&mutexFrame);
    pthread_cond_destroy(&condFrame);
    if(LOG_SHOW)
    {
        LOGE("~WlQueue() 释放完了");
    }

}

void WlQueue::release() {
    if(LOG_SHOW)
    {
        LOGE("WlQueue::release");
    }

    noticeThread();
    clearAvpacket();
    clearAvFrame();
    if(LOG_SHOW)
    {
        LOGE("WlQueue::release success");
    }

}

int WlQueue::putAvpacket(AVPacket *avPacket) {

    pthread_mutex_lock(&mutexPacket);
    queuePacket.push(avPacket);
    pthread_cond_signal(&condPacket);
    pthread_mutex_unlock(&mutexPacket);

    return 0;
}

int WlQueue::getAvpacket(AVPacket *avPacket) {

    pthread_mutex_lock(&mutexPacket);

    while(wlPlayStatus != NULL && !wlPlayStatus->exit)
    {
        if(queuePacket.size() > 0)
        {
            AVPacket *pkt = queuePacket.front();
            if(av_packet_ref(avPacket, pkt) == 0)
            {
                queuePacket.pop();
            }
            av_packet_free(&pkt);
            av_free(pkt);
            pkt = NULL;
            break;
        } else{
            if(!wlPlayStatus->exit)
            {
                pthread_cond_wait(&condPacket, &mutexPacket);
            }
        }
    }
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

int WlQueue::clearAvpacket() {

    pthread_cond_signal(&condPacket);
    pthread_mutex_lock(&mutexPacket);
    while (!queuePacket.empty())
    {
        AVPacket *pkt = queuePacket.front();
        queuePacket.pop();
        av_free(pkt->data);
        av_free(pkt->buf);
        av_free(pkt->side_data);
        pkt = NULL;
    }
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

int WlQueue::getAvPacketSize() {
    int size = 0;
    pthread_mutex_lock(&mutexPacket);
    size = queuePacket.size();
    pthread_mutex_unlock(&mutexPacket);
    return size;
}

int WlQueue::putAvframe(AVFrame *avFrame) {
    pthread_mutex_lock(&mutexFrame);
    queueFrame.push(avFrame);
    pthread_cond_signal(&condFrame);
    pthread_mutex_unlock(&mutexFrame);
    return 0;
}

int WlQueue::getAvframe(AVFrame *avFrame) {
    pthread_mutex_lock(&mutexFrame);

    while(wlPlayStatus != NULL && !wlPlayStatus->exit)
    {
        if(queueFrame.size() > 0)
        {
            AVFrame *frame = queueFrame.front();
            if(av_frame_ref(avFrame, frame) == 0)
            {
                queueFrame.pop();
            }
            av_frame_free(&frame);
            av_free(frame);
            frame = NULL;
            break;
        } else{
            if(!wlPlayStatus->exit)
            {
                pthread_cond_wait(&condFrame, &mutexFrame);
            }
        }
    }
    pthread_mutex_unlock(&mutexFrame);
    return 0;
}

int WlQueue::clearAvFrame() {
    pthread_cond_signal(&condFrame);
    pthread_mutex_lock(&mutexFrame);
    while (!queueFrame.empty())
    {
        AVFrame *frame = queueFrame.front();
        queueFrame.pop();
        av_frame_free(&frame);
        av_free(frame);
        frame = NULL;
    }
    pthread_mutex_unlock(&mutexFrame);
    return 0;
}

int WlQueue::getAvFrameSize() {
    int size = 0;
    pthread_mutex_lock(&mutexFrame);
    size = queueFrame.size();
    pthread_mutex_unlock(&mutexFrame);
    return size;
}

int WlQueue::noticeThread() {
    pthread_cond_signal(&condFrame);
    pthread_cond_signal(&condPacket);
    return 0;
}

int WlQueue::clearToKeyFrame() {
    pthread_cond_signal(&condPacket);
    pthread_mutex_lock(&mutexPacket);
    while (!queuePacket.empty())
    {
        AVPacket *pkt = queuePacket.front();
        if(pkt->flags != AV_PKT_FLAG_KEY)
        {
            queuePacket.pop();
            av_free(pkt->data);
            av_free(pkt->buf);
            av_free(pkt->side_data);
            pkt = NULL;
        } else{
            break;
        }
    }
    pthread_mutex_unlock(&mutexPacket);
    return 0;
}

