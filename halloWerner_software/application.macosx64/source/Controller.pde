class Controller {
  //test variable until sliders are implemented
  int playVideo, particleAmount, colorIntensity, triangleSize;

  GUI gui;
  MidiController midiController;

  PApplet pa;

  public Controller(PApplet pa) {
    colorIntensity = 5;
    particleAmount = 125;
    triangleSize = 1;
    this.pa = pa;
  }

  void init() {
    gui = new GUI(pa);
    gui.init();
    midiController = new MidiController(pa);
    //gui.guiSetup();
    midiController.midiControllerSetup();
  }
  void update() {
    if (GUI) gui.update(); //funktioniert nicht mit midi zusammen
  }
  void updateKnob(int number, int value) {   
    midiController.updateKnob(number, value);
  }
  void updateButton(int pitch, boolean on) {   
    midiController.updateButton(pitch, on);
  }
}