class SoundAnalysis {
  PApplet pa;

  //create object array for frequencys
  AvgFrequency[] avgFrequencys;

  Minim minim;  
  AudioInput mic;
  FFT fftLog;
  AudioPlayer song;

  //sets the time in which the past values of a frequency are stored, after avgSeconds the old values are getting replaced by new ones to determin the avg of the frequency
  int avgSeconds = 15;

  float[] pastAverages;
  float overallAverage;

  public SoundAnalysis(PApplet pa) {
    this.pa = pa;
  }

  void getMicInput() {
    minim = new Minim(pa);
    mic = minim.getLineIn(minim.STEREO, 1024);
    fftLog = new FFT( mic.bufferSize(), mic.sampleRate() );
  }

  void getSongInput() {
    minim = new Minim(pa);
    song = minim.loadFile("Kazabubu.mp3", 1024);
    fftLog = new FFT( song.bufferSize(), song.sampleRate() );
  }

  void setup() {

    float scaleAvg = 3;

    pastAverages = new float[avgSeconds*setFrameRate];

    getMicInput(); 
    //getSongInput();

    fftLog.logAverages(100, 10);

    avgFrequencys = new AvgFrequency[fftLog.avgSize()];
    float avgFrequencyXDisplayLength = pa.width/2/fftLog.avgSize();
    for (int i = 0; i < avgFrequencys.length; i++) {

      avgFrequencys[i] = new AvgFrequency(pa.width/2-avgFrequencys.length/2*avgFrequencyXDisplayLength+i*avgFrequencyXDisplayLength, fftLog.getAverageCenterFrequency(i), scaleAvg, avgFrequencyXDisplayLength);
    }

    pastAverages = new float[avgSeconds*setFrameRate];
  }

  void update() {
    float average = 0;
    //keeps the fft updated with a mix of both left and right mic input
    fftLog.forward( mic.mix );
    //fftLog.forward( song.mix );

    //song.play();
    //iterate thorugh all the avgFrequencys
    for (int i = 0; i < avgFrequencys.length; i++) {
      //reset the peaked boolean to detect beats peaking
      avgFrequencys[i].peaked = false;

      //update the frequency values
      avgFrequencys[i].update(i);

      //scale the values by the average of all frequencys
      //this allows the code to work on quiet as well as loud sound
      //--- don't really know how to implement this yet ---

      //draw frequencys for visualization
      drawFrequencys(i);

      //store currentValues to determine the average of all frequencys at the current frame
      average += avgFrequencys[i].currentValue;
    }

    determineAverage(average);
  }

  void determineAverage(float average) {
    average /= avgFrequencys.length;

    //push the pastAverages one place forward
    for (int i = pastAverages.length - 2; i >= 0; i--) {
      pastAverages[i+1] = pastAverages[i];
    }

    //fill the array at position 0 with the last known average
    pastAverages[0] = average;

    //get the average of all averages over the last 5 seconds
    for (int i = 0; i < pastAverages.length; i++) {
      overallAverage += pastAverages[i];
    }
    overallAverage /= pastAverages.length;

    //draw overall average value
    fill(160, 0, 0, 80);
    noStroke();
    rect(avgFrequencys[avgFrequencys.length-1].xPos+avgFrequencys[0].xDisplayLength, pa.height/1.1, avgFrequencys[0].xDisplayLength, -overallAverage);
  }

  void drawFrequencys(int i) {
    rectMode(CORNER);  
    //draw current value
    noStroke();
    fill(120);
    if ( avgFrequencys[i].peaked == true) {
      fill(0);
    }
    rect(avgFrequencys[i].xPos, pa.height/1.1, avgFrequencys[i].xDisplayLength, -avgFrequencys[i].currentValue);

    //draw maxValues
    stroke(150);
    line(avgFrequencys[i].xPos, pa.height/1.1-avgFrequencys[i].maxValue, avgFrequencys[i].xPos+avgFrequencys[i].xDisplayLength, pa.height/1.1-avgFrequencys[i].maxValue);

    //draw average values
    fill(160, 0, 0, 80);
    noStroke();
    rect(avgFrequencys[i].xPos, pa.height/1.1, avgFrequencys[i].xDisplayLength, -avgFrequencys[i].avgValue);
  }
}