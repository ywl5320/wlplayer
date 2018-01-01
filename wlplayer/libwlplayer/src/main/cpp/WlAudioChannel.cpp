//
// Created by ywl on 2017-12-29.
//

#include "WlAudioChannel.h"

WlAudioChannel::WlAudioChannel(int id, AVRational base) {
    channelId = id;
    time_base = base;
}

WlAudioChannel::WlAudioChannel(int id, AVRational base, int f) {
    channelId = id;
    time_base = base;
    fps = f;
}
