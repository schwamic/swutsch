class ImageInput {
  PApplet pa;
  PImage[] images;
  PImage currentImage;
  PImage newImage;
  int newImageInt = 1;
  ImageInput(PApplet sketch) {
    pa = sketch;
  }
  void init(String[] paths) {
    currentImage = loadImage(paths[0]);
    newImage = loadImage(paths[1]);
    images = new PImage[paths.length];
    for (int i =0; i<paths.length; i++) {
      //println(paths[i]);
      images[i] = loadImage(paths[i]);
    }
  }
  void update() {
    int dimension = currentImage.width * currentImage.height;
    currentImage.loadPixels();
    newImage.loadPixels();
  }
  PImage frame() {
    image(images[0], 0, 0);
    return images[0];
  }
}