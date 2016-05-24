#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;

uniform mat4 u_M;
uniform mat4 u_N;
uniform mat4 u_VP;
uniform float u_Time;

const float PI = 3.14159265359;

void main()
{
	vec4 position = a_Position;
	position.xy *= sin(acos(normalize(position).y) * 10.0 + position.z * PI * 2.0 + u_Time * 2.0) * 0.05 + 0.95;
	
	vec3 normal = normalize(vec3(u_N * vec4(a_Normal, 0.0)));
	normal *= (u_VP * u_M * position).w * 0.0075;
	gl_Position = u_VP * (u_M * position + vec4(normal, 0.0));
	gl_Position.z += 0.0075;
}
