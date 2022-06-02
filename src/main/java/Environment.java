import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Environment {

    private static final double KN = Math.pow(10,5);
    private static final double KT = 2*KN;
    private double deltaT;
    private double L,W,D;
    List<Particle> particles;
    List<Particle> previousParticlesStatus = new ArrayList<>();
    public Environment(double l, double w, double d, List<Particle> particles) {
        L = l;
        W = w;
        D = d;
        this.particles = particles;
        this.deltaT = 0.1 * Math.sqrt(particles.get(0).getMass() / KN);



        //paso de euler previo a beeman
        calculateParticlesForces();

        for(Particle p:particles){
            double rxAnterior =  p.getX() +  (Math.pow((-1*deltaT),2)/(2*p.getMass())) *p.getFx();
            double ryAnterior = p.getY() + (Math.pow((-1*deltaT),2)/(2*p.getMass())) *p.getFy();
            double vxAnterior = (-1*deltaT / p.getMass()) * p.getFx();
            double vyAnterior =  (-1*deltaT / p.getMass()) * p.getFy();

            previousParticlesStatus.add(new Particle(p.getMass(),p.getRadius(),
                    rxAnterior,ryAnterior,vxAnterior,vyAnterior));
        }


        System.out.println("adssa");
        /*

        double rxAnterior =  movingParticle.getX() + (-1*deltaT)*v0 + (Math.pow((-1*deltaT),2)/(2*M)) *f[0];
        double ryAnterior = movingParticle.getY() + (Math.pow((-1*deltaT),2)/(2*M)) *f[1];
        double vxAnterior = v0 + (-1*deltaT / M) * f[0];
        double vyAnterior =  (-1*deltaT / M) * f[1];

         */

    }


    public void evolve(){

        //beeman para todas las particulas
        List<Particle> temp = new ArrayList<>();
        for (int i = 0; i < particles.size() ; i++) {

            double prevAccelX = previousParticlesStatus.get(i).getFx() / previousParticlesStatus
                    .get(i).getMass();
            double prevAccelY = previousParticlesStatus.get(i).getFy() / previousParticlesStatus
                    .get(i).getMass();

            double currAccelX = particles.get(i).getFx() / particles.get(i).getMass();
            double currAccelY = particles.get(i).getFy() / particles.get(i).getMass();

            double nextRx = particles.get(i).getX() + particles.get(i).getVx() * deltaT +
                    ((2.0/3.0)*currAccelX - (1.0/6.0)*prevAccelX)*Math.pow(deltaT,2);
            double nextRy = particles.get(i).getY() + particles.get(i).getVy() * deltaT +
                    ((2.0/3.0)*currAccelY - (1.0/6.0)*prevAccelY)*Math.pow(deltaT,2);

            double nextVx = particles.get(i).getVx() + (3.0/2.0) * currAccelX * deltaT - 0.5*prevAccelX*deltaT;
            double nextVy = particles.get(i).getVy() + (3.0/2.0) * currAccelY * deltaT - 0.5*prevAccelY*deltaT;


            previousParticlesStatus.get(i).setX(particles.get(i).getX());
            previousParticlesStatus.get(i).setY(particles.get(i).getY());
            previousParticlesStatus.get(i).setVx(particles.get(i).getVx());
            previousParticlesStatus.get(i).setVy(particles.get(i).getVy());


            /*
            particles.get(i).setX(nextRx);
            particles.get(i).setY(nextRy);
            particles.get(i).setVx(nextVx);
            particles.get(i).setVy(nextVy);

             */



            Particle aux = new Particle(particles.get(i).getMass(),
                    particles.get(i).getRadius(), nextRx,nextRy,
                    nextVx,nextVy);

            aux.setId(particles.get(i).getId());



            aux.updateForces(particles,KN,KT,L,W,D);


            double nextAccelX = aux.getFx() / particles.get(i).getMass();
            double nextAccelY = aux.getFy() / particles.get(i).getMass();

            nextVx = particles.get(i).getVx() +(1.0/3.0)*nextAccelX*deltaT
                    + (5.0/6.0)*currAccelX*deltaT - (1.0/6.0)*prevAccelX*deltaT;

            nextVy = particles.get(i).getVy() +(1.0/3.0)*nextAccelY*deltaT
                    + (5.0/6.0)*currAccelY*deltaT - (1.0/6.0)*prevAccelY*deltaT;


            aux.setX(nextRx);
            aux.setY(nextRy);
            aux.setVx(nextVx);
            aux.setVy(nextVy);
            temp.add(aux);
        }

        //actualizamos las particulas
        this.previousParticlesStatus = this.particles;
        this.particles = temp;

        System.out.println("actualizadas");


        

    }


    private void calculateParticlesForces(){
        for (Particle p : this.particles){
            p.updateForces(particles,KN,KT,L,W,D);
        }

        System.out.println("termine");
    }



    int count = 0;

    public boolean stopCriteria(){
        return count++ == 10;
    }



}
