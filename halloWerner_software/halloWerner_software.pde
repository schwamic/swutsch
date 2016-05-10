import processing.video.*;
import processing.serial.*;
import java.awt.Rectangle;


Input input;
Controller controller;
Output output;

void setup() {
  size(800, 600);
  input = new Input(this);
  controller = new Controller(this);
  output = new Output(this);
  input.init();
}

void draw() {
  clear();
  controller.gui.render();
  output.out(input.out());
  input.out();
}