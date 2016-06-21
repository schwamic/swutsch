class ChildApplet extends PApplet {

  PGraphics pg;
  GraphicOutput graphicOutput;
  PImage testImage;

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
    testImage = loadImage("image.JPG");

    //create graphicoutput, needs to be created after generative since its using values from it
    graphicOutput = new GraphicOutput(this);

    graphicOutput.setupGraphic();
  }

  public void draw() {

    //call graphicOutput to display stuff
    graphicOutput.drawGraphic();
    image(bildAnpassungen(testImage),0,0);
  }

  PImage bildAnpassungen(PImage input) {
    //int dimensions = input.width*input.height;
    PImage img = input;
    img.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        color c = img.get(x, y);
        int r = (c >> 16) & 0xFF;  
        int g = (c >> 8) & 0xFF;   
        int b = c & 0xFF;

        r= r+ (255-r)*(255/param01);
        img.pixels[x+y] = color(r, g, b);
      }
    }
    img.updatePixels();
    return input;
  }
}