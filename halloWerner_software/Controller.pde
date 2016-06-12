class Controller { //parameters in halloWerner_software
  PApplet pa;
  GUI gui;
  
  void init() {
    gui = new GUI(pa);
    gui.init();
  }

  Controller(PApplet sketch) {
    pa = sketch;
  }

  void update() {
    gui.render();
  }

  void change(int cha, int pit, int vel, boolean cont) {
    gui.change(cha, pit, vel, cont);
  }
}