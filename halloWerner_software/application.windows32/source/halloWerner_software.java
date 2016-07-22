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


PImage img;

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


public void settings() {
  size(1000, 500);
  //size(500, 300);
}

public void setup() {
  img = loadImage("kreisviereck.jpg");
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

public void draw() {
  background(210);
  controller.update();
  videoInput.update();

  //update and draw sound Analysis, draw is inside the update since then I only have to iterate through all frequencys once
  soundAnalysis.update();
}


public void fast() {
  //videoInput.loadVideoOnClick(2, (int) random(-1, videoInput.fast.size()), videoInput.videos); //random
  videoInput.loadVideoOnClick(2, 0, videoInput.videos);  //linear
}

public void middle() {
  videoInput.loadVideoOnClick(1, 0, videoInput.videos);
}

public void slow() {
  videoInput.loadVideoOnClick(0, 0, videoInput.videos);
}

public void reset() {
  videoInput.resetVideo(videoInput.displayedVideo1);
  videoInput.resetVideo(videoInput.displayedVideo2);
}

public void wave() {
  videoInput.loadVideoOnClick(3, 0, videoInput.videos);
}

public void women() {
  videoInput.loadVideoOnClick(4, 0, videoInput.videos);
}


public void custom01() {
  videoInput.loadVideoOnClick(5, 0, videoInput.videos);
}

public void custom02() {
  videoInput.loadVideoOnClick(6, 0, videoInput.videos);
}



//midi input, wird \u00fcner controller an midiController weiter geleitet
public void controllerChange(ControlChange change) {
  controller.updateKnob(change.number(), change.value());
  //println("Number:"+change.number());
  // println("Value:"+change.value());
}

public void noteOn(Note note) {
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
class Button {


  PVector pos, size;
  boolean pressed = false;
  boolean active = false;
  int value;
  String name;
  Button(String name, PVector pos, PVector size) {
    this.pos = pos;
    this.size = size;
    this.name = name;
  }

  public void drawButton() {
    strokeWeight(1);
    stroke(255, 100);
    textAlign(CENTER);
    fill(0, 180);
    text(name, pos.x + size.x/2, pos.y -10);

    if (active == false) {
      fill(120);
    } else if ( active == true) {
      fill(180,0,0);
    }
    rect(pos.x, pos.y, size.x, size.y);
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
        float h = map(controller.gui.hueSlider.value, controller.gui.hueSlider.minValue, controller.gui.hueSlider.maxValue, 240, 360);
        float s = map(controller.gui.saturationSlider.value, controller.gui.saturationSlider.minValue, controller.gui.saturationSlider.maxValue, 0, 360);
        float b = brightness(c);
        float a = 0;
        if (s > 127) s= 127;
        a=controller.gui.videoAlphaSlider.value;

        output.pixels[y*input.width+x] = color(h, s, b, a);
      }
    }
    output.updatePixels();
    return output;
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
        float h = map(controller.gui.hueSlider.value, controller.gui.hueSlider.minValue, controller.gui.hueSlider.maxValue, 240, 360);
        float s = map(controller.gui.saturationSlider.value, controller.gui.saturationSlider.minValue, controller.gui.saturationSlider.maxValue, 0, 360);
        float b = brightness(c);
        float a = alpha(c);
        h= 240+newHSB((int)h)/2*controller.gui.hueSlider.value/127;
        s=(controller.gui.saturationSlider.value-64)*2+s;
        if (s > 127) s= 127;
        output.pixels[y*input.width+x] = color(h, s, b, a);
      }
    }
    output.updatePixels();
    return output;
  }
}
class Controller {
  //test variable until sliders are implemented
  int particleAmount;

  GUI gui;
  MidiController midiController;

  PApplet pa;

   Controller(PApplet pa) {
    particleAmount = 0;
    this.pa = pa;
  }

