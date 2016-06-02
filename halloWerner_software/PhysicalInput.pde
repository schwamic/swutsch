class PhysicalInput {

  //create Sliders to control Animation
  Slider animationSpeed;
  Slider colorIntensity;

  PApplet pa;

  public PhysicalInput(PApplet pa) {

    this.pa = pa;
  }

  void setup() {
    //create sliders to adjust animation variables
    animationSpeed = new Slider("animationSpeed", new PVector(50, 50), new PVector(50, 150), 75, 0, 10);
    colorIntensity = new Slider("colorIntensity", new PVector(50, 250), new PVector(50, 150), 75, 0, 10);
  }

  void draw() {

    animationSpeed.moveSlider();
    animationSpeed.drawSlider();
    colorIntensity.moveSlider();
    colorIntensity.drawSlider();
  }
}