#version 330 core

layout (location = 0) in vec3 aVertex;

uniform mat4 uMVP;

void main()
{
    gl_Position = uMVP * vec4(aVertex, 1.0);
}