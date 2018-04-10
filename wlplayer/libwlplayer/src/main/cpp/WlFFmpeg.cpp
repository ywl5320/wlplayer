//
// Created by ywl on 2017-12-1.
//

#include "WlFFmpeg.h"

void *decodeThread(void *data)
{
    WlFFmpeg *wlFFmpeg = (WlFFmpeg *) data;
    wlFFmpeg->decodeFFmpeg();
    pthread_exit(&wlFFmpeg->decodThread);
}


int WlFFmpeg::preparedFFmpeg() {
    pthread_create(&decodThread, NULL, decodeThread, this);
    return 0;
}

WlFFmpeg::WlFFmpeg(WlJavaCall *javaCall, const char *url, bool onlymusic) {
    pthread_mutex_init(&init_mutex, NULL);
    pthread_mutex_init(&seek_mutex, NULL);
    exitByUser = false;
    isOnlyMusic = onlymusic;
    wlJavaCall = javaCall;
    urlpath = url;
    wlPlayStatus = new WlPlayStatus();
}

int avformat_interrupt_cb(void *ctx)
{
    WlFFmpeg *wlFFmpeg = (WlFFmpeg *) ctx;
    if(wlFFmpeg->wlPlayStatus->exit)
    {
        if(LOG_SHOW)
        {
            LOGE("avformat_interrupt_cb return 1")
        }
        return AVERROR_EOF;
    }
    if(LOG_SHOW)
    {
        LOGE("avformat_interrupt_cb return 0")
    }
    return 0;
}

int WlFFmpeg::decodeFFmpeg() {
    pthread_mutex_lock(&init_mutex);
    exit = false;
    isavi = false;
    av_register_all();
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();
    if (avformat_open_input(&pFormatCtx, urlpath, NULL, NULL) != 0)
    {
        if(LOG_SHOW)
        {
            LOGE("can not open url:%s", urlpath);
        }
        if(wlJavaCall != NULL)
        {
            wlJavaCall->onError(WL_THREAD_CHILD, WL_FFMPEG_CAN_NOT_OPEN_URL, "can not open url");
        }
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }
    pFormatCtx->interrupt_callback.callback = avformat_interrupt_cb;
    pFormatCtx->interrupt_callback.opaque = this;

    if (avformat_find_stream_info(pFormatCtx, NULL) < 0)
    {
        if(LOG_SHOW)
        {
            LOGE("can not find streams from %s", urlpath);
        }
        if(wlJavaCall != NULL) {
            wlJavaCall->onError(WL_THREAD_CHILD, WL_FFMPEG_CAN_NOT_FIND_STREAMS,
                                "can not find streams from url");
        }
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }

    if(pFormatCtx == NULL)
    {
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return -1;
    }

    duration = pFormatCtx->duration / 1000000;
    if(LOG_SHOW)
    {
        LOGD("channel numbers is %d", pFormatCtx->nb_streams);
    }
    for(int i = 0; i < pFormatCtx->nb_streams; i++)
    {
        if(pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO )//音频
        {
            if(LOG_SHOW)
            {
                LOGE("音频");
            }
            WlAudioChannel *wl = new WlAudioChannel(i, pFormatCtx->streams[i]->time_base);
            audiochannels.push_front(wl);
        }
        else if(pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO)//视频
        {
            if(!isOnlyMusic)
            {
                if(LOG_SHOW)
                {
                    LOGE("视频");
                }
                int num = pFormatCtx->streams[i]->avg_frame_rate.num;
                int den = pFormatCtx->streams[i]->avg_frame_rate.den;
                if(num != 0 && den != 0)
                {
                    int fps = pFormatCtx->streams[i]->avg_frame_rate.num / pFormatCtx->streams[i]->avg_frame_rate.den;
                    WlAudioChannel *wl = new WlAudioChannel(i, pFormatCtx->streams[i]->time_base, fps);
                    videochannels.push_front(wl);
                }
            }
        }
    }


    if(audiochannels.size() > 0)
    {
        wlAudio = new WlAudio(wlPlayStatus, wlJavaCall);
        setAudioChannel(0);
        if(wlAudio->streamIndex >= 0 && wlAudio->streamIndex < pFormatCtx->nb_streams)
        {
            if(getAvCodecContext(pFormatCtx->streams[wlAudio->streamIndex]->codecpar, wlAudio) != 0)
            {
                exit = true;
                pthread_mutex_unlock(&init_mutex);
                return 1;
            }
        }


    }
    if(videochannels.size() > 0)
    {
        wlVideo = new WlVideo(wlJavaCall, wlAudio, wlPlayStatus);
        setVideoChannel(0);
        if(wlVideo->streamIndex >= 0 && wlVideo->streamIndex < pFormatCtx->nb_streams)
        {
            if(getAvCodecContext(pFormatCtx->streams[wlVideo->streamIndex]->codecpar, wlVideo) != 0)
            {
                exit = true;
                pthread_mutex_unlock(&init_mutex);
                return 1;
            }
        }
    }

    if(wlAudio == NULL && wlVideo == NULL)
    {
        exit = true;
        pthread_mutex_unlock(&init_mutex);
        return 1;
    }
    if(wlAudio != NULL)
    {
        wlAudio->duration = pFormatCtx->duration / 1000000;
        wlAudio->sample_rate = wlAudio->avCodecContext->sample_rate;
        if(wlVideo != NULL)
        {
            wlAudio->setVideo(true);
        }
    }
    if(wlVideo != NULL)
    {
        if(LOG_SHOW)
        {
            LOGE("codec name is %s", wlVideo->avCodecContext->codec->name);
            LOGE("codec long name is %s", wlVideo->avCodecContext->codec->long_name);
        }
        if(!wlJavaCall->isOnlySoft(WL_THREAD_CHILD))
        {
            mimeType = getMimeType(wlVideo->avCodecContext->codec->name);
        } else{
            mimeType = -1;
        }

        if(mimeType != -1)
        {
            wlJavaCall->onInitMediacodec(WL_THREAD_CHILD, mimeType, wlVideo->avCodecContext->width, wlVideo->avCodecContext->height, wlVideo->avCodecContext->extradata_size, wlVideo->avCodecContext->extradata_size, wlVideo->avCodecContext->extradata, wlVideo->avCodecContext->extradata);
        }
        wlVideo->duration = pFormatCtx->duration / 1000000;
    }
    if(LOG_SHOW)
    {
        LOGE("准备ing");
    }
    wlJavaCall->onParpared(WL_THREAD_CHILD);
    if(LOG_SHOW)
    {
        LOGE("准备end");
    }
    exit = true;
    pthread_mutex_unlock(&init_mutex);
    return 0;
}

