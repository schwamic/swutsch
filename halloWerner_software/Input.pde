class Input {
  PApplet pa;
  Input(PApplet sketch) {
    pa = sketch;
  };
  VideoInput videoInput;
  //String[] images = {"file01.jpg","file02.jpg","file03.jpg","file04.jpg"};

  void update() {
    videoInput.update();
  }
  void init() {
    videoInput = new VideoInput(pa);
    videoInput.init(dataPath("file.mp4")); // use dataPath() to get a save path to the files in "data"
  }
  PImage out() { //wird vom output abgerufen
    return videoInput.frame();
  }
}