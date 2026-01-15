import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
public class App extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("Minimum Spanning Tree");
		stage.show();
		BorderPane bp = new BorderPane();
		HBox topBox = new HBox(10);
		ArrayList<MSTNode> nodes = new ArrayList<MSTNode>();
		ArrayList<StackPane> nodeViews = new ArrayList<StackPane>();
		
		topBox.setAlignment(Pos.CENTER);
		Button btn_addNode = new Button("Add Node");
		Button btn_remLastNode = new Button("Remove Previous Node");
		TextField tf_newNode = new TextField();
		topBox.getChildren().addAll(tf_newNode,btn_addNode,btn_remLastNode);
		
		bp.setTop(topBox);
		
		Scene scene = new Scene(bp,800,600);
		//int y
		HBox nodeBox = new HBox(15);
		nodeBox.setAlignment(Pos.CENTER);
		bp.setCenter(nodeBox);
	
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
		stage.setScene(scene);
	}
	public static void main(String[] args) {
		launch(args);
	}

}
