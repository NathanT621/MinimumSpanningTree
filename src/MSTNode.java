import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
public class MSTNode<DataType> {
public DataType data;
public ArrayList<MSTEdge> edgesLeaving = new ArrayList<MSTEdge>();
public ArrayList<MSTEdge> edgesEntering = new ArrayList<MSTEdge>();
public MSTNode(DataType data) {
	this.data = data;
}
public void addEdgeLeaving(MSTEdge edge) {
	edgesLeaving.add(edge);
}
public void addEdgeEntering(MSTEdge edge) {
	edgesEntering.add(edge);
}
}
