class MidiController {
  PApplet pa;
  MidiBus myBus;

  MidiController(PApplet pa) {
    this.pa = pa;
  }

  void midiControllerSetup() {
    String[] midiArray = MidiBus.availableInputs();
    List<String> midiList = Arrays.asList(midiArray);
    if (midiList.contains("LPD8")) {
      myBus = new MidiBus(pa, "LPD8", 0);
    }
  }

  void updateKnob(int number, int value) {
    switch(number) {
    case 1: 
      controller.gui.hueSlider.value= (int) map(value, 0, 127, controller.gui.hueSlider.minValue, controller.gui.hueSlider.maxValue);
      break;
    case 2: 
      controller.gui.saturationSlider.value=(int) map(value, 0, 127, controller.gui.saturationSlider.minValue, controller.gui.saturationSlider.maxValue);
      break;
    case 3: 
      controller.gui.videoAlphaSlider.value= (int) map(value, 0, 127, controller.gui.videoAlphaSlider.minValue, controller.gui.videoAlphaSlider.maxValue);
      break;
    case 4: 
      //param04=value;
      break;
    case 5: 
      controller.gui.generativAlphaSlider.value= (int) map(value, 0, 127, controller.gui.generativAlphaSlider.minValue, controller.gui.generativAlphaSlider.maxValue);
      break;
    case 6: 
      controller.gui.pitchSlider.value= (int) map(value, 0, 127, controller.gui.pitchSlider.minValue, controller.gui.pitchSlider.maxValue);
      break;
    case 7: 
      //param07=value;
      break;
    case 8: 
      //param08=value;
      break;
    }
  }
  void updateButton(int pitch, boolean on) {
    if (on) {
      println("abc");
      switch(pitch) {
      case 36: 
        women();
        break;
      case 37: 
        wave();
        break;
      case 38: 
        custom01();
        break;
      case 39: 
        custom02();
        break;
      case 40: 
        fast();
        break;
      case 41: 
        middle();
        break;
      case 42: 
        slow();
        break;
      case 43: 
        reset();
        break;
      }
    }
  }
}