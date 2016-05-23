#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;

uniform mat4 u_M;
uniform mat4 u_N;
uniform mat4 u_VP;

void main()
{
	vec3 normal = normalize(vec3(u_N * vec4(a_Normal, 0.0)));
	normal *= (u_VP * u_M * a_Position).w * 0.005;
	gl_Position = u_VP * (u_M * a_Position + vec4(normal, 0.0));
}
