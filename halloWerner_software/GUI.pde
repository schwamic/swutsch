class GUI {
  PApplet pa;


  Slider hueSlider;
  Slider saturationSlider;
  Slider videoAlphaSlider;
  Slider generativAlphaSlider;
  Slider pitchSlider;

  Button[] buttons;
  Button fast;
  Button middle;
  Button slow;
  Button reset;
  Button women;
  Button wave;
  Button custom01;
  Button custom02;
  
  boolean midiInput = false;


  GUI(PApplet sketch) {
    pa = sketch;
  }

  void init() {


    //Slider
    int SliderXPos = 500;
    hueSlider = new Slider("Hue", new PVector(50+SliderXPos, 50), new PVector(50, 150), 50, 0, 255);
    saturationSlider = new Slider("Saturation", new PVector(200+SliderXPos, 50), new PVector(50, 150), 50, 0, 255);
    videoAlphaSlider = new Slider("Video Alpha", new PVector(350+SliderXPos, 50), new PVector(50, 150), 50, 0, 255);
    generativAlphaSlider = new Slider("Generativ Alpha", new PVector(50+SliderXPos, 250), new PVector(50, 150), 50, 0, 255);
    pitchSlider = new Slider("Pitch", new PVector(200+SliderXPos, 250), new PVector(50, 150), 50, 0, 100);

    //Buttons
    int ButtonXPos = 50;
    buttons = new Button[8];
    fast = new Button("Schnelle Videos", new PVector (50+ButtonXPos, 50), new PVector (50, 50));
    buttons[0] = fast;
    middle = new Button("Mittlere Videos", new PVector (150+ButtonXPos, 50), new PVector (50, 50));
    buttons[1] = middle;
    slow = new Button("Langsame Videos", new PVector (250+ButtonXPos, 50), new PVector (50, 50));
    buttons[2] = slow;
    reset = new Button("Stop Videos", new PVector (350+ButtonXPos, 50), new PVector (50, 50));
    buttons[3] = reset;
    women = new Button("Frauen", new PVector(50+ButtonXPos, 150), new PVector(50, 50));
    buttons[4] = women;
    wave = new Button("Wellen", new PVector(150+ButtonXPos, 150), new PVector(50, 50));
    buttons[5] = wave;
    custom01 = new Button("Eigene Videos 1", new PVector(250+ButtonXPos, 150), new PVector(50, 50));
    buttons[6] = custom01;
    custom02 = new Button("Eigene Videos 2", new PVector(350+ButtonXPos, 150), new PVector(50, 50));
    buttons[7] = custom02;
  }
  void update() {
    //Slider
    hueSlider.update();
    saturationSlider.update();
    videoAlphaSlider.update();
    generativAlphaSlider.update();
    pitchSlider.update();

    //Buttons
    for (Button b : buttons) {
      buttonUpdate(b);
    }
  }


  void buttonUpdate(Button b) {
    b.drawButton();
    String name = b.name;
    if (mousePressed || midiInput == true) {
      if ((mouseX > b.pos.x && mouseX < (b.pos.x+b.size.x) && mouseY > b.pos.y && mouseY < (b.pos.y + b.size.y)) || midiInput == true) {
        if (b.pressed == false) {
          resetButtons();
            if ((name == "Schnelle Videos" && mousePressed) || controller.midiController.fast == true) {
            fast();
            controller.midiController.fast = false;
            println("fast1");
            }

          else if (name == "Mittlere Videos" && mousePressed || controller.midiController.middle == true) {
            middle();
            controller.midiController.middle = false;
            println("middle");
            
          }

          else if (name == "Langsame Videos" && mousePressed || controller.midiController.slow == true){
            slow();
            controller.midiController.slow = false;
            println("slow");
            
          }

          else if (name == "Stop Videos" && mousePressed || controller.midiController.reset == true) {
            reset();
            controller.midiController.reset = false;
            println("reset");
            
          }

          else if (name == "Wellen" && mousePressed || controller.midiController.wave == true) {
            wave();
            controller.midiController.wave =false;
            println("wave");
          }

          else if (name == "Frauen" && mousePressed || controller.midiController.women == true){
            women();
            controller.midiController.women = false;
            println("women");
            
          }

          else if (name == "Eigene Videos 1" && mousePressed || controller.midiController.custom01 == true){
            custom01();
            controller.midiController.custom01 = false;
            println("custom01");
            
          }

          else if (name == "Eigene Videos 2" && mousePressed || controller.midiController.custom02 == true) {
            custom02();
            controller.midiController.custom02 = false;
            println("custom02");
            
          }
          midiInput = false;

          b.value = -b.value;

          b.active = true;
        }
        b.pressed = true;
      }
    } else {
      b.pressed = false;
    }
  }

  void resetButtons() {

    for (Button bt : buttons) {
      bt.active = false;
    }
  }
}