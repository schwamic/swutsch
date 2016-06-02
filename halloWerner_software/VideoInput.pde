class VideoInput {
  
  PApplet pa;
  
  Movie myMovie;
  int frame = 0;
  float speed = -1.0;
  PImage image;
  
  VideoInput(PApplet pa) {
    this.pa = pa;
  }

  void init(String path) {
    myMovie= new Movie(pa, path);
    myMovie.speed(speed);
    myMovie.loop();
    image = new PImage(myMovie.width, myMovie.height, RGB);
  }
  void update() {
    if (myMovie.available()) {
      myMovie.read();
    }
    //image(myMovie, 0, 0, width, height);
  }
  void changeSpeed(float spd) {
    speed = spd;
  }

  PImage frame() {
    return myMovie;
  }
}