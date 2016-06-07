#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Direction;

uniform mat4 u_VP;
uniform mat4 u_M;
uniform mat4 u_N;
uniform mat4 u_M2;
uniform mat4 u_N2;
uniform float u_Time;
uniform vec4 u_Color;

varying vec4 v_Normal;
varying vec3 v_Position;
varying vec4 v_Color;

const float PI = 3.14159265359;

void main()
{
	vec4 position = a_Position;
	position.xy *= sin(a_Direction.x * 10.0 + a_Direction.y * PI * 2.0 + u_Time * 2.0) * 0.05 + 0.9;
	v_Color = a_Direction.x == 0.0 ? vec4(0.2, 0.2, 0.2, 1.0) : u_Color;
	
	v_Normal = (u_N * vec4(a_Normal, 0.0)) * (0.5 - a_Position.z) + (u_N2 * vec4(a_Normal, 0.0)) * (0.5 + a_Position.z);
	v_Normal.w = a_Direction.x / PI / 2.0;
	v_Position = vec3(u_M * position) * (0.5 - a_Position.z) + vec3(u_M2 * position) * (0.5 + a_Position.z);
	gl_Position = u_VP * vec4(v_Position, 1.0);
}
