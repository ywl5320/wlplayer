//
// Created by ywl on 2017-12-7.
//

#pragma once
#ifndef WLPLAYER_WLPLAYSTATUS_H
#define WLPLAYER_WLPLAYSTATUS_H


class WlPlayStatus {

public:
    /**
     * 0：播放
     * 1：暂停
     * 2：停止
     */
    int play_status;

public:
    WlPlayStatus();
    ~WlPlayStatus();

};


#endif //WLPLAYER_WLPLAYSTATUS_H
