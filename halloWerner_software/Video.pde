class Video {
  Movie video;
  boolean end, play;
  int fade, loopTimes;

  Video(Movie video, int loopTimes) {
    this.loopTimes = loopTimes;
    this.video = video;
    end = false;
    play = false;
    fade = 255;
  }
}