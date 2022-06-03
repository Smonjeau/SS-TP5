import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Environment {

    private static final double KN = Math.pow(10,5);
    private static final double KT = 2*KN;
    private double deltaT;
    private double L,W,D;
    List<Particle> particles;
    List<Particle> previousParticlesStatus;
    List<Particle> recentlyRespawned;
    public Environment(double l, double w, double d, List<Particle> particles) {
        this.previousParticlesStatus = new ArrayList<>();
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

        recentlyRespawned = new ArrayList<>();


        System.out.println("adssa");
        /*

        double rxAnterior =  movingParticle.getX() + (-1*deltaT)*v0 + (Math.pow((-1*deltaT),2)/(2*M)) *f[0];
        double ryAnterior = movingParticle.getY() + (Math.pow((-1*deltaT),2)/(2*M)) *f[1];
        double vxAnterior = v0 + (-1*deltaT / M) * f[0];
        double vyAnterior =  (-1*deltaT / M) * f[1];

         */

    }

    private double[] newCoords(Particle p) {
        double initialX = p.getX();
        double initialY = L-p.getRadius();
        boolean restart = true;
        while(restart){
            restart = false;
            for(Particle other : recentlyRespawned) {
                if(p.overlaps(other)) {
                    initialY += 2*p.getRadius();
                    break;
                }
            }
        }
        return new double[]{initialX,initialY}; //No hay problema
    }


    public void evolve(){
        List<Particle> nextStep = new ArrayList<>();
        for (int i = 0; i < particles.size() ; i++) {

            double prevAccelX = previousParticlesStatus.get(i).getFx() / previousParticlesStatus
                    .get(i).getMass();
            double prevAccelY = previousParticlesStatus.get(i).getFy() / previousParticlesStatus
                    .get(i).getMass();

            double currAccelX = particles.get(i).getFx() / particles.get(i).getMass();
            double currAccelY = particles.get(i).getFy() / particles.get(i).getMass();

            double nextRx = particles.get(i).getX() + particles.get(i).getVx() * deltaT +
                    ((2.0 / 3.0) * currAccelX - (1.0 / 6.0) * prevAccelX) * Math.pow(deltaT, 2);
            double nextRy = particles.get(i).getY() + particles.get(i).getVy() * deltaT +
                    ((2.0 / 3.0) * currAccelY - (1.0 / 6.0) * prevAccelY) * Math.pow(deltaT, 2);

            if(Double.isNaN(nextRx) || Double.isNaN(nextRy))
                System.out.println("F");

            double nextVx = particles.get(i).getVx() + (3.0 / 2.0) * currAccelX * deltaT - 0.5 * prevAccelX * deltaT;
            double nextVy = particles.get(i).getVy() + (3.0 / 2.0) * currAccelY * deltaT - 0.5 * prevAccelY * deltaT;




            Particle futureParticle = new Particle(particles.get(i).getMass(),
                    particles.get(i).getRadius(), nextRx,nextRy,
                    nextVx,nextVy);

            futureParticle.setId(particles.get(i).getId());

            nextStep.add(futureParticle);

            if(futureParticle.getY() < L*0.9)
                recentlyRespawned.remove(futureParticle);
        }
        for (int i = 0; i < nextStep.size() ; i++) {
            Particle futureParticle = nextStep.get(i);
            futureParticle.updateForces(nextStep,KN,KT,L,W,D);

            double prevAccelX = previousParticlesStatus.get(i).getFx() / previousParticlesStatus
                    .get(i).getMass();
            double prevAccelY = previousParticlesStatus.get(i).getFy() / previousParticlesStatus
                    .get(i).getMass();

            double currAccelX = particles.get(i).getFx() / particles.get(i).getMass();
            double currAccelY = particles.get(i).getFy() / particles.get(i).getMass();

            double nextAccelX = futureParticle.getFx() / nextStep.get(i).getMass();
            double nextAccelY = futureParticle.getFy() / nextStep.get(i).getMass();

            double nextVx = particles.get(i).getVx() +(1.0/3.0)*nextAccelX*deltaT
                    + (5.0/6.0)*currAccelX*deltaT - (1.0/6.0)*prevAccelX*deltaT;

            double nextVy = particles.get(i).getVy() +(1.0/3.0)*nextAccelY*deltaT
                    + (5.0/6.0)*currAccelY*deltaT - (1.0/6.0)*prevAccelY*deltaT;



            futureParticle.setVx(nextVx);
            futureParticle.setVy(nextVy);



        }


        //actualizamos las particulas
        this.previousParticlesStatus = this.particles;
        this.particles = nextStep;

        for (int i = 0; i < particles.size(); i++) {
            Particle p = particles.get(i);
            if(p.getY() <= -L/10) {
                double[] newCoords = this.newCoords(p);
                p.setX(newCoords[0]);
                p.setY(newCoords[1]);
                p.setVy(0);
                p.setVx(0);
                double rxAnterior =  p.getX() +  (Math.pow((-1*deltaT),2)/(2*p.getMass())) *p.getFx();
                double ryAnterior = p.getY() + (Math.pow((-1*deltaT),2)/(2*p.getMass())) *p.getFy();
                double vxAnterior = (-1*deltaT / p.getMass()) * p.getFx();
                double vyAnterior =  (-1*deltaT / p.getMass()) * p.getFy();
                previousParticlesStatus.get(i).setX(rxAnterior);
                previousParticlesStatus.get(i).setY(ryAnterior);
                previousParticlesStatus.get(i).setVx(vxAnterior);
                previousParticlesStatus.get(i).setVy(vyAnterior);

                recentlyRespawned.add(p);
            }
        }



        //System.out.println("actualizadas");


        

    }


    private void calculateParticlesForces(){
        for (Particle p : this.particles){
            p.updateForces(particles,KN,KT,L,W,D);
        }

        System.out.println("termine");
    }



    int count = 0;

    public boolean stopCriteria(){
        return count++ == 7000;
    }



}
