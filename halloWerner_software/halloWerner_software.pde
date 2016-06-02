//import libraries for sound detection
import ddf.minim.analysis.*;
import ddf.minim.*;

//import library for video processing
import processing.video.*;

//import libraries for teensy control
import processing.serial.*;
import java.awt.Rectangle;

//create child PApplet for generative animation and output
ChildApplet outPut;

//create Input objects
SoundAnalysis soundAnalysis;
PhysicalInput physicalInput;
VideoInput videoInput;

//must be setFrameRate % 5 = 0 -- used in some calculations in soundAnalysis
int setFrameRate = 60;


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
}

void draw() {
  background(210);
  
  //throws a lot of errors, so i don't use it
  //videoInput.update();

  //draw sliders
  physicalInput.draw();

  //update and draw sound Analysis, draw is inside the update since then I only have to iterate through all frequencys once
  soundAnalysis.update();
}