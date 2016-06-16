class LEDOutput {
  PApplet pa;
  LEDOutput(PApplet sketch) {
    pa = sketch;
  }
  void init() {
    String[] list = Serial.list();
    portName = Serial.list()[4];
    delay(20);
    println("Serial Ports List:");
    println(list);
    //serialConfigure("/dev/ttyACM0");  // change these to your port names
    //serialConfigure("/dev/ttyACM1");
    if (errorCount > 0) exit();
  }

  void out(PImage image) {
    movieEvent(image);
  }

  String portName;
  int numPorts=0;  // the number of serial ports in use
  int maxPorts=24; // maximum number of serial ports

  Serial[] ledSerial = new Serial[maxPorts];     // each port's actual Serial port
  Rectangle[] ledArea = new Rectangle[maxPorts]; // the area of the movie each port gets, in % (0-100)
  boolean[] ledLayout = new boolean[maxPorts];   // layout of rows, true = even is left->right
  PImage[] ledImage = new PImage[maxPorts];      // image sent to each port
  int errorCount=0;
  float framerate=0;

  void movieEvent(PImage m) {
    // read the movie's next frame
    //m.read(); übernommen von video2serial
    framerate = 30.0; //--> Anpassen für neuen Code

    for (int i=0; i < numPorts; i++) {    
      // copy a portion of the movie's image to the LED image
      int xoffset = percentage(m.width, ledArea[i].x);
      int yoffset = percentage(m.height, ledArea[i].y);
      int xwidth =  percentage(m.width, ledArea[i].width);
      int yheight = percentage(m.height, ledArea[i].height);
      ledImage[i].copy(m, xoffset, yoffset, xwidth, yheight, 
        0, 0, ledImage[i].width, ledImage[i].height);
      // convert the LED image to raw data
      byte[] ledData =  new byte[(ledImage[i].width * ledImage[i].height * 3) + 3];
      image2data(ledImage[i], ledData, ledLayout[i]);
      if (i == 0) { //--> Anpassen für neuen Code
        ledData[0] = '*';  // first Teensy is the frame sync master
        int usec = (int)((1000000.0 / framerate) * 0.75);
        ledData[1] = (byte)(usec);   // request the frame sync pulse
        ledData[2] = (byte)(usec >> 8); // at 75% of the frame time
      } else {
        ledData[0] = '%';  // others sync to the master board
        ledData[1] = 0;
        ledData[2] = 0;
      }
      // send the raw data to the LEDs  :-)
      ledSerial[i].write(ledData);
    }
  }

  // image2data converts an image to OctoWS2811's raw data format.
  // The number of vertical pixels in the image must be a multiple
  // of 8.  The data array must be the proper size for the image.
  void image2data(PImage image, byte[] data, boolean layout) {
    int offset = 3;
    int x, y, xbegin, xend, xinc, mask;
    int linesPerPin = image.height / 8;
    int pixel[] = new int[8];

    for (y = 0; y < linesPerPin; y++) {
      if ((y & 1) == (layout ? 0 : 1)) {
        // even numbered rows are left to right
        xbegin = 0;
        xend = image.width;
        xinc = 1;
      } else {
        // odd numbered rows are right to left
        xbegin = image.width - 1;
        xend = -1;
        xinc = -1;
      }
      for (x = xbegin; x != xend; x += xinc) {
        for (int i=0; i < 8; i++) {
          // fetch 8 pixels from the image, 1 for each pin
          pixel[i] = image.pixels[x + (y + linesPerPin * i) * image.width];
          pixel[i] = colorWiring(pixel[i]);
        }
        // convert 8 pixels to 24 bytes
        for (mask = 0x800000; mask != 0; mask >>= 1) {
          byte b = 0;
          for (int i=0; i < 8; i++) {
            if ((pixel[i] & mask) != 0) b |= (1 << i);
          }
          data[offset++] = b;
        }
      }
    }
  }

  // translate the 24 bit color from RGB to the actual
  // order used by the LED wiring.  GRB is the most common.
  int colorWiring(int c) {
    // return c;  // RGB
    return ((c & 0xFF0000) >> 8) | ((c & 0x00FF00) << 8) | (c & 0x0000FF); // GRB - most common wiring
  }

  // ask a Teensy board for its LED configuration, and set up the info for it.
  void serialConfigure(String portName) {
    if (numPorts >= maxPorts) {
      println("too many serial ports, please increase maxPorts");
      errorCount++;
      return;
    }
    try {
      ledSerial[numPorts] = new Serial(pa, portName, 9600);
      if (ledSerial[numPorts] == null) throw new NullPointerException();
      ledSerial[numPorts].write('?');
    } 
    catch (Throwable e) {
      println("Serial port " + portName + " does not exist or is non-functional");
      errorCount++;
      return;
    }
    delay(50);
    String line = ledSerial[numPorts].readStringUntil(10);
    if (line == null) {
      println("Serial port " + portName + " is not responding.");
      println("Is it really a Teensy 3.0 running VideoDisplay?");
      errorCount++;
      return;
    }
    String param[] = line.split(",");
    if (param.length != 12) {
      println("Error: port " + portName + " did not respond to LED config query");
      errorCount++;
      return;
    }
    // only store the info and increase numPorts if Teensy responds properly
    ledImage[numPorts] = new PImage(Integer.parseInt(param[0]), Integer.parseInt(param[1]), RGB);
    ledArea[numPorts] = new Rectangle(Integer.parseInt(param[5]), Integer.parseInt(param[6]), 
      Integer.parseInt(param[7]), Integer.parseInt(param[8]));
    ledLayout[numPorts] = (Integer.parseInt(param[5]) == 0);
    numPorts++;
  }


  // scale a number by a percentage, from 0 to 100
  int percentage(int num, int percent) {
    double mult = percentageFloat(percent);
    double output = num * mult;
    return (int)output;
  }

  // scale a number by the inverse of a percentage, from 0 to 100
  int percentageInverse(int num, int percent) {
    double div = percentageFloat(percent);
    double output = num / div;
    return (int)output;
  }

  // convert an integer from 0 to 100 to a float percentage
  // from 0.0 to 1.0.  Special cases for 1/3, 1/6, 1/7, etc
  // are handled automatically to fix integer rounding.
  double percentageFloat(int percent) {
    if (percent == 33) return 1.0 / 3.0;
    if (percent == 17) return 1.0 / 6.0;
    if (percent == 14) return 1.0 / 7.0;
    if (percent == 13) return 1.0 / 8.0;
    if (percent == 11) return 1.0 / 9.0;
    if (percent ==  9) return 1.0 / 11.0;
    if (percent ==  8) return 1.0 / 12.0;
    return (double)percent / 100.0;
  }
}