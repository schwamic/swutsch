class Generative {
  PApplet pa;

  //create globals for sound visualization / particle behaviour
  //SoundParticle[] particles;
  ArrayList<SoundParticle> particles = new ArrayList<SoundParticle>();
  int particleNbr;


  public Generative(PApplet pa, int particleNbr) {
    this.pa = pa;
    this.particleNbr = particleNbr;
  }

  void setupParticles() {
    //setup variables for particle simulation
    for (int i = 0; i < particleNbr; i++) {
      PVector randomizePosition = new PVector(random(pa.width), random(pa.height));
      AvgFrequency randomPartnerFrequency = soundAnalysis.avgFrequencys[(int) random(0, soundAnalysis.fftLog.avgSize()-10)];
      //particles.get(i) = new SoundParticle(randomizePosition, randomPartnerFrequency, maxRadius);
      particles.add(i, new SoundParticle(randomizePosition, randomPartnerFrequency, 0));
    }
  }

  void updateParticles() {
    //iterate thorugh all particles
    for (int i = 0; i < particles.size(); i++) {
      //decay particle neighbours
      particles.get(i).neighbours = 0;

      //decay energy over time
      if (particles.get(i).energy > 0) {
        particles.get(i).energy -= (particles.get(i).energy/3.5);
      }

      //increase energy when partnerFrequency is peaking
      if (particles.get(i).partnerFrequency.peaked == true && particles.get(i).partnerFrequency.avgValue > soundAnalysis.overallAverage) {
        //particles.get(i).energy += particles.get(i).energy+2;
        particles.get(i).energy += soundAnalysis.overallAverage*2;
      }


      //add to radius for particles by sound overallAverage and a currentValue from the partnerFrequency
      particles.get(i).rad += (soundAnalysis.overallAverage + particles.get(i).partnerFrequency.avgValue + particles.get(i).partnerFrequency.currentValue) / (particles.size()*0.1);

      //decay radius
      particles.get(i).rad -= particles.get(i).rad/5;

      particles.get(i).energy += particles.get(i).partnerFrequency.maxValue/50;


      for (int j = 0; j < particles.size(); j++) {
        if (i != j) {
          //check for collission with any other particle
          float distance = dist(particles.get(i).pos.x, particles.get(i).pos.y, particles.get(j).pos.x, particles.get(j).pos.y);
          if (distance < particles.get(i).rad+ particles.get(j).rad) {
            particles.get(i).vel.x += (particles.get(i).pos.x - particles.get(j).pos.x) / distance;
            particles.get(i).vel.y += (particles.get(i).pos.y - particles.get(j).pos.y) / distance;
            float xDist = particles.get(i).vel.x - particles.get(j).vel.x;
            float yDist = particles.get(i).vel.y - particles.get(j).vel.y;
            particles.get(i).vel.x += xDist / distance / 2000;
            particles.get(i).vel.y += yDist / distance / 2000;
          }

          //save the closest two neighbour points to later draw a triangle from them if the frequency its partnered with is higher then its maximum
          float neighbourP1distance = dist(particles.get(i).pos.x, particles.get(i).pos.y, particles.get(i).neighbour1Pos.x, particles.get(i).neighbour1Pos.y);
          float neighbourP2distance = dist(particles.get(i).pos.x, particles.get(i).pos.y, particles.get(i).neighbour2Pos.x, particles.get(i).neighbour2Pos.y);

          //if ( particles.get(i).partnerFrequency.currentValue > particles.get(i).partnerFrequency.maxValue) {
          if ((particles.get(i).partnerFrequency.peaked == true && particles.get(i).partnerFrequency.currentValue > soundAnalysis.overallAverage*2) || neighbourP1distance+neighbourP2distance > 50 || particles.get(i).created == true) {
            // if (neighbourP1distance+neighbourP2distance > controller.triangleSize || particles.get(i).created == true) {
            if (distance < neighbourP1distance) {

              particles.get(i).newNeighbour1Pos = particles.get(j).pos;
              particles.get(i).changeNeighbour = true;
            }
            //distance
            if (distance < neighbourP2distance && neighbourP1distance < distance) {
              particles.get(i).newNeighbour2Pos = particles.get(j).pos;
              particles.get(i).changeNeighbour = true;
            }
          }
          if (distance < (particles.get(i).partnerFrequency.avgValue/((pa.width+pa.height+particleNbr)*0.002))) {
            particles.get(i).neighbours += 1;
          }
        }
      }

      //ease out of current neighbour when finding new one
      if (particles.get(i).changeNeighbour == true) {
        particles.get(i).energy -= particles.get(i).energy/1.5 + 10;
        if (particles.get(i).energy <= 10) {
          particles.get(i).neighbour1Pos = particles.get(i).newNeighbour1Pos;
          particles.get(i).neighbour2Pos = particles.get(i).newNeighbour2Pos;
          particles.get(i).changeNeighbour = false;
          particles.get(i).created = false;
        }
      }


      //check for "wall" collission and push away from walls
      if (particles.get(i).pos.x < - 25 + particles.get(i).rad/4) {
        particles.get(i).vel.x += soundAnalysis.overallAverage/4;
      }
      if (particles.get(i).pos.x > pa.width + 25 - particles.get(i).rad/4) {
        particles.get(i).vel.x -= soundAnalysis.overallAverage/4;
      }
      if (particles.get(i).pos.y < - 25 + particles.get(i).rad/4 ) {
        particles.get(i).vel.y += soundAnalysis.overallAverage/4;
      }
      if (particles.get(i).pos.y > pa.height + 25 - particles.get(i).rad/4) {
        particles.get(i).vel.y -= soundAnalysis.overallAverage/4;
      }

      //add velocity to positon
      particles.get(i).vel.x *= 0.5;
      particles.get(i).vel.y *= 0.5;
      particles.get(i).pos.x += particles.get(i).vel.x;
      particles.get(i).pos.y += particles.get(i).vel.y;
    }
  }


  void drawTriangles(PGraphics pg) {
    pg.fill(0, 0, 50, 30);
    pg.beginShape(TRIANGLES);
    for (SoundParticle p : particles) {
      //DODAT another fill formula
      pg.fill(p.energy*controller.colorIntensity, 255, 255, p.energy*controller.colorIntensity);

      pg.strokeWeight(1);
      //DODAT another stroke formula
      pg.stroke(p.energy*controller.colorIntensity, 255, 255, p.energy*controller.colorIntensity);

      //pg.noStroke();
      pg.vertex(p.pos.x, p.pos.y);
      pg.vertex(p.neighbour1Pos.x, p.neighbour1Pos.y);
      pg.vertex(p.neighbour2Pos.x, p.neighbour2Pos.y);
    }
    pg.endShape();
  }


  void drawParticles(PGraphics pg) {
    pg.noStroke();
    //pg.fill(255,0,0, 255);
    for (SoundParticle p : particles) {
      pg.fill(255, 0, 0, p.energy*4);
      pg.ellipse(p.pos.x, p.pos.y, p.neighbours*5, p.neighbours*5);
    }
  }


  void addParticle() {
    if (particles.size() < particleNbr + controller.particleAmount) {
      for (int i = 0; i < particleNbr+controller.particleAmount - particles.size(); i++) {
        PVector randomizePosition = new PVector(random(pa.width), random(pa.height));
        AvgFrequency randomPartnerFrequency = soundAnalysis.avgFrequencys[(int) random(0, soundAnalysis.fftLog.avgSize()-10)];
        particles.add(particles.size(), new SoundParticle(randomizePosition, randomPartnerFrequency, 0));
      }
    }
  }

  //no idea why this works, should throw a nullpointer exception but w/e
  void deleteParticle() {
    if (particles.size() > particleNbr + controller.particleAmount) {
      for (int i = 0; i > particleNbr + controller.particleAmount - particles.size(); i--) {
        particles.remove(particles.get(particles.size()-1));
      }
    }
  }
}