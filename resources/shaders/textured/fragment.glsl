#version 330 core

in vec2 vUV;
out vec4 FragColor;

uniform sampler2D uTexture;
uniform vec4 uColor;

void main()
{
    vec4 textureColor = texture(uTexture, vUV);
    FragColor = uColor * textureColor;
}