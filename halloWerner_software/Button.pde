<<<<<<< HEAD
class Button {


  PVector pos, size;
  boolean pressed = false;
  boolean active = false;
  int value;
  String name;
  Button(String name, PVector pos, PVector size) {
    this.pos = pos;
    this.size = size;
    this.name = name;
  }

  void drawButton() {
    strokeWeight(1);
    stroke(255, 100);
    textAlign(CENTER);
    fill(0, 180);
    text(name, pos.x + size.x/2, pos.y -10);

    if (active == false) {
      fill(120);
    } else if ( active == true) {
      fill(180,0,0);
    }
    rect(pos.x, pos.y, size.x, size.y);
  }
=======
class Button {


  PVector pos, size;
  boolean pressed = false;
  boolean active = false;
  int value;
  String name;
  Button(String name, PVector pos, PVector size) {
    this.pos = pos;
    this.size = size;
    this.name = name;
  }

  void drawButton() {
    strokeWeight(1);
    stroke(255, 100);
    textAlign(CENTER);
    fill(0, 180);
    text(name, pos.x + size.x/2, pos.y -10);

    if (active == false) {
      fill(120);
    } else if ( active == true) {
      fill(180,0,0);
    }
    rect(pos.x, pos.y, size.x, size.y);
  }
>>>>>>> 0067efa7885791ce7cf0e4b66c2c6f2c0a237850
}