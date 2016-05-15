#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec2 a_UV;

uniform mat4 u_MVP;
uniform float u_Time;

varying vec2 v_UV;

void main()
{
	v_UV = vec2(a_UV.x + u_Time * 0.5, a_UV.y + u_Time);

	gl_Position = u_MVP * a_Position;
}
