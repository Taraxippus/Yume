#version 100
precision mediump float;

uniform vec4 u_Color;
uniform vec2 u_Specularity;
uniform vec3 u_Eye;
uniform vec3 u_Light;

varying vec3 v_Normal;
varying vec3 v_Position;

const float c_Ambient = 0.3;

void main()
{
	vec3 normal = normalize(v_Normal);

	float diff = max(0.0, dot(normal, -u_Light));
	
	float spec = clamp(pow(max(0.0, dot(normalize(-reflect(normal, -u_Light)), normalize(u_Eye - v_Position))), u_Specularity.x), 0.0, 1.0);
	
	gl_FragColor = vec4(u_Color.rgb * (c_Ambient + diff + spec * u_Specularity.y), u_Color.a);
}

