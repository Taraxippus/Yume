#version 100
precision mediump float;
precision mediump int;

uniform sampler2D u_Texture;
uniform sampler2D u_Bloom;
uniform sampler2D u_Bloom2;

varying vec2 v_UV;

void main()
{
    gl_FragColor = texture2D(u_Texture, v_UV) + texture2D(u_Bloom, v_UV) + texture2D(u_Bloom2, v_UV);
}
