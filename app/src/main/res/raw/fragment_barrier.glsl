#version 100
precision mediump float;
precision mediump int;

uniform vec4 u_Color;
uniform float u_Fog;
uniform float u_Time;

varying vec3 v_Position;

const float PI = 3.14159265359;

void main()
{
	float alpha = u_Color.a * ((cos(v_Position.z * PI * 10.0 + u_Time - abs(v_Position.x * PI * 2.0) + abs(v_Position.y * PI)) + 1.0) * 0.125 + 0.75);
	gl_FragColor = vec4(mix(u_Color.rgb, vec3(1.0), 1.0 - exp(-(gl_FragCoord.z / gl_FragCoord.w * gl_FragCoord.z / gl_FragCoord.w) * u_Fog)), alpha);
}

