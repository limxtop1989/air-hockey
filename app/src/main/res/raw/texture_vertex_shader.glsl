uniform mat4 u_Matrix;

attribute vec4 a_Position;
//  Two component: the S coordinate and the T coordinate
attribute vec2 a_TextureCoordinates;

varying vec2 v_TextureCoordinates;

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
//    gl_Position = a_Position;
    gl_Position = u_Matrix * a_Position;
}