class VideoInput {

  PApplet pa;
  ArrayList<Video> slow = new ArrayList<Video>();
  ArrayList<Video> middle = new ArrayList<Video>();
  ArrayList<Video> fast = new ArrayList<Video>();
  ArrayList<Video> wave = new ArrayList<Video>();
  ArrayList<Video> women = new ArrayList<Video>();
  ArrayList<Video> custom01 = new ArrayList<Video>();
  ArrayList<Video> custom02 = new ArrayList<Video>();


  ArrayList[] videos = new ArrayList[7];

  int currentVideos = 0;

  Video displayedVideo1, displayedVideo2;
  int videoNumber1, videoNumber2, videoPlays;


  //String currentFolder;


  VideoInput(PApplet pa) {
    this.pa = pa;
  }

  void resetVideo(Video v) {
    v.end = true;
    currentVideos = 0;
    videoNumber1 = 0;
    videoNumber2 = 0;
    videoPlays = 0;
  }

  void videoInputSetup() {

    videos[0] = loadVideos("Slow", slow);
    videos[1] = loadVideos("Middle", middle);
    videos[2] = loadVideos("Fast", fast);
    videos[3] = loadVideos("Wave", wave);
    videos[4] = loadVideos("Women", women);
    videos[5] = loadVideos("Custom01", custom01);
    videos[6] = loadVideos("Custom02", custom02);


    displayedVideo1 = new Video(null, 0);
    //ArrayList<Video> m = videos[0];
    //displayedVideo1 = m.get(0);
    displayedVideo2 = new Video(null, 0);
  }

  ArrayList loadVideos(String f1, ArrayList<Video> m) {
    File[] files;
    File f = new File(dataPath(f1));
    files = f.listFiles();
    for (int i = 0; i< files.length; i++) {
      int loopTimes = 0;
      String fv = files[i].getAbsolutePath();
      if (fv.contains("_loop")) {
        String s = fv;
        loopTimes = Integer.parseInt(s.substring(s.lastIndexOf("_loop")+5, s.lastIndexOf("_loop")+7));
      }
      m.add(new Video(new Movie(pa, fv), loopTimes));
    }
    return m;
  }
  void loadVideoOnClick(int n, int i, ArrayList[] b) {

    ArrayList<Video> m = b[n];

    if (displayedVideo1.play == false && displayedVideo2.play == false) {
      currentVideos = n;
      videoNumber1 = i;
      displayedVideo1 = m.get(i);
      displayedVideo1.video.play();
      displayedVideo1.play = true;
      displayedVideo1.end = false;
      videoPlays = 0;
      //displayedVideo1.video.noLoop();
    } else if ( displayedVideo1.play == true && displayedVideo2.play == false) {
      currentVideos = n;
      videoNumber2 = i;
      displayedVideo2 = m.get(i);
      displayedVideo2.video.play();
      displayedVideo2.play = true;
      displayedVideo2.end = false;
      videoPlays = 0;
      //displayedVideo2.video.noLoop();
      displayedVideo1.end = true;
    } else if (displayedVideo2.play == true && displayedVideo1.play == false) {
      currentVideos = n;
      videoNumber1 = i;
      displayedVideo1 = m.get(i);
      displayedVideo1.video.play();
      displayedVideo1.play = true;
      displayedVideo1.end = false;
      //displayedVideo1.video.noLoop();
      videoPlays = 0;
      displayedVideo2.end = true;
    }
  }


  /* void loadVideo(int n, int i, ArrayList[] b, Video v) {
   ArrayList<Video> m = b[n];
   currentVideos = n;
   videoNumber2 = i;
   videoNumber1 = i;
   
   
   displayedVideo1 = m.get(i);
   displayedVideo1.video.jump(0);
   displayedVideo1.video.play();
   displayedVideo1.play = true;
   displayedVideo1.end = false;
   
   
   //v.video.noLoop();
   videoPlays = 0;
   }*/

  void loadVideo(int n, int i, ArrayList[] b, Video v2) {
    ArrayList<Video> m = b[n];
    currentVideos = n;
    videoNumber2 = i;
    videoNumber1 = i;
    Video v = m.get(i);
    if (v2 == displayedVideo1) {
      displayedVideo1 = v;
      displayedVideo2.fade = 255;
      displayedVideo2.end = true;
      displayedVideo2.play = false;
    } else {
      displayedVideo2 = v;
      displayedVideo1.fade = 255;
      displayedVideo1.end = true;
      displayedVideo1.play = false;
    }
    v.video.jump(0);
    v.video.play();
    v.play = true;
    v.end = false;
    v.video.noLoop();
    videoPlays = 0;
  }

  void update() {
    updateVideo(displayedVideo1, videoNumber1);
    updateVideo(displayedVideo2, videoNumber2);
  }

  void updateVideo(Video v, int vN) {
    try {
      //println(vN + "    " + v.video.time() + "    //    " + v.loopTimes + "   " + v.video.duration());
      //v.video.play();
      //println("available " + v.video.available());

      if (v.video.available() == true && v.play == true) {
        v.video.read();
      }
      if (v.end == true && v.fade <= 255) {
        v.fade += 255/pa.frameRate;
        if (v.fade >= 255) {
          v.play = false;
          v.video.stop();
        }
      }

      if (v.play == true && v.fade >= 0 && v.end == false) {
        v.fade -= 255/pa.frameRate;
      }
      //loop video
      if (v.video.time() >= v.video.duration()-0.12 && v.play == true && videoPlays < v.loopTimes-1 && v.video.duration() > 0) {
        v.video.jump(0);
        videoPlays += 1;
      }
      //next video in folder
      else if (v.video.time() >= v.video.duration()-0.12 && videoPlays >= v.loopTimes-1 && vN < videos[currentVideos].size()-1 && v.play == true && v.video.duration() > 0) {
        videoPlays = 0;
        vN += 1;
        v.video.stop();
        loadVideo(currentVideos, vN, videos, v);
      }
      //first video in folder
      else if (v.video.time() >= v.video.duration()-0.12 && videoPlays >= v.loopTimes-1 && vN >= videos[currentVideos].size()-1 && v.play == true && v.video.duration() > 0) {
        videoPlays = 0;
        vN = 0;
        v.video.stop();
        loadVideo(currentVideos, vN, videos, v);
      }
    }
    catch (NullPointerException e) {
    }
  }
}