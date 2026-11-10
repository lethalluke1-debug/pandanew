#version 330

// Lethal Client sky: a night sky with a colour gradient, twinkling stars, a glowing galaxy band,
// northern lights and shooting stars. The look comes from the preset (see LethalSky.java):
//   TextureMat[0] = top colour, aurora strength      TextureMat[1] = horizon colour, star amount
//   TextureMat[2] = aurora colour 1, galaxy strength  TextureMat[3] = aurora colour 2, shooting stars
//   ColorModulator.x = time in seconds
layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
    mat4 TextureMat;
};

in vec3 skyDir;

out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float hash3(vec3 p) {
    return fract(sin(dot(p, vec3(127.1, 311.7, 74.7))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x), mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p = p * 2.03 + vec2(17.0, 9.0);
        a *= 0.5;
    }
    return v;
}

// Distance from p to the segment a-b, and how far along it (0 at a, 1 at b).
vec2 segment(vec2 p, vec2 a, vec2 b) {
    vec2 ab = b - a;
    float k = clamp(dot(p - a, ab) / dot(ab, ab), 0.0, 1.0);
    return vec2(length(p - a - ab * k), k);
}

void main() {
    vec3 d = normalize(skyDir);
    float t = ColorModulator.x;
    vec3 top = TextureMat[0].rgb;
    float auroraStrength = TextureMat[0].a;
    vec3 horizon = TextureMat[1].rgb;
    float starAmount = TextureMat[1].a;
    vec3 aurora1 = TextureMat[2].rgb;
    float galaxy = TextureMat[2].a;
    vec3 aurora2 = TextureMat[3].rgb;
    float shooting = TextureMat[3].a;

    float h = d.y;
    vec3 col = mix(horizon, top, pow(clamp(h, 0.0, 1.0), 0.5));
    if (h < 0.0) {
        col = horizon * (1.0 + h * 0.8);
    }
    float upper = smoothstep(-0.02, 0.12, h);

    // Galaxy band across the sky.
    if (galaxy > 0.0) {
        vec3 n = normalize(vec3(0.35, 0.55, 0.75));
        float across = dot(d, n);
        float band = exp(-across * across / 0.035);
        // Parameterise the band on a tangent plane to n (no atan, so there is no meridian seam).
        vec3 tA = normalize(cross(n, vec3(0.0, 1.0, 0.0)));
        vec3 tB = cross(n, tA);
        vec2 q = vec2(dot(d, tA) * 6.0, dot(d, tB) * 6.0 + across * 6.0);
        float clouds = fbm(q * 1.6 + vec2(t * 0.004, 0.0));
        float dust = fbm(q * 4.0 + 3.0);
        vec3 tint = mix(aurora1, aurora2, clamp(clouds * 1.4 - 0.2, 0.0, 1.0));
        col += tint * band * clouds * clouds * galaxy * 1.1 * upper;
        col *= 1.0 - band * smoothstep(0.55, 0.8, dust) * 0.35 * galaxy;
    }

    // Stars, with more of them near the galaxy band, twinkling.
    vec3 sd = d * 260.0;
    vec3 cell = floor(sd);
    float r = hash3(cell);
    float chance = 0.014 * starAmount;
    if (r < chance) {
        vec3 c = cell + vec3(hash3(cell + 1.3), hash3(cell + 7.1), hash3(cell + 3.7));
        float dist = length(sd - c);
        float size = 0.18 + 0.25 * fract(r * 913.0);
        float twinkle = 0.55 + 0.45 * sin(t * (1.5 + 3.0 * fract(r * 71.0)) + r * 600.0);
        float glow = smoothstep(size, 0.0, dist);
        vec3 starCol = mix(vec3(0.75, 0.85, 1.0), vec3(1.0, 0.9, 0.75), fract(r * 37.0));
        col += starCol * glow * twinkle * 1.6 * upper;
    }

    // Northern lights: waving ribbons with vertical rays, high in the sky.
    if (auroraStrength > 0.0 && h > 0.0) {
        vec2 p = d.xz / (h + 0.18);
        float lights = 0.0;
        for (int i = 0; i < 3; i++) {
            float fi = float(i);
            float x = p.x * 0.45 + fi * 1.7;
            float centre = sin(x + t * 0.11 * (1.0 + fi * 0.35)) * 0.9 + (fbm(vec2(x * 0.9 + t * 0.03, fi * 5.0)) - 0.5) * 2.4 + fi * 0.9 - 0.6;
            float dy = p.y - centre;
            // Sharp bottom edge that fades upward, like real aurora curtains.
            float curtain = dy > 0.0 ? exp(-dy * 1.6) : exp(-dy * dy * 40.0);
            curtain *= smoothstep(-0.25, 0.0, dy + 0.05);
            float rays = 0.35 + 0.65 * pow(noise(vec2(p.x * 14.0 + t * 0.5 + fi * 13.0, fi * 2.0)), 1.5);
            lights += curtain * rays * (1.0 - fi * 0.22);
        }
        float fade = smoothstep(0.03, 0.3, h) * (1.0 - smoothstep(0.85, 1.0, h));
        // First colour low down, second colour higher up.
        float shift = clamp((h - 0.2) * 1.6 + 0.2 * sin(p.x * 0.4 + t * 0.15), 0.0, 1.0);
        vec3 ac = mix(aurora1, aurora2, shift);
        col += ac * lights * fade * auroraStrength * 0.95;
    }

    // Shooting stars: a few at a time, each a quick bright streak with a fading tail.
    if (shooting > 0.0 && h > 0.05) {
        vec2 p = d.xz / (h + 0.25);
        for (int i = 0; i < 4; i++) {
            float fi = float(i);
            float period = (5.0 + fi * 1.7) / shooting;
            float seed = floor((t + fi * 3.1) / period);
            float phase = fract((t + fi * 3.1) / period) * period;
            float life = 0.9;
            if (phase < life && hash(vec2(seed, fi)) < 0.85) {
                float k = phase / life;
                vec2 start = vec2(hash(vec2(seed, fi + 1.0)) * 6.0 - 3.0, hash(vec2(seed, fi + 2.0)) * 4.0 - 2.0);
                float ang = hash(vec2(seed, fi + 3.0)) * 6.2832;
                vec2 dir = vec2(cos(ang), sin(ang));
                vec2 head = start + dir * (k * 2.2);
                vec2 tail = head - dir * 0.9 * min(1.0, k * 3.0);
                vec2 s = segment(p, tail, head);
                float width = 0.007 + 0.011 * s.y;
                float streak = exp(-s.x * s.x / (width * width)) * s.y * s.y;
                float flash = sin(k * 3.1416);
                col += vec3(1.0, 0.97, 0.9) * streak * flash * 2.2;
            }
        }
    }

    fragColor = vec4(col, 1.0);
}
