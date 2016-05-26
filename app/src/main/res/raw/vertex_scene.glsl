#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;

uniform mat4 u_MVP;
uniform mat4 u_M;
uniform mat4 u_N;

varying vec3 v_Normal;
varying vec3 v_Position;

void main()
{
	v_Normal = vec3(u_N * vec4(a_Normal, 0.0));
	v_Position = vec3(u_M * a_Position);

	gl_Position = u_MVP * a_Position;
}
