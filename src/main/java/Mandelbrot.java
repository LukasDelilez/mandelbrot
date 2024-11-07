import java.util.Arrays;

public class Mandelbrot {


    private double realMin;
    private double realMax;
    private double imaginaryMin;
    private double imaginaryMax;

    private int steps;
    private int maxIterations;
    private double stepSize;
    private double[] real;
    private double[] imaginary;

    public Mandelbrot() {
        init();
    }

    public Mandelbrot(int steps, int iterations) {
        this.steps = steps;
        this.maxIterations = iterations;
        init();
    }

    private void init() {
        readConfig();
        calculateStepSize();
        calculateStepValueRangesRealAndImaginary();
    }

    private void readConfig() {
        if(maxIterations == 0) maxIterations = Config.getMaxIterations();
        if(steps == 0) steps = Config.getSteps();
        realMin = Config.getRealMin();
        realMax = Config.getRealMax();
        imaginaryMin = Config.getImaginaryMin();
        imaginaryMax = Config.getImaginaryMax();
    }

    public int[] getMandelbrotSet() {
        return calculateMandelbrotValuesStream();
    }

    protected void calculateStepSize() {
        stepSize = (Math.abs(realMin) + realMax) / steps;
    }

    protected void calculateStepValueRangesRealAndImaginary() {
        real = new double[steps];
        real[0] = realMin;
        real[steps - 1] = realMax;

        imaginary = new double[steps];
        imaginary[0] = imaginaryMin;
        imaginary[steps - 1] = imaginaryMax;

        double rangeRealMinLocal = realMin;
        double rangeImaginaryMinLocal = imaginaryMin;
        for(int i = 1; i < steps - 1; i++) {
            rangeRealMinLocal += stepSize;
            real[i] = rangeRealMinLocal;

            rangeImaginaryMinLocal += stepSize;
            imaginary[i] = rangeImaginaryMinLocal;
        }
    }

    private int[] calculateMandelbrotValuesStream() {
        return Arrays.stream(real)
            .parallel()
            .boxed()
            .flatMapToInt(r -> Arrays.stream(imaginary)
                    .mapToInt(i -> calculateAbsoluteValue(r, i)))
            .toArray();
    }

    private int calculateAbsoluteValue(double real, double imaginary) {
        double zReal = 0;
        double zImaginary = 0;

        for (int i = 0; i < maxIterations; i++) {
            double zRealNew = zReal * zReal - zImaginary * zImaginary + real;
            zImaginary = 2 * zReal * zImaginary + imaginary;
            zReal = zRealNew;
            if(isNotMandelbrotSetMember(zReal, zImaginary)){
                return i;
            }
        }
        return maxIterations;
    }

    protected int[] calculateJuliaMengeValues(double cReal, double cImaginary) {
        int[] results = new int[real.length * imaginary.length];
        for (int i = 0; i < real.length; i++) {
            double zReal = real[i];
            for (int j = 0; j < imaginary.length; j++) {
                double zImaginary = imaginary[j];
                int index = i * real.length + j;
                results[index] = calculateJuliaMenge(zReal, zImaginary, cReal, cImaginary);
            }
        }
        return results;
    }

    protected int[] getJulisSetFromCoordinate(int cRealIndex, int cImaginaryIndex) {
        int[] results = new int[real.length * imaginary.length];
        double cReal = real[cRealIndex];
        double cImaginary = imaginary[cImaginaryIndex];
        for (int i = 0; i < real.length; i++) {
            double zReal = real[i];
            for (int j = 0; j < imaginary.length; j++) {
                double zImaginary = imaginary[j];
                int index = i * real.length + j;
                results[index] = calculateJuliaMenge(zReal, zImaginary, cReal, cImaginary);
            }
        }
        return results;
    }

    protected int calculateJuliaMenge(double zReal, double zImaginary, double cReal, double cImaginary) {
        for (int i = 0; i < maxIterations; i++) {
            double zRealNew = zReal * zReal - zImaginary * zImaginary + cReal;
            zImaginary = 2 * zReal * zImaginary + cImaginary;
            zReal = zRealNew;
            if(isNotMandelbrotSetMember(zReal, zImaginary)){
                return i;
            }
        }
        return maxIterations;
    }

    private boolean isNotMandelbrotSetMember(double zReal, double zImaginary) {
        return zReal * zReal + zImaginary * zImaginary > 4;
    }
}
