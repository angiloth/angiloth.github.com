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
  int particleWidth = (int)((pixelIndex-1)*0.7);  // particle diameter
  int MIN_particleWidth = (int)(pixelIndex*0.3);
  int MAX_particleWidth = (int)(pixelIndex*1.3);
  float partFact = 1;
  PImage img, img2, img3;              // 2 images, and crossover img space
  int numPixels;             // the number of pixels in the original image
  int numPixelsSmall;        // the number of pixels in the scaled-down-version of the image
  int widthSmall;            // width of the scaled-down-version of the image
  int heightSmall;           // height of the scaled-down-version of the image
  color[] colors, colors2;// color values from the image
  color tempCol;

  
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
  Particle makeParticle(float xpos, float ypos) {
    partPosition = new PVector  ( xpos, ypos);
    newPart = new Particle(partPosition);
    return newPart;
  }



  void systemInit() {
    img = loadImage("fract1.gif");
    img2 = loadImage("fract2.gif");


    if (img.width > 500 || img.height > 500) img.resize(400, 0);
    numPixels = img.width * img.height;
    widthSmall = img.width/pixelIndex;
    heightSmall = img.height/pixelIndex;
    numPixelsSmall = widthSmall * heightSmall;



    particles = new Particle[numPixelsSmall];
    //orgParticles = new Particle[numPixelsSmall]; 
    colors = new color[numPixelsSmall];
    colors2 = new color [numPixelsSmall];


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


  void draw() {
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

  void render(Particle particle){
     if (!moving) {
          partFact = (float) 1/ particle.velocity.mag();
          partFact = constrain(partFact, 1, 2.4);
        }
        
        //filter out unnecessary pixels by transparency, to avoid unneccessary processing
        if ( alpha(tempCol) > .1)
          ellipse(posx, posy, particleWidth*particleWidth*1.6*partFact, partFact* 1.5*pow(particleWidth, 3));
    
  }

  void colorizeParticle(Particle particle){
    if (run)
          tempCol = particle.getColor();
        else 
          tempCol = particle.rgb1;

        fill(tempCol);
    
  }
  void process(Particle particle) {
    for (int i = 0; i < accelOps.size(); i++) {
      AccelerationOp op = (AccelerationOp)accelOps.get(i);
      op.process( particle );
    }

    for (int i = 0; i < velocityOps.size(); i++) {
      VelocityOp op = (VelocityOp)velocityOps.get(i);
      op.process( particle );
    }
  }

  void checkMode(Particle particle){
     if (gravity) {
          particle.dampSpeed = .92;
        }
        else if (reset) {
          particle.dampSpeed =random (.8, .9);
        }
    
  }

  void releaseKey() {
    perlinFactor =  random(2, 5)*.01;
    moving = false;
  }

  void increasePixels() {
    pixelIndex--;
    systemInit();
  }




  void reducePixels() {
    pixelIndex++;
    systemInit();
  }

  void applyPerlin() {

    moving = true;
   

    
  }

  void changeMode() {
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



  void switchImages() {
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

