#version 100
precision mediump float;

uniform sampler2D u_Texture;

varying vec2 v_UV1;
varying vec2 v_UV2;
varying vec2 v_UV3;
varying vec2 v_UV4;
varying vec2 v_UV5;

vec4 getBloom(in vec2 UV)
{
	vec4 pixel = texture2D(u_Texture, UV);
	return vec4(pixel.rgb * (max(0.0, dot(pixel, pixel) - 3.5) + 0.5 * max(0.0, pixel.b - pixel.r - 0.25) + 0.25 * max(0.0, pixel.r - pixel.b - 0.25)), 1.0);
}

void main()
{
	gl_FragColor = getBloom(v_UV1) * 0.06136;
	gl_FragColor += getBloom(v_UV2) * 0.24477;
	gl_FragColor += getBloom(v_UV3) * 0.38774;
	gl_FragColor += getBloom(v_UV4) * 0.24477;
	gl_FragColor += getBloom(v_UV5) * 0.06136;
}

