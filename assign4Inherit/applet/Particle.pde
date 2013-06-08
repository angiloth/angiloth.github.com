class Particle {
  PVector position;                  //current position
  color rgb1, rgb2;                  //color value, for pics 1 and 2
  PVector originalPosition;          //original position; vector
  PVector velocity;                  //current velocity; maybe move to own class? or stick with pulse method
  float oriX, oriY;                  //original x and y position; 
  float bounceDampen = .7;           //how much energy to lose on collide
  float dampSpeed = .95;
  float lerpTimer = 0;  //timing for changing color from one image to next, on scale from 0 to 1
  float explodeAmount = 10;
  color finalColor;

  PVector posCorrection; //adjust starting location with this

  Particle(PVector pos) {

    //make global? so many already...
    posCorrection = new  PVector ( 20, 40, 0);
    pos.add(posCorrection);
    position=pos; 
    oriX = pos.x;
    oriY = pos.y;
    originalPosition = pos;
    velocity = new PVector (0, 10);
  }


  PVector getPosition() {
    return position;
  }


  void resetPosition() {
    position.x = originalPosition.x;
    position.y = originalPosition.y;
  }





  color getColor() {

    if (run) {
      if (lerpTimer <1) {
        finalColor = lerpColor(rgb1, rgb2, lerpTimer);
        lerpTimer+=.015;
      }
    }
    return finalColor;
  }
}

