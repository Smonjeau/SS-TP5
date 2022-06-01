import java.util.List;

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

        return -1 * KN * superposition;
    }

    public double getFt(Particle p,double KT){
        double relVelX = this.vx - p.getVx();
        double relVelY = this.vy - p.getVy();

        double velNormalX,velNormalY;

        if(p.getVx() == 0 && this.vx == 0 && p.vy == 0 && this.vy == 0){
            velNormalX  = 0;
            velNormalY = 0;
        }
        else {
            velNormalX = (p.getVx() - this.vx) /
                    (Math.sqrt(Math.pow(p.getVx() - this.vx, 2) + Math.pow(p.getVy() - this.vy, 2)));

            velNormalY = (p.getVy() - this.vy) /
                    (Math.sqrt(Math.pow(p.getVx() - this.vx, 2) + Math.pow(p.getVy() - this.vy, 2)));


        }
        double superposition = this.getRadius() + p.getRadius() - this.distanceFromParticle(p);

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
        double fnAccumProjectedY = 0;
        double ftAccumProjectedY = 0;
        for(Particle p : particles){
            if(this.id == p.id)
                continue;

            double normalVersorX = (p.x - this.x)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));

            double normalVersorY = (p.y - this.y)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));
            fnAccumProjectedY += getFn(p,KN) * normalVersorY;
            ftAccumProjectedY += getFt(p,KT) *  normalVersorX;

        }
        //ahora las paredes

        double relVelX = this.vx;
        double relVelY = this.vy;

        double velNormalX,velNormalY;

        if( this.vx == 0  && this.vy == 0){
            velNormalX  = 0;
            velNormalY = 0;
        }else {

            velNormalX = (this.vx) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));

            velNormalY = (this.vy) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
        }


        double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
        for(Wall w : Wall.values()){
            double superposition;
            double normalVersorX,normalVersorY;
            switch(w){
                case UPPER:
                    //la pos en x es la misma
                    normalVersorX = 0;

                    normalVersorY = (L - this.y)  /
                            (Math.sqrt(Math.pow(L-this.y,2)));
                    superposition = this.getRadius() - (L - this.y);
                    fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;
                    break;
                case LEFT:
                    normalVersorX = (0 - this.x)  /
                            (Math.sqrt(Math.pow(0-this.x,2) ));

                    //la pos en y es la misma
                    normalVersorY = 0;
                    superposition = this.getRadius() - (0-this.x);
                    fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;

                    break;
                case RIGHT:
                    normalVersorX = (W - this.x)  /
                            (Math.sqrt(Math.pow(W-this.x,2) ));

                    //la pos en y es la misma
                    normalVersorY = 0;
                    superposition = this.getRadius() - (W - this.x);
                    fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;
                    break;
                case LOWER_LEFT:
                    if(this.x <= W/2 - D/2) {
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
                    }
                    fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;

                    break;
                case LOWER_RIGHT:
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
                    fnAccumProjectedY += -1*KN*superposition*normalVersorY;
                    ftAccumProjectedY += -1 * KT * superposition * velTang * normalVersorX;
                    break;


            }
        }

        return fnAccumProjectedY + ftAccumProjectedY + this.mass * (- 9.80665);
    }


    private double calculateXForceProjection(List<Particle> particles, double KN,double KT,double L, double W, double D){
        //sumamos la fuerza normal y tangencial proyectada a la componente x

        //primero las particulas
        double fnAccumProjectedX = 0;
        double ftAccumProjectedX = 0;
        for(Particle p : particles){
            if(this.id == p.id)
                continue;

            double normalVersorX = (p.x - this.x)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));

            double normalVersorY = (p.y - this.y)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));
            fnAccumProjectedX += getFn(p,KN) * normalVersorX ;
            ftAccumProjectedX += getFt(p,KT) * -1* normalVersorY;

        }
        //ahora las paredes

        double relVelX = this.vx;
        double relVelY = this.vy;
        double velNormalX,velNormalY;

        if( this.vx == 0  && this.vy == 0){
            velNormalX  = 0;
            velNormalY = 0;
        }else {

            velNormalX = (this.vx) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));

            velNormalY = (this.vy) / (Math.sqrt(Math.pow(this.vx, 2) + Math.pow(this.vy, 2)));
        }
        double velTang = -1*velNormalY*relVelX + velNormalX * relVelY;
        for(Wall w : Wall.values()){
            double superposition;
            double normalVersorX,normalVersorY;
            switch(w){
                case UPPER:
                    //la pos en x es la misma
                    normalVersorX = 0;

                    normalVersorY = (L - this.y)  /
                            (Math.sqrt(Math.pow(L-this.y,2)));
                    superposition = this.getRadius() - (L - this.y);
                    fnAccumProjectedX += -1*KN*superposition*normalVersorX;
                    ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;
                    break;
                case LEFT:
                    normalVersorX = (0 - this.x)  /
                            (Math.sqrt(Math.pow(0-this.x,2) ));

                    //la pos en y es la misma
                    normalVersorY = 0;
                    superposition = this.getRadius() - (0-this.x);
                    fnAccumProjectedX += -1*KN*superposition * normalVersorX;
                    ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;

                    break;
                case RIGHT:
                    normalVersorX = (W - this.x)  /
                            (Math.sqrt(Math.pow(W-this.x,2) ));

                    //la pos en y es la misma
                    normalVersorY = 0;
                    superposition = this.getRadius() - (W - this.x);
                    fnAccumProjectedX += -1*KN*superposition * normalVersorX;
                    ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;
                    break;
                case LOWER_LEFT:
                    if(this.x <= W/2 - D/2) {
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
                    }
                    fnAccumProjectedX += -1*KN*superposition * normalVersorX;
                    ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;

                    break;
                case LOWER_RIGHT:
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
                    ftAccumProjectedX += -1 * KT * superposition * velTang * -1 * normalVersorY;
                    break;


            }
        }

        return fnAccumProjectedX + ftAccumProjectedX;
    }
    public void updateForces(List<Particle> particles, double KN, double KT,double L, double W, double D){

        this.fx = calculateXForceProjection(particles, KN, KT, L, W, D);
        this.fy = calculateYForceProjection(particles, KN, KT, L, W, D);

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
}
