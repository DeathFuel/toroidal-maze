#version 330 core

out vec4 color;
in vec2 pos;
in float time;
in vec2 exitPos;
in float tileSize;

const float PI = 3.14159265358f;

void main() {
    float t = tileSize / 360.;
    vec2 center = vec2(exitPos.x + t/2. - pos.x, exitPos.y - t/2. - pos.y);
    vec3 rainbow = vec3(
        sin(time/7),
        sin(time/7 + 2*PI/3),
        sin(time/7 + 4*PI/3)
    );
    rainbow = (rainbow + 2) * 3 / 8;
    color = vec4(rainbow, 1);
    float pulse = sin(sqrt(7) * time)/4. + 3.25;
    center *= 4.5;
    color *= pulse * 0.003 / pow(abs(pow(center.x, 6) + pow(center.y, 6) - pow(tileSize/180., 6)), 0.15);
    color *= tileSize;
    color.a *= 0.5;
    color.a = pow(color.a, 2);
}