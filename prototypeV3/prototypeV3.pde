/* Author: Sebastian Oley 
 * Project: MS Werner, FH Augsburg
 * 5/3/16
 */

import processing.video.*;

Movie lava;
PImage mask;
color[][] matrix;
int matrixHorizontalSegments = 60;
int matrixVerticalSegments; // will be set dynamically, depends on horizonal Segments and video res
int[][] size;
int scaling = 6;
int minSize = scaling * 3;
int medSize = scaling * 4;
int maxSize = scaling * 5;
PVector[][] pos;

void setup() {
  //size(1920, 1080);
  fullScreen();
  
  // load video content + mask
  lava = new Movie(this, "file02.mp4");
  lava.loop();
  mask = loadImage("mask7.jpg");
  
  // calc size
  matrixVerticalSegments = height / (width / matrixHorizontalSegments);
  println("matrix size=" + matrixHorizontalSegments + "x" + matrixVerticalSegments);
  
  matrix = new color[matrixHorizontalSegments][matrixVerticalSegments];
  size = new int[matrixHorizontalSegments][matrixVerticalSegments];
  pos = new PVector[matrixHorizontalSegments][matrixVerticalSegments];
  
  // init mask
  mask.resize(matrixHorizontalSegments, matrixVerticalSegments);
  
  for (int i = 0; i < matrixHorizontalSegments; i++) {
    for (int k = 0; k < matrixVerticalSegments; k++) {
      int loc = i + k*matrixHorizontalSegments;
      // Test the brightness
      // println(brightness(mask.pixels[loc]));
      if (brightness(mask.pixels[loc]) < 95) {
        size[i][k] = minSize;
      }  else if (brightness(mask.pixels[loc]) < 180) {
        size[i][k] = medSize;
      }  else {
        size[i][k] = maxSize;
      }
    }
  }
  
  // init led pos
  for (int i = 0; i < matrixHorizontalSegments; i++) {
    int ypos = 0;
    for (int k = 0; k < matrixVerticalSegments; k++) {
      int ledSize = size[i][k];
      float spacing = (width / matrixHorizontalSegments - ledSize) / 2;
      float offset = random(-spacing, spacing);
      
      pos[i][k] = new PVector(i * width/matrixHorizontalSegments + offset, ypos + ledSize/2);
      
      ypos += ledSize;
    }
  }
}

void draw() {
  // paint current frame of lava
  image(lava, 0, 0, matrixHorizontalSegments, matrixVerticalSegments);
  
  // fill led array using painted image
  for (int i = 0; i < matrixHorizontalSegments; i++) {
    for (int k = 0; k < matrixVerticalSegments; k++) {
      matrix[matrixHorizontalSegments - 1 - i][k] = get(i, k);
    }
  }
  
  // draw LEDs
  background(0);
  noStroke();
  ellipseMode(CENTER);
  
  for (int i = 0; i < matrixHorizontalSegments; i++) {
    for (int k = 0; k < matrixVerticalSegments; k++) {
      int ledSize = size[i][k];
      
      fill(matrix[i][k]);
      // ellipse(i * width/matrixHorizontalSegments, k * height/matrixVerticalSegments, ledSize, ledSize);
      ellipse(pos[i][k].x, pos[i][k].y, ledSize, ledSize);
    }
  }
}

void movieEvent(Movie m) {
  m.read();
}