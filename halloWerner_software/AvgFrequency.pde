
class AvgFrequency {

  float currentValue, maxValue, avgValue, xPos, displayedAvgFrequency, scaleAvg, xDisplayLength;
  boolean peaked, drawStoredValues;
  float[] pastAverages;

  AvgFrequency(float xPos, float displayedAvgFrequency, float scaleAvg, float xDisplayLength) {

    this.pastAverages = new float[soundAnalysis.avgSeconds*setFrameRate];
    this.drawStoredValues = false;
    this.maxValue = 0;
    this.xPos = xPos;
    this.displayedAvgFrequency = displayedAvgFrequency;
    this.scaleAvg = scaleAvg;
    this.xDisplayLength = xDisplayLength;
  }

  void update(int i) {
    currentValue = soundAnalysis.fftLog.getAvg(i)*scaleAvg;
    if (currentValue > maxValue) {
      maxValue = currentValue;
      peaked = true;
    }
    
    //decay maxValue over time
    maxValue -= maxValue/500;

    for (int j = pastAverages.length - 2; j >= 0; j--) {
      pastAverages[j+1] = pastAverages[j];
    }
    pastAverages[0] = currentValue;

    for (int j = 0; j < pastAverages.length; j++) {
      avgValue += pastAverages[j];
    }

    avgValue /= pastAverages.length;
  }
}