import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Ej1 {
    public static void main(String[] args) throws Exception {
        ej1_a(args);
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
            for (Integer flow: environment.getFlowrate()           ) {
                pw.println(flow);
            }

            pw.close();

        }
    }

}
