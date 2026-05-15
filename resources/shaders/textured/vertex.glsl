#version 330 core

layout (location = 0) in vec3 aVertex;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aUV;

uniform mat4 uMVP;

out vec2 vUV;

void main()
{
    gl_Position = uMVP * vec4(aVertex, 1.0);
    vUV = aUV;
}