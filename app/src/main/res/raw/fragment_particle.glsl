#version 100
precision mediump float;

varying vec4 v_Color;
varying vec2 v_Direction;

void main()
{
	gl_FragColor = v_Color;
}

