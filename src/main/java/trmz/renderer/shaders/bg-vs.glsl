#version 330 core

layout (location = 0) in vec2 _pos;
out vec2 pos;
out float time;

uniform float _time;

void main()
{
    pos = _pos * 2 - 1;
    gl_Position = vec4(pos.xy, 0.0f, 1.0f);
    pos.x = 16.0 / 9.0 * pos.x;
    time = _time / 8. - 0.7;
}