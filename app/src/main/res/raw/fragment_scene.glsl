#version 100
precision mediump float;

uniform vec4 u_Color;
uniform vec2 u_Specularity;
uniform vec3 u_Eye;
uniform vec3 u_Light;

varying vec3 v_Normal;
varying vec3 v_Position;

const float c_Ambient = 0.3;

const float A = 0.1;
const float B = 0.3;
const float C = 0.6;
const float D = 1.0;

void main()
{
	vec3 normal = normalize(v_Normal);

	float diff = max(0.0, dot(normal, -u_Light));
	float spec = max(0.0, pow(dot(normalize(u_Eye - v_Position), normalize(reflect(u_Light, normal))), u_Specularity.x));
	
	if (diff == 0.0)
		spec = 0.0;
	
	if (diff < A) diff = diff * 0.125;
    else if (diff < B) diff = A + (diff - A) * 0.25;
    else if (diff < C) diff = B + (diff - B) * 0.5;
    else diff = C + (diff - C) * 0.75;
    
    spec = step(0.25, spec) + max(0.0, spec - 0.25) * 0.75;
	
	gl_FragColor = vec4(u_Color.rgb * (c_Ambient + diff * (1.0 - c_Ambient) + spec * u_Specularity.y), u_Color.a);
}

