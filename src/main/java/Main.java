import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws Exception {
        Config config = new Config("static_input.txt", "dynamic_input.txt");
        Environment environment = new Environment(config.getHeightLength(),config.getWidthLength()
                ,config.getExitWidthLength(),config.getParticles());


        DecimalFormat df = new DecimalFormat("#.0000",
                DecimalFormatSymbols.getInstance(Locale.US));
        File file = new File("dynamic_output.txt");
        PrintWriter pw = new PrintWriter(file);
        while(!environment.stopCriteria()){
            environment.evolve();
            pw.println("x,y,vx,vy,radius");

            for(Particle p : environment.particles)
                pw.printf("%s,%s,%s,%s,%s\n",df.format(p.getX()),
                        df.format(p.getY()),df.format(p.getVx()),df.format(p.getVy()),
                        df.format(p.getRadius()));
        }


        pw.close();

    }
}