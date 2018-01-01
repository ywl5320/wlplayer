precision mediump float;
varying vec2 v_texPo;
uniform sampler2D sampler_y;
uniform sampler2D sampler_u;
uniform sampler2D sampler_v;
void main() {
    float y,u,v;
    y = texture2D(sampler_y,v_texPo).x;
    u = texture2D(sampler_u,v_texPo).x- 128./255.;
    v = texture2D(sampler_v,v_texPo).x- 128./255.;

    vec3 rgb;
    rgb.r = y + 1.403 * v;
    rgb.g = y - 0.344 * u - 0.714 * v;
    rgb.b = y + 1.770 * u;

    gl_FragColor = vec4(rgb,1);
}