  public void init() {
    gui = new GUI(pa);
    gui.init();
    midiController = new MidiController(pa);
    midiController.midiControllerSetup();
  }
  public void update() {
     gui.update();
  }
  public void updateKnob(int number, int value) {   
    midiController.updateKnob(number, value);
  }
  public void updateButton(int pitch, boolean on) {   
    midiController.updateButton(pitch, on);
  }
}
class GUI {
  PApplet pa;


  Slider hueSlider;
  Slider saturationSlider;
  Slider videoAlphaSlider;
  Slider generativAlphaSlider;
  Slider pitchSlider;

  Button[] buttons;
  Button fast;
  Button middle;
  Button slow;
  Button reset;
  Button women;
  Button wave;
  Button custom01;
  Button custom02;

  boolean midiInput = false;


  GUI(PApplet sketch) {
    pa = sketch;
  }

  public void init() {


    //Slider
    int SliderXPos = 500;
    hueSlider = new Slider("Hue", new PVector(350+SliderXPos, 50), new PVector(50, 150), 50, 0, 255);
    saturationSlider = new Slider("Saturation", new PVector(200+SliderXPos, 50), new PVector(50, 150), 50, 0, 255);
    videoAlphaSlider = new Slider("Video Alpha", new PVector(50+SliderXPos, 50), new PVector(50, 150), 50, 0, 255);
    generativAlphaSlider = new Slider("Generativ Alpha", new PVector(50+SliderXPos, 250), new PVector(50, 150), 50, 0, 255);
    pitchSlider = new Slider("Pitch", new PVector(200+SliderXPos, 250), new PVector(50, 150), 50, 0, 100);

    //Buttons
    int ButtonXPos = 50;
    buttons = new Button[8];
    fast = new Button("Schnelle Videos", new PVector (50+ButtonXPos, 50), new PVector (50, 50));
    buttons[0] = fast;
    middle = new Button("Mittlere Videos", new PVector (150+ButtonXPos, 50), new PVector (50, 50));
    buttons[1] = middle;
    slow = new Button("Langsame Videos", new PVector (250+ButtonXPos, 50), new PVector (50, 50));
    buttons[2] = slow;
    reset = new Button("Stop Videos", new PVector (350+ButtonXPos, 50), new PVector (50, 50));
    buttons[3] = reset;
    women = new Button("Frauen", new PVector(50+ButtonXPos, 150), new PVector(50, 50));
    buttons[4] = women;
    wave = new Button("Wellen", new PVector(150+ButtonXPos, 150), new PVector(50, 50));
    buttons[5] = wave;
    custom01 = new Button("Eigene Videos 1", new PVector(250+ButtonXPos, 150), new PVector(50, 50));
    buttons[6] = custom01;
    custom02 = new Button("Eigene Videos 2", new PVector(350+ButtonXPos, 150), new PVector(50, 50));
    buttons[7] = custom02;
  }
  public void update() {
    //Slider
    hueSlider.update();
    saturationSlider.update();
    videoAlphaSlider.update();
    generativAlphaSlider.update();
    pitchSlider.update();

    //Buttons
    for (Button b : buttons) {
      buttonUpdate(b);
    }
  }


