class GUI {
  ControlP5 cp5;
  PApplet pa;
  Knob knob1;
  Knob knob2;
  Knob knob3;
  Knob knob4;
  Knob knob5;
  Knob knob6;
  Knob knob7;
  Knob knob8;

  GUI(PApplet sketch) {
    pa = sketch;
  }

  void init() {
    cp5 = new ControlP5(pa);

    knob1 = cp5.addKnob("param01")
      .setRange(0, 255)
      .setValue(param01)
      .setPosition(440, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;

    knob2 = cp5.addKnob("param02")
      .setRange(0, 255)
      .setValue(param02)
      .setPosition(550, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;

    knob3 = cp5.addKnob("param03")
      .setRange(0, 255)
      .setValue(param03)
      .setPosition(660, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;
    knob4 = cp5.addKnob("param04")
      .setRange(0, 255)
      .setValue(param04)
      .setPosition(770, 0)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;   
    knob5 = cp5.addKnob("param05")
      .setRange(0, 255)
      .setValue(param05)
      .setPosition(440, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;
    knob6 = cp5.addKnob("param06")
      .setRange(0, 255)
      .setValue(param06)
      .setPosition(550, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;
    knob7 = cp5.addKnob("param07")
      .setRange(0, 255)
      .setValue(param07)
      .setPosition(660, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;   
    knob8 = cp5.addKnob("param08")
      .setRange(0, 255)
      .setValue(param08)
      .setPosition(770, 110)
      .setRadius(50)
      .setColorForeground(color(255))
      .setColorBackground(color(0, 0, 0))
      .setColorActive(color(255, 0, 0))
      .setDragDirection(Knob.HORIZONTAL)
      ;   

    cp5.addButton("button01")
      .setValue(0)
      .setPosition(0, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button02")
      .setValue(0)
      .setPosition(110, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button03")
      .setValue(0)
      .setPosition(220, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button04")
      .setValue(0)
      .setPosition(330, 0)
      .setSize(100, 100)
      ;
    cp5.addButton("button05")
      .setValue(0)
      .setPosition(0, 110)
      .setSize(100, 100)
      ;
    cp5.addButton("button06")
      .setValue(0)
      .setPosition(110, 110)
      .setSize(100, 100)
      ;
    cp5.addButton("button07")
      .setValue(0)
      .setPosition(220, 110)
      .setSize(100, 100)
      ;
    cp5.addButton("button08")
      .setValue(0)
      .setPosition(330, 110)
      .setSize(100, 100)
      ;
  }
  void render() {//Zeigt die gui am bildschirm an
  }
  public void white(int theValue) {
    println("a button event from white: "+theValue);
  }
  void change(int cha, int pit, int vel, boolean cont) {
  }
}