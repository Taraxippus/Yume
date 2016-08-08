#version 100
precision mediump float;

attribute vec2 a_Position;

uniform vec2 u_Dir;

varying vec2 v_UV1;
varying vec2 v_UV2;
varying vec2 v_UV3;
varying vec2 v_UV4;
varying vec2 v_UV5;

void main()
{
	v_UV3 = a_Position * 0.5 + vec2(0.5, 0.5);
	v_UV1 = v_UV3.xy - 2.0 * u_Dir;
	v_UV2 = v_UV3.xy - 1.0 * u_Dir;
	v_UV4 = v_UV3.xy + 1.0 * u_Dir;
	v_UV5 = v_UV3.xy + 2.0 * u_Dir;
	
	gl_Position = vec4(a_Position, 0.0, 1.0);
}
