class Slider {

  PVector pos, size;
  int displayValue, sliderSize;
  float minValue, maxValue, value;
  String name;

  Slider(String name, PVector pos, PVector size, int defaultValue, float minValue, float maxValue) {
    this.pos = pos;
    this.size = size;
    this.sliderSize = 10;
    this.displayValue = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.name = name;
  }

  void moveSlider() {
    if (mousePressed) {
      if (mouseX > pos.x && mouseX < (pos.x + size.x) && mouseY > pos.y+sliderSize/2 && mouseY <  pos.y+size.y-sliderSize/2) {
        if ( displayValue < size.y-sliderSize/2 && displayValue > sliderSize/2) {
          displayValue = (int)  mouseY - (int)  pos.y;
        }
      }
    }
  }


  void update() {
    moveSlider();
    strokeWeight(1);
    stroke(255, 100);

    textAlign(CENTER);
    fill(0, 180);
    value = (int) map(displayValue, sliderSize/2, size.y-sliderSize/2, minValue, maxValue+1);
    text(name + ": " + value, pos.x+size.x/2, pos.y - 10);

    noFill();
    rect(pos.x, pos.y, size.x, size.y);

    fill(255, 100);
    rect(pos.x, pos.y+displayValue-sliderSize/2, size.x, sliderSize);
    noFill();
  }
}