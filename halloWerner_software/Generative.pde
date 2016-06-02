class Generative {
  PApplet pa;

  //create globals for sound visualization / particle behaviour
  SoundParticle[] particles;


  public Generative(PApplet pa) {
    this.pa = pa;
  }

  void setupParticles(int particleNbr) {
    //setup variables for particle simulation
    particles = new SoundParticle[particleNbr];
    for (int i = 0; i < particles.length; i++) {
      PVector randomizePosition = new PVector(random(pa.width), random(pa.height));
      AvgFrequency randomPartnerFrequency = soundAnalysis.avgFrequencys[(int) random(0, soundAnalysis.fftLog.avgSize()-10)];
      particles[i] = new SoundParticle(randomizePosition, randomPartnerFrequency);
    }
  }

  void drawTriangles(PGraphics pg) {
    pg.fill(0, 0, 50, 30);
    pg.beginShape(TRIANGLES);
    for (SoundParticle p : particles) {
      //fill(255, 0, 0, map(((overallAverage/2+p.partnerFrequency.maxValue*(overallAverage/25))), 0, 800, 0, 180));
      pg.fill(map(((soundAnalysis.overallAverage/2+p.partnerFrequency.maxValue*(soundAnalysis.overallAverage/25))), 0, 500, 0, 255), 0, 0, map(((soundAnalysis.overallAverage/2+p.partnerFrequency.maxValue*(soundAnalysis.overallAverage/25)))*physicalInput.colorIntensity.sliderValue(), 0, 500, 0, 180));
      pg.strokeWeight(1);
      pg.stroke(255, 0, 0, map(((soundAnalysis.overallAverage/2+p.partnerFrequency.maxValue*(soundAnalysis.overallAverage/25))), 0, 800, 0, 230));
      pg.vertex(p.pos.x, p.pos.y);
      pg.vertex(p.neighbourP1.x, p.neighbourP1.y);
      pg.vertex(p.neighbourP2.x, p.neighbourP2.y);
    }
    pg.endShape();
  }

  void updateParticles() {
    //iterate thorugh all particles
    for (int i = 0; i < particles.length; i++) {

      //add to radius for particles by sound overallAverage and a currentValue from the partnerFrequency
      particles[i].rad += (soundAnalysis.overallAverage/(particles.length/45) + particles[i].partnerFrequency.avgValue/(particles.length/32) + particles[i].partnerFrequency.currentValue/(particles.length/35));
      particles[i].rad -= particles[i].rad/5;


      for (int j = 0; j < particles.length; j++) {
        if (i != j) {
          //check for collission with any other particle
          float distance = dist(particles[i].pos.x, particles[i].pos.y, particles[j].pos.x, particles[j].pos.y);
          if (distance < particles[i].rad+ particles[j].rad) {
            particles[i].vel.x += (particles[i].pos.x - particles[j].pos.x) / distance;
            particles[i].vel.y += (particles[i].pos.y - particles[j].pos.y) / distance;
            float xDist = particles[i].vel.x - particles[j].vel.x;
            float yDist = particles[i].vel.y - particles[j].vel.y;
            particles[i].vel.x += xDist / distance / 5;
            particles[i].vel.y += yDist / distance / 5;
          }

          //save the closest two neighbour points to later draw a triangle from them if the frequency its partnered with is higher then its maximum
          float neighbourP1distance = dist(particles[i].pos.x, particles[i].pos.y, particles[i].neighbourP1.x, particles[i].neighbourP1.y);
          float neighbourP2distance = dist(particles[i].pos.x, particles[i].pos.y, particles[i].neighbourP2.x, particles[i].neighbourP2.y);
          //if ( particles[i].partnerFrequency.currentValue > particles[i].partnerFrequency.maxValue) {
          if ((particles[i].partnerFrequency.peaked == true && particles[i].partnerFrequency.currentValue > soundAnalysis.overallAverage*2) || pa.frameCount < 50 || neighbourP1distance+neighbourP2distance > 250) {
            if (distance < neighbourP1distance) {
              particles[i].neighbourP1 = particles[j].pos;
            }
            if (distance < neighbourP2distance && neighbourP1distance < distance) {
              particles[i].neighbourP2 = particles[j].pos;
            }
          }
        }
      }

      //check for "wall" collission and push away from walls
      if (particles[i].pos.x < particles[i].rad/2) {
        particles[i].vel.x += (7+soundAnalysis.overallAverage/2);
      }
      if (particles[i].pos.x > pa.width - particles[i].rad/2) {
        particles[i].vel.x -= (7+soundAnalysis.overallAverage/2);
      }
      if (particles[i].pos.y < particles[i].rad/2) {
        particles[i].vel.y += (7+soundAnalysis.overallAverage/2);
      }
      if (particles[i].pos.y > pa.height - particles[i].rad/2) {
        particles[i].vel.y -= (7+soundAnalysis.overallAverage/2);
      }

      //add velocity to positon
      particles[i].vel.x *= 0.8;
      particles[i].vel.y *= 0.8;
      particles[i].pos.x += particles[i].vel.x/5;
      particles[i].pos.y += particles[i].vel.y/5;
    }
  }

  void drawParticles(PGraphics pg) {
    pg.noStroke();
    pg.fill(255, 60);
    for (SoundParticle p : particles) {
      pg.ellipse(p.pos.x, p.pos.y, 2, 2);
    }
  }
}