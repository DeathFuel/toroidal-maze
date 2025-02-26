#version 330 core

layout (location = 0) in vec2 _pos;
out vec2 pos;
out float time;
out vec2 exitPos;
out float tileSize;

uniform float _time;
uniform vec2 _exitPos;
uniform float _tileSize;

void main()
{
    pos = _pos * 2 - 1;
    gl_Position = vec4(pos.xy, 0.0f, 1.0f);
    pos.x = 16.0 / 9.0 * pos.x;
    time = _time - 18000;
    exitPos = _exitPos;
    tileSize = _tileSize;
}