  public void buttonUpdate(Button b) {
    b.drawButton();
    String name = b.name;
    if (mousePressed || midiInput == true) {
      if ((mouseX > b.pos.x && mouseX < (b.pos.x+b.size.x) && mouseY > b.pos.y && mouseY < (b.pos.y + b.size.y)) || midiInput == true) {
        if (b.pressed == false) {
          resetButtons();
          if ((name == "Schnelle Videos" && mousePressed) || controller.midiController.fast == true) {
            fast();
            controller.midiController.fast = false;
            println("fast1");
            buttons[0].active = true;
          } else if (name == "Mittlere Videos" && mousePressed || controller.midiController.middle == true) {
            middle();
            controller.midiController.middle = false;
            println("middle");
            buttons[1].active = true;
          } else if (name == "Langsame Videos" && mousePressed || controller.midiController.slow == true) {
            slow();
            controller.midiController.slow = false;
            println("slow");
            buttons[2].active = true;
          } else if (name == "Stop Videos" && mousePressed || controller.midiController.reset == true) {
            reset();
            controller.midiController.reset = false;
            println("reset");
            buttons[3].active = true;
          } else if (name == "Wellen" && mousePressed || controller.midiController.wave == true) {
            wave();
            controller.midiController.wave =false;
            println("wave");
            buttons[5].active = true;
          } else if (name == "Frauen" && mousePressed || controller.midiController.women == true) {
            women();
            controller.midiController.women = false;
            println("women");
            buttons[4].active = true;
          } else if (name == "Eigene Videos 1" && mousePressed || controller.midiController.custom01 == true) {
            custom01();
            controller.midiController.custom01 = false;
            println("custom01");
            buttons[6].active = true;
          } else if (name == "Eigene Videos 2" && mousePressed || controller.midiController.custom02 == true) {
            custom02();
            controller.midiController.custom02 = false;
            println("custom02");
            buttons[7].active = true;
          }
          midiInput = false;

          b.value = -b.value;

          //b.active = true;
        }
        b.pressed = true;
      }
    } else {
      b.pressed = false;
    }
  }

  public void resetButtons() {

    for (Button bt : buttons) {
      bt.active = false;
    }
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
      pg.fill(p.energy*5, 255, 255, p.energy*5);
      

      pg.strokeWeight(0);
      //DODAT another stroke formula
      //pg.stroke(p.energy*5, 255, 255, p.energy*5);

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
  PGraphics pgVideo;
  PGraphics pgGenerativ;
  Generative generative;

  GraphicOutput(PApplet pa) {
    this.pa = pa;
  }



  public void setupGraphic() {
    pgVideo  = pa.createGraphics(pa.width, pa.height);
    pgGenerativ = pa.createGraphics(pa.width, pa.height);

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
    
    pgGenerativ.beginDraw();
    pgGenerativ.clear();
    pgGenerativ.endDraw();
    
    pgGenerativ.beginDraw();
    pgGenerativ.scale(0.1f);
    generative.drawTriangles(pgGenerativ);
    pgGenerativ.endDraw();

    //draw everything into the PGraphic pg to later scale to LED screen size

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
    scaledGraphic.tint(255, controller.gui.generativAlphaSlider.value);
    scaledGraphic.image(outPut.generativeAlteration(pgGenerativ), 0, 0);
    
    scaledGraphic.tint(255,255);
    
    //hier Einrichtbild
    //scaledGraphic.image(img,0,0);
    scaledGraphic.endDraw();

    //LED OUTPUT FUNCTIONS
    ledOutput.getGraphic(scaledGraphic);
    ledOutput.drawDisplay();


    //displays scaled down graphic output for LEDs
    //pa.image(scaledGraphic, 0, 0, pa.width, pa.height);
    pa.image(scaledGraphic, 0, 0, pa.width, pa.height);



    //you can scale PGraphics with pa.scale(0.1 to 1)
  }
}
/*                                  
 LED_OUTPUT - MS WERNER
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
 */

class LEDOutput {
  
  PApplet pa;
  PGraphics pg;
  //Konstruktor
  LEDOutput(PApplet pa) {
    this.pa = pa;
  }
  //GetMethode
  public void getGraphic(PGraphics newPg) {
    pg = newPg;
  }

  //VARIABLEN
  int WidthModule = 16;
  int[] Pixels;
  int state;
  int count;
  int state1;
  int state2;
  boolean firstRaw;
  String val;
  Serial myPort;  // Create object from Serial class
  byte[] dataLED;
  float[] moduleEmpty;
  int[][] allModules;
  float[][] rgbModules;

