import java.util.concurrent.ConcurrentLinkedQueue;

public class FractalController {

    private final Mandelbrot mandelbrot;

    public FractalController() {
        this.mandelbrot = new Mandelbrot();
    }

    public void streamMandelbrotSet(ConcurrentLinkedQueue<AsyncFractalView.PixelUpdate> pixelBuffer) {
        mandelbrot.streamMandelbrotCalculations(pixelBuffer);
    }

    public void StreamJuliaSet(int x, int y, ConcurrentLinkedQueue<AsyncFractalView.PixelUpdate> pixelBuffer) {
        mandelbrot.streamJulisSetFromCoordinate(x, y, pixelBuffer);
    }
}
