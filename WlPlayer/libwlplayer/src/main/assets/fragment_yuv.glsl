precision mediump float;
uniform sampler2D sampler_y;
uniform sampler2D sampler_u;
uniform sampler2D sampler_v;

varying vec2 texture_pos;

void main() {
    float y,u,v;

    //多个y对应一组uv uv存在负数并且单字节存储 读取的时候 -128才能得到原值
    // uv 在-.5到.5之间
    y = texture2D(sampler_y,texture_pos).x;
    u = texture2D(sampler_u,texture_pos).x-128. / 255.;
    v = texture2D(sampler_v,texture_pos).x-128. / 255.;

    vec3 rgb;
    rgb.r = y + 1.403 * v;
    rgb.g = y - 0.344 * u - 0.714 * v;
    rgb.b = y + 1.770 * u;
    gl_FragColor = vec4(rgb,1);
}
