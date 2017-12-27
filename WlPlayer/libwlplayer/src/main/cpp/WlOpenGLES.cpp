//
// Created by ywl on 2017-12-4.
//

#include "WlOpenGLES.h"

const float VERTEX[] = {
        -1, -1,
        1, -1,
        -1, 1,
        1, 1
};

const float TEXTURE[] = {
        0, 1,
        1, 1,
        0, 0,
        1, 0
};

WlOpenGLES::WlOpenGLES() {

}

WlOpenGLES::~WlOpenGLES() {
    LOGE("~WlOpenGLES()");
}

void WlOpenGLES::loadShader(GLuint shader, const char *code) {
//加载着色器代码 count,char** ,int*;
    glShaderSource(shader, 1, &code, 0);
    //编译
    glCompileShader(shader);
    // 获得编译状态
    GLint ret;
    glGetShaderiv(shader, GL_COMPILE_STATUS, &ret);
    //如果为0 表示失败
    if (!ret) {
        GLchar error[1024];
        glGetShaderInfoLog(shader, 1024, 0, error);
        LOGE("compile shader error:%s", error);
    }
}

void WlOpenGLES::setWinSize(int w, int h) {
    glViewport(0, 0, w, h);
}

void WlOpenGLES::draw(int w, int h, const void *y, const void *u, const void *v) {

//清屏
    glClearColor(0, 0, 0, 0);
    //清理缓存
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    //使 GL_TEXTURE0 单元 活跃 opengl最多支持16个纹理
    //纹理单元是显卡中所有的可用于在shader中进行纹理采样的显存 数量与显卡类型相关，至少16个
    glActiveTexture(GL_TEXTURE0);
    //绑定纹理空间 下面的操作就会作用在这个空间中
    glBindTexture(GL_TEXTURE_2D, texture[0]);
    //创建一个2d纹理 使用亮度颜色模型并且纹理数据也是亮度颜色模型
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, w, h, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, y);
    //绑定采样器与纹理单元
    glUniform1i(sampler_y, 0);


    glActiveTexture(GL_TEXTURE1);
    glBindTexture(GL_TEXTURE_2D, texture[1]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, w / 2, h / 2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE,
                 u);
    glUniform1i(sampler_u, 1);
//
    glActiveTexture(GL_TEXTURE2);
    glBindTexture(GL_TEXTURE_2D, texture[2]);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, w / 2, h / 2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE,
                 v);
    glUniform1i(sampler_v, 2);

    //画 从0开始4个订单
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);

    //解绑
    glBindTexture(GL_TEXTURE_2D, 0);
}

void WlOpenGLES::release() {
    glDeleteShader(vertex_shader);
    glDeleteShader(fragment_shader);
    glDeleteProgram(program);
}

void WlOpenGLES::setRenderGLSL(const char *vertex, const char *fragment) {
//把屏幕变成黑色
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //创建顶点着色器
    vertex_shader = glCreateShader(GL_VERTEX_SHADER);
    loadShader(vertex_shader, vertex);
    //创建片元着色器
    fragment_shader = glCreateShader(GL_FRAGMENT_SHADER);
    loadShader(fragment_shader, fragment);
    //创建着色器程序
    program = glCreateProgram();
    //着色器程序绑定着色器
    glAttachShader(program, vertex_shader);
    glAttachShader(program, fragment_shader);
    //连接着色器程序
    glLinkProgram(program);
    //获得连接状态
    GLint ret;
    glGetProgramiv(program, GL_LINK_STATUS, &ret);
    if (!ret) {
        GLchar error[1024];
        glGetProgramInfoLog(program, 1024, 0, error);
        LOGE("link program error:%s", error);
    }
//    program.getAttribute("vertex_pos");
    //获得属性与采样器的索引
    vertex_pos = glGetAttribLocation(program, "vertex_pos");
    in_texture_pos = glGetAttribLocation(program, "in_texture_pos");
    sampler_y = glGetUniformLocation(program, "sampler_y");
    sampler_u = glGetUniformLocation(program, "sampler_u");
    sampler_v = glGetUniformLocation(program, "sampler_v");

    //使用着色器
    glUseProgram(program);

    //将顶点数据传入着色器
    glVertexAttribPointer(vertex_pos, 2, GL_FLOAT, false, 0, VERTEX);
    //让其有效
    glEnableVertexAttribArray(vertex_pos);
    glVertexAttribPointer(in_texture_pos, 2, GL_FLOAT, false, 0, TEXTURE);
    glEnableVertexAttribArray(in_texture_pos);

    //创建纹理空间
    glGenTextures(3, texture);
    for (int i = 0; i < 3; ++i) {
        // 绑定纹理空间
        glBindTexture(GL_TEXTURE_2D, texture[i]);
        //设置属性 当显示的纹理比加载的纹理大时 使用纹理坐标中最接近的若干个颜色 通过加权算法获得绘制颜色
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // 比加载的小
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // 如果纹理坐标超出范围 0,0-1,1 坐标会被截断在范围内
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }


    //解除绑定
    glBindTexture(GL_TEXTURE_2D, 0);
}
