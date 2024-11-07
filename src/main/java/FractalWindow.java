import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class FractalWindow {

    private final Stage stage;
    private final AsyncFractalView fraktalView;
    private final ZoomableScrollPane zoomableScrollPane;

    public FractalWindow(String title, AsyncFractalView fraktalView, Stage owner) {
        this.stage = new Stage();
        this.fraktalView = fraktalView;

        initializeWindow(title, owner);
        zoomableScrollPane = createZoomableScrollPane();

        createScene();
        setUpRenderingLifecycle();
    }

    private void initializeWindow(String title, Stage owner) {
        stage.setTitle(title);
        if (owner != null) {
            stage.initOwner(owner);
        }
    }

    private ZoomableScrollPane createZoomableScrollPane() {
        StackPane contentPane = new StackPane(fraktalView.getView());
        return new ZoomableScrollPane(contentPane);
    }

    private void createScene() {
        int screenWidth = Config.getScreenWidth();
        int screenHeight = Config.getScreenHeight();
        Scene scene = new Scene(zoomableScrollPane, screenWidth, screenHeight);
        stage.setScene(scene);
    }

    private void setUpRenderingLifecycle() {
        stage.setOnShown(_ -> fraktalView.startRendering());

        stage.setOnCloseRequest(_ -> fraktalView.stopRendering());

        stage.iconifiedProperty().addListener((_, _, isMinimized) -> {
            if(isMinimized) fraktalView.stopRendering();
            else fraktalView.startRendering();
        });
    }

    public void show() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }
}
