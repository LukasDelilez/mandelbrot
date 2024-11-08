import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    protected void calculateStepSize() {
        stepSize = (Math.abs(realMin) + realMax) / steps;
    }

    protected void calculateStepValueRangesRealAndImaginary() {
        real = new double[steps];
        real[0] = realMin;

        imaginary = new double[steps];
        imaginary[0] = imaginaryMin;

        double rangeRealMinLocal = realMin;
        double rangeImaginaryMinLocal = imaginaryMin;
        for(int i = 1; i < steps; i++) {
            rangeRealMinLocal += stepSize;
            real[i] = rangeRealMinLocal;

            rangeImaginaryMinLocal += stepSize;
            imaginary[i] = rangeImaginaryMinLocal;
        }
    }

    protected void streamMandelbrotCalculations(ConcurrentLinkedQueue<AsyncFractalView.PixelUpdate> pixelBuffer) {
        Arrays.stream(real)
            .parallel()
            .forEach(r -> Arrays.stream(imaginary)
                    .forEach(i -> {
                        int x = (int) ((r - realMin) / stepSize);
                        int y = (int) ((i - imaginaryMin) / stepSize);
                        int iterations = calculateAbsoluteValue(0, 0, r, i);
                        addBackPressure(pixelBuffer);
                        pixelBuffer.offer(new AsyncFractalView.PixelUpdate(x, y, iterations));
                    }));
    }

    protected void streamJulisSetFromCoordinate(int cRealIndex, int cImaginaryIndex, ConcurrentLinkedQueue<AsyncFractalView.PixelUpdate> pixelBuffer) {
        double cReal = real[cRealIndex];
        double cImaginary = imaginary[cImaginaryIndex];
        Arrays.stream(real)
            .parallel()
                .forEach(zReal -> Arrays.stream(imaginary).forEach(zImaginary -> {
                    int x = (int) ((zReal - realMin) / stepSize);
                    int y = (int) ((zImaginary - imaginaryMin) / stepSize);
                    int iterations = calculateAbsoluteValue(zReal, zImaginary, cReal, cImaginary);
                    addBackPressure(pixelBuffer);
                    pixelBuffer.offer(new AsyncFractalView.PixelUpdate(x, y, iterations));
                }));
    }

    private void addBackPressure(ConcurrentLinkedQueue<AsyncFractalView.PixelUpdate> pixelBuffer) {
        while (pixelBuffer.size() > Config.getPixelBufferBatchSize() * 2) {
            Thread.yield();
        }
    }

    protected int calculateAbsoluteValue(double zReal, double zImaginary, double cReal, double cImaginary) {
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
