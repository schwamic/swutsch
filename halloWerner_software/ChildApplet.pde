class ChildApplet extends PApplet {

  Generative generative;
  PGraphics pg;
  Output LEDoutput;

  public ChildApplet() {
    super();
    PApplet.runSketch(new String[]{this.getClass().getName()}, this);
  }

  void settings() {
    size(1280, 720);
  }

  void setup() {
    this.frameRate(setFrameRate);
    surface.setTitle("Output window - child PApplet");
    generative = new Generative(this);

    //creates a number of particles --- !!has to be particleNbr%3 = 0, because we want to draw triangles with them
    generative.setupParticles(150);
    
    //create output to LEDs
    LEDoutput = new Output(this);
    

    pg = createGraphics(this.width, this.height);
  }


  void draw() {
    //update particles
    generative.updateParticles();

    //draw everything into the PGraphic pg to later scale to LED screen size
    pg.beginDraw();
    pg.background(0);

    //throws a lot of errors, so i don't use it
    //pg.image(videoInput.frame(),0,0);

    generative.drawParticles(pg);
    generative.drawTriangles(pg);
    pg.endDraw();
    
    LEDoutput.out(pg);
    
    image(pg, 0,0);
  }
}