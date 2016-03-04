#version 100
precision mediump float;

uniform vec3 u_ReflectionOffset;
uniform vec3 u_ReflectionDir;

uniform float u_AlphaStart;
uniform float u_AlphaFactor;

varying vec4 v_Color;
varying vec3 v_Position;
varying vec2 v_Direction;

void main()
{
	vec3 alphaFactor = (v_Position + u_ReflectionOffset) * u_ReflectionDir;
	float alphaStart = pow(u_AlphaStart, dot(u_ReflectionDir, u_ReflectionDir));

	gl_FragColor = v_Color;
	gl_FragColor.a *= clamp(alphaStart + (alphaFactor.x + alphaFactor.y + alphaFactor.z) * u_AlphaFactor, 0.0, alphaStart);
}

