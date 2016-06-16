class MidiController {

  PApplet pa;
  MidiBus myBus;

  MidiController(PApplet pa) {
    this.pa = pa;
  }

  void midiControllerSetup() {
    //MidiBus.list();
    myBus = new MidiBus(pa, 0, 0);
  }

  void midiControllerUpdate(int pressed, int button, int value) {
    //if any button is pressed on the midi Controller
    if (pressed == 144) {
      //button 1 is for water (left top)
      if (button == 40) {
        videoInput.videoFolder1 = "Water";
      }

      //button 2 is for mystic (2 left top)
      if (button == 41) {
        videoInput.videoFolder1 = "Mystic";
      }

      //button 3 is for reeper (2.right top)
      if (button == 42) {
        videoInput.videoFolder1 = "Reeper";
      }

      //button 4 is for clear (1.right top)
      if (button == 43) {
        controller.playVideo = 0;
        videoInput.myVideo.stop();
      }

      //button 5 is for fast (1.left bot)
      if (button == 36) {
        videoInput.videoFolder2 = "Fast";
        workaround();
        videoInput.loadVideo();
      }

      //button 6 is for medium (2.left bot)
      if (button == 37) {
        videoInput.videoFolder2 = "Medium";
        workaround();
        videoInput.loadVideo();
      }

      //button 7 is for slow (2.left bot)
      if (button == 38) {
        videoInput.videoFolder2 = "Slow";
        workaround();
        videoInput.loadVideo();
      }

      //button 8 is for slow (2.left bot)
      if (button == 39) {
        videoInput.videoFolder1 = "OwnContent";
        videoInput.videoFolder2 = "";
        videoInput.loadVideo();
      }
    }

    if (pressed == 176) {

      //"slider" 1 is for particleAmount (slider top left);
      if (button == 1) {
        controller.particleAmount = (int)map(value, 0, 127, 0, 255);
      }

      //"slider" 2 is for colorIntensity (2.slider top left);
      if (button == 2) {
        controller.colorIntensity = (int)map(value, 0, 127, 0, 10);
      }

      //"slider" 3 is for triangleAngle (2.slider top right);
      if (button == 3) {
        controller.triangleSize = (int) map(value, 0, 127, 50, 150);
      }
    }
  }

  void workaround() {
    //workaround, fix later
    if (videoInput.videoFolder1 == "OwnContent") {
      videoInput.videoFolder1 = "Mystic";
    }
  }
}