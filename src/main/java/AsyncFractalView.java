import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;

public class AsyncFractalView extends FractalView {

    private record PixelUpdate(int x, int y, int iterations){}
    private final ConcurrentLinkedQueue<PixelUpdate> pixelBuffer;
    private final Timeline updateTimeline;
    private final int BATCH_SIZE = 10000;

    public AsyncFractalView(int steps, int maxIterations) {
        super(steps, maxIterations);
        pixelBuffer = new ConcurrentLinkedQueue<>();
        updateTimeline = createUpdateTimeline();
    }

    private Timeline createUpdateTimeline() {
        KeyFrame keyFrame = new KeyFrame(Duration.millis(16), _ -> processBuffer());
        Timeline timeline = new Timeline(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        return timeline;
    }

    public void startRendering() {
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
            }
        }
    }

    @Override
    public void draw(int[] results) {
        CompletableFuture.runAsync(() -> {
            IntStream.range(0, steps).parallel()
                .forEach(x -> {
                    IntStream.range(0, steps).forEach(y -> {
                        int index = x * steps + y;
                        if (index < steps * steps) {
                            pixelBuffer.offer(new PixelUpdate(x, y, results[index]));
                        }
                    });
                });
        });
    }
}
