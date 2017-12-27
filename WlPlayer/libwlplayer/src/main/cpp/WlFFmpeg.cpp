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

int WlFFmpeg::initFFmpeg() {
    pthread_create(&decodThread, NULL, decodeThread, this);
    return 0;
}

WlFFmpeg::WlFFmpeg(WlListener *listener, WlAudio *audio, WlVideo *video, WlPlayStatus *playStatus, const char *url) {
    urlpath = url;
    wlListener = listener;
    wlAudio = audio;
    wlVideo = video;
    wlPlayStatus = playStatus;

}

int WlFFmpeg::decodeFFmpeg() {
    av_register_all();
    avformat_network_init();
    pFormatCtx = avformat_alloc_context();
    if (avformat_open_input(&pFormatCtx, urlpath, NULL, NULL) != 0)
    {
        LOGE("can not open url:%s", urlpath);
        wlListener->onError(0, 1, "can not open url");
        return 1;
    }
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0)
    {
        LOGE("can not find streams from %s", urlpath);
        wlListener->onError(0, 2, "can not find streams from url");
        return 1;
    }

    LOGD("channel numbers is %d", pFormatCtx->nb_streams);
    for(int i = 0; i < pFormatCtx->nb_streams; i++)
    {
        if(pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO && wlAudio->streamIndex < 0)//音频
        {
            wlAudio->streamIndex = i;
            wlAudio->time_base = pFormatCtx->streams[i]->time_base;
        }
        else if(pFormatCtx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO && wlVideo->streamIndex < 0)//视频
        {
            wlVideo->streamIndex = i;
            wlVideo->time_base = pFormatCtx->streams[i]->time_base;
            wlVideo->rate = pFormatCtx->streams[i]->r_frame_rate.num;
        }
    }

    if(wlAudio->streamIndex >= 0)
    {
        if(getAvCodecContext(pFormatCtx->streams[wlAudio->streamIndex]->codecpar, wlAudio) != 0)
        {
            return 1;
        }
    }

    if(wlVideo->streamIndex >= 0)
    {
        if(getAvCodecContext(pFormatCtx->streams[wlVideo->streamIndex]->codecpar, wlVideo) != 0)
        {
            return 1;
        }
    }

    int count = 0;
    int ret  = -1;
    wlAudio->playAudio();
    wlVideo->playVideo();
    while(1)
    {
        if(wlPlayStatus->play_status == 2)//停止
        {
            LOGE("解码停止.........")
            break;
        }
        if(wlPlayStatus->play_status == 1)//暂停
        {
            LOGE("解码暂停.........")
            continue;
        }
        if(wlVideo->queue->queuePacket.size() > 50)
        {
            continue;
        }
        AVPacket *packet = av_packet_alloc();
        ret = av_read_frame(pFormatCtx, packet);
        if(ret == 0)
        {
            if(packet->stream_index ==  wlAudio->streamIndex)
            {
                count++;
                wlAudio->queue->putAvpacket(packet);
            }
            else if(packet->stream_index == wlVideo->streamIndex){
                wlVideo->queue->putAvpacket(packet);
            }
            else{
                av_packet_free(&packet);
            }
        } else{
            if(wlAudio->queue->queuePacket.empty())
            {
                wlPlayStatus->play_status = 2;
                break;
            }
            av_packet_free(&packet);
        }
    }
    LOGE("释放了");
    return 0;
}

int WlFFmpeg::getAvCodecContext(AVCodecParameters *parameters, WlBasePlayer *wlBasePlayer) {

    AVCodec *dec = avcodec_find_decoder(parameters->codec_id);
    if(!dec)
    {
        wlListener->onError(0, 3, "get avcodec fail");
        return 1;
    }
    wlBasePlayer->avCodecContext = avcodec_alloc_context3(dec);
    if(!wlAudio->avCodecContext)
    {
        wlListener->onError(0, 4, "alloc avcodecctx fail");
        return 1;
    }
    if(avcodec_parameters_to_context(wlBasePlayer->avCodecContext, parameters) != 0)
    {
        wlListener->onError(0, 5, "copy avcodecctx fail");
        return 1;
    }
    if(avcodec_open2(wlBasePlayer->avCodecContext, dec, 0) != 0)
    {
        wlListener->onError(0, 6, "open avcodecctx fail");
        return 1;
    }
    return 0;
}

void WlFFmpeg::release() {
    if(wlAudio != NULL)
    {
        wlAudio->realease();
        delete(wlAudio);
        wlAudio = NULL;
    }
    LOGE("音频释放完...")
    if(wlVideo != NULL)
    {
        wlVideo->release();
        delete(wlVideo);
        wlVideo = NULL;
    }
    LOGE("视频释放完...")
    if(pFormatCtx != NULL)
    {
        avformat_free_context(pFormatCtx);
        pFormatCtx = NULL;
    }
    LOGE("解码器上下文释放完...")
    if(wlPlayStatus != NULL)
    {
        delete(wlPlayStatus);
        wlPlayStatus = NULL;
    }
    LOGE("播放状态放完...")
    if(wlListener != NULL)
    {
        wlListener->release();
        delete(wlListener);
        wlListener = NULL;
    }
    LOGE("监听释放完...")
}

WlFFmpeg::~WlFFmpeg() {
    LOGE("~WlFFmpeg() 释放了");
}
