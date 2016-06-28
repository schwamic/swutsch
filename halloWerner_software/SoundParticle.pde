class SoundParticle {

  PVector pos, vel, newNeighbour1Pos, newNeighbour2Pos, neighbour1Pos, neighbour2Pos;

  float rad, energy, neighbours;
  boolean changeNeighbour, created;
  AvgFrequency partnerFrequency;


  SoundParticle(PVector pos, AvgFrequency partnerFrequency, float rad) {
    changeNeighbour = true;
    this.rad = rad;
    this.pos = pos;
    this.energy = 0;
    vel = new PVector(0, 0);
    this.neighbour1Pos = new PVector(0,0);
    this.neighbour2Pos = new PVector(width,height);
    this.newNeighbour1Pos = new PVector(0,0);
    this.newNeighbour2Pos = new PVector(width,height);
    this.partnerFrequency = partnerFrequency;
    created = true;
  }
}