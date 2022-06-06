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








    private double[] calculateParticlesAndWallForces(List<Particle> particles, double KN,double KT,double L, double W, double D){
        //sumamos la fuerza normal y tangencial proyectada a la componente x

        //primero las particulas
        double fAccumProjectedX = 0;
        double fAccumProjectedY = this.mass * (- 9.80665);
        for(Particle p : particles){
            if(this.id == p.id)
                continue;

            double normalVersorX = (p.x - this.x)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));

            double normalVersorY = (p.y - this.y)  /
                    (Math.sqrt(Math.pow(p.x-this.x,2) + Math.pow(p.y-this.y,2)));
            fAccumProjectedX += getFn(p,KN) * normalVersorX ; //Fuerza normal proyectada en X
            fAccumProjectedX += getFt(p,KT) * -1* normalVersorY; //Fuerza tangencial proyectada en X


            fAccumProjectedY += getFn(p,KN) * normalVersorY;
            fAccumProjectedY += getFt(p,KT) *  normalVersorX;

        }
        //ahora las paredes


        double superposition = this.getRadius() - this.x;
        if(superposition > 0 ) {
            //LEFT
            fAccumProjectedX += KN*superposition;
           // if(fAccumProjectedX<0)
             //   System.out.println("dio negativo bro");
        } else {
            superposition = this.getRadius() - (W - this.x);
            if(superposition > 0) {
                //RIGHT
                fAccumProjectedX += -1*KN*superposition;
            }
        }
        double normalVersorY;
        superposition = this.getRadius() - this.y;
        if (superposition > 0 && this.y > 0 && this.x < (W/2 - D/2)) {
            //Lowerleft
            normalVersorY = (0 - this.y)  /
                    (Math.sqrt(Math.pow(0-this.y,2)));

            fAccumProjectedY += -1*KN*superposition*normalVersorY;
        } else {
            //lower right
            superposition = this.getRadius() - this.y;
            if (superposition > 0 && this.y > 0 && this.x > (W/2 + D/2)) {
                normalVersorY = (0 - this.y)  /
                        (Math.sqrt(Math.pow(0-this.y,2)));
                fAccumProjectedY += -1*KN*superposition*normalVersorY;
            }
        }



        return new double[]{fAccumProjectedX,fAccumProjectedY};
    }
    public void updateForces(List<Particle> particles, Particle[] fixedParticles, double KN, double KT,double L, double W, double D){
        this.fx = 0;
        this.fy = 0;

        for(int i = 0; i < fixedParticles.length; i++) {

            double normalVersorX = (fixedParticles[i].x - this.x)  /
                    (Math.sqrt(Math.pow(fixedParticles[i].x-this.x,2)
                            + Math.pow(fixedParticles[i].y-this.y,2)));

            double normalVersorY = (fixedParticles[i].y - this.y)  /
                    (Math.sqrt(Math.pow(fixedParticles[i].x-this.x,2) + Math.pow(fixedParticles[i].y-this.y,2)));
            this.fx += getFn(fixedParticles[i],KN) * normalVersorX ; //Fuerza normal proyectada en X
            this.fx += getFt(fixedParticles[i],KT) * -1* normalVersorY; //Fuerza tangencial proyectada en X

            this.fy += getFn(fixedParticles[i],KN) * normalVersorY;
            this.fy += getFt(fixedParticles[i],KT) *  normalVersorX;
        }




        double[] forces = calculateParticlesAndWallForces(particles, KN, KT, L, W, D);
        this.fx += forces[0];
        this.fy += forces[1];


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

    public void setFx(double fx) {
        this.fx = fx;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setId(int id) {
        this.id = id;
    }
}
