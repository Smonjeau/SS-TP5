import java.util.List;
import java.util.Objects;

public class Particle {
    private double mass,radius,x,y,vx,vy,fx,fy;
    private int id;

    private static int counter = 0;
    public Particle(double mass, double radius, double x, double y, double vx, double vy) {
        this.id = counter++;
        this.mass = mass;
        this.radius = radius;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public boolean overlaps(Particle p){
        return Math.sqrt((this.getY()-p.getY()) * (this.getY()-p.getY()) +
                (this.getX() - p.getX()) * (this.getX() - p.getX())) - this.getRadius() - p.getRadius()
                < 0;
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getFn(Particle p,double KN){
        double superposition = this.getRadius() + p.getRadius() - this.distanceFromParticle(p);
        if(superposition<0)
            return 0;

        return -1 * KN * superposition;
    }

    public double getFt(Particle p,double KT){
        double relVelX = this.vx - p.getVx();
        double relVelY = this.vy - p.getVy();

        double velNormalX,velNormalY;


        velNormalX = (p.getX() - this.getX()) /
                (Math.sqrt(Math.pow(p.getX() - this.getX(), 2) + Math.pow(p.getY() - this.getY(), 2)));

        velNormalY = (p.getY() - this.getY()) /
                (Math.sqrt(Math.pow(p.getX() - this.getX(), 2) + Math.pow(p.getY() - this.getY(), 2)));


        double superposition = this.getRadius() + p.getRadius() - this.distanceFromParticle(p);
        if(superposition < 0)
            return 0;

        double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;


        return -1 * KT * superposition * velTang;
    }

    public double distanceFromParticle(Particle p){
        return Math.sqrt(Math.pow(p.x-this.x,2)+Math.pow(p.y-this.y,2));
    }



    /*

    public double getFnWithWall(double wallX, double wallY, double KN){
        double superposition = this.getRadius();
        return -1 * KN * superposition;
    }


    public double getFtWithWall(double wallX, double wallY, double KT){
        double relVelX = this.vx;
        double relVelY = this.vy;
        double velNormalX = (this.vx) /(Math.sqrt(Math.pow(this.vx,2) + Math.pow(this.vy,2)));

        double velNormalY = (this.vy) /(Math.sqrt(Math.pow(this.vx,2) + Math.pow(this.vy,2)));


        double superposition = this.getRadius() - this.distanceFromParticle(new Particle(0,0,
                wallX,wallY,0,0));

        double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;


        return -1 * KT * superposition * velTang;



    }


     */

    private double calculateYForceProjection(List<Particle> particles, double KN,double KT,double L, double W, double D){
        //sumamos la fuerza normal y tangencial proyectada a la componente x

        //primero las particulas
        double fAccumProjectedY = 0;

        for(Particle p : particles){
            if(this.id == p.id)
                continue;

            double normalVersorX = (p.x - this.x)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));

            double normalVersorY = (p.y - this.y)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));
            fAccumProjectedY += getFn(p,KN) * normalVersorY;
            fAccumProjectedY += getFt(p,KT) *  normalVersorX;

        }
        //ahora las paredes

        double relVelX = this.vx;
        double relVelY = this.vy;

        double cos, sen, velTang;




        //TODO ver que hacer en los casos de pegar en el bordecito
        int contador = 0;

        for(Wall w : Wall.values()){
            double superposition;
            double normalVersorX,normalVersorY;
            switch(w){
                case LEFT:
                    //superposition = this.getRadius() - (0-this.x);
                    //cos = (this.vx) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));

                    //sen = (this.vy) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
                    //velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
                    //normalVersorX = (0 - this.x)  /
                      //      (Math.sqrt(Math.pow(0-this.x,2) ));

                    //la pos en y es la misma
                    //normalVersorY = 0;
                    //
                    //fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    //fAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;

                    break;
                case RIGHT:
                    //velNormalX = (this.vx) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));

                    //velNormalY = (this.vy) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
                    //velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
                    //normalVersorX = (W - this.x)  /
                      //      (Math.sqrt(Math.pow(W-this.x,2) ));

                    //la pos en y es la misma
                    //normalVersorY = 0;
                    //superposition = this.getRadius() - (W - this.x);
                    //fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    //ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;
                    break;
                case LOWER_LEFT:
                    superposition = this.getRadius() - this.y;
                    if (superposition < 0 || this.x > (W/2 - D/2))
                        break;
                    contador++;

                    //velNormalX = (this.vx) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));

                    //velNormalY = (this.vy) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
                    //velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
                    //if(this.x <= W/2 - D/2) {
                      //  superposition = this.getRadius() - (0 - this.y);
                        //la pos en x es la misma
                        //normalVersorX = 0;

                        //normalVersorY = (0 - this.y)  /
                          //      (Math.sqrt(Math.pow(0-this.y,2)));
                    //}
                    //else {
                      //  normalVersorX = (W / 2 - D / 2 - this.x)  /
                        //        (Math.sqrt(Math.pow(W / 2 - D / 2-this.x,2) +
                          //              Math.pow(0-this.y,2)));

                        //normalVersorY = (0 - this.y)  /
                          //      (Math.sqrt(Math.pow(W / 2 - D / 2 -this.x,2)
                            //            + Math.pow(0-this.y,2)));

                        //superposition = this.getRadius() - this.distanceFromParticle(new Particle(
                          //      0, 0, W / 2 - D / 2, 0, 0, 0
                        //));
                    //}
                    normalVersorY = (0 - this.y)  /
                                  (Math.sqrt(Math.pow(0-this.y,2)));

                    fAccumProjectedY += -1*KN*superposition*normalVersorY;
                    //ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;

                    break;
                case LOWER_RIGHT:
                    superposition = this.getRadius() - this.y;
                    if (superposition < 0 || this.x < (W/2 + D/2))
                        break;
                    contador++;
//                    velNormalX = (this.vx) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
//
//                    velNormalY = (this.vy) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
//                    velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
//                    if(this.x>= W/2 + D/2) {
//                        superposition = this.getRadius() - (0 - this.y);
//                        //la pos en x es la misma
//                        normalVersorX = 0;
//
//                        normalVersorY = (0 - this.y)  /
//                                (Math.sqrt(Math.pow(0-this.y,2)));
//                    }
//                    else {
//                        superposition = this.getRadius() - this.distanceFromParticle(new Particle(
//                                0, 0, W / 2 + D / 2, 0, 0, 0
//                        ));
//
//                        normalVersorX = (W / 2 + D / 2 - this.x)  /
//                                (Math.sqrt(Math.pow(W / 2 + D / 2-this.x,2) +
//                                        Math.pow(0-this.y,2)));
//
//                        normalVersorY = (0 - this.y)  /
//                                (Math.sqrt(Math.pow(W / 2 + D / 2 -this.x,2)
//                                        + Math.pow(0-this.y,2)));
//                    }
                    normalVersorY = (0 - this.y)  /
                                (Math.sqrt(Math.pow(0-this.y,2)));
                    fAccumProjectedY += -1*KN*superposition*normalVersorY;
                    //ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;
                    break;


            }
        }
        if(contador>1) {
            System.out.println("Se cago en proyeccion Y");
            System.out.println(this.getX() + " " + this.getY());
        }
        return fAccumProjectedY + this.mass * (- 9.80665);
    }


    private double calculateXForceProjection(List<Particle> particles, double KN,double KT,double L, double W, double D){
        //sumamos la fuerza normal y tangencial proyectada a la componente x

        //primero las particulas
        double fAccumProjectedX = 0;
        for(Particle p : particles){
            if(this.id == p.id)
                continue;

            double normalVersorX = (p.x - this.x)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));

            double normalVersorY = (p.y - this.y)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));
            fAccumProjectedX += getFn(p,KN) * normalVersorX ; //Fuerza normal proyectada en X
            fAccumProjectedX += getFt(p,KT) * -1* normalVersorY; //Fuerza tangencial proyectada en X

        }
        //ahora las paredes

        double relVelX = this.vx;
        double relVelY = this.vy;
        double cos, sen;



        double superposition = this.getRadius() - this.x;
        if(superposition >= 0) {
            //LEFT
            fAccumProjectedX += KN*superposition;
           // if(fAccumProjectedX<0)
             //   System.out.println("dio negativo bro");
        } else {
            superposition = this.getRadius() - (W - this.x);
            if(superposition >= 0) {
                //RIGHT
                fAccumProjectedX += -1*KN*superposition;
            }
        }

        //int contador = 0;
        //for(Wall w : Wall.values()){
          //  double superposition;
            //double normalVersorX,normalVersorY;
            //switch(w){
              //  case LEFT:
                //    superposition = this.getRadius() - this.x;
                  //  if(superposition < 0)
                    //    break;
                    //contador++;
                    //Tiempo hasta choque
                    //double yFinal = this.getVy()* (this.getX()/this.vx);
                    //double hip = (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY()-yFinal, 2)));
                    //cos = (this.getX()) / hip;
                    //sen = (this.getY()-yFinal) / hip;

                    //double velTang = -1*sen*relVelX + cos * relVelY;
                    //double velTang = this.getVy();

                    //la pos en y es la misma
                    //normalVersorY = 0;


                    //fAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;


                   // break;
              //  case RIGHT:
                //    superposition = this.getRadius() - (W - this.x);
                //    if(superposition < 0)
                 //       break;
                //    contador++;
                    //cos = (this.getX()) / (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
                    //sen = (this.getY()) / (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
                    //double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
                    //normalVersorX = (W - this.x)  /
                      //      (Math.sqrt(Math.pow(W-this.x,2) ));

                    //la pos en y es la misma
                    //normalVersorY = 0;

                 //   fAccumProjectedX += -1*KN*superposition;
                    //ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;
                 //   break;
              //  case LOWER_LEFT:
                    //if(this.getX() < (W/2 - D/2 + this.getRadius()))

                    //velNormalX = (this.getX()) / (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
                    //velNormalY = (this.getY()) / (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
                    //double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
                    /*if(this.x <= W/2 - D/2) {
                        superposition = this.getRadius() - (0 - this.y);
                        //la pos en x es la misma
                        normalVersorX = 0;

                        normalVersorY = (0 - this.y)  /
                                (Math.sqrt(Math.pow(0-this.y,2)));
                    }
                    else {
                        normalVersorX = (W / 2 - D / 2 - this.x)  /
                                (Math.sqrt(Math.pow(W / 2 - D / 2-this.x,2) +
                                        Math.pow(0-this.y,2)));

                        normalVersorY = (0 - this.y)  /
                                (Math.sqrt(Math.pow(W / 2 - D / 2 -this.x,2)
                                        + Math.pow(0-this.y,2)));

                        superposition = this.getRadius() - this.distanceFromParticle(new Particle(
                                0, 0, W / 2 - D / 2, 0, 0, 0
                        ));
                    }*/
                    //fnAccumProjectedX += -1*KN*superposition * normalVersorX;
                    //ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;

                //    break;
             //   case LOWER_RIGHT:
                    /*velNormalX = (this.getX()) / (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
                    velNormalY = (this.getY()) / (Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2)));
                    double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
                    if(this.x>= W/2 + D/2) {
                        superposition = this.getRadius() - (0 - this.y);
                        //la pos en x es la misma
                        normalVersorX = 0;

                        normalVersorY = (0 - this.y)  /
                                (Math.sqrt(Math.pow(0-this.y,2)));
                    }
                    else {
                        superposition = this.getRadius() - this.distanceFromParticle(new Particle(
                                0, 0, W / 2 + D / 2, 0, 0, 0
                        ));

                        normalVersorX = (W / 2 + D / 2 - this.x)  /
                                (Math.sqrt(Math.pow(W / 2 + D / 2-this.x,2) +
                                        Math.pow(0-this.y,2)));

                        normalVersorY = (0 - this.y)  /
                                (Math.sqrt(Math.pow(W / 2 + D / 2 -this.x,2)
                                        + Math.pow(0-this.y,2)));
                    }
                    fnAccumProjectedX += -1*KN*superposition * normalVersorX;
                    ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;*/
               //     break;


            //}
        //}


        //if(contador>1) {
       //     System.out.println("Se cago en proyeccion X");
       //     System.out.println(this.getX() + " " + this.getY());
       // }


        return fAccumProjectedX;
    }
    public void updateForces(List<Particle> particles, double KN, double KT,double L, double W, double D){

        this.fx = calculateXForceProjection(particles, KN, KT, L, W, D);
        this.fy = calculateYForceProjection(particles, KN, KT, L, W, D);
        if(Double.isNaN(this.fx) || Double.isNaN(this.fy))
            System.out.println("isnan");

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return id == particle.id;
    }


    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }

    public int getId() {
        return id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setId(int id) {
        this.id = id;
    }
}