int WlFFmpeg::getAvCodecContext(AVCodecParameters *parameters, WlBasePlayer *wlBasePlayer) {

    AVCodec *dec = avcodec_find_decoder(parameters->codec_id);
    if(!dec)
    {
        wlJavaCall->onError(WL_THREAD_CHILD, 3, "get avcodec fail");
        exit = true;
        return 1;
    }
    wlBasePlayer->avCodecContext = avcodec_alloc_context3(dec);
    if(!wlBasePlayer->avCodecContext)
    {
        wlJavaCall->onError(WL_THREAD_CHILD, 4, "alloc avcodecctx fail");
        exit = true;
        return 1;
    }
    if(avcodec_parameters_to_context(wlBasePlayer->avCodecContext, parameters) != 0)
    {
        wlJavaCall->onError(WL_THREAD_CHILD, 5, "copy avcodecctx fail");
        exit = true;
        return 1;
    }
    if(avcodec_open2(wlBasePlayer->avCodecContext, dec, 0) != 0)
    {
        wlJavaCall->onError(WL_THREAD_CHILD, 6, "open avcodecctx fail");
        exit = true;
        return 1;
    }
    return 0;
}


WlFFmpeg::~WlFFmpeg() {
    pthread_mutex_destroy(&init_mutex);
    if(LOG_SHOW)
    {
        LOGE("~WlFFmpeg() 释放了");
    }
}


int WlFFmpeg::getDuration() {
    return duration;
}

