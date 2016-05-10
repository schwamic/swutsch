class GUI {
  PApplet pa;
  GUI(PApplet sketch) {
    pa = sketch;
  }

  void render() {//Zeigt die gui am bildschirm an
    fill(#FF00FF);
    rect(width/2, height/2, 20, 20);
  }
}