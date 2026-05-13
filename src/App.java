import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
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

    private boolean selecting = false;
    private boolean selectingSource = true;

    private StackPane selectedSourceView = null;
    private MSTNode<Double> selectedSourceNode = null;

    private static class VisualEdge {
        private StackPane sourceView;
        private StackPane destView;
        private MSTEdge<Double> graphEdge;

        public VisualEdge(StackPane sourceView, StackPane destView, MSTEdge<Double> graphEdge) {
            this.sourceView = sourceView;
            this.destView = destView;
            this.graphEdge = graphEdge;
        }
    }

    private final ArrayList<VisualEdge> visualEdges = new ArrayList<>();
    private final ArrayList<MSTEdge<Double>> highlightedEdges = new ArrayList<>();

    private void addArrow(Pane edgeLayer, StackPane source, StackPane dest, Color color) {
        var b1 = source.localToScene(source.getBoundsInLocal());
        var b2 = dest.localToScene(dest.getBoundsInLocal());

        var sBounds = edgeLayer.sceneToLocal(b1);
        var dBounds = edgeLayer.sceneToLocal(b2);

        double sx = (sBounds.getMinX() + sBounds.getMaxX()) / 2.0;
        double sy = (sBounds.getMinY() + sBounds.getMaxY()) / 2.0;

        double ex = (dBounds.getMinX() + dBounds.getMaxX()) / 2.0;
        double ey = (dBounds.getMinY() + dBounds.getMaxY()) / 2.0;

        double dx = ex - sx;
        double dy = ey - sy;
        double len = Math.hypot(dx, dy);

        if (len < 1e-6) {
            return;
        }

        double ux = dx / len;
        double uy = dy / len;

        double radius = 20;
        double arrowLen = 14;
        double arrowWid = 8;

        double startX = sx + ux * radius;
        double startY = sy + uy * radius;

        double tipX = ex - ux * radius;
        double tipY = ey - uy * radius;

        double lineEndX = tipX - ux * arrowLen;
        double lineEndY = tipY - uy * arrowLen;

        Line line = new Line(startX, startY, lineEndX, lineEndY);
        line.setStroke(color);
        line.setStrokeWidth(3);

        double angle = Math.atan2(uy, ux);

        double x1 = tipX - arrowLen * Math.cos(angle) + arrowWid * Math.sin(angle);
        double y1 = tipY - arrowLen * Math.sin(angle) - arrowWid * Math.cos(angle);

        double x2 = tipX - arrowLen * Math.cos(angle) - arrowWid * Math.sin(angle);
        double y2 = tipY - arrowLen * Math.sin(angle) + arrowWid * Math.cos(angle);

        Polygon head = new Polygon(tipX, tipY, x1, y1, x2, y2);
        head.setFill(color);

        edgeLayer.getChildren().addAll(line, head);
    }

    private void redrawEdges(Pane edgeLayer) {
        edgeLayer.getChildren().clear();

        for (VisualEdge visualEdge : visualEdges) {
            Color edgeColor = Color.BLACK;

            if (highlightedEdges.contains(visualEdge.graphEdge)) {
                edgeColor = Color.RED;
            }

            addArrow(
                    edgeLayer,
                    visualEdge.sourceView,
                    visualEdge.destView,
                    edgeColor
            );
        }
    }

    private void showStatus(Text statusText, Color color, String message, double seconds) {
        statusText.setFill(color);
        statusText.setText(message);
        statusText.setVisible(true);

        PauseTransition hide = new PauseTransition(Duration.seconds(seconds));
        hide.setOnFinished(e -> statusText.setVisible(false));
        hide.play();
    }

    private void setButtonsDisabled(
            Button btn_addNode,
            Button btn_remLastNode,
            Button btn_createEdge,
            Button btn_runPrim,
            boolean disabled
    ) {
        btn_addNode.setDisable(disabled);
        btn_remLastNode.setDisable(disabled);
        btn_createEdge.setDisable(disabled);
        btn_runPrim.setDisable(disabled);
    }

    private void clearNodeClickHandlers(ArrayList<StackPane> nodeViews) {
        for (StackPane stack : nodeViews) {
            stack.setOnMouseClicked(null);
        }
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Minimum Spanning Tree");

        Graph<Double> graph = new Graph<>();
        ArrayList<StackPane> nodeViews = new ArrayList<>();

        BorderPane bp = new BorderPane();

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Text statusText = new Text();
        statusText.setFont(Font.font("Arial", 18));
        statusText.setVisible(false);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(6);
        outputArea.setPromptText("Prim's output will appear here.");

        VBox topBox = new VBox(6, buttonBar, statusText, outputArea);
        topBox.setAlignment(Pos.CENTER);

        TextField tf_newNode = new TextField();
        tf_newNode.setPromptText("Node value");

        Button btn_addNode = new Button("Add Node");
        Button btn_remLastNode = new Button("Remove Previous Node");
        Button btn_createEdge = new Button("Create Edge");
        Button btn_runPrim = new Button("Run Prim");

        buttonBar.getChildren().addAll(
                tf_newNode,
                btn_addNode,
                btn_remLastNode,
                btn_createEdge,
                btn_runPrim
        );

        bp.setTop(topBox);

        Pane nodeBox = new Pane();

        Pane edgeLayer = new Pane();
        edgeLayer.setPickOnBounds(false);
        edgeLayer.setMouseTransparent(true);

        StackPane centerLayer = new StackPane(nodeBox, edgeLayer);
        bp.setCenter(centerLayer);

        edgeLayer.prefWidthProperty().bind(centerLayer.widthProperty());
        edgeLayer.prefHeightProperty().bind(centerLayer.heightProperty());

        centerLayer.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> redrawEdges(edgeLayer));
        });

        nodeBox.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> redrawEdges(edgeLayer));
        });

        btn_addNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            outputArea.clear();
            highlightedEdges.clear();
            Platform.runLater(() -> redrawEdges(edgeLayer));

            if (selecting) {
                event.consume();
                return;
            }

            String txt = tf_newNode.getText().trim();

            if (txt.isEmpty()) {
                showStatus(statusText, Color.RED, "Enter a node value first.", 2);
                event.consume();
                return;
            }

            try {
                double value = Double.valueOf(txt);

                MSTNode<Double> newNode = graph.addNode(value);

                Circle circle = new Circle(20, Color.rgb(144, 238, 144));

                Text text = new Text(txt);
                text.setMouseTransparent(true);

                StackPane stack = new StackPane(circle, text);
                stack.setUserData(newNode);

                double radius = 20;
                double minDist = radius * 2 + 10;

                double x;
                double y;
                boolean valid;

                double paneWidth = Math.max(nodeBox.getWidth(), 760);
                double paneHeight = Math.max(nodeBox.getHeight(), 500);

                do {
                    valid = true;

                    double padding = 30;

                    x = Math.random() * (paneWidth - 2 * padding) + padding;
                    y = Math.random() * (paneHeight - 2 * padding) + padding;

                    for (StackPane other : nodeViews) {
                        double ox = other.getLayoutX() + radius;
                        double oy = other.getLayoutY() + radius;

                        double dx = x - ox;
                        double dy = y - oy;

                        double dist = Math.hypot(dx, dy);

                        if (dist < minDist) {
                            valid = false;
                            break;
                        }
                    }
                } while (!valid);

                stack.setLayoutX(x - radius);
                stack.setLayoutY(y - radius);

                nodeViews.add(stack);
                nodeBox.getChildren().add(stack);

                stack.boundsInParentProperty().addListener((obs, oldVal, newVal) -> {
                    Platform.runLater(() -> redrawEdges(edgeLayer));
                });

                tf_newNode.clear();

                Platform.runLater(() -> redrawEdges(edgeLayer));

            } catch (NumberFormatException ex) {
                showStatus(statusText, Color.RED, "Node value must be a number.", 2);
            }

            event.consume();
        });

        btn_remLastNode.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            outputArea.clear();
            highlightedEdges.clear();

            if (selecting) {
                event.consume();
                return;
            }

            if (!nodeViews.isEmpty()) {
                StackPane lastView = nodeViews.remove(nodeViews.size() - 1);

                MSTNode<Double> nodeToRemove = (MSTNode<Double>) lastView.getUserData();
                graph.removeNode(nodeToRemove);

                nodeBox.getChildren().remove(lastView);

                visualEdges.removeIf(edge ->
                        edge.sourceView == lastView || edge.destView == lastView
                );

                Platform.runLater(() -> redrawEdges(edgeLayer));
            }

            event.consume();
        });

        btn_createEdge.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            outputArea.clear();
            highlightedEdges.clear();
            Platform.runLater(() -> redrawEdges(edgeLayer));

            if (graph.getGraphSize() < 2) {
                showStatus(statusText, Color.RED, "Insufficient nodes to create edge.", 2);
                event.consume();
                return;
            }

            selecting = true;
            selectingSource = true;

            selectedSourceView = null;
            selectedSourceNode = null;

            setButtonsDisabled(btn_addNode, btn_remLastNode, btn_createEdge, btn_runPrim, true);

            statusText.setFill(Color.BLUE);
            statusText.setText("Select the source node.");
            statusText.setVisible(true);

            for (StackPane stack : nodeViews) {
                stack.setOnMouseClicked(e -> {
                    if (!selecting) {
                        e.consume();
                        return;
                    }

                    Circle circle = (Circle) stack.getChildren().get(0);

                    if (selectingSource) {
                        selectedSourceView = stack;
                        selectedSourceNode = (MSTNode<Double>) stack.getUserData();

                        circle.setStroke(Color.BLUE);
                        circle.setStrokeWidth(3);

                        selectingSource = false;

                        statusText.setFill(Color.BLUE);
                        statusText.setText("Select the destination node.");

                        e.consume();
                        return;
                    }

                    StackPane selectedDestView = stack;

                    if (selectedDestView == selectedSourceView) {
                        showStatus(statusText, Color.RED, "Destination must be different.", 2);
                        e.consume();
                        return;
                    }

                    MSTNode<Double> selectedDestNode = (MSTNode<Double>) selectedDestView.getUserData();

                    double edgeWeight = Math.abs(
                            selectedDestNode.getData() - selectedSourceNode.getData()
                    );

                    MSTEdge<Double> newGraphEdge;

                    try {
                        newGraphEdge = graph.addEdge(edgeWeight, selectedSourceNode, selectedDestNode);
                    } catch (IllegalArgumentException ex) {
                        showStatus(statusText, Color.RED, ex.getMessage(), 2);
                        e.consume();
                        return;
                    }

                    Circle destCircle = (Circle) selectedDestView.getChildren().get(0);
                    destCircle.setStroke(Color.BLUE);
                    destCircle.setStrokeWidth(3);

                    visualEdges.add(new VisualEdge(
                            selectedSourceView,
                            selectedDestView,
                            newGraphEdge
                    ));

                    Platform.runLater(() -> redrawEdges(edgeLayer));

                    String sourceLabel = ((Text) selectedSourceView.getChildren().get(1)).getText();
                    String destLabel = ((Text) selectedDestView.getChildren().get(1)).getText();

                    statusText.setFill(Color.BLUE);
                    statusText.setText(
                            "New edge added from " + sourceLabel
                                    + " to " + destLabel
                                    + " with weight " + edgeWeight
                    );
                    statusText.setVisible(true);

                    StackPane sourceViewToUnhighlight = selectedSourceView;
                    StackPane destViewToUnhighlight = selectedDestView;

                    selecting = false;
                    selectingSource = true;

                    selectedSourceView = null;
                    selectedSourceNode = null;

                    setButtonsDisabled(btn_addNode, btn_remLastNode, btn_createEdge, btn_runPrim, false);
                    clearNodeClickHandlers(nodeViews);

                    PauseTransition delay = new PauseTransition(Duration.seconds(1));
                    delay.setOnFinished(ev -> {
                        if (sourceViewToUnhighlight != null) {
                            Circle src = (Circle) sourceViewToUnhighlight.getChildren().get(0);
                            src.setStroke(null);
                            src.setStrokeWidth(0);
                        }

                        if (destViewToUnhighlight != null) {
                            Circle dst = (Circle) destViewToUnhighlight.getChildren().get(0);
                            dst.setStroke(null);
                            dst.setStrokeWidth(0);
                        }
                    });
                    delay.play();

                    PauseTransition hide = new PauseTransition(Duration.seconds(2));
                    hide.setOnFinished(ev -> statusText.setVisible(false));
                    hide.play();

                    e.consume();
                });
            }

            event.consume();
        });

        btn_runPrim.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (graph.getGraphSize() == 0) {
                outputArea.clear();
                highlightedEdges.clear();
                Platform.runLater(() -> redrawEdges(edgeLayer));

                showStatus(statusText, Color.RED, "Add at least one node first.", 2);
                event.consume();
                return;
            }

            selecting = true;

            setButtonsDisabled(btn_addNode, btn_remLastNode, btn_createEdge, btn_runPrim, true);

            statusText.setFill(Color.BLUE);
            statusText.setText("Select the starting node for Prim's algorithm.");
            statusText.setVisible(true);

            for (StackPane stack : nodeViews) {
                stack.setOnMouseClicked(e -> {
                    if (!selecting) {
                        e.consume();
                        return;
                    }

                    MSTNode<Double> startNode = (MSTNode<Double>) stack.getUserData();

                    String result = graph.prim(startNode);

                    highlightedEdges.clear();
                    highlightedEdges.addAll(graph.primEdges(startNode));

                    Platform.runLater(() -> redrawEdges(edgeLayer));

                    System.out.println(result);

                    outputArea.clear();
                    outputArea.setText(result);

                    Circle circle = (Circle) stack.getChildren().get(0);
                    circle.setStroke(Color.PURPLE);
                    circle.setStrokeWidth(3);

                    statusText.setFill(Color.BLUE);
                    statusText.setText("Prim's algorithm completed.");
                    statusText.setVisible(true);

                    selecting = false;

                    setButtonsDisabled(btn_addNode, btn_remLastNode, btn_createEdge, btn_runPrim, false);
                    clearNodeClickHandlers(nodeViews);

                    StackPane nodeToUnhighlight = stack;

                    PauseTransition delay = new PauseTransition(Duration.seconds(1));
                    delay.setOnFinished(ev -> {
                        Circle selectedCircle = (Circle) nodeToUnhighlight.getChildren().get(0);
                        selectedCircle.setStroke(null);
                        selectedCircle.setStrokeWidth(0);
                    });
                    delay.play();

                    PauseTransition hide = new PauseTransition(Duration.seconds(2));
                    hide.setOnFinished(ev -> statusText.setVisible(false));
                    hide.play();

                    e.consume();
                });
            }

            event.consume();
        });

        Scene scene = new Scene(bp, 900, 700);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}