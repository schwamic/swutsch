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
    buttonCheck();
  }
  void updateKnob(int number, int value) {   
    midiController.updateKnob(number, value);
  }
  void updateButton(int pitch, boolean on) {   
    midiController.updateButton(pitch, on);
  }
  void buttonCheck() {
    /*if (button01) {
      //Hier einfügen was passieren soll
      videoInput.loadVideo(0, (int) random(-1, videoInput.slow.size()), videoInput.videos);
      controller.playVideo = 1;
      button01 = false;
    }
    if (button02) {
      videoInput.loadVideo(1, (int) random(-1, videoInput.middle.size()), videoInput.videos);
      controller.playVideo = 1;
      button02 = false;
    }
    if (button03) {
      videoInput.loadVideo(2, (int) random(-1, videoInput.fast.size()), videoInput.videos);
      controller.playVideo = 1;
      button03 = false;
    }
    if (button04) {
      controller.playVideo = 0;
      videoInput.displayedVideo1.end = true;
      videoInput.displayedVideo2.end = true;
      button04 = false;
    }
    if (button05) {
      videoInput.loadVideo(3, (int) random(-1, videoInput.wave.size()), videoInput.videos);
      controller.playVideo = 1;
      button05 = false;
    }
    if (button06) {
      videoInput.loadVideo(4, (int) random(-1, videoInput.women.size()), videoInput.videos);
      controller.playVideo = 1;
      button06 = false;
    }
    if (button07) {
      button07 = false;
    }
    if (button08) {
      button08 = false;
    }*/
  }
}