  //################################################################# SETUP #################################################################
  public void setupDisplay() {
    //println(Serial.list()[3]);
    //println(Serial.list());
    //String portName = Serial.list()[3]; //3 entspricht ...usbmodem1635641
    myPort = new Serial(pa, "COM4", 115200);  //9600 ist langsam -> hochsetzen auf 115200
    myPort.setDTR(true);
    myPort.bufferUntil('\n'); 

    firstRaw = true;
    Pixels = new int[0];
    count = 0;
    state1=0;
    state2=5;
    allModules = new int[10][0];
    rgbModules = new float[10][0];
    dataLED =  new byte[0];  
    float[] moduleEmpty = new float[0]; 

    for (int i = 0; i < 144; i++) {
      moduleEmpty = append(moduleEmpty, PApplet.parseFloat(255));
    }
  }

  //################################################################# DRAW #################################################################
  public void drawDisplay() {
    fillPixelArray();
    moduleAufbau();

    //Bild in 10 Module aufteilen und 16x16 f\u00fcllen
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

    //Module f\u00fcr das Teensy vorbereiten
    float[] newrgb1 = makeRightOrderSide(rgbModules[0], moduleOrder1);
    float[] newrgb2 = makeRightOrder(rgbModules[1], moduleOrder2);
    float[] newrgb3 = makeRightOrder(rgbModules[2], moduleOrder3);
    float[] newrgb4 = makeRightOrder(rgbModules[3], moduleOrder4);
    float[] newrgb5 = makeRightOrderSide(rgbModules[4], moduleOrder5);
    float[] newrgb6 = makeRightOrder(rgbModules[5], moduleOrder6);
    float[] newrgb7 = makeRightOrder(rgbModules[6], moduleOrder7);
    float[] newrgb8 = makeRightOrder(rgbModules[7], moduleOrder8);
    float[] newrgb9 = makeRightOrder(rgbModules[8], moduleOrder9);
    float[] newrgb10 = makeRightOrder(rgbModules[9], moduleOrder10);

    //F\u00fcr Serial ein byteString erstellen
    makeDataLED(newrgb3); //1  
    fillEmpty();
    
    makeDataLED(newrgb2); //2 
    fillEmpty();

    makeDataLED(newrgb1); //3
    makeDataLED(newrgb6);

    makeDataLED(newrgb4); //4
    fillEmpty();

    makeDataLED(newrgb8); //5
    fillEmpty();


    makeDataLED(newrgb7); //6
    fillEmpty();

    makeDataLED(newrgb5);
    makeDataLED(newrgb10); //7
        for (int i = 0; i < 18; i++) {    //Ausgleich: (74-68)*3 = 18
      dataLED = append(dataLED, PApplet.parseByte(0));
    }


    makeDataLED(newrgb9); //8
    fillEmpty(); 

    //An Teensy versenden
    myPort.write(dataLED);
    
    //Reset
    clearAll();
  }

  //################################################################# FUNKTIONEN #################################################################
  //Zum Modulausgleich
  public void fillEmpty() {
    for (int i = 0; i<222; i++) {
      dataLED = append(dataLED, PApplet.parseByte(0));
    }
  }

