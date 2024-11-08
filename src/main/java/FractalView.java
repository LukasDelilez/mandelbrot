import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.concurrent.ConcurrentLinkedQueue;

public class FractalView {

    private ImageView fractalView;
    private WritableImage writableImage;
    protected PixelWriter pixelWriter;
    protected final int steps;
    protected final int maxIterations;

    record PixelUpdate(int x, int y, int iterations){}
    private final ConcurrentLinkedQueue<PixelUpdate> pixelBuffer;
    private final Timeline updateTimeline;
    protected final int BATCH_SIZE = Config.getPixelBufferBatchSize();

    public FractalView(int steps, int maxIterations) {
        this.steps = steps;
        this.maxIterations = maxIterations;
        this.pixelBuffer = new ConcurrentLinkedQueue<>();
        this.updateTimeline = createUpdateTimeline();
        initializeView();
    }

    protected void initializeView() {
        writableImage = new WritableImage(steps, steps);
        pixelWriter = writableImage.getPixelWriter();
        fractalView = new ImageView(writableImage);
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

    protected Color getColorInRange(int iterations) {
        double fraction = (double)iterations / maxIterations;
        int hex = (int) (16777215 * fraction);
        int bitwise = (0xFFFFFF ^ hex);
        String hexString = String.format("#%06x", bitwise);
        return Color.web(hexString);
    }

    public Node getView() {
        return fractalView;
    }
}