int WlFFmpeg::start() {
    exit = false;
    int count = 0;
    int ret  = -1;
    if(wlAudio != NULL)
    {
        wlAudio->playAudio();
    }
    if(wlVideo != NULL)
    {
        if(mimeType == -1)
        {
            wlVideo->playVideo(0);
        }
        else
        {
            wlVideo->playVideo(1);
        }
    }

    AVBitStreamFilterContext* mimType = NULL;
    if(mimeType == 1)
    {
        mimType =  av_bitstream_filter_init("h264_mp4toannexb");
    }
    else if(mimeType == 2)
    {
        mimType =  av_bitstream_filter_init("hevc_mp4toannexb");
    }
    else if(mimeType == 3)
    {
        mimType =  av_bitstream_filter_init("h264_mp4toannexb");
    }
    else if(mimeType == 4)
    {
        mimType =  av_bitstream_filter_init("h264_mp4toannexb");
    }

    while(!wlPlayStatus->exit)
    {
        exit = false;
        if(wlPlayStatus->pause)//暂停
        {
            av_usleep(1000 * 100);
            continue;
        }
        if(wlAudio != NULL && wlAudio->queue->getAvPacketSize() > 100)
        {
//            LOGE("wlAudio 等待..........");
            av_usleep(1000 * 100);
            continue;
        }
        if(wlVideo != NULL && wlVideo->queue->getAvPacketSize() > 100)
        {
//            LOGE("wlVideo 等待..........");
            av_usleep(1000 * 100);
            continue;
        }
        AVPacket *packet = av_packet_alloc();
        pthread_mutex_lock(&seek_mutex);
        ret = av_read_frame(pFormatCtx, packet);
        pthread_mutex_unlock(&seek_mutex);
        if(wlPlayStatus->seek)
        {
            av_packet_free(&packet);
            av_free(packet);
            continue;
        }
        if(ret == 0)
        {
            if(wlAudio != NULL && packet->stream_index ==  wlAudio->streamIndex)
            {
                count++;
                if(LOG_SHOW)
                {
                    LOGE("解码第 %d 帧", count);
                }
                wlAudio->queue->putAvpacket(packet);
            }else if(wlVideo != NULL && packet->stream_index == wlVideo->streamIndex)
            {
                if(mimType != NULL && !isavi)
                {
                    uint8_t *data;
                    av_bitstream_filter_filter(mimType, pFormatCtx->streams[wlVideo->streamIndex]->codec, NULL, &data, &packet->size, packet->data, packet->size, 0);
                    uint8_t *tdata = NULL;
                    tdata = packet->data;
                    packet->data = data;
                    if(tdata != NULL)
                    {
                        av_free(tdata);
                    }
                }
                wlVideo->queue->putAvpacket(packet);
            }
            else{
                av_packet_free(&packet);
                av_free(packet);
                packet = NULL;
            }
        } else{
            av_packet_free(&packet);
            av_free(packet);
            packet = NULL;
            if((wlVideo != NULL && wlVideo->queue->getAvFrameSize() == 0) || (wlAudio != NULL && wlAudio->queue->getAvPacketSize() == 0))
            {
                wlPlayStatus->exit = true;
                break;
            }
        }
    }
    if(mimType != NULL)
    {
        av_bitstream_filter_close(mimType);
    }
    if(!exitByUser && wlJavaCall != NULL)
    {
        wlJavaCall->onComplete(WL_THREAD_CHILD);
    }
    exit = true;
    return 0;
}

void WlFFmpeg::release() {
    wlPlayStatus->exit = true;
    pthread_mutex_lock(&init_mutex);
    if(LOG_SHOW)
    {
        LOGE("开始释放 wlffmpeg");
    }
    int sleepCount = 0;
    while(!exit)
    {
        if(sleepCount > 1000)//十秒钟还没有退出就自动强制退出
        {
            exit = true;
        }
        if(LOG_SHOW)
        {
            LOGE("wait ffmpeg  exit %d", sleepCount);
        }

        sleepCount++;
        av_usleep(1000 * 10);//暂停10毫秒
    }
    if(LOG_SHOW)
    {
        LOGE("释放audio....................................");
    }

    if(wlAudio != NULL)
    {
        if(LOG_SHOW)
        {
            LOGE("释放audio....................................2");
        }

        wlAudio->realease();
        delete(wlAudio);
        wlAudio = NULL;
    }
    if(LOG_SHOW)
    {
        LOGE("释放video....................................");
    }

    if(wlVideo != NULL)
    {
        if(LOG_SHOW)
        {
            LOGE("释放video....................................2");
        }

        wlVideo->release();
        delete(wlVideo);
        wlVideo = NULL;
    }
    if(LOG_SHOW)
    {
        LOGE("释放format...................................");
    }

    if(pFormatCtx != NULL)
    {
        avformat_close_input(&pFormatCtx);
        avformat_free_context(pFormatCtx);
        pFormatCtx = NULL;
    }
    if(LOG_SHOW)
    {
        LOGE("释放javacall.................................");
    }

    if(wlJavaCall != NULL)
    {
        wlJavaCall = NULL;
    }
    pthread_mutex_unlock(&init_mutex);
}

