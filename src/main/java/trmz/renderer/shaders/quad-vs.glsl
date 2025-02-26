#version 330 core

layout (location = 0) in vec2 pos;
out vec2 textureCoords;

uniform mat4 transform;

void main()
{
    gl_Position = transform * vec4(pos.xy, 0.0f, 1.0f);
    textureCoords = pos;
}