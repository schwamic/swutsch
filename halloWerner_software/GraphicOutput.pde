class GraphicOutput {

  PApplet pa;
  LEDOutput ledOutput;
  PGraphics pg;
  Generative generative;

  GraphicOutput(PApplet pa) {
    this.pa = pa;
  }



  void setupGraphic() {
    pg = pa.createGraphics(pa.width, pa.height);

    //create generative soudn visualizer
    generative = new Generative(pa, 150);
    generative.setupParticles();

    //create output to LEDs
    ledOutput = new LEDOutput(pa);
  }


  void drawGraphic() {
    //update particles
    if (controller.playVideo == 0) {
      generative.updateParticles();
      generative.deleteParticle();
      generative.addParticle();
    }


    //draw everything into the PGraphic pg to later scale to LED screen size
    pg.beginDraw();
    //pg.background(0);
    pg.fill(0, 0, 0, 180);
    pg.rect(0, 0, pa.width, pa.height);
    pg.noFill();
    if (controller.playVideo == 0) {
      //generative.drawParticles(pg);
      generative.drawTriangles(pg);
    }
    try {
      pg.tint(255, 255-videoInput.displayedVideo1.fade);
      //pg.image(outPut.bildAnpassungen(videoInput.displayedVideo1.video), 0, 0, pa.width, pa.height);
      pg.image(videoInput.displayedVideo1.video, 0, 0, pa.width, pa.height);
      pg.tint(255, 255-videoInput.displayedVideo2.fade);
      //pg.image(outPut.bildAnpassungen(videoInput.displayedVideo2.video), 0, 0, pa.width, pa.height);
      pg.image(videoInput.displayedVideo2.video, 0, 0, pa.width, pa.height);
    } 
    catch(NullPointerException e) {
    }
    pg.endDraw();
    //pa.scale(0.1);
   // pg = (PGraphics) outPut.bildAnpassungen(pg);
    ledOutput.out(pg);
    pa.image(pg, 0, 0);


    //you can scale PGraphics with pa.scale(0.1 to 1)
  }
}