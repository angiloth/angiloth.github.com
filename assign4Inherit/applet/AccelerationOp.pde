class AccelerationOp {
  PVector accel; //our main acceleration. classify!
  int t=0; //random counter

  //is this even being used?
  AccelerationOp() {
    accel = new PVector (random(-.3, .3), .8, 0);
  } 

  void process(Particle part) {
  }
}

