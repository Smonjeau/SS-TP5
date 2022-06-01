import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Config {

    private double mass;
    private double widthLength;
    private double heightLength;
    private double exitWidthLength;


    List<Particle> particles = new ArrayList<>();

    private void parseStaticInput(String staticInputFilename) throws Exception {
        File staticInputFile = new File(staticInputFilename);
        Scanner staticReader = new Scanner(staticInputFile);

        if(staticReader.hasNextLine())
            widthLength = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("W must be in line 1");

        if(staticReader.hasNextLine())
            heightLength = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("L must be in line 2");

        if(staticReader.hasNextLine())
            exitWidthLength = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("D must be in line 3");

        if(staticReader.hasNextLine())
            mass = Double.parseDouble(staticReader.nextLine());
        else
            throw new Exception("Mass length must be in line 4");


        staticReader.close();


    }


    public Config (String staticInputFilename, String dynamicInputFilename) throws Exception {

        parseStaticInput(staticInputFilename);

        File dynamicInputFile = new File(dynamicInputFilename);
        Scanner dynamicReader = new Scanner(dynamicInputFile);


        if(dynamicReader.hasNextLine()) {
            dynamicReader.nextLine(); // t0
        } else {
            throw new Exception("t0 not found");
        }

        String row;
        String[] parts;
        while(dynamicReader.hasNextLine()) {
            row = dynamicReader.nextLine();
            parts = row.split(" ");

            particles.add(new Particle(mass,Double.parseDouble(parts[2])
                    ,Double.parseDouble(parts[0]),
                    Double.parseDouble(parts[1]),0,0));
        }
        dynamicReader.close();
    }



    public Config (String staticInputFilename) throws Exception {
        parseStaticInput(staticInputFilename);
    }



    public double getMass() {
        return mass;
    }

    public double getWidthLength() {
        return widthLength;
    }

    public double getHeightLength() {
        return heightLength;
    }


    public List<Particle> getParticles() {
        return particles;
    }

    public double getExitWidthLength() {
        return exitWidthLength;
    }
}