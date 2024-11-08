import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncFractalView extends FractalView {

    protected record PixelUpdate(int x, int y, int iterations){}
    private final ConcurrentLinkedQueue<PixelUpdate> pixelBuffer;
    private final Timeline updateTimeline;
    private volatile boolean isCalculating = false;
    private final long totalPixels;
    private long pixelsProcessed;

    public AsyncFractalView(int steps, int maxIterations) {
        super(steps, maxIterations);
        pixelBuffer = new ConcurrentLinkedQueue<>();
        updateTimeline = createUpdateTimeline();
        totalPixels = (long) steps * steps;
        pixelsProcessed = 0;
    }

    private Timeline createUpdateTimeline() {
        KeyFrame keyFrame = new KeyFrame(Duration.millis(16), _ -> processBuffer());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    protected ConcurrentLinkedQueue<PixelUpdate> getPixelBuffer() {
        return pixelBuffer;
    }

    public void startRendering() {
        isCalculating = true;
        updateTimeline.play();
    }

    public void stopRendering() {
        updateTimeline.stop();
    }

    private void processBuffer() {
        int processed = 0;
        while(!pixelBuffer.isEmpty() && processed < BATCH_SIZE) {
            PixelUpdate update = pixelBuffer.poll();
            if(update != null) {
                Color color = getColorInRange(update.iterations());
                pixelWriter.setColor(update.x, update.y, color);
                processed++;
                pixelsProcessed++;
            }
            if (pixelsProcessed >= totalPixels) {
                System.out.println("Finished processing");
                isCalculating = false;
            }
        }

        if (!isCalculating && pixelBuffer.isEmpty()){
            stopRendering();
        }
    }
}
