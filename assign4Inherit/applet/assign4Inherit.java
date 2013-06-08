import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class assign4Inherit extends PApplet {

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
float perlinFactor=.02f;
float perlinCount = 0.01f;

public void setup() {
  size(357, 500); 
  noStroke();
  ellipseMode(CENTER);
  smooth();

  //most of the work is done in particle system, maintaining its particles
  physics = new ParticleSystem(0, 0.05f);
}

public void draw() {

 
    
  
  background(0);
  noStroke(); //because strokes are awful
  physics.draw();
}



public void mousePressed() {
  physics.changeMode();  //change state
  
}


public void keyPressed() {
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
   if(perlinCount == .01f)
     perlinCount = 100000;
    else if(perlinCount==100000)
      perlinCount = .01f;
      
      
   }
  //start adding perlin operator
  else 
    physics.applyPerlin();
}


public void keyReleased() {
  physics.releaseKey();
  
}

class AccelerationOp {
  PVector accel; //our main acceleration. classify!
  int t=0; //random counter

  //is this even being used?
  AccelerationOp() {
    accel = new PVector (random(-.3f, .3f), .8f, 0);
  } 

  public void process(Particle part) {
  }
}

class Dampen extends AccelerationOp {


  public void process( Particle part) {
    if(!moving){
    float magni = part.velocity.mag(); 
    part.velocity.normalize();
    part.velocity.mult(magni*part.dampSpeed); //multiply but current amount of friction
  }}
}

class Explosion extends AccelerationOp {  
  float amt = 10;
  public void process( Particle part) {
    //normal gravity
    if (gravity && !moving) {
      if(part.explodeAmount>0)
        part.explodeAmount = part.explodeAmount-.1f;
      accel = new PVector (random(-part.explodeAmount,part.explodeAmount),-3+random(-part.explodeAmount,part.explodeAmount), 0);
      //println (amt);
      part.velocity.add(accel);
    }
  }
}

class Gravity extends AccelerationOp {

  public void process( Particle part) {
    //normal gravity
    if (gravity && !moving) {
      accel = new PVector (random(-2,2),5, 0);
      part.velocity.add(accel);
    }
  }
}

//this class acclerates the particles to their original positions
//forming one of the two images

class OriginalPositions extends AccelerationOp {

