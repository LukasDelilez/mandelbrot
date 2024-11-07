import javafx.application.Application;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class Visualization extends Application {

    private FractalWindow fractalWindow;
    private FractalController fractalController;

    @Override
    public void start(Stage primaryStage) {


        fractalController = new FractalController();
        int[] mandelbrotResults = fractalController.calculateMandelbrotSet();

        AsyncFractalView mandelbrotView = new AsyncFractalView(Config.getSteps(), Config.getMaxIterations());
        mandelbrotView.startRendering();
        fractalWindow = new FractalWindow("MandelbrotSet", mandelbrotView, null);

        mandelbrotView.draw(mandelbrotResults);

        setUpJuliaSetHandler(mandelbrotView);
        fractalWindow.getStage().setOnCloseRequest(_ -> {
            mandelbrotView.stopRendering();
        });

        fractalWindow.getStage().iconifiedProperty().addListener((_, _, isMinimized) -> {
            if(isMinimized) mandelbrotView.stopRendering();
            else mandelbrotView.startRendering();
        });
        fractalWindow.show();
    }

    private void setUpJuliaSetHandler(FractalView mandelbrotView) {
        mandelbrotView.getView().setOnMouseClicked((mouseEvent) -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                int x = (int) mouseEvent.getX();
                int y = (int) mouseEvent.getY();
                createJuliaSetView(x, y);
            }
        });
    }

    private void createJuliaSetView(int x, int y) {
        int[] juliaMenge = fractalController.calculateJuliaSet(x, y);

        AsyncFractalView juliaView = new AsyncFractalView(Config.getSteps(), Config.getMaxIterations());
        juliaView.draw(juliaMenge);

        FractalWindow juliaSetWindow = new FractalWindow("Julia Set", juliaView, fractalWindow.getStage());
        juliaSetWindow.show();
    }
}

