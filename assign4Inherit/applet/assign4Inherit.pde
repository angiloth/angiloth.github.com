//---------Chris Dean-----------//
//Assignment 4ish
//Particles Systems

//---------INSTRUCTIONS--------//
//press tab to increase visual acuity
//press backspace to improve performance, at loss of visuals
//press any other key to see periln flow movement
//mouse click to change between image states and particle movements

ParticleSystem physics;    // the particle system


//accel states/modes
boolean gravity = true;     //gravity
boolean reset = false;      //move particles back to original, with spring movement
boolean run = false;
boolean moving = false;

float xperl, yperl;
float perlinFactor=.02;
float perlinCount = 0.01;

void setup() {
  size(357, 500); 
  noStroke();
  ellipseMode(CENTER);
  smooth();

  //most of the work is done in particle system, maintaining its particles
  physics = new ParticleSystem(0, 0.05);
}

void draw() {

 
    
  
  background(0);
  noStroke(); //because strokes are awful
  physics.draw();
}



void mousePressed() {
  physics.changeMode();  //change state
  
}


void keyPressed() {
  //increase number of particles//
  if (keyCode == TAB ) {
    physics.increasePixels();
    return;
  }

  //reduce number of particles//
  else if (keyCode == BACKSPACE && physics.pixelIndex != 1) {
    physics.reducePixels();
    return;
  }
  

   else if (key == 'n'|| key == 'N' ){
   if(perlinCount == .01)
     perlinCount = 100000;
    else if(perlinCount==100000)
      perlinCount = .01;
      
      
   }
  //start adding perlin operator
  else 
    physics.applyPerlin();
}


void keyReleased() {
  physics.releaseKey();
  
}

