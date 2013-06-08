class collider{
 
  ArrayList circles;
 collider(){
  circles = new ArrayList();
  
 } 
  
  void instantiate(){
  for(int i = 0; i < 2; i++){
    circles.add( new PVector ( random(40, 300), random(300, 450)));
 
   //println(circles.get(i));
   
  }
   
 }
  
  void render(Particle p){
   
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
  
  void makeCollider(){
     for(int i = 0; i < circles.size(); i++){
       PVector xy = (PVector )circles.get(i);
    fill(255);
      ellipse(xy.x,xy.y, 80,80); 
     }
  }
  
  
  
  
  
}
