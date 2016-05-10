class VideoInput {
  PApplet pa;
  Movie myMovie;

  VideoInput(PApplet sketch) {
    pa = sketch;
  }
  void init(String path) {
    myMovie= new Movie(pa, path);
  }

  void start() {
    myMovie.loop();
  }
  
  PImage frame(int frame){
    PImage image = new PImage(myMovie.width,myMovie.height,RGB);
    image.copy(myMovie,0,0,myMovie.width,myMovie.height,0,0,width,height);
  return image;
  }
}