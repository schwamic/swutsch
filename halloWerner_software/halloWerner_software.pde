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


void settings() {
  size(1000, 500);
  //size(500, 300);
}

void setup() {
  videoInput = new VideoInput(this);
  videoInput.videoInputSetup();

  controller = new Controller(this);
  controller.init();

  //analysis has to be called before the output, since the generative animation needs sound values
  soundAnalysis = new SoundAnalysis(this);
  soundAnalysis.setup();
  outPut = new ChildApplet();

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


void fast() {
  videoInput.loadVideoOnClick(2, (int) random(-1, videoInput.fast.size()), videoInput.videos);
}

void middle() {
  videoInput.loadVideoOnClick(1, (int) random(-1, videoInput.middle.size()), videoInput.videos);
}

void slow() {
  videoInput.loadVideoOnClick(0, (int) random(-1, videoInput.slow.size()), videoInput.videos);
}

void reset() {
  videoInput.resetVideo(videoInput.displayedVideo1);
  videoInput.resetVideo(videoInput.displayedVideo2);
}

void wave() {
  videoInput.loadVideoOnClick(3, (int) random(-1, videoInput.wave.size()), videoInput.videos);
}

void women() {
  videoInput.loadVideoOnClick(4, (int) random(-1, videoInput.women.size()), videoInput.videos);
}


void custom01() {
  videoInput.loadVideoOnClick(5, (int) random(-1, videoInput.custom01.size()), videoInput.videos);
}

void custom02() {
  videoInput.loadVideoOnClick(6, (int) random(-1, videoInput.custom02.size()), videoInput.videos);
}



//midi input, wird üner controller an midiController weiter geleitet
void controllerChange(ControlChange change) {
  controller.updateKnob(change.number(), change.value());
  //println("Number:"+change.number());
  // println("Value:"+change.value());
}

void noteOn(Note note) {
  try {
    try {
      controller.updateButton(note.pitch(), true);
    }
    catch(IllegalStateException e) {
    }
  }
  catch(NullPointerException e) {
  }
}