  //Erstellt aus dem Frame ein Farbarray
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
    for (int y = 0; y < WidthModule; y++) { 
      for (int x = 0; x < WidthModule; x++) { 
        float red = (allModules[index1][index2]) >> 16 & 0xFF;
        float green = (allModules[index1][index2]) >> 8 & 0xFF;
        float blue =  (allModules[index1][index2]) & 0xFF;
        makeRGBModules(index1, red, green, blue);
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

  //Wichtige Funktion zum Aufteilen der Module
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

  //Erstellt die Reihenfolge f\u00fcr die Module.
  public float[] makeRightOrder(float[] rgb, int[] order) {
    int count = 1;
    float[] newrgb = new float[735];      //245*3=735 304*3=912
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

  //Reset f\u00fcr den n\u00e4chsten Frame
  public void clearAll() {
    Pixels = new int[0];
    allModules = new int[10][0];
    rgbModules = new float[10][0];
    dataLED =  new byte[0];
    count = 0;
    state1=0;
    state2=5;
  }

  //########################################################### SERIALE KOMMUNIKATION #######################################################
  public void serialEvent( Serial myPort) {
    val = myPort.readStringUntil('\n');
    //make sure our data isn't empty before continuing
    if (val != null) {
      //trim whitespace and formatting characters (like carriage return)
      val = trim(val);
      //println(val);
    }
  }

  //################################################################# DATA #################################################################
  //Modul1 mit 74 Pixel
  int[] moduleOrder1 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 111, 112, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 116, 115, 114, 113, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 140, 141, 142, 143, 144, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, 151, 150, 149, 148, 147, 146, 145, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 171, 172, 173, 174, 175, 176, 
    -1, -1, -1, -1, -1, -1, -1, -1, 184, 183, 182, 181, 180, 179, 178, 177, 
    -1, -1, -1, -1, -1, -1, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
    -1, -1, -1, -1, -1, -1, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    -1, -1, -1, -1, -1, -1, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
    -1, -1, -1, -1, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
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
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 
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
    64, 63, 62, 61, 60, 59, 58, 57, 56, -1, -1, -1, -1, -1, -1, -1, 
    65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 
    96, 95, 94, 93, 92, 91, 90, 89, 88, 87, 86, 85, 84, 83, 82, 81, 
    97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, -1, 
    128, 127, 126, 125, 124, 123, 122, 121, 120, 119, 118, 117, 116, 115, 114, -1, 
    129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, -1, 
    160, 159, 158, 157, 156, 155, 154, 153, 152, 151, 150, 149, 148, 147, 146, 145, 
    161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, 213, 212, 211, 210, 209, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, 244, 243, 242, 241 
  };

  //Modul5 mit 68 Pixel
  int[] moduleOrder5 = {
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    97, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    128, 127, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    129, 130, 131, 132, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    160, 159, 158, 157, 156, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    161, 162, 163, 164, 165, 166, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    192, 191, 190, 189, 188, 187, 186, -1, -1, -1, -1, -1, -1, -1, -1, -1, 
    193, 194, 195, 196, 197, 198, 199, 200, -1, -1, -1, -1, -1, -1, -1, -1, 
    224, 223, 222, 221, 220, 219, 218, 217, 216, 215, 214, -1, -1, -1, -1, -1, 
    225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, -1, -1, -1, -1, 
    256, 255, 254, 253, 252, 251, 250, 249, 248, 247, 246, 245, -1, -1, -1, -1
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
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 179, 
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
    192, 191, 190, 189, 188, 187, 186, 185, 184, 183, 182, 181, 180, 179, 178, 177, 
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
    193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 205, 
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
}
class MidiController {
  PApplet pa;
  MidiBus myBus;
  boolean fast, middle, slow, women, wave, custom01, custom02, reset;

  MidiController(PApplet pa) {
    this.pa = pa;
  }

  public void midiControllerSetup() {
    String[] midiArray = MidiBus.availableInputs();
    List<String> midiList = Arrays.asList(midiArray);
    if (midiList.contains("LPD8")) {
      myBus = new MidiBus(pa, "LPD8", 0);
    }
  }

  public void updateKnob(int number, int value) {
    switch(number) {
    case 1: 
      controller.gui.videoAlphaSlider.displayValue= (int) map(value, 0, 127, controller.gui.videoAlphaSlider.sliderSize/2, controller.gui.videoAlphaSlider.size.y-controller.gui.videoAlphaSlider.sliderSize/2-1);
      break;
    case 2: 
      controller.gui.saturationSlider.displayValue= (int) map(value, 0, 127, controller.gui.saturationSlider.sliderSize/2, controller.gui.saturationSlider.size.y-controller.gui.saturationSlider.sliderSize/2-1);
      break;
    case 3: 
      controller.gui.hueSlider.displayValue= (int) map(value, 0, 127, controller.gui.hueSlider.sliderSize/2, controller.gui.hueSlider.size.y-controller.gui.hueSlider.sliderSize/2-1);
      break;
    case 4: 
      //param04=value;
      break;
    case 5: 
      controller.gui.generativAlphaSlider.displayValue= (int) map(value, 0, 127, controller.gui.generativAlphaSlider.sliderSize/2, controller.gui.generativAlphaSlider.size.y-controller.gui.generativAlphaSlider.sliderSize/2-1);
      break;
    case 6: 
      controller.gui.pitchSlider.displayValue= (int) map(value, 0, 127, controller.gui.pitchSlider.sliderSize/2, controller.gui.pitchSlider.size.y-controller.gui.pitchSlider.sliderSize/2-1);
      break;
    case 7: 
      //param07=value;
      break;
    case 8: 
      //param08=value;
      break;
    }
  }

  public void startVideosOnClick() {
  }

  public void updateButton(int pitch, boolean on) {
    if (on) {
      switch(pitch) {
      case 36: 
        women = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.women, true);
        println("women");

        break;
      case 37: 
        wave = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.wave, true);
        println("wave");

        break;
      case 38: 
        custom01 = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.custom01, true);
        println("custom01");

        break;
      case 39: 
        custom02 = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.custom02, true);
        println("custom02");

        break;
      case 40: 
        fast = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.fast, true);
        println("fast");

        break;
      case 41: 
        middle = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.middle, true);
        println("middle");

