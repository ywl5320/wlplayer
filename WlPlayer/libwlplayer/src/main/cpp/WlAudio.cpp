//
// Created by ywl on 2017-12-3.
//

#include "WlAudio.h"
#include "WlOpenSLES.h"

WlAudio::WlAudio(WlPlayStatus *playStatus) {
    wlPlayStatus = playStatus;
    streamIndex = -1;
    out_buffer = (uint8_t *) malloc(44100 * 2 * 2 * 2 / 3);
    queue = new WlQueue();
    wlOpenSLES = new WlOpenSLES(this);
}

WlAudio::~WlAudio() {
    LOGE("~WlAudio() 释放完了");
}

void WlAudio::realease() {
    if(queue != NULL)
    {
        queue->release();
        delete(queue);
        queue = NULL;
    }
    if(wlOpenSLES != NULL)
    {
        wlOpenSLES->release();
        delete (wlOpenSLES);
        wlOpenSLES = NULL;
    }
    if(out_buffer != NULL)
    {
        free(out_buffer);
        out_buffer = NULL;
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

void *audioPlayThread(void *context)
{
    WlAudio *audio = (WlAudio *) context;
    audio->wlOpenSLES->initOpenSL();
    pthread_exit(&audio->audioThread);
}

void WlAudio::playAudio() {
    pthread_create(&audioThread, NULL, audioPlayThread, this);
}

int WlAudio::getPcmData(void **pcm) {
    while(1) {
        if(wlPlayStatus->play_status == 2)//停止
        {
            return -1;
        }
        if(wlPlayStatus->play_status == 1)//暂停
        {
            continue;
        }

        if(!avCodecContext)
        {
            return -1;
        }
        AVPacket *packet = av_packet_alloc();
        if(queue->getAvpacket(packet) != 0)
        {
            av_packet_free(&packet);
            continue;
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
        // 设置通道数或channel_layout
        if (frame->channels > 0 && frame->channel_layout == 0)
            frame->channel_layout = av_get_default_channel_layout(frame->channels);
        else if (frame->channels == 0 && frame->channel_layout > 0)
            frame->channels = av_get_channel_layout_nb_channels(frame->channel_layout);

        enum AVSampleFormat dst_format = AV_SAMPLE_FMT_S16;//av_get_packed_sample_fmt((AVSampleFormat)frame->format);
        SwrContext *swr_ctx;
        //重采样为立体声
        dst_layout = AV_CH_LAYOUT_STEREO;
        // 设置转换参数
        swr_ctx = swr_alloc_set_opts(NULL, dst_layout, dst_format, frame->sample_rate,
                                     frame->channel_layout,
                                     (enum AVSampleFormat) frame->format,
                                     frame->sample_rate, 0, NULL);
        if (!swr_ctx || (ret = swr_init(swr_ctx)) < 0) {
            LOGD("wrong3 ret %d", ret);
            av_frame_free(&frame);
            swr_free(&swr_ctx);
            av_packet_free(&packet);
            continue;
        }
        // 计算转换后的sample个数 a * b / c
        dst_nb_samples = av_rescale_rnd(
                swr_get_delay(swr_ctx, frame->sample_rate) + frame->nb_samples,
                frame->sample_rate, frame->sample_rate, AV_ROUND_INF);
        // 转换，返回值为转换后的sample个数
        nb = swr_convert(swr_ctx, &out_buffer, dst_nb_samples,
                         (const uint8_t **) frame->data, frame->nb_samples);

        //根据布局获取声道数
        out_channels = av_get_channel_layout_nb_channels(dst_layout);
        data_size = out_channels * nb * av_get_bytes_per_sample(dst_format);
        av_frame_free(&frame);
        swr_free(&swr_ctx);
        clock = packet->pts * av_q2d(time_base);
        LOGE("clock is %f", clock);
        av_packet_free(&packet);
        *pcm = out_buffer;
        break;
    }
    return data_size;
}


