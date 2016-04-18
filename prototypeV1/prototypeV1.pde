import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*;

Minim minim;  
AudioPlayer music;
FFT fft;

int rectSideLength = 80;
int gridNum = 3; //3
int spacing = 20; //2

void setup() {
  size(2160, 1440);

  minim = new Minim(this);
  music = minim.loadFile("music.wav", 1024);
  fft = new FFT(music.bufferSize(), music.sampleRate());
  fft.linAverages(30);

  strokeCap(SQUARE);
  strokeJoin(MITER);
}

void draw() {
  music.play();
  
  fft.forward(music.mix);
  background(#000000);
  
  scale(4);
  translate(rectSideLength, rectSideLength);

  int counter = 1;
  noStroke();

  for (int i = 0; i < gridNum; i++) {
    for (int k = 0; k < gridNum; k++) {
      if (fft.getAvg(counter) < 10 - counter) fill(#111111);
      else if (fft.getAvg(counter) < 12 - counter) fill(#444444);
      else fill(#ffffff);

      ellipse(k * (rectSideLength + spacing), i * (rectSideLength + spacing), rectSideLength, rectSideLength);

      counter++;
    }
  }
}