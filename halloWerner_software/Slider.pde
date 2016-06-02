class Slider {

  PVector pos, size;
  int value, minValue, maxValue, sliderSize;
  String name;

  Slider(String name, PVector pos, PVector size, int defaultValue, int minValue, int maxValue) {
    this.pos = pos;
    this.size = size;
    this.sliderSize = 10;
    this.value = defaultValue;
    this.minValue = minValue;
    this.maxValue = maxValue;
    this.name = name;
  }

  void moveSlider() {
    if (mousePressed) {
      if (mouseX > pos.x && mouseX < (pos.x + size.x) && mouseY > pos.y+sliderSize/2 && mouseY <  pos.y+size.y-sliderSize/2) {
        if ( value < size.y-sliderSize/2 && value > sliderSize/2) {
          value = (int)  mouseY - (int)  pos.y;
        }
      }
    }
  }


  void drawSlider() {
    strokeWeight(1);
    stroke(255, 100);
    
    textAlign(CENTER);
    fill(0, 180);
    text(name + ": " + sliderValue(), pos.x+size.x/2, pos.y - 10);
    
    noFill();
    rect(pos.x, pos.y, size.x, size.y);

    fill(255, 100);
    rect(pos.x, pos.y+value-sliderSize/2, size.x, sliderSize);
    noFill();
  }

  int sliderValue() {
    return (int) map(value, sliderSize/2, size.y-sliderSize/2, minValue, maxValue+1);
  }
}