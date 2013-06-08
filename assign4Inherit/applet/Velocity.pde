//general part.velocity; bounces off walls with energy loss
//screen.width and .height not working, so hard coded in values

int height =  500;
int width = 357;
float bounceLoss = .33;

class Velocity extends VelocityOp {


  void process(Particle part) {

    if (part.position.x<0 || part.position.x >width ) {
      part.velocity.x= - part.velocity.x*bounceLoss; //lose energy on bounce
      part.position.x = constrain(part.position.x, 1, width-1);
    }
    if (part.position.y<0 || part.position.y > height) {
      part.velocity.y = -part.velocity.y*bounceLoss; //lose energy on bounce
      part.position.y = constrain(part.position.y, 1, height-1);
    } // dampen
    part.position.add(part.velocity);
  }
}

