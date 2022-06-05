import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Environment {

    private static final double KN = Math.pow(10,5);
    private static double KT = 2*KN;
    private double deltaT;
    private double L,W,D;
    List<Particle> particles;
    List<Particle> previousParticlesStatus;
    List<Particle> recentlyRespawned;
    Particle particleLeft, particleRight;

    List<Integer> flowrate;
    private int respawnCount=0;
    public Environment(double l, double w, double d, List<Particle> particles) {
        this.previousParticlesStatus = new ArrayList<>();
        L = l;
        W = w;
        D = d;
        this.particles = particles;
        this.deltaT = 0.1 * Math.sqrt(particles.get(0).getMass() / KN) * 0.3;
        System.out.println(deltaT);

        this.particleLeft = new Particle(0, 0, W/2 - D/2, 0, 0, 0);
        this.particleRight = new Particle(0, 0, W/2 + D/2, 0, 0, 0);



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
        flowrate=new ArrayList<>();




    }

    private Particle newCoords(Particle p) {
        boolean foundPlace = false, overlap;
        Random random = new Random(System.currentTimeMillis());
        Particle auxParticle = new Particle(p.getMass(),p.getRadius(),0,0,0,0);
        auxParticle.setId(p.getId());
        while(!foundPlace) {
            overlap=false;
            auxParticle.setX(p.getRadius() + (W - 2 * p.getRadius()) * random.nextDouble());
            auxParticle.setY(L*0.8 + p.getRadius() + (L*0.45) * random.nextDouble());
            for(Particle other : particles) {
                if(other.getId() != auxParticle.getId() && auxParticle.overlaps(other)) {
                    overlap = true;
                    break;
                }
            }
            if(!overlap)
                foundPlace=true;
        }




        return auxParticle;
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


            double nextVx = particles.get(i).getVx() + (3.0 / 2.0) * currAccelX * deltaT - 0.5 * prevAccelX * deltaT;
            double nextVy = particles.get(i).getVy() + (3.0 / 2.0) * currAccelY * deltaT - 0.5 * prevAccelY * deltaT;




            Particle futureParticle = new Particle(particles.get(i).getMass(),
                    particles.get(i).getRadius(), nextRx,nextRy,
                    nextVx,nextVy);

            futureParticle.setId(particles.get(i).getId());

            nextStep.add(futureParticle);

            if(futureParticle.getY() < L*0.8)
                recentlyRespawned.remove(futureParticle);
        }
        for (int i = 0; i < nextStep.size() ; i++) {
            Particle futureParticle = nextStep.get(i);
            futureParticle.updateForces(nextStep, new Particle[]{particleLeft,particleRight}, KN,KT,L,W,D);

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
                p = this.newCoords(p);
                particles.set(i, p);
                double rxAnterior = p.getX() +  (Math.pow((-1*deltaT),2)/(2*p.getMass())) *p.getFx();
                double ryAnterior = p.getY() + (Math.pow((-1*deltaT),2)/(2*p.getMass())) *p.getFy();
                double vxAnterior = (-1*deltaT / p.getMass()) * p.getFx();
                double vyAnterior =  (-1*deltaT / p.getMass()) * p.getFy();
                previousParticlesStatus.get(i).setX(rxAnterior);
                previousParticlesStatus.get(i).setY(ryAnterior);
                previousParticlesStatus.get(i).setVx(vxAnterior);
                previousParticlesStatus.get(i).setVy(vyAnterior);
                previousParticlesStatus.get(i).setFx(0);
                previousParticlesStatus.get(i).setFy(0);

                recentlyRespawned.add(p);
                respawnCount++;


            }

        }
//        respawnCount+=recentlyRespawned.size();
        if (count!=0&&count%100==0){
            flowrate.add(respawnCount);
            respawnCount=0;
            System.out.println(count);
        }


        //System.out.println("actualizadas");


        

    }


    private void calculateParticlesForces(){
        for (Particle p : this.particles){
            p.updateForces(particles, new Particle[]{particleLeft,particleRight}, KN,KT,L,W,D);
        }

        System.out.println("termine");
    }



    int count = 0;

    public boolean stopCriteria(){
        return count++ == 320000;
    }

    public List<Integer> getFlowrate() {
        return flowrate;
    }

    public static double getKN() {
        return KN;
    }
    public void setKt(double kt){
        KT=kt;
    }
}
