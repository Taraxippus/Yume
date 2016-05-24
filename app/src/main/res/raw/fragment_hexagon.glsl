#version 100
precision mediump float;

uniform vec4 u_Color;
uniform vec2 u_Specularity;
uniform vec3 u_Eye;
uniform vec3 u_Light;
uniform float u_Time;

varying vec3 v_Normal;
varying vec3 v_Position;

const float c_Ambient = 0.5;

const float A = 0.1;
const float B = 0.3;
const float C = 0.6;
const float D = 1.0;

vec3 rotate(in vec3 vecIn, float z)
{
	vec3 vecOut = vec3(0, 0, vecIn.z);
	vecOut.x = vecIn.x * cos(z) + vecIn.y * -sin(z);
	vecOut.y = vecIn.x * -sin(z) + vecIn.y * cos(z);

	return vecOut;
}

void main()
{
	vec3 normal = normalize(v_Normal);

	float diff = max(0.0, dot(normal, -u_Light));
	float diff2 = max(0.0, dot(normal, u_Light));
	float spec = max(0.0, pow(dot(normalize(u_Eye - v_Position), normalize(reflect(u_Light, normal))), u_Specularity.x));
	float spec2 = max(0.0, pow(dot(normalize(u_Eye - v_Position), normalize(reflect(-u_Light, normal))), u_Specularity.x * 0.5));
	
	if (diff == 0.0)
		spec = 0.0;
	if (diff2 == 0.0)
		spec2 = 0.0;
		
	diff = diff * 0.875 + diff2 * 0.125;
	spec = spec + spec2;
	
	if (diff < A) diff = diff * 0.125;
    else if (diff < B) diff = A + (diff - A) * 0.25;
    else if (diff < C) diff = B + (diff - B) * 0.5;
    else diff = C + (diff - C) * 0.75;
    
    spec = step(0.25, spec) + max(0.0, spec - 0.25) * 0.75;
	
	gl_FragColor = vec4((rotate(normal, u_Time) * 0.5 + vec3(0.5)) * u_Color.rgb * (c_Ambient + diff * (1.0 - c_Ambient) + spec * u_Specularity.y), u_Color.a);
}

