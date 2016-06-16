class VideoInput {

  PApplet pa;
  String videoFolder1, videoFolder2;
  int videoNumber = 0;
  Movie myVideo;
  File f;
  File[] files;

  VideoInput(PApplet pa) {
    this.pa = pa;

    //fill Movie with something to not get nullpointerexception, god this is some dirty ass koting
    videoFolder1 = "Water";
    videoFolder2 = "Slow";
    f = new File(dataPath("/"+videoFolder1+"/"+videoFolder2));
    files = f.listFiles();
    int startAtRandom = (int) random(-1, files.length);
    videoNumber = startAtRandom;
    String fv = files[startAtRandom].getAbsolutePath();
    Movie newVideo = new Movie(pa, fv);
    myVideo = newVideo;
    myVideo.noLoop();
    myVideo.volume(0);
    myVideo.play();
  }

  void loadVideo() {
    controller.playVideo = 1;
    myVideo.stop();
    f = new File(dataPath("/"+videoFolder1+"/"+videoFolder2));
    files = f.listFiles();
    int startAtRandom = (int) random(-1, files.length);
    //int startAtRandom = 0;
    videoNumber = startAtRandom;
    String fv = files[startAtRandom].getAbsolutePath();
    Movie newVideo = new Movie(pa, fv);
    myVideo = newVideo;
    println(fv);
    myVideo.noLoop();
    myVideo.volume(0);
    myVideo.play();
  }

  PImage displayVideo() {
    return myVideo;
  }

  void update() {
    //myVideo.play();
    if (myVideo.available() == true) {
      myVideo.read();
    } 
    try {
      if (myVideo.time() >= myVideo.duration() && videoNumber < files.length-1) {
        videoNumber += 1;
        loadVideo();
      }
      if (myVideo.time() >= myVideo.duration() && videoNumber >= files.length-1) {
        videoNumber = (int) random(-1, files.length);
        loadVideo();
      }
    }
    catch (NullPointerException e) {
      println("No Video Selected");
    }
  }
}