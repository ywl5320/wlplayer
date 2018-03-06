//
// Created by ywl on 2017-12-1.
//

#ifndef WLPLAYER_WLFFMPEG_H
#define WLPLAYER_WLFFMPEG_H

#include "AndroidLog.h"
#include "pthread.h"
#include "WlBasePlayer.h"
#include "WlJavaCall.h"
#include "WlAudio.h"
#include "WlVideo.h"
#include "WlPlayStatus.h"
#include "WlAudioChannel.h"

extern "C"
{
#include <libavformat/avformat.h>
}


class WlFFmpeg {

public:
    const char *urlpath = NULL;
    WlJavaCall *wlJavaCall = NULL;
    pthread_t decodThread;
    AVFormatContext *pFormatCtx = NULL;//封装格式上下文
    int duration = 0;
    WlAudio *wlAudio = NULL;
    WlVideo *wlVideo = NULL;
    WlPlayStatus *wlPlayStatus = NULL;
    bool exit = false;
    bool exitByUser = false;
    int mimeType = 1;
    bool isavi = false;
    bool isOnlyMusic = false;

    std::deque<WlAudioChannel*> audiochannels;
    std::deque<WlAudioChannel*> videochannels;

    pthread_mutex_t init_mutex;
    pthread_mutex_t seek_mutex;

public:
    WlFFmpeg(WlJavaCall *javaCall, const char *urlpath, bool onlymusic);
    ~WlFFmpeg();
    int preparedFFmpeg();
    int decodeFFmpeg();
    int start();
    int seek(int64_t sec);
    int getDuration();
    int getAvCodecContext(AVCodecParameters * parameters, WlBasePlayer *wlBasePlayer);
    void release();
    void pause();
    void resume();
    int getMimeType(const char* codecName);
    void setAudioChannel(int id);
    void setVideoChannel(int id);
    int getAudioChannels();
    int getVideoWidth();
    int getVideoHeight();
};


#endif //WLPLAYER_WLFFMPEG_H
