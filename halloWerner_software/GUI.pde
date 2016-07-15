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
    b.drawSlider();
    String name = b.name;
    if (mousePressed) {
      if (mouseX > b.pos.x && mouseX < (b.pos.x+b.size.x) && mouseY > b.pos.y && mouseY < (b.pos.y + b.size.y)) {
        if (b.pressed == false) {
          for (Button bt : buttons) {
            bt.active = false;
          }
          if (name == "Schnelle Videos")
            fast();

          else if (name == "Mittlere Videos")
            middle();

          else if (name == "Langsame Videos")
            slow();

          else if (name == "Stop Videos") 
            reset();

          else if (name == "Wellen") 
            wave();

          else if (name == "Frauen")
            women();

          else if (name == "Eigene Videos 1")
            custom01();

          else if (name == "Eigene Videos 2") 
            custom02();

          b.value = -b.value;

          b.active = true;
        }
        b.pressed = true;
      }
    } else {
      b.pressed = false;
    }
  }
}