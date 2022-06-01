import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticlesGenerator {

    public static void main(String[] args) throws Exception {


        Config config = new Config("static_input.txt");
        long startTime = System.currentTimeMillis();

        //TODO ponerlo mejó
        double L = config.getHeightLength();
        double W = config.getWidthLength();
        List<Particle> particles = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        while(System.currentTimeMillis()<startTime + 10000){
            double radius = 0.01 + (0.015 - 0.01) * random.nextDouble();
            double x = radius + (W - 2 * radius) * random.nextDouble();
            double y = radius + (L - 2 * radius) * random.nextDouble();


            Particle toAdd = new Particle( config.getMass(),radius,x,y,0,0);

            boolean overlaps = false;
            for(Particle p : particles){
                if(toAdd.overlaps(p)){
                    overlaps = true;
                    break;
                }
            }
            if(!overlaps)
                particles.add(toAdd);

        }

        File file = new File("dynamic_input.txt");
        PrintWriter pw = new PrintWriter(file);
        pw.println("t0");
        for(Particle particle : particles){
            pw.println(particle.getX() + " " + particle.getY() + " " + particle.getRadius());
        }
        pw.close();







    }
}
