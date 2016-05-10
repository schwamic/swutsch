class ImageInput {
  PApplet pa;
  PImage[] images;
  PImage currentImage;
  PImage newImage;
  int newImageInt = 2;
  int imageCounter = 0;
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
    for (int i = 0; i < dimension; i ++) { 
      currentImage.pixels[i] = lerpColor(currentImage.pixels[i], newImage.pixels[i], 0.1);
     
    } 
    currentImage.updatePixels();
    if (imageCounter >= 100) {
      imageCounter = 0;
      currentImage = newImage;
      newImage = images[newImageInt];
      if (newImageInt >= images.length-1) {
        newImageInt = 0;
      } else {
        newImageInt ++;
      }
    } else {
      imageCounter++;
    }
    println(newImageInt);
  }
  PImage frame() {
    image(currentImage, 0, 0);
    return currentImage;
  }
}