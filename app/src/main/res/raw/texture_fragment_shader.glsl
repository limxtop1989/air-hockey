precision mediump float;

// refers to an array of two-dimensional texture data
uniform sampler2D u_TextureUnit;
// interpolated texture coordinates
varying vec2 v_TextureCoordinates;

void main() {
    // read in the color value for the texture at that particular coordinate
    gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);
}