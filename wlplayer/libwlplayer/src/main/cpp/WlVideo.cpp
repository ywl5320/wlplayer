//
// Created by ywl on 2017-12-17.
//

#include "WlVideo.h"

WlVideo::WlVideo(WlJavaCall *javaCall, WlAudio *audio, WlPlayStatus *playStatus) {
    streamIndex = -1;
    clock = 0;
    wljavaCall = javaCall;
    wlAudio = audio;
    queue = new WlQueue(playStatus);
    wlPlayStatus = playStatus;
}

void WlVideo::release() {
    if(LOG_SHOW)
    {
        LOGE("开始释放audio ...");
    }

    if(wlPlayStatus != NULL)
    {
        wlPlayStatus->exit = true;
    }
    if(queue != NULL)
    {
        queue->noticeThread();
    }
    int count = 0;
    while(!isExit || !isExit2)
    {
        if(LOG_SHOW)
        {
            LOGE("等待渲染线程结束...%d", count);
        }

        if(count > 1000)
        {
            isExit = true;
            isExit2 = true;
        }
        count++;
        av_usleep(1000 * 10);
    }
    if(queue != NULL)
    {
        queue->release();
        delete(queue);
        queue = NULL;
    }
    if(wljavaCall != NULL)
    {
        wljavaCall = NULL;
    }
    if(wlAudio != NULL)
    {
        wlAudio = NULL;
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

void *decodVideoT(void *data)
{
    WlVideo *wlVideo = (WlVideo *) data;
    wlVideo->decodVideo();
    pthread_exit(&wlVideo->videoThread);

}

void *codecFrame(void *data)
{
    WlVideo *wlVideo = (WlVideo *) data;

    while(!wlVideo->wlPlayStatus->exit)
    {
        if(wlVideo->wlPlayStatus->seek)
        {
            continue;
        }
        wlVideo->isExit2 = false;
        if(wlVideo->queue->getAvFrameSize() > 20)
        {
            continue;
        }
        if(wlVideo->codecType == 1)
        {
            if(wlVideo->queue->getAvPacketSize() == 0)//加载
            {
                if(!wlVideo->wlPlayStatus->load)
                {
                    wlVideo->wljavaCall->onLoad(WL_THREAD_CHILD, true);
                    wlVideo->wlPlayStatus->load = true;
                }
                continue;
            } else{
                if(wlVideo->wlPlayStatus->load)
                {
                    wlVideo->wljavaCall->onLoad(WL_THREAD_CHILD, false);
                    wlVideo->wlPlayStatus->load = false;
                }
            }
        }
        AVPacket *packet = av_packet_alloc();
        if(wlVideo->queue->getAvpacket(packet) != 0)
        {
            av_packet_free(&packet);
            av_free(packet);
            packet = NULL;
            continue;
        }

        int ret = avcodec_send_packet(wlVideo->avCodecContext, packet);
        if (ret < 0 && ret != AVERROR(EAGAIN) && ret != AVERROR_EOF) {
            av_packet_free(&packet);
            av_free(packet);
            packet = NULL;
            continue;
        }
        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(wlVideo->avCodecContext, frame);
        if (ret < 0 && ret != AVERROR_EOF) {
            av_frame_free(&frame);
            av_free(frame);
            frame = NULL;
            av_packet_free(&packet);
            av_free(packet);
            packet = NULL;
            continue;
        }
        wlVideo->queue->putAvframe(frame);
        av_packet_free(&packet);
        av_free(packet);
        packet = NULL;
    }
    wlVideo->isExit2 = true;
    pthread_exit(&wlVideo->decFrame);
}


void WlVideo::playVideo(int type) {
    codecType = type;
    if(codecType == 0)
    {
        pthread_create(&decFrame, NULL, codecFrame, this);
    }
    pthread_create(&videoThread, NULL, decodVideoT, this);

}

void WlVideo::decodVideo() {
    while(!wlPlayStatus->exit)
    {
        isExit = false;
        if(wlPlayStatus->pause)//暂停
        {
            continue;
        }
        if(wlPlayStatus->seek)
        {
            wljavaCall->onLoad(WL_THREAD_CHILD, true);
            wlPlayStatus->load = true;
            continue;
        }
        if(queue->getAvPacketSize() == 0)//加载
        {
            if(!wlPlayStatus->load)
            {
                wljavaCall->onLoad(WL_THREAD_CHILD, true);
                wlPlayStatus->load = true;
            }
            continue;
        } else{
            if(wlPlayStatus->load)
            {
                wljavaCall->onLoad(WL_THREAD_CHILD, false);
                wlPlayStatus->load = false;
            }
        }
        if(codecType == 1)
        {
            AVPacket *packet = av_packet_alloc();
            if(queue->getAvpacket(packet) != 0)
            {
                av_free(packet->data);
                av_free(packet->buf);
                av_free(packet->side_data);
                packet = NULL;
                continue;
            }
            double time = packet->pts * av_q2d(time_base);

            if(LOG_SHOW)
            {
                LOGE("video clock is %f", time);
                LOGE("audio clock is %f", wlAudio->clock);
            }
            if(time < 0)
            {
                time = packet->dts * av_q2d(time_base);
            }

            if(time < clock)
            {
                time = clock;
            }
            clock = time;
            double diff = 0;
            if(wlAudio != NULL)
            {
                diff = wlAudio->clock - clock;
            }
            playcount++;
            if(playcount > 500)
            {
                playcount = 0;
            }
            if(diff >= 0.5)
            {
                if(frameratebig)
                {
                    if(playcount % 3 == 0 && packet->flags != AV_PKT_FLAG_KEY)
                    {
                        av_free(packet->data);
                        av_free(packet->buf);
                        av_free(packet->side_data);
                        packet = NULL;
                        continue;
                    }
                } else{
                    av_free(packet->data);
                    av_free(packet->buf);
                    av_free(packet->side_data);
                    packet = NULL;
                    continue;
                }
            }

            delayTime = getDelayTime(diff);
            if(LOG_SHOW)
            {
                LOGE("delay time %f diff is %f", delayTime, diff);
            }

            av_usleep(delayTime * 1000);
            wljavaCall->onVideoInfo(WL_THREAD_CHILD, clock, duration);
            wljavaCall->onDecMediacodec(WL_THREAD_CHILD, packet->size, packet->data, 0);
            av_free(packet->data);
            av_free(packet->buf);
            av_free(packet->side_data);
            packet = NULL;
        }
        else if(codecType == 0)
        {
            AVFrame *frame = av_frame_alloc();
            if(queue->getAvframe(frame) != 0)
            {
                av_frame_free(&frame);
                av_free(frame);
                frame = NULL;
                continue;
            }
            if ((framePts = av_frame_get_best_effort_timestamp(frame)) == AV_NOPTS_VALUE)
            {
               framePts = 0;
            }
            framePts *= av_q2d(time_base);
            clock = synchronize(frame, framePts);
            double diff = 0;
            if(wlAudio != NULL)
            {
                diff = wlAudio->clock - clock;
            }
            delayTime = getDelayTime(diff);
            if(LOG_SHOW)
            {
                LOGE("delay time %f diff is %f", delayTime, diff);
            }
//            if(diff >= 0.8)
//            {
//                av_frame_free(&frame);
//                av_free(frame);
//                frame = NULL;
//                continue;
//            }

            playcount++;
            if(playcount > 500)
            {
                playcount = 0;
            }
            if(diff >= 0.5)
            {
                if(frameratebig)
                {
                    if(playcount % 3 == 0)
                    {
                        av_frame_free(&frame);
                        av_free(frame);
                        frame = NULL;
                        queue->clearToKeyFrame();
                        continue;
                    }
                } else{
                    av_frame_free(&frame);
                    av_free(frame);
                    frame = NULL;
                    queue->clearToKeyFrame();
                    continue;
                }
            }

            av_usleep(delayTime * 1000);
            wljavaCall->onVideoInfo(WL_THREAD_CHILD, clock, duration);
            wljavaCall->onGlRenderYuv(WL_THREAD_CHILD, frame->linesize[0], frame->height, frame->data[0], frame->data[1], frame->data[2]);
            av_frame_free(&frame);
            av_free(frame);
            frame = NULL;
        }
    }
    isExit = true;

}

WlVideo::~WlVideo() {
    if(LOG_SHOW)
    {
        LOGE("video s释放完");
    }

}

double WlVideo::synchronize(AVFrame *srcFrame, double pts) {
    double frame_delay;

    if (pts != 0)
        video_clock = pts; // Get pts,then set video clock to it
    else
        pts = video_clock; // Don't get pts,set it to video clock

    frame_delay = av_q2d(time_base);
    frame_delay += srcFrame->repeat_pict * (frame_delay * 0.5);

    video_clock += frame_delay;

    return pts;
}

double WlVideo::getDelayTime(double diff) {

    if(LOG_SHOW)
    {
        LOGD("audio video diff is %f", diff);
    }

    if(diff > 0.003)
    {
        delayTime = delayTime / 3 * 2;
        if(delayTime < rate / 2)
        {
            delayTime = rate / 3 * 2;
        }
        else if(delayTime > rate * 2)
        {
            delayTime = rate * 2;
        }

    }
    else if(diff < -0.003)
    {
        delayTime = delayTime * 3 / 2;
        if(delayTime < rate / 2)
        {
            delayTime = rate / 3 * 2;
        }
        else if(delayTime > rate * 2)
        {
            delayTime = rate * 2;
        }
    }else if(diff == 0)
    {
        delayTime = rate;
    }
    if(diff > 1.0)
    {
        delayTime = 0;
    }
    if(diff < -1.0)
    {
        delayTime = rate * 2;
    }
    if(fabs(diff) > 10)
    {
        delayTime = rate;
    }
    return delayTime;
}

void WlVideo::setClock(int secds) {
    clock = secds;
}



