/* Author: Sebastian Oley */

import processing.video.*;

Capture cam; // using cam for now, must be replaced by video stream later on

color[][] matrix;
int matrixHorizontalSegments = 25;
int matrixVerticalSegments; // will be set dynamically, depends on horizonal Segments and video res
int ledSize = 50;

void setup() {
  //size(640, 480);
  size(1920, 1080);
  //size(1280, 960);

  String[] cameras = Capture.list();
  
  if (cameras.length == 0) {
    println("no camera available.");
    exit();
  } else {
    cam = new Capture(this, 640, 480, 30);
    cam.start(); 
  }      
  
  matrixVerticalSegments = height / (width / matrixHorizontalSegments);
  println("matrix size=" + matrixHorizontalSegments + "x" + matrixVerticalSegments);
  
  matrix = new color[matrixHorizontalSegments][matrixVerticalSegments];
}

void draw() {
  background(0);
  if (cam.available() == true) {
    cam.read();
  }
  
  image(cam, 0, 0, matrixHorizontalSegments, matrixVerticalSegments);
  
  // fill array
  for (int i = 0; i < matrixHorizontalSegments; i++) {
    for (int k = 0; k < matrixVerticalSegments; k++) {
    }
  }
  
  // draw LEDs
  background(0);
  noStroke();
  ellipseMode(CORNER);
  
  for (int i = 0; i < matrixHorizontalSegments; i++) {
    for (int k = 0; k < matrixVerticalSegments; k++) {
      fill(matrix[i][k]);
      ellipse(i * width/matrixHorizontalSegments, k * height/matrixVerticalSegments, ledSize, ledSize);
      
    }
  }
}