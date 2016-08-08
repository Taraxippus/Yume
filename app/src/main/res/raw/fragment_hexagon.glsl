#version 100
precision mediump float;

uniform float u_Fog;
varying vec4 v_Color;

void main()
{
	gl_FragColor = vec4(mix(v_Color.rgb, vec3(1.0), 1.0 - exp(-(gl_FragCoord.z / gl_FragCoord.w * gl_FragCoord.z / gl_FragCoord.w) * u_Fog)), v_Color.a);
}

