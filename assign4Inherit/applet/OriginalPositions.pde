//this class acclerates the particles to their original positions
//forming one of the two images

class OriginalPositions extends AccelerationOp {

  void process ( Particle part ) {
    if (!moving) {
      PVector orig = (PVector) part.originalPosition;
      PVector newPos = part.position;
      accel = new PVector ( part.oriX-newPos.x, part.oriY-newPos.y, 0);
      if (accel.mag() > 1)
        accel.normalize();
      part.velocity.add(accel);
    }
  }
}

