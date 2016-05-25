#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Direction;

uniform mat4 u_MVP;
uniform mat4 u_M;
uniform mat4 u_N;
uniform float u_Time;

varying vec3 v_Normal;
varying vec3 v_Position;

const float PI = 3.14159265359;

void main()
{
	vec4 position = a_Position;
	position.xy *= sin(a_Direction.x * 10.0 + a_Direction.y * PI * 2.0 + u_Time * 2.0) * 0.05 + 0.9;
	
	v_Normal = normalize(vec3(u_N * vec4(a_Normal, 0.0)));
	v_Position = vec3(u_M * position);

	gl_Position = u_MVP * position;
}
