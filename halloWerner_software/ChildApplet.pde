class ChildApplet extends PApplet {

  PGraphics pg;
  GraphicOutput graphicOutput;

  public ChildApplet() {
    super();
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }

  void settings() {
    size(1280, 720);
    //size(displayWidth,displayHeight);
    //size(640,360);
  }

  void setup() {
    this.frameRate(setFrameRate);
    surface.setTitle("Output window - child PApplet");
    
    //create graphicoutput, needs to be created after generative since its using values from it
    graphicOutput = new GraphicOutput(this);
    
    graphicOutput.setupGraphic();
  }
  
  void draw() {
    
    //call graphicOutput to display stuff
    graphicOutput.drawGraphic();
    
  }
}