void WlFFmpeg::pause() {
    if(wlPlayStatus != NULL)
    {
        wlPlayStatus->pause = true;
        if(wlAudio != NULL)
        {
            wlAudio->pause();
        }
    }
}

void WlFFmpeg::resume() {
    if(wlPlayStatus != NULL)
    {
        wlPlayStatus->pause = false;
        if(wlAudio != NULL)
        {
            wlAudio->resume();
        }
    }
}

int WlFFmpeg::getMimeType(const char *codecName) {

    if(strcmp(codecName, "h264") == 0)
    {
        return 1;
    }
    if(strcmp(codecName, "hevc") == 0)
    {
        return 2;
    }
    if(strcmp(codecName, "mpeg4") == 0)
    {
        isavi = true;
        return 3;
    }
    if(strcmp(codecName, "wmv3") == 0)
    {
        isavi = true;
        return 4;
    }

    return -1;
}

int WlFFmpeg::seek(int64_t sec) {
    if(sec >= duration)
    {
        return -1;
    }
    if(wlPlayStatus->load)
    {
        return -1;
    }
    if(pFormatCtx != NULL)
    {
        wlPlayStatus->seek = true;
        pthread_mutex_lock(&seek_mutex);
        int64_t rel = sec * AV_TIME_BASE;
        int ret = avformat_seek_file(pFormatCtx, -1, INT64_MIN, rel, INT64_MAX, 0);
        if(wlAudio != NULL)
        {
            wlAudio->queue->clearAvpacket();
//            av_seek_frame(pFormatCtx, wlAudio->streamIndex, sec * wlAudio->time_base.den, AVSEEK_FLAG_FRAME | AVSEEK_FLAG_BACKWARD);
            wlAudio->setClock(0);
        }
        if(wlVideo != NULL)
        {
            wlVideo->queue->clearAvFrame();
            wlVideo->queue->clearAvpacket();
//            av_seek_frame(pFormatCtx, wlVideo->streamIndex, sec * wlVideo->time_base.den, AVSEEK_FLAG_FRAME | AVSEEK_FLAG_BACKWARD);
            wlVideo->setClock(0);
        }
        wlAudio->clock = 0;
        wlAudio->now_time = 0;
        pthread_mutex_unlock(&seek_mutex);
        wlPlayStatus->seek = false;
    }
    return 0;
}

void WlFFmpeg::setAudioChannel(int index) {
    if(wlAudio != NULL)
    {
        int channelsize = audiochannels.size();
        if(index < channelsize)
        {
            for(int i = 0; i < channelsize; i++)
            {
                if(i == index)
                {
                    wlAudio->time_base = audiochannels.at(i)->time_base;
                    wlAudio->streamIndex = audiochannels.at(i)->channelId;
                }
            }
        }
    }

}

void WlFFmpeg::setVideoChannel(int id) {
    if(wlVideo != NULL)
    {
        wlVideo->streamIndex = videochannels.at(id)->channelId;
        wlVideo->time_base = videochannels.at(id)->time_base;
        wlVideo->rate = 1000 / videochannels.at(id)->fps;
        if(videochannels.at(id)->fps >= 60)
        {
            wlVideo->frameratebig = true;
        } else{
            wlVideo->frameratebig = false;
        }

    }
}

int WlFFmpeg::getAudioChannels() {
    return audiochannels.size();
}

int WlFFmpeg::getVideoWidth() {
    if(wlVideo != NULL && wlVideo->avCodecContext != NULL)
    {
        return wlVideo->avCodecContext->width;
    }
    return 0;
}

int WlFFmpeg::getVideoHeight() {
    if(wlVideo != NULL && wlVideo->avCodecContext != NULL)
    {
        return wlVideo->avCodecContext->height;
    }
    return 0;
}
