#version 330 core

out vec4 color;
in vec2 pos;
in float time;

const float PI = 3.14159265358f;
const int ITERS = 7;

vec2 rotate(vec2 v, float angle) {
    return vec2(cos(angle) * v.x + sin(angle) * v.y, -sin(angle) * v.x + cos(angle) * v.y);
}

void main() {
    vec2 p = 2 * rotate(pos, ITERS*time/sqrt(61));
    color = vec4(0,0,0,1);
    for (int i = 1; i < ITERS; i++) {
        vec4 tmp = vec4(0);
        p *= 0.75;
        float glowsq = 0.000007 * (ITERS - i + 3) / pow(abs(pow(p.x, 6) + pow(p.y, 6) - 0.0075), 1.25);
        float rem = 0;
        if (glowsq > 1) { rem = glowsq - 1; glowsq = 1; }
        tmp = vec4(glowsq, glowsq, glowsq, 1);
        vec4 rainbow = vec4(
            sin(i/1.5 + time/9),
            sin(i/1.5 + time/9 + 2*PI/3),
            sin(i/1.5 + time/9 + 4*PI/3),
            1
        );
        rainbow /= 2;
        rainbow += vec4(0.75);
        rainbow.w = 1;
        tmp *= rainbow;
        tmp = pow(tmp, vec4(0.75));
        tmp += rainbow/48;
        tmp += vec4(rem, rem, rem, 1);
        color += clamp(tmp, 0, clamp(((1.4-i/20.) * (time+0.65) - pow(i,1.2)/24), 0, 1));
        p = rotate(p, -time/sqrt(37));
    }
    color *= 0.75;
}