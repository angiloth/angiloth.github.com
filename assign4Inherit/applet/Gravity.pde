class Gravity extends AccelerationOp {

  void process( Particle part) {
    //normal gravity
    if (gravity && !moving) {
      accel = new PVector (random(-2,2),5, 0);
      part.velocity.add(accel);
    }
  }
}

