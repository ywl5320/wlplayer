//
// Created by ywl on 2017-12-4.
//

#ifndef WLPLAYER_WLOPENGLES_H
#define WLPLAYER_WLOPENGLES_H

#include <GLES2/gl2.h>
#include "AndroidLog.h"

class WlOpenGLES {

public:
    GLuint vertex_shader;
    GLuint fragment_shader;
    GLuint program;
    GLint vertex_pos;
    GLint in_texture_pos;
    GLint sampler_y;
    GLint sampler_u;
    GLint sampler_v;
    GLuint texture[3];
public:

    WlOpenGLES();
    ~WlOpenGLES();

    void setRenderGLSL(const char *vertex, const char *fragment);
    void loadShader(GLuint shader, const char *code);

    void setWinSize(int w, int h);

    void draw(int w, int h, const void *y, const void *u, const void *v);

    void release();

};


#endif //WLPLAYER_WLOPENGLES_H
