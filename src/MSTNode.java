import java.util.List;
import java.util.LinkedList;
public class MSTNode<DataType> {
public DataType data;
public List<MSTEdge> edgesLeaving = new LinkedList<MSTEdge>();
public List<MSTEdge> edgesEntering = new LinkedList<MSTEdge>();
public MSTNode(DataType data) {
	this.data = data;
}
}
