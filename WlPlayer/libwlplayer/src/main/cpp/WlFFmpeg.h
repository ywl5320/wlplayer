//
// Created by ywl on 2017-12-1.
//

#ifndef WLPLAYER_WLFFMPEG_H
#define WLPLAYER_WLFFMPEG_H

#include "AndroidLog.h"
#include "WlListener.h"
#include "pthread.h"
#include "WlAudio.h"
#include "WlVideo.h"
#include "WlPlayStatus.h"

extern "C"
{
#include <libavformat/avformat.h>
}


class WlFFmpeg {

public:
    const char *urlpath;
    pthread_t decodThread;
    WlListener *wlListener;
    WlAudio *wlAudio;
    WlVideo *wlVideo;
    WlPlayStatus *wlPlayStatus;
    AVFormatContext *pFormatCtx;//封装格式上下文

public:
    WlFFmpeg(WlListener *listener, WlAudio *wlAudio, WlVideo *wlVideo, WlPlayStatus *wlPlayStatus, const char *urlpath);
    ~WlFFmpeg();
    int initFFmpeg();
    int decodeFFmpeg();

    int getAvCodecContext(AVCodecParameters * parameters, WlBasePlayer *wlBasePlayer);

    void release();

};


#endif //WLPLAYER_WLFFMPEG_H
