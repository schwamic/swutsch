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
Controller controller;
VideoInput videoInput;

//must be setFrameRate % 5 = 0 -- used in some calculations in soundAnalysis
int setFrameRate = 30;

//control parameters
float speed = 1.0;
int param01 = 0;
int param02 = 0;
int param03 = 0;
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



void settings() {
  size(1000, 500);
  //size(500, 300);
}

void setup() {

  //videoInput = new VideoInput(this);


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
  //switchVideo();
  /*if (controller.playVideo == 1) {
   videoInput.update();
   } else {
   videoInput.myVideo.stop();
   }
   */
  controller.update();

  //update and draw sound Analysis, draw is inside the update since then I only have to iterate through all frequencys once
  soundAnalysis.update();
  println(param01+" "+param02+" "+param03+" "+param04+" "+param05+" "+param06+" "+param07+" "+param08);
}

/*void switchVideo() {
 if (controller.playVideo == 1) {
 if (controller.mystic == 1) {
 videoInput.startVideo(new String("Mystic"));
 }
 if ( controller.reeper == 1) {
 videoInput.startVideo(new String("Reeper"));
 }
 if ( controller.water == 1) {
 videoInput.startVideo(new String("Water"));
 }
 }
 }*/



//this has to be done here because the libraries (midibus and ControlP5) only check for these methods in the PApplet itself, not in it's classes...

//this is called when any button is pressed
void controlEvent(ControlEvent theEvent) {
  if (frameCount > 10) {
    // println(theEvent.getController().getName());
  }
}

void water(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button4: "+theValue);
    videoInput.videoFolder2 = "Fast";
    videoInput.loadVideo();
  }
}

void playVideo(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button1: "+theValue);
    controller.playVideo = 0;
  }
}

void mystic(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
  }
}

void reeper(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button3: "+theValue);
  }
}

void controllerChange(ControlChange change) {
  controller.updateKnob(change.number(),change.value());
  println("Number:"+change.number());
  println("Value:"+change.value());
}

void noteOn(Note note) {
  controller.updateButton(note.pitch(),true);
  println("Pitch:"+note.pitch());
}

void noteOff(Note note) {
  controller.updateButton(note.pitch(),false);
  //println("Pitch:"+note.pitch());
}