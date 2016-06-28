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
    size(640,360);
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
    //image(bildAnpassungen(graphicOutput.pg), 0, 0);
  }

  PImage bildAnpassungen(PImage input) {
    PGraphics output;
    output = createGraphics(input.width, input.height);
    output.beginDraw();
    output.image(input, 0, 0);
    output.endDraw();

    input.loadPixels();
    for (int y=0; y<input.height; y++) {
      for (int x= 0; x<input.width; x++) {
        color c = input.get(x, y);
        float h = hue(c);
        float s = saturation(c);
        float b = brightness(c);
        float a = 0;
        /* int r = (c >> 16) & 0xFF;  
         int g = (c >> 8) & 0xFF;   
         int b = c & 0xFF;*/

        //h=(param01-64)*2+h;//alle farben ändern sich
        //h=232+param01;//tint
        //h=(h/360)*232+param01;//test
        h= 240+newHSB((int)h)/2*param01/127;//final
        s=(param02-64)*2+s;
        if(s > 127) s= 127;
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
  int newHSB(int oldHSB) {
    if (oldHSB>0 && oldHSB<240)return 360-oldHSB/2;
    else return oldHSB;
  }
}