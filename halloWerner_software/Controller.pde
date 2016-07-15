class Controller {
  //test variable until sliders are implemented
  int particleAmount, playVideo;

  GUI gui;
  MidiController midiController;

  PApplet pa;

  public Controller(PApplet pa) {
    particleAmount = 0;
    this.pa = pa;
  }

  void init() {
    gui = new GUI(pa);
    gui.init();
    midiController = new MidiController(pa);
    midiController.midiControllerSetup();
  }
  void update() {
     gui.update();
  }
  void updateKnob(int number, int value) {   
    midiController.updateKnob(number, value);
  }
  void updateButton(int pitch, boolean on) {   
    midiController.updateButton(pitch, on);
  }
}