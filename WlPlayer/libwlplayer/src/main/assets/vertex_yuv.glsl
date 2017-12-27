//要画的轮廓顶点
attribute vec4 vertex_pos;
//采样的纹理坐标
attribute vec2 in_texture_pos;
varying vec2 texture_pos;

void main() {
    gl_Position = vertex_pos;
    texture_pos = in_texture_pos;
}
