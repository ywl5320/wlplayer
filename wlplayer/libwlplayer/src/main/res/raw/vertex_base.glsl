attribute vec4 av_Position;//定点坐标，应用传入
attribute vec2 af_Position;//采样纹理坐标，应用传入
varying vec2 v_texPo;//把获取的纹理坐标传入fragment里面
void main() {
    v_texPo = af_Position;
    gl_Position = av_Position;
}