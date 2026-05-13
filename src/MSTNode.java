import java.util.ArrayList;

public class MSTNode<DataType> {

    private DataType data;
    private int id;

    private ArrayList<MSTEdge<DataType>> edgesLeaving;
    private ArrayList<MSTEdge<DataType>> edgesEntering;

    public MSTNode(DataType data, int id) {
        this.data = data;
        this.id = id;
        this.edgesLeaving = new ArrayList<>();
        this.edgesEntering = new ArrayList<>();
    }

    public DataType getData() {
        return data;
    }

    public int getNodeId() {
        return id;
    }

    public ArrayList<MSTEdge<DataType>> getEdgesLeaving() {
        return edgesLeaving;
    }

    public ArrayList<MSTEdge<DataType>> getEdgesEntering() {
        return edgesEntering;
    }

    public void addEdgeLeaving(MSTEdge<DataType> edge) {
        edgesLeaving.add(edge);
    }

    public void addEdgeEntering(MSTEdge<DataType> edge) {
        edgesEntering.add(edge);
    }

    public void removeEdgeLeaving(MSTEdge<DataType> edge) {
        edgesLeaving.remove(edge);
    }

    public void removeEdgeEntering(MSTEdge<DataType> edge) {
        edgesEntering.remove(edge);
    }

    @Override
    public String toString() {
        return data.toString();
    }
}