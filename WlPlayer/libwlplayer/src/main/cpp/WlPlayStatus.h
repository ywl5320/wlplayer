//
// Created by hlwky001 on 2017/12/18.
//

#ifndef WLPLAYER_WLPLAYSTATUS_H
#define WLPLAYER_WLPLAYSTATUS_H


class WlPlayStatus {

public:
    bool exit;
    bool pause;
    bool load;
    bool seek;

public:
    WlPlayStatus();
    ~WlPlayStatus();

};


#endif //WLPLAYER_WLPLAYSTATUS_H
