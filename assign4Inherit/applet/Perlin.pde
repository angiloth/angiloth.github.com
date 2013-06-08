//class makes velocity flow along a single generated perlin texture, 
//which slowly (very slowly) changes over time. hitting 'n'
//changes the presets for different movement

class Perlin extends VelocityOp {
  float t=0;
  boolean reset;

  void process(Particle part) {
    if (moving) {
     if(perlinCount < 5 && !reset){
       t=0;
       reset = true;
     }
     if (perlinCount > 5000 && reset){
      reset=false;
      
     }
      
      t+=perlinCount;
     // print(perlinCount);
      part.velocity.x=0;part.velocity.y=0;
      part.position.x += 4* (float) ImprovedNoise.noise((part.velocity.x)*perlinFactor, part.position.y*perlinFactor, t);
      part.position.y += 2*(float) ImprovedNoise.noise((part.position.x)*perlinFactor, part.velocity.y*perlinFactor, t);
    }
  }
}

