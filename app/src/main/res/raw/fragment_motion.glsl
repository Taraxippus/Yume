#version 100
precision mediump float;
precision mediump int;

uniform sampler2D u_Texture;
uniform sampler2D u_Depth;

varying vec2 v_UV;

uniform sampler2D uTexLinearDepth;

uniform mat4 uInverseModelViewMat; // inverse model->view
uniform mat4 uPrevModelViewProj; // previous model->view->projection

noperspective varying vec2 vTexcoord;
noperspective varying vec3 vViewRay; // for extracting current world space position
 
void main()
{
   // get current world space position:
      vec3 current = vViewRay * texture(uTexLinearDepth, vTexcoord).r;
      current = uInverseModelViewMat * current;
 
   // get previous screen space position:
      vec4 previous = uPrevModelViewProj * vec4(current, 1.0);
      previous.xyz /= previous.w;
      previous.xy = previous.xy * 0.5 + 0.5;

      vec2 blurVec = previous.xy - vTexcoord;
}
