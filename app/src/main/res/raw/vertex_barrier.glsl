#version 100
precision mediump float;
precision mediump int;

attribute vec4 a_Position;
attribute vec3 a_Normal;

uniform mat4 u_M;
uniform mat4 u_N;
uniform mat4 u_VP;

varying vec3 v_Position;

void main()
{
	v_Position = a_Position.xyz;
	vec3 normal = normalize(vec3(u_N * vec4(a_Normal, 0.0))) * 0.01;
	gl_Position = u_VP * (u_M * a_Position + vec4(normal, 0.0));
}
