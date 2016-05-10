class Input {
  PApplet pa;
  Input(PApplet sketch) {
    pa = sketch;
  };
  VideoInput videoInput;
  ImageInput imageInput;
  String[] images = {"image01.jpg","image02.jpg","image03.jpg"};
  void update() {
  }
  void init() {
    //videoInput = new VideoInput(pa);
    imageInput = new ImageInput(pa);
    imageInput.init(images);
  }
  PImage out() { //wird vom output abgerufen
    //return videoInput.frame(4);
    return imageInput.frame();
  }
}