//import libraries for sound detection
import ddf.minim.analysis.*;
import ddf.minim.*;

//import library for video processing
import processing.video.*;

//import libraries for teensy control
import processing.serial.*;
import java.awt.Rectangle;

//import libraries for gui and midi input
import themidibus.*;
import controlP5.*;

//create child PApplet for generative animation and output
ChildApplet outPut;

//create Input objects
SoundAnalysis soundAnalysis;
PhysicalInput physicalInput;
VideoInput videoInput;

//create Controller
Controller controller;

//must be setFrameRate % 5 = 0 -- used in some calculations in soundAnalysis
int setFrameRate = 60;

//control parameters
float speed = 1.0;
int param01 = 10;
int param02 = 125;
int param03 = 0;
int param04 = 0;
int param05 = 155;
int param06 = 0;
int param07 = 255;
int param08 = 0;


void settings() {
  size(1000, 500);
}

void setup() {
  //throws a lot of errors, so i don't use it
  /*videoInput = new VideoInput(this);
   videoInput.init(dataPath("file.mp4")); // use dataPath() to get a save path to the files in "data"*/


  //analysis has to be called before the output, since the generative animation needs sound values
  soundAnalysis = new SoundAnalysis(this);
  soundAnalysis.setup();
  outPut = new ChildApplet();

  physicalInput = new PhysicalInput(this);
  physicalInput.setup();

  surface.setTitle("Input - main sketch");
  frameRate(setFrameRate);
  //create Controller
  controller = new Controller(this);
  controller.init();
}

void draw() {
  background(210);

  //throws a lot of errors, so i don't use it
  //videoInput.update();

  //draw sliders
  physicalInput.draw();

  controller.update(); 

  //update and draw sound Analysis, draw is inside the update since then I only have to iterate through all frequencys once
  soundAnalysis.update();
}