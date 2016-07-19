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

  PImage videoAlteration(PImage input) {
    PGraphics output;
    output = createGraphics(input.width, input.height);
    output.beginDraw();
    output.image(input, 0, 0);
    output.endDraw();

    input.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        color c = input.get(x, y);
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

  int newHSB(int oldHSB) {
    if (oldHSB>0 && oldHSB<240)return 360-oldHSB/2;
    else return oldHSB;
  }

  PImage generativeAlteration(PImage input) {
    PGraphics output;
    output = createGraphics(input.width, input.height);
    output.beginDraw();
    output.image(input, 0, 0);
    output.endDraw();

    input.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        color c = input.get(x, y);
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