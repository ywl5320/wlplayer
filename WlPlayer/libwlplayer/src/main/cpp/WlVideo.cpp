//
// Created by ywl on 2017-12-3.
//

#include "WlVideo.h"

WlVideo::WlVideo(WlPlayStatus *playStatus) {
    wlPlayStatus = playStatus;
    streamIndex = -1;
    queue = new WlQueue();
    pthread_mutex_init(&mutexFrame, NULL);
    pthread_cond_init(&condFrame, NULL);
}

WlVideo::~WlVideo() {
    pthread_mutex_destroy(&mutexFrame);
    pthread_cond_destroy(&condFrame);
    LOGE("~WlVideo()");
}

void WlVideo::release() {
    if(queue != NULL)
    {
        queue->release();//清除packet数据
        delete(queue);
        queue = NULL;
        clearAvFrame();//清除frame数据
    }
    if(avCodecContext != NULL)
    {
        avcodec_close(avCodecContext);
        avcodec_free_context(&avCodecContext);
        avCodecContext = NULL;
    }
    if(wlPlayStatus != NULL)
    {
        wlPlayStatus = NULL;
    }
}

int WlVideo::putAvframe(AVFrame *avFrame) {
    pthread_mutex_lock(&mutexFrame);
    queueFrame.push_back(avFrame);
    pthread_cond_signal(&condFrame);
    pthread_mutex_unlock(&mutexFrame);
    return 0;
}

int WlVideo::getAvframe(AVFrame *avFrame) {
    pthread_mutex_lock(&mutexFrame);

    while(1)
    {
        if(queueFrame.size() > 0)
        {
            AVFrame *frame = queueFrame.front();
            if(av_frame_ref(avFrame, frame) == 0)
            {
                queueFrame.pop_front();
            }
            av_frame_free(&frame);
            av_free(frame);
            break;
        } else{
            pthread_cond_wait(&condFrame, &mutexFrame);
        }
    }
    pthread_mutex_unlock(&mutexFrame);
    return 0;
}

void *frameCodec(void *context)
{
    WlVideo *wlVideo = (WlVideo *) context;
    wlVideo->decodeFrame();
    pthread_exit(&wlVideo->frameCodecThread);
}


int WlVideo::playVideo() {
    pthread_create(&frameCodecThread, NULL, frameCodec, this);

    return 0;
}

int WlVideo::decodeFrame() {
    while(1)
    {
        if(!queue)
        {
            break;
        }
        if(!wlPlayStatus)
        {
            break;
        }
        if(wlPlayStatus->play_status == 2)
        {
            break;
        }
        if(wlPlayStatus->play_status == 1)
        {
            continue;
        }
        if(queueFrame.size() > 80)
        {
            continue;
        }
        if(avCodecContext == NULL)
        {
            return -1;
        }
        AVPacket *packet = av_packet_alloc();
        if(queue->getAvpacket(packet) < 0)
        {
            av_packet_free(&packet);
            break;
        }

        ret = avcodec_send_packet(avCodecContext, packet);
        if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
            LOGD("worng");
            av_packet_free(&packet);
            continue;
        }
        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(avCodecContext, frame);
        if (ret < 0 && ret != AVERROR_EOF) {
            LOGD("wrong2");
            av_frame_free(&frame);
            av_packet_free(&packet);
            continue;
        }

        putAvframe(frame);
        av_packet_free(&packet);
    }
    return 0;
}

double WlVideo::synchronize(AVFrame *srcFrame, double pts) {
    double frame_delay;

    if (pts != 0)
        video_clock = pts; // Get pts,then set video clock to it
    else
        pts = video_clock; // Don't get pts,set it to video clock

    frame_delay = av_q2d(avCodecContext->time_base);
    frame_delay += srcFrame->repeat_pict * (frame_delay * 0.5);

    video_clock += frame_delay;

    return pts;
}

int WlVideo::clearAvFrame() {
    pthread_mutex_lock(&mutexFrame);
    while (!queueFrame.empty())
    {
        AVFrame *frame = queueFrame.front();
        queueFrame.pop_front();
        av_frame_free(&frame);
        av_free(frame);
        frame = NULL;
    }
    queueFrame.clear();
    pthread_mutex_unlock(&mutexFrame);
    return 0;
}





