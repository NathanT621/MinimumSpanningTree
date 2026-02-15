import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class App extends Application {

    private boolean selectingSource = true;
    private boolean selecting = false;
    private StackPane selectedSource = null;

    // store edges as pairs of StackPanes (source,dest)
    private final ArrayList<StackPane[]> edges = new ArrayList<>();

    private void addArrow(Pane edgeLayer, StackPane source, StackPane dest) {
        var b1 = source.localToScene(source.getBoundsInLocal());
        var b2 = dest.localToScene(dest.getBoundsInLocal());
        var sBounds = edgeLayer.sceneToLocal(b1);
        var dBounds = edgeLayer.sceneToLocal(b2);

        double sx = (sBounds.getMinX() + sBounds.getMaxX()) / 2.0;
        double sy = (sBounds.getMinY() + sBounds.getMaxY()) / 2.0;
        double ex = (dBounds.getMinX() + dBounds.getMaxX()) / 2.0;
        double ey = (dBounds.getMinY() + dBounds.getMaxY()) / 2.0;

        double dx = ex - sx, dy = ey - sy;
        double len = Math.hypot(dx, dy);
        if (len < 1e-6) return;

        // unit vector from source -> dest
        double ux = dx / len, uy = dy / len;

        double r = 20;          // circle radius
        double arrowLen = 14;
        double arrowWid = 8;

        // start at edge of source circle
        double startX = sx + ux * r;
        double startY = sy + uy * r;

        // tip at edge of destination circle
        double tipX = ex - ux * r;
        double tipY = ey - uy * r;

        // line ends at base of arrowhead (a bit before the tip)
        double lineEndX = tipX - ux * arrowLen;
        double lineEndY = tipY - uy * arrowLen;

        Line line = new Line(startX, startY, lineEndX, lineEndY);
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3);

        double angle = Math.atan2(uy, ux);

        double x1 = tipX - arrowLen * Math.cos(angle) + arrowWid * Math.sin(angle);
        double y1 = tipY - arrowLen * Math.sin(angle) - arrowWid * Math.cos(angle);

        double x2 = tipX - arrowLen * Math.cos(angle) - arrowWid * Math.sin(angle);
        double y2 = tipY - arrowLen * Math.sin(angle) + arrowWid * Math.cos(angle);

        Polygon head = new Polygon(tipX, tipY, x1, y1, x2, y2);
        head.setFill(Color.BLACK);

        edgeLayer.getChildren().addAll(line, head);
    }

    private void redrawEdges(Pane edgeLayer) {
        edgeLayer.getChildren().clear();
        for (StackPane[] e : edges) {
            if (e[0] != null && e[1] != null) {
                addArrow(edgeLayer, e[0], e[1]);
            }
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Minimum Spanning Tree");

        BorderPane bp = new BorderPane();

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Text statusText = new Text();
        statusText.setFont(Font.font("Arial", 18));
        statusText.setVisible(false);

        VBox topBox = new VBox(6, buttonBar, statusText);
        topBox.setAlignment(Pos.CENTER);

        ArrayList<MSTNode> nodes = new ArrayList<>();
        ArrayList<StackPane> nodeViews = new ArrayList<>();

        Button btn_addNode = new Button("Add Node");
        Button btn_remLastNode = new Button("Remove Previous Node");
        Button btn_createEdge = new Button("Create Edge");
        TextField tf_newNode = new TextField();

        buttonBar.getChildren().addAll(tf_newNode, btn_addNode, btn_remLastNode, btn_createEdge);
        bp.setTop(topBox);

        // center content
        HBox nodeBox = new HBox(15);
        nodeBox.setAlignment(Pos.CENTER);

        Pane edgeLayer = new Pane();
        edgeLayer.setPickOnBounds(false);
        edgeLayer.setMouseTransparent(true);

        // nodeBox below, arrows above (so arrowheads aren’t hidden)
        StackPane centerLayer = new StackPane(nodeBox, edgeLayer);
        bp.setCenter(centerLayer);

        // --- Add Node ---
        btn_addNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (selecting) return; // extra safety (should already be disabled)

            String txt = tf_newNode.getText().trim();
            if (txt.isEmpty()) return;

            MSTNode newNode = new MSTNode(Double.valueOf(txt));
            nodes.add(newNode);

            Circle circle = new Circle(20, Color.rgb(144, 238, 144));
            Text text = new Text(txt);
            text.setMouseTransparent(true); // clicks go to stack

            StackPane stack = new StackPane(circle, text);
            nodeViews.add(stack);
            nodeBox.getChildren().add(stack);

            tf_newNode.clear();

            // after layout shifts, redraw existing arrows
            Platform.runLater(() -> redrawEdges(edgeLayer));

            event.consume();
        });

        // --- Remove Node ---
        btn_remLastNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (selecting) return; // extra safety (should already be disabled)

            if (!nodes.isEmpty()) {
                nodes.remove(nodes.size() - 1);

                StackPane lastView = nodeViews.remove(nodeViews.size() - 1);
                nodeBox.getChildren().remove(lastView);

                // remove any edges that used this node
                edges.removeIf(e -> e[0] == lastView || e[1] == lastView);

                Platform.runLater(() -> redrawEdges(edgeLayer));
            }
            event.consume();
        });

        // --- Create Edge ---
        btn_createEdge.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (nodes.size() < 2) {
                statusText.setFill(Color.RED);
                statusText.setText("Insufficient Nodes to Create Edge");
                statusText.setVisible(true);

                PauseTransition hide = new PauseTransition(Duration.seconds(2));
                hide.setOnFinished(e -> statusText.setVisible(false));
                hide.play();

                event.consume();
                return;
            }

            // start edge selection
            selecting = true;
            selectingSource = true;
            selectedSource = null;

            btn_addNode.setDisable(true);
            btn_remLastNode.setDisable(true);

            statusText.setFill(Color.BLUE);
            statusText.setText("Select the Source Node");
            statusText.setVisible(true);

            for (StackPane stack : nodeViews) {
                stack.setOnMouseClicked(e -> {
                    if (!selecting) return;

                    Circle circle = (Circle) stack.getChildren().get(0);

                    // SOURCE
                    if (selectingSource) {
                        selectedSource = stack;

                        circle.setStroke(Color.BLUE);
                        circle.setStrokeWidth(3);

                        selectingSource = false;
                        statusText.setFill(Color.BLUE);
                        statusText.setText("Select the Destination Node");

                        e.consume();
                        return;
                    }

                    // DESTINATION
                    StackPane selectedDest = stack;

                    if (selectedDest == selectedSource) {
                        statusText.setFill(Color.RED);
                        statusText.setText("Destination must be different. Pick another node.");
                        statusText.setVisible(true);
                        e.consume();
                        return;
                    }

                    Circle destCircle = (Circle) selectedDest.getChildren().get(0);
                    destCircle.setStroke(Color.BLUE);
                    destCircle.setStrokeWidth(3);

                    // store edge + redraw (so it survives future HBox re-layout)
                    edges.add(new StackPane[]{selectedSource, selectedDest});
                    Platform.runLater(() -> redrawEdges(edgeLayer));

                    statusText.setFill(Color.BLUE);
                    statusText.setText(
                            "New Edge Added From " +
                                    ((Text) selectedSource.getChildren().get(1)).getText() +
                                    " to " +
                                    ((Text) selectedDest.getChildren().get(1)).getText()
                    );
                    statusText.setVisible(true);

                    // stop listening + re-enable buttons
                    selecting = false;
                    selectingSource = true;

                    btn_addNode.setDisable(false);
                    btn_remLastNode.setDisable(false);

                    for (StackPane s : nodeViews) {
                        s.setOnMouseClicked(null);
                    }

                    // unhighlight after 1 second
                    PauseTransition delay = new PauseTransition(Duration.seconds(1));
                    delay.setOnFinished(ev -> {
                        Circle src = (Circle) selectedSource.getChildren().get(0);
                        src.setStroke(null);
                        src.setStrokeWidth(0);

                        Circle dst = (Circle) selectedDest.getChildren().get(0);
                        dst.setStroke(null);
                        dst.setStrokeWidth(0);
                    });
                    delay.play();

                    // hide status after 2 seconds
                    PauseTransition hide = new PauseTransition(Duration.seconds(2));
                    hide.setOnFinished(ev -> statusText.setVisible(false));
                    hide.play();

                    e.consume();
                });
            }

            event.consume();
        });

        Scene scene = new Scene(bp, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
