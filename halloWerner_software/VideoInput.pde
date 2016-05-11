class VideoInput {
  PApplet pa;
  Movie myMovie;
  int frame = 0;
  VideoInput(PApplet sketch) {
    pa = sketch;
  }
  void init(String path) {
    myMovie= new Movie(pa, path);
    start();
  }

  void start() {
    myMovie.loop();
  }
  void update() {
    image(myMovie, 0, 0, width, height);
  }
  PImage frame() {
    PImage image = new PImage(myMovie.width, myMovie.height, RGB);
    return image;
  }
}