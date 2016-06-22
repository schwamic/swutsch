class ChildApplet extends PApplet {

  PGraphics pg;
  GraphicOutput graphicOutput;

  public ChildApplet() {
    super();
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }

  public void settings() {
    size(1280, 720);
    //size(displayWidth,displayHeight);
    //size(640,360);
  }

  public void setup() {
    this.frameRate(setFrameRate);
    surface.setTitle("Output window - child PApplet");

    //create graphicoutput, needs to be created after generative since its using values from it
    graphicOutput = new GraphicOutput(this);

    graphicOutput.setupGraphic();
  }

  public void draw() {

    //call graphicOutput to display stuff
    graphicOutput.drawGraphic();
    image(bildAnpassungen(testImage), 0, 0);
  }

  PImage bildAnpassungen(PImage input) {
    input.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        color c = input.get(x, y);
        int r = (c >> 16) & 0xFF;  
        int g = (c >> 8) & 0xFF;   
        int b = c & 0xFF;

        r= param01;
        b= param02;

        input.pixels[y*input.width+x] = color(r, g, b);
      }
    }
    input.updatePixels();
    return input;
  }
}