<<<<<<< HEAD
class MidiController {
  PApplet pa;
  MidiBus myBus;
  boolean fast, middle, slow, women, wave, custom01, custom02, reset;

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
      controller.gui.hueSlider.displayValue= (int) map(value, 0, 127, controller.gui.hueSlider.sliderSize/2, controller.gui.hueSlider.size.y-controller.gui.hueSlider.sliderSize/2-1);
      break;
    case 2: 
      controller.gui.saturationSlider.displayValue= (int) map(value, 0, 127, controller.gui.saturationSlider.sliderSize/2, controller.gui.saturationSlider.size.y-controller.gui.saturationSlider.sliderSize/2-1);
      break;
    case 3: 
      controller.gui.videoAlphaSlider.displayValue= (int) map(value, 0, 127, controller.gui.videoAlphaSlider.sliderSize/2, controller.gui.videoAlphaSlider.size.y-controller.gui.videoAlphaSlider.sliderSize/2-1);
      break;
    case 4: 
      //param04=value;
      break;
    case 5: 
      controller.gui.generativAlphaSlider.displayValue= (int) map(value, 0, 127, controller.gui.generativAlphaSlider.sliderSize/2, controller.gui.generativAlphaSlider.size.y-controller.gui.generativAlphaSlider.sliderSize/2-1);
      break;
    case 6: 
      controller.gui.pitchSlider.displayValue= (int) map(value, 0, 127, controller.gui.pitchSlider.sliderSize/2, controller.gui.pitchSlider.size.y-controller.gui.pitchSlider.sliderSize/2-1);
      break;
    case 7: 
      //param07=value;
      break;
    case 8: 
      //param08=value;
      break;
    }
  }
  
  void startVideosOnClick() {
    
    
    
    
  }
  
  void updateButton(int pitch, boolean on) {
    if (on) {
      switch(pitch) {
      case 36: 
        women = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.women, true);
        println("women");
        
        break;
      case 37: 
        wave = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.wave, true);
        println("wave");

        break;
      case 38: 
        custom01 = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.custom01, true);
        println("custom01");

        break;
      case 39: 
        custom02 = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.custom02, true);
        println("custom02");

        break;
      case 40: 
        fast = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.fast, true);
        println("fast");

        break;
      case 41: 
        middle = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.middle, true);
        println("middle");

        break;
      case 42: 
        slow = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.slow, true);
        println("slow");

        break;
      case 43: 
        reset = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.reset, true);
        println("reset");

        break;
      }
    }
  }
=======
class MidiController {
  PApplet pa;
  MidiBus myBus;
  boolean fast, middle, slow, women, wave, custom01, custom02, reset;

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
      controller.gui.hueSlider.displayValue= (int) map(value, 0, 127, controller.gui.hueSlider.sliderSize/2, controller.gui.hueSlider.size.y-controller.gui.hueSlider.sliderSize/2-1);
      break;
    case 2: 
      controller.gui.saturationSlider.displayValue= (int) map(value, 0, 127, controller.gui.saturationSlider.sliderSize/2, controller.gui.saturationSlider.size.y-controller.gui.saturationSlider.sliderSize/2-1);
      break;
    case 3: 
      controller.gui.videoAlphaSlider.displayValue= (int) map(value, 0, 127, controller.gui.videoAlphaSlider.sliderSize/2, controller.gui.videoAlphaSlider.size.y-controller.gui.videoAlphaSlider.sliderSize/2-1);
      break;
    case 4: 
      //param04=value;
      break;
    case 5: 
      controller.gui.generativAlphaSlider.displayValue= (int) map(value, 0, 127, controller.gui.generativAlphaSlider.sliderSize/2, controller.gui.generativAlphaSlider.size.y-controller.gui.generativAlphaSlider.sliderSize/2-1);
      break;
    case 6: 
      controller.gui.pitchSlider.displayValue= (int) map(value, 0, 127, controller.gui.pitchSlider.sliderSize/2, controller.gui.pitchSlider.size.y-controller.gui.pitchSlider.sliderSize/2-1);
      break;
    case 7: 
      //param07=value;
      break;
    case 8: 
      //param08=value;
      break;
    }
  }
  
  void startVideosOnClick() {
    
    
    
    
  }
  
  void updateButton(int pitch, boolean on) {
    if (on) {
      switch(pitch) {
      case 36: 
        women = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.women, true);
        println("women");
        
        break;
      case 37: 
        wave = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.wave, true);
        println("wave");

        break;
      case 38: 
        custom01 = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.custom01, true);
        println("custom01");

        break;
      case 39: 
        custom02 = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.custom02, true);
        println("custom02");

        break;
      case 40: 
        fast = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.fast, true);
        println("fast");

        break;
      case 41: 
        middle = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.middle, true);
        println("middle");

        break;
      case 42: 
        slow = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.slow, true);
        println("slow");

        break;
      case 43: 
        reset = true;
        controller.gui.midiInput = true;
        //controller.gui.buttonUpdate(controller.gui.reset, true);
        println("reset");

        break;
      }
    }
  }
>>>>>>> 0067efa7885791ce7cf0e4b66c2c6f2c0a237850
}