  public void process ( Particle part ) {
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

class Particle {
  PVector position;                  //current position
  int rgb1, rgb2;                  //color value, for pics 1 and 2
  PVector originalPosition;          //original position; vector
  PVector velocity;                  //current velocity; maybe move to own class? or stick with pulse method
  float oriX, oriY;                  //original x and y position; 
  float bounceDampen = .7f;           //how much energy to lose on collide
  float dampSpeed = .95f;
  float lerpTimer = 0;  //timing for changing color from one image to next, on scale from 0 to 1
  float explodeAmount = 10;
  int finalColor;

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


  public PVector getPosition() {
    return position;
  }


  public void resetPosition() {
    position.x = originalPosition.x;
    position.y = originalPosition.y;
  }





  public int getColor() {

    if (run) {
      if (lerpTimer <1) {
        finalColor = lerpColor(rgb1, rgb2, lerpTimer);
        lerpTimer+=.015f;
      }
    }
    return finalColor;
  }
}

class ParticleSystem {

  //operations for controlling accel / vel of particles

  Particle newPart;

  //general position variables
  PVector position;
  PVector partPosition;
   float posx, posy;

  ArrayList accelOps = new ArrayList();
  ArrayList velocityOps = new ArrayList();

  //image / pixel math variables
  int pixelIndex =3;          // to speed things up multiply this with 2                      // only each nth pixel will get into the particle system
  int mousecount= 0;                            
  int particleWidth = (int)((pixelIndex-1)*0.7f);  // particle diameter
  int MIN_particleWidth = (int)(pixelIndex*0.3f);
  int MAX_particleWidth = (int)(pixelIndex*1.3f);
  float partFact = 1;
  PImage img, img2, img3;              // 2 images, and crossover img space
  int numPixels;             // the number of pixels in the original image
  int numPixelsSmall;        // the number of pixels in the scaled-down-version of the image
  int widthSmall;            // width of the scaled-down-version of the image
  int heightSmall;           // height of the scaled-down-version of the image
  int[] colors, colors2;// color values from the image
  int tempCol;

  
  Particle[] particles;      // the moving particles

  collider collide = new collider();
    //move this to perlin state in a bit




  //AccelerationOp hithere;

  ParticleSystem(float start, float something) {

    systemInit();
     //velocity and accel ops, instantiate
    Gravity g = new Gravity(); 
    Dampen d = new Dampen();
    OriginalPositions o = new OriginalPositions();
    Velocity v = new Velocity();
    Explosion explode = new Explosion();
      accelOps.add(explode);
     accelOps.add(g);
     accelOps.add(d);
     accelOps.add(o);
     velocityOps.add(v);
     collide.instantiate();
     Perlin p = new Perlin();
     velocityOps.add(p);
     //accelOps.add (e);
    
  }
  //create particle
  public Particle makeParticle(float xpos, float ypos) {
    partPosition = new PVector  ( xpos, ypos);
    newPart = new Particle(partPosition);
    return newPart;
  }



  public void systemInit() {
    img = loadImage("fract1.gif");
    img2 = loadImage("fract2.gif");


    if (img.width > 500 || img.height > 500) img.resize(400, 0);
    numPixels = img.width * img.height;
    widthSmall = img.width/pixelIndex;
    heightSmall = img.height/pixelIndex;
    numPixelsSmall = widthSmall * heightSmall;



    particles = new Particle[numPixelsSmall];
    //orgParticles = new Particle[numPixelsSmall]; 
    colors = new int[numPixelsSmall];
    colors2 = new int [numPixelsSmall];


    img.loadPixels();
    int a;
    for (int x=0; x<widthSmall; x++) {           // go through all rows
      for (int y=0; y<heightSmall; y++) {        // go through all columns
        a = y*widthSmall+x;
       
        particles[a] = makeParticle(x*pixelIndex, y*pixelIndex);
        particles[a].rgb1 = img.pixels[y*pixelIndex*img.width+x*pixelIndex];
        particles[a].rgb2 = img2.pixels[y*pixelIndex*img.width+x*pixelIndex];
       
      }
    }
    
   
  }


  public void draw() {
    int a;
    //float w;
   collide.makeCollider();
    for (int x=0; x<widthSmall; x++) {
      for (int y=0; y<heightSmall; y++) {
        a = y*widthSmall+x;
        posx = particles[a].getPosition().x;
        posy = particles[a].getPosition().y;
        
        process(particles[a]);           //move and accelerate parts
        checkMode(particles[a]);        //set dampening amount
        colorizeParticle(particles[a]); //color part
        render(particles[a]);  
         collide.render(particles[a]);
      }
    }
    fill(0);
  }

  public void render(Particle particle){
     if (!moving) {
          partFact = (float) 1/ particle.velocity.mag();
          partFact = constrain(partFact, 1, 2.4f);
        }
        
        //filter out unnecessary pixels by transparency, to avoid unneccessary processing
        if ( alpha(tempCol) > .1f)
          ellipse(posx, posy, particleWidth*particleWidth*1.6f*partFact, partFact* 1.5f*pow(particleWidth, 3));
    
  }

  public void colorizeParticle(Particle particle){
    if (run)
          tempCol = particle.getColor();
        else 
          tempCol = particle.rgb1;

        fill(tempCol);
    
  }
  public void process(Particle particle) {
    for (int i = 0; i < accelOps.size(); i++) {
      AccelerationOp op = (AccelerationOp)accelOps.get(i);
      op.process( particle );
    }

    for (int i = 0; i < velocityOps.size(); i++) {
      VelocityOp op = (VelocityOp)velocityOps.get(i);
      op.process( particle );
    }
  }

  public void checkMode(Particle particle){
     if (gravity) {
          particle.dampSpeed = .92f;
        }
        else if (reset) {
          particle.dampSpeed =random (.8f, .9f);
        }
    
  }

  public void releaseKey() {
    perlinFactor =  random(2, 5)*.01f;
    moving = false;
  }

  public void increasePixels() {
    pixelIndex--;
    systemInit();
  }




  public void reducePixels() {
    pixelIndex++;
    systemInit();
  }

  public void applyPerlin() {

    moving = true;
   

    
  }

  public void changeMode() {
    int a = 0;

    img.loadPixels();
    for (int x=0; x<widthSmall; x++) {
      for (int y=0; y<heightSmall; y++) {
        a = y*widthSmall+x;
        colors[a] = img.pixels[y*pixelIndex*img.width+x*pixelIndex];
        
        particles[a].explodeAmount=10;
        // particles[a].resetPosition();
      }
    }

    run = true;
    gravity= !gravity;
    reset = ! reset;

    switchImages();
  }



  public void switchImages() {
    int a = 0;
    mousecount++;
    if (mousecount%2 <1) {
      img3 = img2;
      img2 = img;
      img = img3; 
      print(img);
      for (int x=0; x<widthSmall; x++) {           // go through all rows
        for (int y=0; y<heightSmall; y++) {        // go through all columns
          a = y*widthSmall+x;
          particles[a].lerpTimer = 0;
          //print("working");
         
          particles[a].rgb1 = img.pixels[y*pixelIndex*img.width+x*pixelIndex];
          particles[a].rgb2 = img2.pixels[y*pixelIndex*img.width+x*pixelIndex];
        }
      }
    }
  }
}

//class makes velocity flow along a single generated perlin texture, 
//which slowly (very slowly) changes over time. hitting 'n'
//changes the presets for different movement

class Perlin extends VelocityOp {
  float t=0;
  boolean reset;

  public void process(Particle part) {
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

//general part.velocity; bounces off walls with energy loss
//screen.width and .height not working, so hard coded in values

int height =  500;
int width = 357;
float bounceLoss = .33f;

class Velocity extends VelocityOp {


  public void process(Particle part) {

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

class VelocityOp {

  public void process(Particle particle) {
  }
}

class collider{
 
  ArrayList circles;
 collider(){
  circles = new ArrayList();
  
 } 
  
  public void instantiate(){
  for(int i = 0; i < 2; i++){
    circles.add( new PVector ( random(40, 300), random(300, 450)));
 
   //println(circles.get(i));
   
  }
   
 }
  
  public void render(Particle p){
   
    for(int i = 0; i < circles.size(); i++){
      PVector xy = (PVector )circles.get(i);
     // print(circles[0]);
     
      if(PVector.dist(xy, p.getPosition()) < 48){
        p.velocity = new PVector ( p.position.x-xy.x, p.position.y-xy.y);
        p.velocity.normalize();
        p.velocity.mult(8);
      }
      
    }
    
     
  }
  
  public void makeCollider(){
     for(int i = 0; i < circles.size(); i++){
       PVector xy = (PVector )circles.get(i);
    fill(255);
      ellipse(xy.x,xy.y, 80,80); 
     }
  }
  
  
  
  
  
}
  static public void main(String args[]) {
    PApplet.main(new String[] { "--present", "--bgcolor=#666666", "--stop-color=#cccccc", "assign4Inherit" });
  }
}
