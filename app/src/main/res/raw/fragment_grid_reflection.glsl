#version 100
precision mediump float;

uniform vec4 u_Color;
uniform vec2 u_Specularity;
uniform vec3 u_Eye;
uniform vec3 u_Light;
uniform vec3 u_ReflectionOffset;
uniform vec3 u_ReflectionDir;
uniform vec4 u_Center;

uniform float u_AlphaStart;
uniform float u_AlphaFactor;

varying vec3 v_Normal;
varying vec3 v_Position;

const float c_Ambient = 0.3;

void main()
{
	vec3 normal = normalize(v_Normal);
	vec3 light = normalize(u_Light - v_Position);

	float diff = max(0.0, dot(normal, light));
	float spec = clamp(pow(max(dot(normalize(-reflect(normal, light)), normalize(u_Eye - v_Position)), 0.0), u_Specularity.x), 0.0, 1.0);
	
	vec3 alphaFactor = (v_Position + u_ReflectionOffset) * u_ReflectionDir;
	float alphaStart = pow(u_AlphaStart, dot(u_ReflectionDir, u_ReflectionDir));
	
	gl_FragColor = vec4(u_Color.rgb * (c_Ambient + diff + spec * u_Specularity.y), u_Color.a * clamp(alphaStart + (alphaFactor.x + alphaFactor.y + alphaFactor.z) * u_AlphaFactor, 0.0, alphaStart) * max(0.0, 1.0 - length(u_Center.xyz - v_Position.xyz) / u_Center.w));
}
