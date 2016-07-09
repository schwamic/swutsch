import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.analysis.*; 
import ddf.minim.*; 
import processing.video.*; 
import processing.serial.*; 
import java.awt.Rectangle; 
import themidibus.*; 
import controlP5.*; 
import java.util.Arrays; 
import java.util.List; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class halloWerner_software extends PApplet {

//import libraries for sound detection



//import library for video processing


//import libraries for teensy control



//import libraries for gui and midi input


//import for midi devices check



//create child PApplet for generative animation and output
ChildApplet outPut;

//create Input objects
SoundAnalysis soundAnalysis;
Controller controller;
VideoInput videoInput;

//must be setFrameRate % 5 = 0 -- used in some calculations in soundAnalysis
int setFrameRate = 30;

//control parameters
float speed = 1.0f;
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


public void settings() {
  size(1000, 500);
  //size(500, 300);
}

public void setup() {
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

public void draw() {
  background(210);
  controller.update();
  videoInput.update();
  //update and draw sound Analysis, draw is inside the update since then I only have to iterate through all frequencys once
  soundAnalysis.update();
}



//this has to be done here because the libraries (midibus and ControlP5) only check for these methods in the PApplet itself, not in it's classes...
//slow = 0; middle = 1; fast = 2; wave = 3; women = 4;

// Moved to Controller
public void button01(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button3: "+theValue);
    videoInput.loadVideoOnClick(0, (int) random(-1, videoInput.slow.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}

public void button02(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(1, (int) random(-1, videoInput.middle.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}

public void button03(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(2, (int) random(-1, videoInput.fast.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}

public void button04(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    controller.playVideo = 0;
    videoInput.resetVideo(videoInput.displayedVideo1);
    videoInput.resetVideo(videoInput.displayedVideo2);
  }
}

public void button05(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(3, (int) random(-1, videoInput.wave.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}


public void keyPressed() {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(1, (int) random(-1, videoInput.middle.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}


public void button06(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(4, (int) random(-1, videoInput.women.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}


public void button07(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(5, (int) random(-1, videoInput.custom01.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}

public void button08(int theValue) {
  if (frameCount > 10) {
    //println("a button event from button2: "+theValue);
    videoInput.loadVideoOnClick(6, (int) random(-1, videoInput.custom02.size()), videoInput.videos);
    controller.playVideo = 1;
  }
}



//midi input, wird \u00fcner controller an midiController weiter geleitet
public void controllerChange(ControlChange change) {
  controller.updateKnob(change.number(), change.value());
  println("Number:"+change.number());
  println("Value:"+change.value());
}

public void noteOn(Note note) {
  controller.updateButton(note.pitch(), true);
  println("Pitch:"+note.pitch());
}

public void noteOff(Note note) {
  controller.updateButton(note.pitch(), false);
  //println("Pitch:"+note.pitch());
}

class AvgFrequency {

  float currentValue, maxValue, avgValue, xPos, displayedAvgFrequency, scaleAvg, xDisplayLength;
  boolean peaked, drawStoredValues;
  float[] pastAverages;

  AvgFrequency(float xPos, float displayedAvgFrequency, float scaleAvg, float xDisplayLength) {

    this.pastAverages = new float[soundAnalysis.avgSeconds*setFrameRate];
    this.drawStoredValues = false;
    this.maxValue = 0;
    this.xPos = xPos;
    this.displayedAvgFrequency = displayedAvgFrequency;
    this.scaleAvg = scaleAvg;
    this.xDisplayLength = xDisplayLength;
  }

  public void update(int i) {
    currentValue = soundAnalysis.fftLog.getAvg(i)*scaleAvg;
    if (currentValue > maxValue) {
      maxValue = currentValue;
      peaked = true;
    }
    
    //decay maxValue over time
    maxValue -= maxValue/500;

    for (int j = pastAverages.length - 2; j >= 0; j--) {
      pastAverages[j+1] = pastAverages[j];
    }
    pastAverages[0] = currentValue;

    for (int j = 0; j < pastAverages.length; j++) {
      avgValue += pastAverages[j];
    }

    avgValue /= pastAverages.length;
  }
}
class ChildApplet extends PApplet {

  PGraphics pg;
  GraphicOutput graphicOutput;

  public ChildApplet() {
    super();
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }

  public void settings() {
    //size(1280, 720);
    //size(displayWidth,displayHeight);
    size(800, 320);
  }

  public void setup() {
    colorMode(HSB, 360, 127, 127, 127);
    this.frameRate(setFrameRate);
    surface.setTitle("Output window - child PApplet");

    //create graphicoutput, needs to be created after generative since its using values from it
    graphicOutput = new GraphicOutput(this);

    graphicOutput.setupGraphic();
  }

  public void draw() {

    //call graphicOutput to display stuff
    graphicOutput.drawGraphic();
  }

  public PImage videoAlteration(PImage input) {
    PGraphics output;
    output = createGraphics(input.width, input.height);
    output.beginDraw();
    output.image(input, 0, 0);
    output.endDraw();

    input.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        int c = input.get(x, y);
        float h = hue(c);
        float s = saturation(c);
        float b = brightness(c);
        float a = 0;
        /* int r = (c >> 16) & 0xFF;  
         int g = (c >> 8) & 0xFF;   
         int b = c & 0xFF;*/

        //h=(param01-64)*2+h;//alle farben \u00e4ndern sich
        //h=232+param01;//tint
        //h=(h/360)*232+param01;//test
        h= 240+newHSB((int)h)/2*param01/127;//final
        s=(param02-64)*2+s;
        if (s > 127) s= 127;
        //b=(param03-64)*2+b;
        a=param03;

        output.pixels[y*input.width+x] = color(h, s, b, a);
      }
    }
    output.updatePixels();
    //output.blend(input, 0, 0, input.width, input.height, 0, 0, input.width, input.height, OVERLAY);
    if (!button01)return output;
    else return input;
  }
  public int newHSB(int oldHSB) {
    if (oldHSB>0 && oldHSB<240)return 360-oldHSB/2;
    else return oldHSB;
  }

  public PImage generativeAlteration(PImage input) {
    PGraphics output;
    output = createGraphics(input.width, input.height);
    output.beginDraw();
    output.image(input, 0, 0);
    output.endDraw();

    input.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        int c = input.get(x, y);
        float h = hue(c);
        float s = saturation(c);
        float b = brightness(c);
        float a = 0;
        /* int r = (c >> 16) & 0xFF;  
         int g = (c >> 8) & 0xFF;   
         int b = c & 0xFF;*/

        //h=(param01-64)*2+h;//alle farben \u00e4ndern sich
        //h=232+param01;//tint
        //h=(h/360)*232+param01;//test
        h= 240+newHSB((int)h)/2*param01/127;//final
        s=(param02-64)*2+s;
        if (s > 127) s= 127;
        //b=(param03-64)*2+b;
        a=param05;

        output.pixels[y*input.width+x] = color(h, s, b, a);
      }
    }
    output.updatePixels();
    //output.blend(input, 0, 0, input.width, input.height, 0, 0, input.width, input.height, OVERLAY);
    if (!button01)return output;
    else return input;
  }
}
class Controller {
  //test variable until sliders are implemented
  int playVideo, particleAmount, colorIntensity, triangleSize;

  GUI gui;
  MidiController midiController;

  PApplet pa;

  public Controller(PApplet pa) {
    colorIntensity = 5;
    particleAmount = 125;
    triangleSize = 1;
    this.pa = pa;
  }

  public void init() {
    gui = new GUI(pa);
    gui.init();
    midiController = new MidiController(pa);
    //gui.guiSetup();
    midiController.midiControllerSetup();
  }
  public void update() {
    if (GUI) gui.update(); //funktioniert nicht mit midi zusammen
  }
  public void updateKnob(int number, int value) {   
    midiController.updateKnob(number, value);
  }
  public void updateButton(int pitch, boolean on) {   
    midiController.updateButton(pitch, on);
  }
}
class GUI {
  ControlP5 cp5;
  PApplet pa;
  Knob knob1;
  Knob knob2;
  Knob knob3;
  Knob knob4;
  Knob knob5;
  Knob knob6;
  Knob knob7;
  Knob knob8;

  GUI(PApplet sketch) {
    pa = sketch;
  }

  public void init() {
    cp5 = new ControlP5(pa);

    knob1 = cp5.addKnob("param01")
      .setRange(0, 127)
      .setValue(param01)
      .setPosition(440, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;

    knob2 = cp5.addKnob("param02")
      .setRange(0, 127)
      .setValue(param02)
      .setPosition(550, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;

    knob3 = cp5.addKnob("param03")
      .setRange(0, 127)
      .setValue(param03)
      .setPosition(660, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;
    knob4 = cp5.addKnob("param04")
      .setRange(0, 127)
      .setValue(param04)
      .setPosition(770, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;   
    knob5 = cp5.addKnob("param05")
      .setRange(0, 127)
      .setValue(param05)
      .setPosition(440, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;
    knob6 = cp5.addKnob("param06")
      .setRange(0, 127)
      .setValue(param06)
      .setPosition(550, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;
    knob7 = cp5.addKnob("param07")
      .setRange(0, 127)
      .setValue(param07)
      .setPosition(660, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;   
    knob8 = cp5.addKnob("param08")
      .setRange(0, 127)
      .setValue(param08)
      .setPosition(770, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;   

    cp5.addButton("button01")
      .setValue(0)
      .setPosition(0, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button02")
      .setValue(0)
      .setPosition(110, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button03")
      .setValue(0)
      .setPosition(220, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button04")
      .setValue(0)
      .setPosition(330, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button05")
      .setValue(0)
      .setPosition(0, 110)
      .setSize(100, 100)
      ;
    cp5.addButton("button06")
      .setValue(0)
      .setPosition(110, 110)
      .setSize(100, 100)
      ;
    cp5.addButton("button07")
      .setValue(0)
      .setPosition(220, 110)
      .setSize(100, 100)
      ;
    cp5.addButton("button08")
      .setValue(0)
      .setPosition(330, 110)
      .setSize(100, 100)
      ;
  }
  public void white(int theValue) {
    println("a button event from white: "+theValue);
  }
  public void update() {
    param01=(int)knob1.getValue();
    param02=(int)knob2.getValue();
    param03=(int)knob3.getValue();
    param04=(int)knob4.getValue();
    param05=(int)knob5.getValue();
    param06=(int)knob6.getValue();
    param07=(int)knob7.getValue();
    param08=(int)knob8.getValue();
  }
}
class Generative {
  PApplet pa;

  //create globals for sound visualization / particle behaviour
  //SoundParticle[] particles;
  ArrayList<SoundParticle> particles = new ArrayList<SoundParticle>();
  int particleNbr;


  public Generative(PApplet pa, int particleNbr) {
    this.pa = pa;
    this.particleNbr = particleNbr;
  }

  public void setupParticles() {
    //setup variables for particle simulation
    for (int i = 0; i < particleNbr; i++) {
      PVector randomizePosition = new PVector(random(pa.width), random(pa.height));
      AvgFrequency randomPartnerFrequency = soundAnalysis.avgFrequencys[(int) random(0, soundAnalysis.fftLog.avgSize()-10)];
      //particles.get(i) = new SoundParticle(randomizePosition, randomPartnerFrequency, maxRadius);
      particles.add(i, new SoundParticle(randomizePosition, randomPartnerFrequency, 0));
    }
  }

  public void updateParticles() {
    //iterate thorugh all particles
    for (int i = 0; i < particles.size(); i++) {
      //decay particle neighbours
      particles.get(i).neighbours = 0;

      //decay energy over time
      if (particles.get(i).energy > 0) {
        particles.get(i).energy -= (particles.get(i).energy/3.5f);
      }

      //increase energy when partnerFrequency is peaking
      if (particles.get(i).partnerFrequency.peaked == true && particles.get(i).partnerFrequency.avgValue > soundAnalysis.overallAverage) {
        //particles.get(i).energy += particles.get(i).energy+2;
        particles.get(i).energy += soundAnalysis.overallAverage*2;
      }


      //add to radius for particles by sound overallAverage and a currentValue from the partnerFrequency
      particles.get(i).rad += (soundAnalysis.overallAverage + particles.get(i).partnerFrequency.avgValue + particles.get(i).partnerFrequency.currentValue) / (particles.size()*0.1f);

      //decay radius
      particles.get(i).rad -= particles.get(i).rad/5;

      particles.get(i).energy += particles.get(i).partnerFrequency.maxValue/50;


      for (int j = 0; j < particles.size(); j++) {
        if (i != j) {
          //check for collission with any other particle
          float distance = dist(particles.get(i).pos.x, particles.get(i).pos.y, particles.get(j).pos.x, particles.get(j).pos.y);
          if (distance < particles.get(i).rad+ particles.get(j).rad) {
            particles.get(i).vel.x += (particles.get(i).pos.x - particles.get(j).pos.x) / distance;
            particles.get(i).vel.y += (particles.get(i).pos.y - particles.get(j).pos.y) / distance;
            float xDist = particles.get(i).vel.x - particles.get(j).vel.x;
            float yDist = particles.get(i).vel.y - particles.get(j).vel.y;
            particles.get(i).vel.x += xDist / distance / 2000;
            particles.get(i).vel.y += yDist / distance / 2000;
          }

          //save the closest two neighbour points to later draw a triangle from them if the frequency its partnered with is higher then its maximum
          float neighbourP1distance = dist(particles.get(i).pos.x, particles.get(i).pos.y, particles.get(i).neighbour1Pos.x, particles.get(i).neighbour1Pos.y);
          float neighbourP2distance = dist(particles.get(i).pos.x, particles.get(i).pos.y, particles.get(i).neighbour2Pos.x, particles.get(i).neighbour2Pos.y);

          //if ( particles.get(i).partnerFrequency.currentValue > particles.get(i).partnerFrequency.maxValue) {
          if ((particles.get(i).partnerFrequency.peaked == true && particles.get(i).partnerFrequency.currentValue > soundAnalysis.overallAverage*2) || neighbourP1distance+neighbourP2distance > 50 || particles.get(i).created == true) {
            // if (neighbourP1distance+neighbourP2distance > controller.triangleSize || particles.get(i).created == true) {
            if (distance < neighbourP1distance) {

              particles.get(i).newNeighbour1Pos = particles.get(j).pos;
              particles.get(i).changeNeighbour = true;
            }
            //distance
            if (distance < neighbourP2distance && neighbourP1distance < distance) {
              particles.get(i).newNeighbour2Pos = particles.get(j).pos;
              particles.get(i).changeNeighbour = true;
            }
          }
          if (distance < (particles.get(i).partnerFrequency.avgValue/((pa.width+pa.height+particleNbr)*0.002f))) {
            particles.get(i).neighbours += 1;
          }
        }
      }

      //ease out of current neighbour when finding new one
      if (particles.get(i).changeNeighbour == true) {
        particles.get(i).energy -= particles.get(i).energy/1.5f + 10;
        if (particles.get(i).energy <= 10) {
          particles.get(i).neighbour1Pos = particles.get(i).newNeighbour1Pos;
          particles.get(i).neighbour2Pos = particles.get(i).newNeighbour2Pos;
          particles.get(i).changeNeighbour = false;
          particles.get(i).created = false;
        }
      }


      //check for "wall" collission and push away from walls
      if (particles.get(i).pos.x < - 25 + particles.get(i).rad/4) {
        particles.get(i).vel.x += soundAnalysis.overallAverage/4;
      }
      if (particles.get(i).pos.x > pa.width + 25 - particles.get(i).rad/4) {
        particles.get(i).vel.x -= soundAnalysis.overallAverage/4;
      }
      if (particles.get(i).pos.y < - 25 + particles.get(i).rad/4 ) {
        particles.get(i).vel.y += soundAnalysis.overallAverage/4;
      }
      if (particles.get(i).pos.y > pa.height + 25 - particles.get(i).rad/4) {
        particles.get(i).vel.y -= soundAnalysis.overallAverage/4;
      }

      //add velocity to positon
      particles.get(i).vel.x *= 0.5f;
      particles.get(i).vel.y *= 0.5f;
      particles.get(i).pos.x += particles.get(i).vel.x;
      particles.get(i).pos.y += particles.get(i).vel.y;
    }
  }


  public void drawTriangles(PGraphics pg) {
    pg.fill(0, 0, 50, 30);
    pg.beginShape(TRIANGLES);
    for (SoundParticle p : particles) {
      //DODAT another fill formula
      pg.fill(p.energy*controller.colorIntensity, 255, 255, p.energy*controller.colorIntensity);

      pg.strokeWeight(1);
      //DODAT another stroke formula
      pg.stroke(p.energy*controller.colorIntensity, 255, 255, p.energy*controller.colorIntensity);

      //pg.noStroke();
      pg.vertex(p.pos.x, p.pos.y);
      pg.vertex(p.neighbour1Pos.x, p.neighbour1Pos.y);
      pg.vertex(p.neighbour2Pos.x, p.neighbour2Pos.y);
    }
    pg.endShape();
  }


  public void drawParticles(PGraphics pg) {
    pg.noStroke();
    //pg.fill(255,0,0, 255);
    for (SoundParticle p : particles) {
      pg.fill(255, 0, 0, p.energy*4);
      pg.ellipse(p.pos.x, p.pos.y, p.neighbours*5, p.neighbours*5);
    }
  }


  public void addParticle() {
    if (particles.size() < particleNbr + controller.particleAmount) {
      for (int i = 0; i < particleNbr+controller.particleAmount - particles.size(); i++) {
        PVector randomizePosition = new PVector(random(pa.width), random(pa.height));
        AvgFrequency randomPartnerFrequency = soundAnalysis.avgFrequencys[(int) random(0, soundAnalysis.fftLog.avgSize()-10)];
        particles.add(particles.size(), new SoundParticle(randomizePosition, randomPartnerFrequency, 0));
      }
    }
  }

  //no idea why this works, should throw a nullpointer exception but w/e
  public void deleteParticle() {
    if (particles.size() > particleNbr + controller.particleAmount) {
      for (int i = 0; i > particleNbr + controller.particleAmount - particles.size(); i--) {
        particles.remove(particles.get(particles.size()-1));
      }
    }
  }
}
class GraphicOutput {

  PApplet pa;
  LEDOutput ledOutput;
  PGraphics pgGenerative;
  PGraphics pgVideo;
  Generative generative;

  GraphicOutput(PApplet pa) {
    this.pa = pa;
  }



  public void setupGraphic() {
    pgVideo  = pa.createGraphics(pa.width, pa.height);
    pgGenerative = pa.createGraphics(pa.width, pa.height);

    //create generative soudn visualizer
    generative = new Generative(pa, 150);
    generative.setupParticles();

    //create output to LEDs
    //LED OUTPUT FUNCTIONS
    ledOutput = new LEDOutput(pa);
    ledOutput.setupDisplay();
  }


  public void drawGraphic() {
    //update particles
    generative.updateParticles();
    generative.deleteParticle();
    generative.addParticle();


    //draw everything into the PGraphic pg to later scale to LED screen size

    pgGenerative.beginDraw();
    pgGenerative.scale(0.1f);
    pgGenerative.background(0);
    generative.drawTriangles(pgGenerative);
    pgGenerative.endDraw();


    pgVideo.beginDraw();
    pgVideo.scale(0.1f);
    pgVideo.background(0);
    try {
      //println("video1 : " + (255-videoInput.displayedVideo1.fade));
      pgVideo.tint(255, 255-videoInput.displayedVideo1.fade);
      pgVideo.image(videoInput.displayedVideo1.video, 0, 0, pa.width, pa.height);
    } 
    catch(NullPointerException e) {
    }
    try {
      //println("video2: " + (255-videoInput.displayedVideo2.fade));
      pgVideo.tint(255, 255-videoInput.displayedVideo2.fade);
      pgVideo.image(videoInput.displayedVideo2.video, 0, 0, pa.width, pa.height);
    } 
    catch(NullPointerException e) {
    }
    pgVideo.endDraw();

    //displays large size graphic output
    //pa.image(outPut.bildAnpassungen(pg), 0, 0);

    //println("graphicOutput Draw call");

    //scale PGraphic and give it to LEDOutput
    PGraphics scaledGraphic = pa.createGraphics(80, 32);
    scaledGraphic.beginDraw();
    scaledGraphic.background(0);
    scaledGraphic.image(outPut.videoAlteration(pgVideo), 0, 0);
    scaledGraphic.image(outPut.generativeAlteration(pgGenerative), 0, 0);
    scaledGraphic.endDraw();

    //LED OUTPUT FUNCTIONS
    ledOutput.getGraphic(scaledGraphic);
    ledOutput.drawDisplay();


    //displays scaled down graphic output for LEDs
    //pa.image(scaledGraphic, 0, 0, pa.width, pa.height);
    pa.image(scaledGraphic, 0, 0);



    //you can scale PGraphics with pa.scale(0.1 to 1)
  }
}
/*                                  
 OUTPUT - MS WERNER
 Jedes Modul in Processing auf 16 x 16 angelegt. An 
 der Wand sind die Module jedoch vom Index und der
 Anzahl nicht gleich. Damit alles passt, hier die
 L\u00f6sung des Problems:
 
 
 Modul an der Wand: 
 /--------------------------------------------------/
 /    x    /    x    /    x    /    x    /    1     /
 /--------------------------------------------------/
 /    x    /    x    /    x    /    3    /    2     /
 /--------------------------------------------------/
 /    x    /    x    /    4    /    5    /    6     /
 /--------------------------------------------------/
 /    x    /   10    /    9    /    8    /    7     /
 /--------------------------------------------------/
 /    x    /   11    /    12   /    13   /    14    /
 /--------------------------------------------------/ usw.
 
 Modul in Processing:
 /--------------------------------------------------/
 /    1    /    2    /    3    /    4    /    5     /
 /--------------------------------------------------/
 /    6    /    7    /    8    /    9    /    10    /
 /--------------------------------------------------/
 /    11   /   12    /    13   /    14   /    15    /
 /--------------------------------------------------/
 /    16   /   17    /    18   /    19   /    20    /
 /--------------------------------------------------/
 /    21   /   22    /    23   /    24   /    25    /
 /--------------------------------------------------/ usw.
 
 
 Somit muss das Array zum Auslesen f\u00fcr das ProcessingModul wie 
 folgt aussehen: [5,10,9,13,14,15,20,19,18,17,22,23,24,25],
 damit es am Display in Reihe ausgegeben werden kann. 
 
 
 //println( c ); // -16711936        
 //println( hex(c) ); // FF00FF00                         
 */
class LEDOutput {
  
  
  PApplet pa;
  LEDOutput(PApplet pa){
    this.pa = pa;
  }
  
  //VARIABLEN
  int WidthModule = 16;
  int[] Pixels;
  PImage img;
  PGraphics pg;
  int state;
  int count;
  int state1;
  int state2;
  boolean firstRaw;
  String val;
  //Serial
  Serial myPort;  // Create object from Serial class
  //byte[][] allModulesLED;
  byte[] dataLED;
  byte[] dataLED2;
  float[] moduleEmpty;

  //MODULE
  int[][] allModules;
  float[][] rgbModules;

  public void setupDisplay() {

    println(Serial.list()[3]);
    //INIZIALISIEREN


    //Serial

    //println(Serial.list());
    String portName = Serial.list()[3]; //3 entspricht ...usbmodem1635641
    myPort = new Serial(pa, portName, 115200);  //9600 ist langsam -> hochsetzen auf 115200
    myPort.setDTR(true);
    //println(Serial.list());
    myPort.bufferUntil('\n'); 


    //strokeWeight(12);  
    firstRaw = true;
    Pixels = new int[0];
    count = 0;
    state1=0;
    state2=5;
    allModules = new int[10][0];
    rgbModules = new float[10][0];
    dataLED =  new byte[0];    //Hier f\u00fcr 245LEDs pro Modul -> muss sp\u00e4ter angepasst werden!!!
    dataLED2 =  new byte[0]; 
    float[] moduleEmpty = new float[0]; 
    for (int i = 0; i < 144; i++) {
      moduleEmpty = append(moduleEmpty, PApplet.parseFloat(255));
    }
    //println(moduleEmpty[99]);
  }
  
  public void getGraphic(PGraphics newPg) {
  pg = newPg;
  }

  public void drawDisplay() {
    fillPixelArray();  //HIER FEHLER SUCHEN - DAUER ZU LANGE
    moduleAufbau();
    //drawAll();

    drawM1();
    drawM2();
    drawM3();
    drawM4();
    drawM5();

    drawM6();

    drawM7();
    drawM8();
    drawM9();

    drawM10();

    //***************
    //HIER MUSS (newrgb1 mit newrgb6) und (newrgb5 mit newrgb10) zusammengefuegt werden.

    float[] newrgb1 = makeRightOrderSide(rgbModules[0], moduleOrder1); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb2 = makeRightOrder(rgbModules[1], moduleOrder2); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb3 = makeRightOrder(rgbModules[2], moduleOrder3); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb4 = makeRightOrder(rgbModules[3], moduleOrder4); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb5 = makeRightOrderSide(rgbModules[4], moduleOrder5); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb6 = makeRightOrder(rgbModules[5], moduleOrder6); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb7 = makeRightOrder(rgbModules[6], moduleOrder7); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb8 = makeRightOrder(rgbModules[7], moduleOrder8); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb9 = makeRightOrder(rgbModules[8], moduleOrder9); //hier richtiges Modul \u00fcbergeben!
    float[] newrgb10 = makeRightOrder(rgbModules[9], moduleOrder10); //hier richtiges Modul \u00fcbergeben!

    //println("10: "+newrgb5.length);

    //float[] newrgb5u10 = ?
    //println(newrgb);


    makeDataLED(newrgb3); //passt  
    fillEmpty();

    makeDataLED(newrgb1); //passt
    //fillEmpty(); 18;
    for (int i = 0; i < 18; i++) {

      dataLED = append(dataLED, PApplet.parseByte(0));
    }
    makeDataLED(newrgb6);
    //makeDataLED(newrgb3); //


    makeDataLED(newrgb2); //passt 
    fillEmpty();

    makeDataLED(newrgb4); //passt
    fillEmpty();

    makeDataLED(newrgb8); //passt
    fillEmpty();


    makeDataLED(newrgb7); //passt 
    fillEmpty();

    makeDataLED(newrgb5);
    makeDataLED(newrgb10); //passt -> 5 + 10


    makeDataLED(newrgb9); //passt
    fillEmpty(); //muss 10 sein

    //println(dataLED.length);


    //println(newrgb7.length); //-> 735 -> 3*245 -> muss noch auf 300 angepasst werden. -> 900
    // Auch Arduino !
    //println(dataLED.length);
    //println(dataLED[5877]);

    for (int i = 0; i < 5880; i++) {
      dataLED2 = append(dataLED, PApplet.parseByte(255));
    }
    //println("dataLED2: "+dataLED[50]);
    //***************

    //myPort.write(dataLED);
    myPort.write(dataLED);
    clearAll();
    //delay(10);
  }

  public void fillEmpty() {
    for (int i = 0; i<222; i++) {
      dataLED = append(dataLED, PApplet.parseByte(0));
    }
  }

  /*#########################################*/
  public void serialEvent( Serial myPort) {
    val = myPort.readStringUntil('\n');
    //make sure our data isn't empty before continuing
    if (val != null) {
      //trim whitespace and formatting characters (like carriage return)
      val = trim(val);
      //println(val);
    }
  }
  /*#########################################*/


  public void fillPixelArray() {
    pg.loadPixels();
    for (int i = 0; i < pg.height; i += 1) {
      for (int j = 0; j < pg.width; j += 1) { 
        Pixels = append(Pixels, pg.pixels[j + i*pg.width ]);
      }
    }
  }

  //Zeichnet und gibt dem makeRGBModule() die rgb-Werte
  public void drawPixels(int index1, int posX, int posY) {
    int index2 = 0;

    //println("0: "+allModules[0]);
    for (int y = 0; y < WidthModule; y++) { 
      for (int x = 0; x < WidthModule; x++) { 
        float red = (allModules[index1][index2]) >> 16 & 0xFF;
        float green = (allModules[index1][index2]) >> 8 & 0xFF;
        float blue =  (allModules[index1][index2]) & 0xFF;
        //println("red: "+bed);
        //println("green: "+breen);
        //println("blue: "+bbue);
        //println(byte(-1));
        //float all = byte(colorWiring(allModules[index1][index2]));
        //println("all: "+ all);
        makeRGBModules(index1, red, green, blue);
        //stroke(red, green, blue);
        //point(x*16+posX, y*16+posY);

        index2++;
      }
    }
  }

  //Erstellt ein RGB-Modul-Array
  public void makeRGBModules(int index1, float red, float green, float blue) {
    rgbModules[index1] = append(rgbModules[index1], red);
    rgbModules[index1] = append(rgbModules[index1], green);
    rgbModules[index1] = append(rgbModules[index1], blue);
  }

  //Erstellt aus der RGB-Array ein Byte-Array
  public void makeDataLED(float[] rgbArray) {
    for (int i = 0; i<rgbArray.length; i++) {
      dataLED = append(dataLED, PApplet.parseByte(rgbArray[i]));
      //println(dataLED[i]);
    }
  }

  //ganz wichtig, zum Schluss alles leeren.
  public void clearAll() {
    Pixels = new int[0];
    allModules = new int[10][0];
    rgbModules = new float[10][0];
    dataLED =  new byte[0];
    dataLED2 =  new byte[0];
    count = 0;
    state1=0;
    state2=5;
  }

  //Funktionen zum Zeichnen der Module
  public void drawM1() {
    drawPixels(0, 9, 110);
  }

  public void drawM2() {
    drawPixels(1, 264, 110);
  }

  public void drawM3() {
    drawPixels(2, 519, 110);
  }

  public void drawM4() {
    drawPixels(3, 774, 110);
  }

  public void drawM5() {
    drawPixels(4, 1029, 110);
  }

  public void drawM6() {
    drawPixels(5, 9, 365);
  }

  public void drawM7() {
    drawPixels(6, 264, 365);
  }
  public void drawM8() {
    drawPixels(7, 519, 365);
  }

  public void drawM9() {
    drawPixels(8, 774, 365);
  }

  public void drawM10() {
    drawPixels(9, 1029, 365);
  }

  //Zum Zeichen aller Module
  public void drawAll() {
    int index = 0;
    for (int i = 20; i < height; i += 22) {
      for (int j = 8; j < width+8; j += 16) { 
        float red = (Pixels[index]) >> 16 & 0xFF;
        float green = (Pixels[index]) >> 8 & 0xFF;
        float blue =  (Pixels[index]) & 0xFF;    //shiften geht schneller
        //stroke(red, green, blue);
        //point(j, i);
        index++;
      }
    }
  }

  //Zum F\u00fcllen der Module
  public void fillModules(int state, int value) {
    switch(state) {
    case 0: 
      allModules[0] = append(allModules[0], value);
      break;
    case 1: 
      allModules[1] = append(allModules[1], value);
      break;
    case 2: 
      allModules[2] = append(allModules[2], value);
      break;
    case 3: 
      allModules[3] = append(allModules[3], value);
      break;
    case 4: 
      allModules[4] = append(allModules[4], value);
      break;
    case 5: 
      allModules[5] = append(allModules[5], value);
      break;
    case 6: 
      allModules[6] = append(allModules[6], value);
      break;
    case 7:  
      allModules[7] = append(allModules[7], value);
      break;
    case 8: 
      allModules[8] = append(allModules[8], value);
      break;
    case 9: 
      allModules[9] = append(allModules[9], value);
      break;
    default:             
      println("ERROR fillModules"); 
      break;
    }
  }

  //Grundlegende Funktion f\u00fcr den Modulaufbau
  public void moduleAufbau() {
    state = 0;
    for (int i = 0; i < Pixels.length; i++) {
      updateState(i);
      fillModules(state, Pixels[i]);
    }
  }

  //Wichtige Funktion zum Aufteilen der Module -> State: SwitchCase
  public void updateState(int index) {

    if (index < 1280) { //1280 = 16 * 80 -> die ersten 16 Reihen fuer Module 1-5
      firstRaw = true;
      if ((index%16)==0 && index != 0) {
        state1 = state1 + 1;
      }
      if (state1 == 5) {
        state1 = 0;
      }
    } else {
      firstRaw = false;
      if ((index%16)==0) {
        if (count > 0) {
          state2 = state2 + 1;
        }

        count++;
      }
      if (state2 == 10) {
        state2=5;
      }
    }
    if (firstRaw) {
      state = state1;
    } else {
      state = state2;
    }
  }

  //LEERARRAY 16*3 -> 48
  /*for(int i = 0; i < 144; i++){
   float[] moduleEmpty = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
   0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
   0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
   }
   */

  //Modul1 mit 68 Pixel
  int[] moduleOrder1 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 79, 80, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 82, 81, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 110, 111, 112, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 114, 113, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 140, 141, 142, 143, 144, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 149, 148, 147, 146, 145, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 171, 172, 173, 174, 175, 176, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 182, 181, 180, 179, 178, 177, 
    -1, -1, -1, -1, -1, -1, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
    -1, -1, -1, -1, -1, -1, -1, -1, 216, 215, 214, 213, 212, 211, 210, 209, 
    -1, -1, -1, -1, -1, -1, -1, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
    -1, -1, -1, -1, -1, -1, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //Modul2 fehlt mit 204 Pixel
  int[] moduleOrder2 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, 41, 42, 43, 44, 45, 46, 47, 48, 
    -1, -1, -1, -1, -1, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 
    -1, -1, -1, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
    -1, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 
    -1, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 
    -1, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, 113, 
    -1, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, 145, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //Modul3 mit 238 Pixel
  int[] moduleOrder3 = {
    -1, -1, -1, -1, -1, -1, -1, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
    -1, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    -1, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, -1, 
    -1, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    -1, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, 113, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    -1, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, 145, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
    -1, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, -1, 
    -1, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, -1, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //Modul4 mit xxx Pixel
  int[] moduleOrder4 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    33, 34, 35, 36, 37, 38, 39, 40, -1, -1, -1, -1, -1, -1, -1, -1, 
    64, 63, 62, 61, 60, 59, 58, 57, -1, -1, -1, -1, -1, -1, -1, -1, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
    96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, -1, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, -1, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //Modul5 mit 74 Pixel
  int[] moduleOrder5 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    64, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    65, 66, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    96, 95, 94, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    97, 98, 99, 100, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    128, 127, 126, 125, 124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    129, 130, 131, 132, 133, 134, 135, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    160, 159, 158, 157, 156, 155, 154, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    161, 162, 163, 164, 165, 166, 167, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, -1, -1, -1, -1, -1, -1, -1, 
    193, 194, 195, 196, 197, 198, 199, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, -1, -1, -1, -1, -1, -1, -1, -1, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, -1, -1, -1, -1, -1, -1, 
    256, 255, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
  };

  //Modul5 mit 74 Pixel
  int[] moduleOrder55 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    64, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    65, 66, 67, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    96, 95, 94, 93, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    97, 98, 99, 100, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    128, 127, 126, 125, 124, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    129, 130, 131, 132, 133, 134, 135, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    160, 159, 158, 157, 156, 155, 154, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    161, 162, 163, 164, 165, 166, 167, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, -1, -1, -1, -1, -1, -1, -1, 
    193, 194, 195, 196, 197, 198, 199, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, -1, -1, -1, -1, -1, -1, -1, -1, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, -1, -1, -1, -1, -1, -1, 
    256, 255, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
  };

  //Modul6 mit 222 Pixel
  int[] moduleOrder6 = {
    -1, -1, -1, -1, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
    -1, -1, -1, -1, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 
    -1, -1, -1, -1, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    -1, -1, -1, -1, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 
    -1, -1, -1, -1, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
    -1, -1, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 
    -1, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 
    -1, -1, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, 113, 
    -1, -1, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    -1, -1, -1, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, 145, 
    -1, -1, -1, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
    -1, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //Modul7 mit 245 Pixel
  int[] moduleOrder7 = {
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
    32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, -1, 
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, -1, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, -1, 
    96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, -1, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, -1, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, -1, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, -1, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, -1, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, -1, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, -1, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241
  };  

  //Modul8 mit 245 Pixel
  int[] moduleOrder8 = {
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
    32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, -1, 
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, -1, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, -1, 
    96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, -1, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, -1, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, -1, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, -1, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, -1, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, -1, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, -1, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241
  }; 

  //Modul9 mit 245 Pixel
  int[] moduleOrder9 = {
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
    32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, -1, 
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, -1, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, -1, 
    96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, -1, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, -1, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, -1, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, -1, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, -1, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, -1, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, -1, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  }; 

  //Modul10 mit 226 Pixel
  int[] moduleOrder10 = {
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, -1, -1, -1, -1, 
    32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, -1, -1, -1, -1, -1, 
    33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, -1, -1, -1, -1, 
    64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, -1, -1, -1, -1, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, -1, -1, 
    96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, -1, -1, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, -1, -1, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, -1, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, -1, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, -1, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, -1, -1, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //float[] makeLeftSideOrder(float[] rgb, int[] order) {}
  //float[] makeRightSideOrder(float[] rgb, int[] order) {}
  //Funktion f\u00fcr das kleine Modul -> Spiegeln

  //Erstellt die Reihenfolge f\u00fcr die Module.
  public float[] makeRightOrder(float[] rgb, int[] order) {
    int count = 1;
    float[] newrgb = new float[735];      //245*3=735 304*3=912
    //float[] newrgb = new float[900];    //max=300 -> 3*300=900
    for (int i = 0; i<order.length; i++) {
      if (order[i] != -1) {
        newrgb[(count*3)-3] = rgb[(order[i]*3)-3];
        newrgb[(count*3)-2] = rgb[(order[i]*3)-2];
        newrgb[(count*3)-1] = rgb[(order[i]*3)-1];
        count += 1;
      }
    }
    return newrgb;
  }

  public float[] makeRightOrderSide(float[] rgb, int[] order) {
    float[] newrgb = new float[0]; 
    for (int i = 0; i<order.length; i++) {
      if (order[i] != -1) {
        newrgb = append(newrgb, rgb[(order[i]*3)-3]);
        newrgb = append(newrgb, rgb[(order[i]*3)-2]);
        newrgb = append(newrgb, rgb[(order[i]*3)-1]);
      }
    }
    return newrgb;
  }

  /*ZUM MERKEN
   
   int colorWiring(int c) {
   //   return c;  // RGB
   return ((c & 0xFF0000) >> 8) | ((c & 0x00FF00) << 8) | (c & 0x0000FF); // GRB - most common wiring
   }
   
   void changePixelsHorizontal() {
   for (int x = 0; x < width; x ++) {
   for (int y = 0; y < height; y+=4) {
   pixels[y*width+x] = color(255, 0, 0);
   }
   }
   }
   
   void changePixelsVertical() {
   for (int y = 0; y < height; y ++) {
   for (int x = 0; x < width; x+=4) {
   pixels[y*width+x] = color(255, 0, 0);
   }
   }
   }
   */

  /*BoilerModule mit 256 Pixel
   int[] boiler = {
   1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 
   32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 
   33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
   64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 
   65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
   96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 
   97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 
   128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, 113, 
   129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
   160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, 145, 
   161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
   192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
   193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
   224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
   225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
   256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
   };
   */
}

class MidiController {
  PApplet pa;
  MidiBus myBus;

  MidiController(PApplet pa) {
    this.pa = pa;
  }

  public void midiControllerSetup() {
    String[] midiArray = MidiBus.availableInputs();
    List<String> midiList = Arrays.asList(midiArray);
    if (midiList.contains("LPD8")) {
      myBus = new MidiBus(pa, "LPD8", 0);
      GUI = false;
    } else
    {
      GUI = true;
    }
  }

  public void updateKnob(int number, int value) {
    switch(number) {
    case 1: 
      param01=value;
      println("updateKnob "+value);
      break;
    case 2: 
      param02=value;
      break;
    case 3: 
      param03=value;
      break;
    case 4: 
      param04=value;
      break;
    case 5: 
      param05=value;
      break;
    case 6: 
      param06=value;
      break;
    case 7: 
      param07=value;
      break;
    case 8: 
      param08=value;
      break;
    }
  }
  public void updateButton(int pitch, boolean on) {
    if (on) {
      println("abc");
      switch(pitch) {
      case 36: 
        //button01 = true;
        button05(0);
        break;
      case 37: 
        button06(0);
        //button02 = true;
        break;
      case 38: 
        button07(0);
        //button03 = true;
        break;
      case 39: 
        button08(0);
        //button04 = true;
        break;
      case 40: 
        button01(0);
        //button05 = true;
        break;
      case 41: 
        button02(0);
        //button06 = true;
        break;
      case 42: 
        button03(0);
        //button07 = true;
        break;
      case 43: 
        button04(0);
        //button08 = true;
        break;
      }
    } else {
      /*switch(pitch) {
       case 36: 
       button01 = false;
       break;
       case 37: 
       button02 = false;
       break;
       case 38: 
       button03 = false;
       break;
       case 39: 
       button04 = false;
       break;
       case 40: 
       button05 = false;
       break;
       case 41: 
       button06 = false;
       break;
       case 42: 
       button07 = false;
       break;
       case 43: 
       button08 = false;
       break;
       }*/
    }
  }
}
class SoundAnalysis {
  PApplet pa;

  //create object array for frequencys
  AvgFrequency[] avgFrequencys;

  Minim minim;  
  AudioInput mic;
  FFT fftLog;
  AudioPlayer song;

  //sets the time in which the past values of a frequency are stored, after avgSeconds the old values are getting replaced by new ones to determin the avg of the frequency
  int avgSeconds = 15;

  float[] pastAverages;
  float overallAverage;

  public SoundAnalysis(PApplet pa) {
    this.pa = pa;
  }

  public void getMicInput() {
    minim = new Minim(pa);
    mic = minim.getLineIn(minim.STEREO, 1024);
    fftLog = new FFT( mic.bufferSize(), mic.sampleRate() );
  }

  public void getSongInput() {
    minim = new Minim(pa);
    song = minim.loadFile("Kazabubu.mp3", 1024);
    fftLog = new FFT( song.bufferSize(), song.sampleRate() );
  }

  public void setup() {

    float scaleAvg = 0.8f;

    pastAverages = new float[avgSeconds*setFrameRate];

    getMicInput(); 
    //getSongInput();

    fftLog.logAverages(100, 10);

    avgFrequencys = new AvgFrequency[fftLog.avgSize()];
    float avgFrequencyXDisplayLength = pa.width/2/fftLog.avgSize();
    for (int i = 0; i < avgFrequencys.length; i++) {

      avgFrequencys[i] = new AvgFrequency(pa.width/2-avgFrequencys.length/2*avgFrequencyXDisplayLength+i*avgFrequencyXDisplayLength, fftLog.getAverageCenterFrequency(i), scaleAvg, avgFrequencyXDisplayLength);
    }

    pastAverages = new float[avgSeconds*setFrameRate];
  }

  public void update() {
    float average = 0;
    //keeps the fft updated with a mix of both left and right mic input
    fftLog.forward( mic.mix );
    //fftLog.forward( song.mix );

    //song.play();
    //iterate thorugh all the avgFrequencys
    for (int i = 0; i < avgFrequencys.length; i++) {
      //reset the peaked boolean to detect beats peaking
      avgFrequencys[i].peaked = false;

      //update the frequency values
      avgFrequencys[i].update(i);

      //scale the values by the average of all frequencys
      //this allows the code to work on quiet as well as loud sound
      //--- don't really know how to implement this yet ---

      //draw frequencys for visualization
      drawFrequencys(i);

      //store currentValues to determine the average of all frequencys at the current frame
      average += avgFrequencys[i].currentValue;
    }

    determineAverage(average);
  }

  public void determineAverage(float average) {
    average /= avgFrequencys.length;

    //push the pastAverages one place forward
    for (int i = pastAverages.length - 2; i >= 0; i--) {
      pastAverages[i+1] = pastAverages[i];
    }

    //fill the array at position 0 with the last known average
    pastAverages[0] = average;

    //get the average of all averages over the last 5 seconds
    for (int i = 0; i < pastAverages.length; i++) {
      overallAverage += pastAverages[i];
    }
    overallAverage /= pastAverages.length;

    //draw overall average value
    fill(160, 0, 0, 80);
    noStroke();
    rect(avgFrequencys[avgFrequencys.length-1].xPos+avgFrequencys[0].xDisplayLength, pa.height/1.1f, avgFrequencys[0].xDisplayLength, -overallAverage);
  }

  public void drawFrequencys(int i) {
    rectMode(CORNER);  
    //draw current value
    noStroke();
    fill(120);
    if ( avgFrequencys[i].peaked == true) {
      fill(0);
    }
    rect(avgFrequencys[i].xPos, pa.height/1.1f, avgFrequencys[i].xDisplayLength, -avgFrequencys[i].currentValue);

    //draw maxValues
    stroke(150);
    line(avgFrequencys[i].xPos, pa.height/1.1f-avgFrequencys[i].maxValue, avgFrequencys[i].xPos+avgFrequencys[i].xDisplayLength, pa.height/1.1f-avgFrequencys[i].maxValue);

    //draw average values
    fill(160, 0, 0, 80);
    noStroke();
    rect(avgFrequencys[i].xPos, pa.height/1.1f, avgFrequencys[i].xDisplayLength, -avgFrequencys[i].avgValue);
  }
}
class SoundParticle {

  PVector pos, vel, newNeighbour1Pos, newNeighbour2Pos, neighbour1Pos, neighbour2Pos;

  float rad, energy, neighbours;
  boolean changeNeighbour, created;
  AvgFrequency partnerFrequency;


  SoundParticle(PVector pos, AvgFrequency partnerFrequency, float rad) {
    changeNeighbour = true;
    this.rad = rad;
    this.pos = pos;
    this.energy = 0;
    vel = new PVector(0, 0);
    this.neighbour1Pos = new PVector(0,0);
    this.neighbour2Pos = new PVector(width,height);
    this.newNeighbour1Pos = new PVector(0,0);
    this.newNeighbour2Pos = new PVector(width,height);
    this.partnerFrequency = partnerFrequency;
    created = true;
  }
}
class Video {
  Movie video;
  boolean end, play;
  int fade, loopTimes;

  Video(Movie video, int loopTimes) {
    this.loopTimes = loopTimes;
    this.video = video;
    end = true;
    play = false;
    fade = 255;
  }
}
class VideoInput {

  PApplet pa;
  ArrayList<Video> slow = new ArrayList<Video>();
  ArrayList<Video> middle = new ArrayList<Video>();
  ArrayList<Video> fast = new ArrayList<Video>();
  ArrayList<Video> wave = new ArrayList<Video>();
  ArrayList<Video> women = new ArrayList<Video>();
  ArrayList<Video> custom01 = new ArrayList<Video>();
  ArrayList<Video> custom02 = new ArrayList<Video>();


  ArrayList[] videos = new ArrayList[7];

  int currentVideos = 0;

  Video displayedVideo1, displayedVideo2;
  int videoNumber1, videoNumber2, videoPlays;


  String currentFolder;


  VideoInput(PApplet pa) {
    this.pa = pa;
  }

  public void resetVideo(Video v) {
    v.end = true;
    currentVideos = 0;
    videoNumber1 = 0;
    videoNumber2 = 0;
    videoPlays = 0;
  }

  public void videoInputSetup() {

    videos[0] = loadVideos("Slow", slow);
    videos[1] = loadVideos("Middle", middle);
    videos[2] = loadVideos("Fast", fast);
    videos[3] = loadVideos("Wave", wave);
    videos[4] = loadVideos("Women", women);
    videos[5] = loadVideos("Custom01", custom01);
    videos[6] = loadVideos("Custom02", custom02);


    displayedVideo1 = new Video(null, 0);
    displayedVideo2 = new Video(null, 0);
  }

  public ArrayList loadVideos(String f1, ArrayList<Video> m) {
    File[] files;
    File f = new File(dataPath(f1));
    files = f.listFiles();
    for (int i = 0; i< files.length; i++) {
      int loopTimes = 0;
      String fv = files[i].getAbsolutePath();
      if (fv.contains("_loop")) {
        String s = fv;
        loopTimes = Integer.parseInt(s.substring(s.lastIndexOf("_loop")+5, s.lastIndexOf("_loop")+7));
      }
      m.add(new Video(new Movie(pa, fv), loopTimes));
    }
    return m;
  }
  public void loadVideoOnClick(int n, int i, ArrayList[] b) {

    ArrayList<Video> m = b[n];

    if (displayedVideo1.play == false && displayedVideo2.play == false) {
      currentVideos = n;
      videoNumber1 = i;
      displayedVideo1 = m.get(i);
      displayedVideo1.video.play();
      displayedVideo1.play = true;
      displayedVideo1.end = false;
      videoPlays = 0;
      displayedVideo1.video.noLoop();
    } else if ( displayedVideo1.play == true && displayedVideo2.play == false) {
      currentVideos = n;
      videoNumber2 = i;
      displayedVideo2 = m.get(i);
      displayedVideo2.video.play();
      displayedVideo2.play = true;
      displayedVideo2.end = false;
      videoPlays = 0;
      displayedVideo2.video.noLoop();
      displayedVideo1.end = true;
    } else if (displayedVideo2.play == true && displayedVideo1.play == false) {
      currentVideos = n;
      videoNumber1 = i;
      displayedVideo1 = m.get(i);
      displayedVideo1.video.play();
      displayedVideo1.play = true;
      displayedVideo1.end = false;
      displayedVideo1.video.noLoop();
      videoPlays = 0;
      displayedVideo2.end = true;
    }
  }


  public void loadVideo(int n, int i, ArrayList[] b, Video v) {
    ArrayList<Video> m = b[n];
    currentVideos = n;
    videoNumber2 = i;
    v = m.get(i);
    v.video.jump(0);
    v.video.play();
    v.play = true;
    v.end = false;
    videoPlays = 0;
    v.video.noLoop();
  }

  public void update() {
    updateVideo(displayedVideo1, videoNumber1);
    updateVideo(displayedVideo2, videoNumber2);
  }

  public void updateVideo(Video v, int vN) {
    try {
      //println(vN + "    " + v.video.time() + "    //    " + v.loopTimes + "   " + v.video.duration());
      if (v.video.available() == true && v.play == true) {
        v.video.read();
      }
      if (v.end == true && v.fade <= 255) {
        v.fade += 255/pa.frameRate;
        if (v.fade >= 255) {
          v.play = false;
          v.video.stop();
        }
      }

      if (v.play == true && v.fade >= 0 && v.end == false) {
        v.fade -= 255/pa.frameRate;
      }
      //loop video
      if (v.video.time() >= v.video.duration()-0.12f && v.play == true && videoPlays < v.loopTimes) {
        v.video.jump(0);
        videoPlays += 1;
      }
      //next video in folder
      else if (v.video.time() >= v.video.duration()-0.12f && videoPlays >= v.loopTimes && vN < videos[currentVideos].size()-1 && v.play == true) {
        videoPlays = 0;
        vN += 1;
        loadVideo(currentVideos, vN, videos, v);
      }
      //random video in same folder
      else if (v.video.time() >= v.video.duration()-0.12f && videoPlays >= v.loopTimes && vN >= videos[currentVideos].size()-1 && v.play == true) {
        videoPlays = 0;
        vN = (int) random(-1, videos[currentVideos].size()- 1);
        loadVideo(currentVideos, vN, videos, v);
      }
    }
    catch (NullPointerException e) {
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "halloWerner_software" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