        break;
      case 42: 
        slow = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.slow, true);
        println("slow");

        break;
      case 43: 
        reset = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.reset, true);
        println("reset");

        break;
      }
    }
  }
}
class Slider {

  PVector pos, size;
  int displayValue, sliderSize;
  float minValue, maxValue, value;
  String name;

  Slider(String name, PVector pos, PVector size, int defaultValue, float minValue, float maxValue) {
    this.pos = pos;
    this.size = size;
    this.sliderSize = 10;
    this.displayValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.name = name;
  }

  public void moveSlider() {
    if (mousePressed) {
      if (mouseX > pos.x && mouseX < (pos.x + size.x) && mouseY > pos.y+sliderSize/2 && mouseY <  pos.y+size.y-sliderSize/2) {
        if ( displayValue < size.y-sliderSize/2 && displayValue > sliderSize/2) {
          displayValue = (int)  mouseY - (int)  pos.y;
        }
      }
    }
  }


  public void update() {
    moveSlider();
    strokeWeight(1);
    stroke(255, 100);

    textAlign(CENTER);
    fill(0, 180);
    value = (int) map(displayValue, sliderSize/2, size.y-sliderSize/2, minValue, maxValue+1);
    text(name + ": " + value, pos.x+size.x/2, pos.y - 10);

    noFill();
    rect(pos.x, pos.y, size.x, size.y);

    fill(255, 100);
    rect(pos.x, pos.y+displayValue-sliderSize/2, size.x, sliderSize);
    noFill();
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

    float scaleAvg = controller.gui.pitchSlider.value;

    pastAverages = new float[avgSeconds*setFrameRate];

    getMicInput(); 
    //getSongInput();

    fftLog.logAverages(100, 10);

    avgFrequencys = new AvgFrequency[fftLog.avgSize()];
    float avgFrequencyXDisplayLength = pa.width/2/1.2f/fftLog.avgSize();
    for (int i = 0; i < avgFrequencys.length; i++) {

      avgFrequencys[i] = new AvgFrequency(pa.width/2-avgFrequencys.length/2*avgFrequencyXDisplayLength+i*avgFrequencyXDisplayLength-220, fftLog.getAverageCenterFrequency(i), scaleAvg, avgFrequencyXDisplayLength);
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
      avgFrequencys[i].scaleAvg = map(controller.gui.pitchSlider.value, controller.gui.pitchSlider.minValue, controller.gui.pitchSlider.maxValue, 0, 4);
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
    this.neighbour1Pos = new PVector(0, 0);
    this.neighbour2Pos = new PVector(width, height);
    this.newNeighbour1Pos = new PVector(0, 0);
    this.newNeighbour2Pos = new PVector(width, height);
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


  //String currentFolder;


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
    //ArrayList<Video> m = videos[0];
    //displayedVideo1 = m.get(0);
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
      //displayedVideo1.video.noLoop();
    } else if ( displayedVideo1.play == true && displayedVideo2.play == false && m.get(i) != displayedVideo1) {
      currentVideos = n;
      videoNumber2 = i;
      displayedVideo2 = m.get(i);
      displayedVideo2.video.play();
      displayedVideo2.play = true;
      displayedVideo2.end = false;
      videoPlays = 0;
      //displayedVideo2.video.noLoop();
      displayedVideo1.end = true;
    } else if (displayedVideo2.play == true && displayedVideo1.play == false && m.get(i) != displayedVideo2) {
      currentVideos = n;
      videoNumber1 = i;
      displayedVideo1 = m.get(i);
      displayedVideo1.video.play();
      displayedVideo1.play = true;
      displayedVideo1.end = false;
      //displayedVideo1.video.noLoop();
      videoPlays = 0;
      displayedVideo2.end = true;
    }
  }


