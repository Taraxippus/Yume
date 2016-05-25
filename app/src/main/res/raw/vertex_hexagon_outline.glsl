#version 100
precision mediump float;

attribute vec4 a_Position;
attribute vec3 a_Normal;
attribute vec2 a_Direction;

uniform mat4 u_M;
uniform mat4 u_N;
uniform mat4 u_VP;
uniform float u_Time;

const float PI = 3.14159265359;

void main()
{
	vec4 position = a_Position;
	position.xy *= sin(a_Direction.x * 10.0 + a_Direction.y * PI * 2.0 + u_Time * 2.0) * 0.05 + 0.9;
	
	vec3 normal = normalize(vec3(u_N * vec4(a_Normal, 0.0)));
	normal *= max((u_VP * u_M * position).w, 1.0) * 0.006;
	gl_Position = u_VP * (u_M * position + vec4(normal, 0.0));
	//gl_Position.z += 0.0075;
}
