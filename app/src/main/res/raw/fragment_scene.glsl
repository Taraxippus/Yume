#version 100
precision mediump float;

uniform vec4 u_Color;
uniform vec3 u_Eye;

varying vec3 v_Normal;
varying vec3 v_Position;

const vec3 c_Light = vec3(0, 12.5, 0);
const float c_Ambient = 0.3;

void main()
{
	vec3 normal = normalize(v_Normal);
	vec3 light = normalize(c_Light - v_Position);

	float diff = max(0.0, dot(normal, light));
	
	float spec = clamp(pow(max(dot(normalize(-reflect(normal, light)), normalize(u_Eye - v_Position)), 0.0), u_Color.a), 0.0, 1.0);
	
	gl_FragColor = vec4(u_Color.rgb * (c_Ambient + diff + spec * 0.1), 1);
}

