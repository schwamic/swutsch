class Input {
  PApplet pa;
  Input(PApplet sketch) {
    pa = sketch;
  };
  VideoInput videoInput;
  //ImageInput imageInput;
  //String[] images = {"file01.jpg","file02.jpg","file03.jpg","file04.jpg"};
  
  void update() {
    //imageInput.update();
    videoInput.update();
  }
  void init() {
    videoInput = new VideoInput(pa);
    videoInput.init("file.mov");
    //imageInput = new ImageInput(pa);
    //imageInput.init(images);
  }
  PImage out() { //wird vom output abgerufen
    return videoInput.frame();
    //return imageInput.frame();
  }
}