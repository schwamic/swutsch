class ImageInput {
  PApplet pa;
PImage[] images;
  ImageInput(PApplet sketch) {
    pa = sketch;
  }
  void init(String[] paths) {
    for(int i =0; i<paths.length;i++){
    images[i] = loadImage(paths[i]);
    }
  }

  void start() {
    
  }
  
  PImage frame(){
  return images[0];
  }
}