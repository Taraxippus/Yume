#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Direction;

uniform mat4 u_M;
uniform mat4 u_N;
uniform mat4 u_M2;
uniform mat4 u_N2;
uniform mat4 u_VP;
uniform float u_Time;

const float PI = 3.14159265359;

void main()
{
	vec4 position = a_Position;
	position.xy *= sin(a_Direction.x * 10.0 + a_Direction.y * PI * 2.0 + u_Time * 2.0) * 0.05 + 0.9;
	
	vec3 normal = normalize(vec3(u_N * vec4(a_Normal, 0.0)) * (0.5 - a_Position.z) + vec3(u_N2 * vec4(a_Normal, 0.0)) * (0.5 + a_Position.z));
	position = vec4(vec3(u_M * position) * (0.5 - a_Position.z) + vec3(u_M2 * position) * (0.5 + a_Position.z), 1.0);
	normal *= 0.01 + max((u_VP * position).w, 1.0) * 0.005;
	gl_Position = u_VP * vec4(position.xyz + normal, 1.0);

	//gl_Position.z += 0.0075;
}
