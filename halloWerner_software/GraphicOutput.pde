class GraphicOutput {

  PApplet pa;
  LEDOutput ledOutput;
  PGraphics pg;
  Generative generative;
  int fade = 0;

  GraphicOutput(PApplet pa) {
    this.pa = pa;
  }



  void setupGraphic() {
    pg = createGraphics(pa.width, pa.height);

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
    pg.background(0);

    if (controller.playVideo == 1) {
      if (videoInput.myVideo.duration()-videoInput.myVideo.time() < 3 && fade > 0) {
        fade -= 10;
      }
      if (videoInput.myVideo.time() + videoInput.myVideo.duration() < videoInput.myVideo.duration()+3 && fade < 255) {
        fade += 10;
      }
      //pg.tint(255, fade);
      pg.image(videoInput.displayVideo(), 0, 0);
    }
    if (controller.playVideo == 0) {
      generative.drawParticles(pg);
      generative.drawTriangles(pg);
    }
    pg.endDraw();
    //pa.scale(0.1);

    ledOutput.out(pg);
    pa.image(pg, 0, 0);


    //you can scale PGraphics with pa.scale(0.1 to 1)
  }
}