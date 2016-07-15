class GraphicOutput {

  PApplet pa;
  LEDOutput ledOutput;
  PGraphics pgVideo;
  PGraphics pgGenerativ;
  Generative generative;

  GraphicOutput(PApplet pa) {
    this.pa = pa;
  }



  void setupGraphic() {
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


  void drawGraphic() {
    //update particles
    generative.updateParticles();
    generative.deleteParticle();
    generative.addParticle();
    
    
    pgGenerativ.beginDraw();
    pgGenerativ.scale(0.1);
    pgGenerativ.background(0);
    generative.drawTriangles(pgGenerativ);
    pgGenerativ.endDraw();

    //draw everything into the PGraphic pg to later scale to LED screen size

    pgVideo.beginDraw();
    pgVideo.scale(0.1);
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
    scaledGraphic.image(outPut.generativeAlteration(pgGenerativ), 0, 0);
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