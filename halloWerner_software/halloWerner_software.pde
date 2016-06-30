PImage testImage;


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
//import for midi devices check
import java.util.Arrays;
import java.util.List;

//create child PApplet for generative animation and output
ChildApplet outPut;

//create Input objects
SoundAnalysis soundAnalysis;
Controller controller;
VideoInput videoInput;

//must be setFrameRate % 5 = 0 -- used in some calculations in soundAnalysis
int setFrameRate = 30;

//control parameters
float speed = 1.0;
int param01 = 127;
int param02 = 64;
int param03 = 127;
int param04 = 0;
int param05 = 0;
int param06 = 0;
int param07 = 0;
int param08 = 0;
boolean button01 = false;
boolean button02 = false;
boolean button03 = false;
boolean button04 = false;
boolean button05 = false;
boolean button06 = false;
boolean button07 = false;
boolean button08 = false;
boolean GUI = false;


void settings() {
  size(1000, 500);
  //size(500, 300);
}

void setup() {
  testImage = loadImage("image04.jpg");

  videoInput = new VideoInput(this);
  videoInput.videoInputSetup();


  //analysis has to be called before the output, since the generative animation needs sound values
  soundAnalysis = new SoundAnalysis(this);
  soundAnalysis.setup();
  outPut = new ChildApplet();

  controller = new Controller(this);
  controller.init();

  surface.setTitle("Input - main sketch");
  frameRate(setFrameRate);
}

void draw() {
  background(210);
  controller.update();
  videoInput.update();
  //update and draw sound Analysis, draw is inside the update since then I only have to iterate through all frequencys once
  soundAnalysis.update();
}



//this has to be done here because the libraries (midibus and ControlP5) only check for these methods in the PApplet itself, not in it's classes...
//slow = 0; middle = 1; fast = 2; wave = 3; women = 4;

// Moved to Controller
 void button01(int theValue) {
 if (frameCount > 10) {
 //println("a button event from button3: "+theValue);
 videoInput.loadVideo(0, (int) random(-1, videoInput.slow.size()), videoInput.videos);
 controller.playVideo = 1;
 }
 }
 
 void button02(int theValue) {
 if (frameCount > 10) {
 //println("a button event from button2: "+theValue);
 videoInput.loadVideo(1, (int) random(-1, videoInput.middle.size()), videoInput.videos);
 controller.playVideo = 1;
 }
 }
 
 void button03(int theValue) {
 if (frameCount > 10) {
 //println("a button event from button2: "+theValue);
 videoInput.loadVideo(2, (int) random(-1, videoInput.fast.size()), videoInput.videos);
 controller.playVideo = 1;
 }
 }
 
 void button04(int theValue) {
 if (frameCount > 10) {
 //println("a button event from button2: "+theValue);
 controller.playVideo = 0;
 videoInput.displayedVideo1.end = true;
 videoInput.displayedVideo2.end = true;
 }
 }
 
 void button05(int theValue) {
 if (frameCount > 10) {
 //println("a button event from button2: "+theValue);
 videoInput.loadVideo(3, (int) random(-1, videoInput.wave.size()), videoInput.videos);
 controller.playVideo = 1;
 }
 }
 
 void button06(int theValue) {
 if (frameCount > 10) {
 //println("a button event from button2: "+theValue);
 videoInput.loadVideo(4, (int) random(-1, videoInput.women.size()), videoInput.videos);
 controller.playVideo = 1;
 }
 }



//midi input, wird üner controller an midiController weiter geleitet
void controllerChange(ControlChange change) {
  controller.updateKnob(change.number(), change.value());
  println("Number:"+change.number());
  println("Value:"+change.value());
}

void noteOn(Note note) {
  controller.updateButton(note.pitch(), true);
  println("Pitch:"+note.pitch());
}

void noteOff(Note note) {
  controller.updateButton(note.pitch(), false);
  //println("Pitch:"+note.pitch());
}