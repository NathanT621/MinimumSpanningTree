import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import java.util.ArrayList;
import javafx.scene.paint.Color;

import javafx.scene.shape.*;
public class App extends Application{

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		stage.setTitle("Minimum Spanning Tree");
		stage.show();
		BorderPane bp = new BorderPane();
		HBox centerBox = new HBox(10);
		ArrayList<MSTNode> nodes = new ArrayList<MSTNode>();
		HBox nodeBox = new HBox(centerBox.getWidth()/(nodes.size()+1));
		
		centerBox.setAlignment(Pos.CENTER);
		Button btn_addNode = new Button("Add Node");
		Button btn_remLastNode = new Button("Remove Previous Node");
		centerBox.getChildren().addAll(btn_addNode,btn_remLastNode,nodeBox);
		bp.setCenter(centerBox);
		
		Scene scene = new Scene(bp,800,600);
		btn_addNode.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			MSTNode newNode = new MSTNode((int) Math.random()*10);
			nodes.add(newNode);
			Circle circle = new Circle(25.0, 25.0, 10, Color.ANTIQUEWHITE);
			nodeBox.getChildren().addAll(circle);
			event.consume();
		});
		stage.setScene(scene);
	}
	public static void main(String[] args) {
		launch(args);
	}

}
