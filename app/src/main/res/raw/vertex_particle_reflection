#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_Direction;

uniform mat4 u_MV;
uniform mat4 u_P;
uniform mat4 u_M;

varying vec4 v_Color;
varying vec3 v_Position;
varying vec2 v_Direction;

void main()
{
	v_Color = a_Color;
	v_Position = vec3(u_M * vec4(a_Position.xyz, 1.0));
	
	v_Direction = a_Direction;
	gl_Position = u_P * (vec4(a_Direction * a_Position.w, 0.0, 0.0) + u_MV * vec4(a_Position.xyz, 1.0));
}
