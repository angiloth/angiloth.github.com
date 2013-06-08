class Explosion extends AccelerationOp {  
  float amt = 10;
  void process( Particle part) {
    //normal gravity
    if (gravity && !moving) {
      if(part.explodeAmount>0)
        part.explodeAmount = part.explodeAmount-.1;
      accel = new PVector (random(-part.explodeAmount,part.explodeAmount),-3+random(-part.explodeAmount,part.explodeAmount), 0);
      //println (amt);
      part.velocity.add(accel);
    }
  }
}

