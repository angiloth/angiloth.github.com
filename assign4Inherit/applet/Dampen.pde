class Dampen extends AccelerationOp {


  void process( Particle part) {
    if(!moving){
    float magni = part.velocity.mag(); 
    part.velocity.normalize();
    part.velocity.mult(magni*part.dampSpeed); //multiply but current amount of friction
  }}
}

