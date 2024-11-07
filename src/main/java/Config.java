import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    static {
        try (InputStream iS = Config.class.getResourceAsStream("/app.config")) {
            if(iS == null) throw new RuntimeException();
            properties.load(iS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getSteps() {
        return Integer.parseInt(properties.getProperty("steps"));
    }

    public static int getMaxIterations() {
        return Integer.parseInt(properties.getProperty("iterations"));
    }

    public static int getScreenWidth() {
        return Integer.parseInt(properties.getProperty("width"));
    }

    public static int getScreenHeight() {
        return Integer.parseInt(properties.getProperty("height"));
    }

    public static double getRealMin() {
        return Double.parseDouble(properties.getProperty("realMin"));
    }

    public static double getRealMax() {
        return Double.parseDouble(properties.getProperty("realMax"));
    }
    public static double getImaginaryMin() {
        return Double.parseDouble(properties.getProperty("imaginaryMin"));
    }
    public static double getImaginaryMax() {
        return Double.parseDouble(properties.getProperty("imaginaryMax"));
    }
}
