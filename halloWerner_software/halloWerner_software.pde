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


//I'm just giving the midiBus class the byte status from here
void rawMidi(byte[] data) { // You can also use rawMidi(byte[] data, String bus_name)
  // Receive some raw data
  // data[0] will be the status byte
  // data[1] and data[2] will contain the parameter of the message (e.g. pitch and volume for noteOn noteOff)
  //println();
  // println("Raw Midi Data:");
  // println("----------");
  // println("Status Byte/MIDI Command:"+(int)(data[0] & 0xFF));
  // N.B. In some cases (noteOn, noteOff, controllerChange, etc) the first half of the status byte is the command and the second half if the channel
  // In these cases (data[0] & 0xF0) gives you the command and (data[0] & 0x0F) gives you the channel

  //in this case and with our midi controller the data array only has a length of 3
  //data[0] changes when ANY button is pressed on the controller
  // for (int i = 1; i < data.length; i++) {
  //   println("Param "+(i)+": "+(int)(data[i] & 0xFF));
  // }


  controller.midiController.midiControllerUpdate((int) (data[0] & 0xFF), (int) (data[1] & 0xFF), (int) (data[2] & 0xFF));
}