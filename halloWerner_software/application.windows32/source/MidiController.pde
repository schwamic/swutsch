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
      GUI = false;
    } else
    {
      GUI = true;
    }
  }

  void updateKnob(int number, int value) {
    switch(number) {
    case 1: 
      param01=value;
      println("updateKnob "+value);
      break;
    case 2: 
      param02=value;
      break;
    case 3: 
      param03=value;
      break;
    case 4: 
      param04=value;
      break;
    case 5: 
      param05=value;
      break;
    case 6: 
      param06=value;
      break;
    case 7: 
      param07=value;
      break;
    case 8: 
      param08=value;
      break;
    }
  }
  void updateButton(int pitch, boolean on) {
    if (on) {
      println("abc");
      switch(pitch) {
      case 36: 
        //button01 = true;
        button05(0);
        break;
      case 37: 
        button06(0);
        //button02 = true;
        break;
      case 38: 
        button07(0);
        //button03 = true;
        break;
      case 39: 
        button08(0);
        //button04 = true;
        break;
      case 40: 
        button01(0);
        //button05 = true;
        break;
      case 41: 
        button02(0);
        //button06 = true;
        break;
      case 42: 
        button03(0);
        //button07 = true;
        break;
      case 43: 
        button04(0);
        //button08 = true;
        break;
      }
    } else {
      /*switch(pitch) {
       case 36: 
       button01 = false;
       break;
       case 37: 
       button02 = false;
       break;
       case 38: 
       button03 = false;
       break;
       case 39: 
       button04 = false;
       break;
       case 40: 
       button05 = false;
       break;
       case 41: 
       button06 = false;
       break;
       case 42: 
       button07 = false;
       break;
       case 43: 
       button08 = false;
       break;
       }*/
    }
  }
}