class Video {
  Movie video;
  boolean end, play;
  int fade, loopTimes;

  Video(Movie video, int loopTimes) {
    this.loopTimes = loopTimes;
    this.video = video;
    end = true;
    play = false;
    fade = 255;
  }
}