/*
 * Communication between SwutschApp and Teensy
 * July 2016
 */
 
#include <OctoWS2811.h>

const int ledsPerStrip = 319;
const int NUM_LEDS = 2552;    // Ledstripe * 8

DMAMEM int displayMemory[ledsPerStrip * 6];
int drawingMemory[ledsPerStrip * 6];
const int config = WS2811_GRB;
OctoWS2811 leds(ledsPerStrip, displayMemory, drawingMemory, config);

void setup() {
  Serial.begin(115200); //USB is always 12 Mbit/sec
  leds.begin();
  leds.show();
  Serial.println("Swutsch start");
}

int getByte() {
  while (!Serial.available()) {}
  return Serial.read();
}

void loop() {
  byte r, g, b;
  int i;
  
  for (i = 0; i < NUM_LEDS; i++) {
    //Sonderzeichen - 255
    //if(((r=getByte())==255){return;}
    //braucht man nicht wegen OctoLib
    r = getByte();
    g = getByte();
    b = getByte();

    //echo to processing
    if(i == NUM_LEDS-1){
        Serial.println("Finished -> Next Frame");
      }
  
    leds.setPixel(i, Color(r, g, b));
  }
  leds.show();
}

// Create a 24 bit color value from R,G,B
unsigned int Color(byte r, byte g, byte b)
{
  //GRB
  return (((unsigned int)b) | ((unsigned int)r << 16) | (unsigned int)g << 8);
}

