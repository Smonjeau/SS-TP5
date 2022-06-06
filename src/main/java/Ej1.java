import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Ej1 {
    public static void main(String[] args) throws Exception {
//        ej1_a(args);
//        ej1_c(args);
        ej1_d(args);
    }
    public static void ej1_a(String[] args) throws Exception {
        Config config = new Config("static_input.txt", "dynamic_input.txt");

        for (int i=0;i<4;i++){
            Environment environment = new Environment(config.getHeightLength(),config.getWidthLength()
                    ,0.15+i*((0.25-0.15)/3.0),config.getParticles());


            DecimalFormat df = new DecimalFormat("#.0000",
                    DecimalFormatSymbols.getInstance(Locale.US));
            File file = new File("dynamic_output_"+i+".txt");
            PrintWriter pw = new PrintWriter(file);

            while(!environment.stopCriteria()){

                environment.evolve();

            }
            for (Integer flow: environment.getFlowrate()) {
                pw.println(flow);
            }

            pw.close();

        }
    }
    public static void ej1_c(String[] args) throws Exception {
        Config config = new Config("static_input.txt", "dynamic_input.txt");
        DecimalFormat df = new DecimalFormat("#.0000",
                DecimalFormatSymbols.getInstance(Locale.US));
        File file = new File("dynamic_output_energy.txt");
        PrintWriter pw = new PrintWriter(file);
        List<List<Double>>particleListList=new ArrayList<>();
        for (int i=0;i<4;i++){
            particleListList.add(i,new ArrayList<>());
            Environment environment = new Environment(config.getHeightLength(),config.getWidthLength()
                    ,0.15+i*((0.25-0.15)/3.0),config.getParticles());

            while(!environment.stopCriteria()){

                environment.evolve();
                double kineticEnergytotal=0;
                for (Particle particle:environment.getParticles()) {
                    kineticEnergytotal+=0.5*particle.getMass()*(Math.pow(particle.getVx(),2)+Math.pow(particle.getVy(),2));
                }
                particleListList.get(i).add(kineticEnergytotal);

            }
        }
        pw.println("D0,D1,D2,D3");
        for (int j = 0; j < particleListList.get(0).size(); j++) {
            StringBuilder stringBuilder=new StringBuilder();
            for (int k = 0; k < particleListList.size(); k++) {
                stringBuilder.append(particleListList.get(k).get(j));
                if (k!= particleListList.size()-1)
                    stringBuilder.append(",");
            }
            pw.println(stringBuilder);
        }
        pw.close();
    }
    public static void ej1_d(String[] args) throws Exception {
        Config config = new Config("static_input.txt", "dynamic_input.txt");
        DecimalFormat df = new DecimalFormat("#.0000",
                DecimalFormatSymbols.getInstance(Locale.US));
        File file = new File("dynamic_output_closed_energy.txt");
        PrintWriter pw = new PrintWriter(file);
        List<List<Double>>particleListList=new ArrayList<>();
        for (int i=1;i<4;i++){
            particleListList.add(i-1 ,new ArrayList<>());
            Environment environment = new Environment(config.getHeightLength(),config.getWidthLength()
                    ,0,config.getParticles());

            environment.setKt(i*Environment.getKN());
            while(!environment.stopCriteria()){

                environment.evolve();
                double kineticEnergytotal=0;
                for (Particle particle:environment.getParticles()) {
                    kineticEnergytotal+=0.5*particle.getMass()*(Math.pow(particle.getVx(),2)+Math.pow(particle.getVy(),2));
                }
                particleListList.get(i-1).add(kineticEnergytotal);

            }
        }
        pw.println("KT1,KT2,KT3");
        for (int j = 0; j < particleListList.get(0).size(); j++) {
            StringBuilder stringBuilder=new StringBuilder();
            for (int k = 0; k < particleListList.size(); k++) {
                stringBuilder.append(particleListList.get(k).get(j));
                if (k!= particleListList.size()-1)
                    stringBuilder.append(",");
            }
            pw.println(stringBuilder);
        }
        pw.close();
    }

}