  /* void loadVideo(int n, int i, ArrayList[] b, Video v) {
   ArrayList<Video> m = b[n];
   currentVideos = n;
   videoNumber2 = i;
   videoNumber1 = i;
   
   
   displayedVideo1 = m.get(i);
   displayedVideo1.video.jump(0);
   displayedVideo1.video.play();
   displayedVideo1.play = true;
   displayedVideo1.end = false;
   
   
   //v.video.noLoop();
   videoPlays = 0;
   }*/

  public void loadVideo(int n, int i, ArrayList[] b, Video v2) {
    ArrayList<Video> m = b[n];
    currentVideos = n;
    videoNumber2 = i;
    videoNumber1 = i;
    Video v = m.get(i);
    if (v2 == displayedVideo1) {
      displayedVideo1 = v;
      displayedVideo2.fade = 255;
      displayedVideo2.end = true;
      displayedVideo2.play = false;
    } else {
      displayedVideo2 = v;
      displayedVideo1.fade = 255;
      displayedVideo1.end = true;
      displayedVideo1.play = false;
    }
    v.video.jump(0);
    v.video.play();
    v.play = true;
    v.end = false;
    v.video.noLoop();
    videoPlays = 0;
  }

  public void update() {
    updateVideo(displayedVideo1, videoNumber1);
    updateVideo(displayedVideo2, videoNumber2);
  }

  public void updateVideo(Video v, int vN) {
    try {
      //println(vN + "    " + v.video.time() + "    //    " + v.loopTimes + "   " + v.video.duration());
      //v.video.play();
      //println("available " + v.video.available());

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
      if (v.video.time() >= v.video.duration()-0.12f && v.play == true && videoPlays < v.loopTimes-1 && v.video.duration() > 0) {
        v.video.jump(0);
        videoPlays += 1;
      }
      //next video in folder
      else if (v.video.time() >= v.video.duration()-0.12f && videoPlays >= v.loopTimes-1 && vN < videos[currentVideos].size()-1 && v.play == true && v.video.duration() > 0) {
        videoPlays = 0;
        vN += 1;
        v.video.stop();
        loadVideo(currentVideos, vN, videos, v);
      }
      //first video in folder
      else if (v.video.time() >= v.video.duration()-0.12f && videoPlays >= v.loopTimes-1 && vN >= videos[currentVideos].size()-1 && v.play == true && v.video.duration() > 0) {
        videoPlays = 0;
        vN = 0;
        v.video.stop();
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
