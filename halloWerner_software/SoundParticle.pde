class SoundParticle {

  PVector pos, vel, neighbourP1, neighbourP2;

  float rad;
  AvgFrequency partnerFrequency;


  SoundParticle(PVector pos, AvgFrequency partnerFrequency) {
    this.rad = 35;
    this.pos = pos;
    vel = new PVector(0, 0);
    neighbourP1 = new PVector(-50, -50);
    neighbourP2 = new PVector(-51, -51);
    this.partnerFrequency = partnerFrequency;
  }
}