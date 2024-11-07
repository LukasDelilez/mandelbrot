public class FractalController {

    private final Mandelbrot mandelbrot;

    public FractalController() {
        this.mandelbrot = new Mandelbrot();
    }

    public int[] calculateMandelbrotSet() {
        return mandelbrot.getMandelbrotSet();
    }

    public int[] calculateJuliaSet(int x, int y) {
        return mandelbrot.getJulisSetFromCoordinate(x, y);
    }
}
