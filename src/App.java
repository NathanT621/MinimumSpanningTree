import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;

import javafx.scene.text.Font;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.shape.*;
import javafx.scene.text.Text;

public class App extends Application{
private boolean selectingSource = true;
private boolean selecting = false;
private StackPane selectedSource = null;
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

    // TIP at edge of destination circle (pointing into destination)
    double tipX = ex - ux * r;
    double tipY = ey - uy * r;

    // line ends at base of arrowhead (a bit before the tip)
    double lineEndX = tipX - ux * arrowLen;
    double lineEndY = tipY - uy * arrowLen;

    Line line = new Line(startX, startY, lineEndX, lineEndY);
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(3);

    // Arrowhead triangle around tip, pointing toward destination
    double angle = Math.atan2(uy, ux);

    double x1 = tipX - arrowLen * Math.cos(angle) + arrowWid * Math.sin(angle);
    double y1 = tipY - arrowLen * Math.sin(angle) - arrowWid * Math.cos(angle);

    double x2 = tipX - arrowLen * Math.cos(angle) - arrowWid * Math.sin(angle);
    double y2 = tipY - arrowLen * Math.sin(angle) + arrowWid * Math.cos(angle);

    Polygon head = new Polygon(tipX, tipY, x1, y1, x2, y2);
    head.setFill(Color.BLACK);

    edgeLayer.getChildren().addAll(line, head);
}



	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("Minimum Spanning Tree");
		stage.show();
		BorderPane bp = new BorderPane();
		HBox buttonBar = new HBox(10);
		buttonBar.setAlignment(Pos.CENTER);

		Text statusText = new Text();
		statusText.setFont(Font.font("Arial", 18));
		statusText.setVisible(false);

		VBox topBox = new VBox(6, buttonBar, statusText);
		topBox.setAlignment(Pos.CENTER);
		ArrayList<MSTNode> nodes = new ArrayList<MSTNode>();
		ArrayList<StackPane> nodeViews = new ArrayList<StackPane>();
		
		topBox.setAlignment(Pos.CENTER);
		Button btn_addNode = new Button("Add Node");
		Button btn_remLastNode = new Button("Remove Previous Node");
		Button btn_createEdge = new Button("Create Edge");
		TextField tf_newNode = new TextField();
		buttonBar.getChildren().addAll(tf_newNode,btn_addNode,btn_remLastNode, btn_createEdge);
		
		bp.setTop(topBox);
		
		Scene scene = new Scene(bp,800,600);
		//int y
		HBox nodeBox = new HBox(15);
		nodeBox.setAlignment(Pos.CENTER);

		Pane edgeLayer = new Pane();
		edgeLayer.setPickOnBounds(false);     // don’t block clicks
		edgeLayer.setMouseTransparent(true);  // clicks pass through to nodes

		StackPane centerLayer = new StackPane(nodeBox, edgeLayer);
		edgeLayer.setMouseTransparent(true); // so clicks still go to nodes
		bp.setCenter(centerLayer);

	
		btn_addNode.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			MSTNode newNode = new MSTNode(Double.valueOf(tf_newNode.getText()));
			nodes.add(newNode);
			Circle circle = new Circle(20, Color.rgb(144,238,144));
			
			Text text = new Text(tf_newNode.getText());
			
			StackPane stack = new StackPane(circle, text);
			nodeViews.add(stack);
			nodeBox.getChildren().add(stack);
			tf_newNode.clear();
			event.consume();
		});
		btn_remLastNode.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			if (!nodes.isEmpty()) {
				nodes.remove(nodes.size()-1);
				StackPane lastView = nodeViews.remove(nodeViews.size()-1);
				nodeBox.getChildren().remove(lastView);
			}
			event.consume();
		});
		
		btn_createEdge.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			PauseTransition pause = new PauseTransition(Duration.seconds(3));
			if (nodes.size()<2) {
				pause.setOnFinished(e -> statusText.setVisible(false));
				statusText.setText("Insufficient Nodes to Create Edge");
				
				
				statusText.setFill(Color.RED);
				
				statusText.setVisible(true);
			}
			else {
			    
			 // when starting edge selection:
			    selecting = true;
			    btn_addNode.setDisable(true);
			    btn_remLastNode.setDisable(true);

		

			    selectingSource = true;
			    selectedSource = null;

			    statusText.setFill(Color.BLUE);
			    statusText.setText("Select the Source Node");
			    statusText.setVisible(true);

			    for (StackPane stack : nodeViews) {

			        // attach a handler we can later remove by setting it to null
			        stack.setOnMouseClicked(e -> {
			            if (!selecting) return; // not in selection mode

			            Circle circle = (Circle) stack.getChildren().get(0);
			            Text label = (Text) stack.getChildren().get(1);

			            // SOURCE click
			            if (selectingSource) {
			                selectedSource = stack;

			                circle.setStroke(Color.BLUE);
			                circle.setStrokeWidth(3);

			                selectingSource = false;
			                statusText.setText("Select the Destination Node");
			                e.consume();
			                return;
			            }

			            // DESTINATION click
			            StackPane selectedDest = stack;
			    	    // when finishing edge selection (after destination chosen):
					    selecting = false;
					    btn_addNode.setDisable(false);
					    btn_remLastNode.setDisable(false);
			            // (optional) prevent selecting the same node twice
			            if (selectedDest == selectedSource) {
			                statusText.setFill(Color.RED);
			                statusText.setText("Destination must be different. Pick another node.");
			                statusText.setVisible(true);
			                return;
			            }

			            Circle destCircle = (Circle) selectedDest.getChildren().get(0);
			            destCircle.setStroke(Color.BLUE);
			            destCircle.setStrokeWidth(3);
			            addArrow(edgeLayer, selectedSource, selectedDest);
			            System.out.println("Source: " + ((Text) selectedSource.getChildren().get(1)).getText());
			            System.out.println("Dest:   " + label.getText());

			            // ✅ STOP LISTENING
			            selecting = false;
			            selectingSource = true;

			            // remove handlers so clicks do nothing now
			            for (StackPane s : nodeViews) {
			                s.setOnMouseClicked(null);
			            }

			            statusText.setText("New Edge Added From " + ((Text) selectedSource.getChildren().get(1)).getText() + " to " + ((Text) selectedDest.getChildren().get(1)).getText());
			            PauseTransition delay = new PauseTransition(Duration.seconds(1));
			            delay.setOnFinished(ev -> {
			                // unhighlight source
			                Circle src = (Circle) selectedSource.getChildren().get(0);
			                src.setStroke(null);
			                src.setStrokeWidth(0);

			                // unhighlight destination
			                Circle dst = (Circle) selectedDest.getChildren().get(0);
			                dst.setStroke(null);
			                dst.setStrokeWidth(0);
			            });
			            delay.play();
			            PauseTransition p = new PauseTransition(Duration.seconds(2));
			            p.setOnFinished(ev -> statusText.setVisible(false));
			            p.play();

			            e.consume();
			        });
			    }
			}

			
			pause.play();
		});
		stage.setScene(scene);
	}
	public static void main(String[] args) {
		launch(args);
	}

}
