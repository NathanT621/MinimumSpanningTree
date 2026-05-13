import java.util.ArrayList;

public class MSTQueue<DataType> {

    private ArrayList<MSTNode<DataType>> nodes;

    public MSTQueue() {
        nodes = new ArrayList<>();
    }

    public void enqueue(MSTNode<DataType> node) {
        nodes.add(node);
    }

    public MSTNode<DataType> dequeue() {
        if (nodes.isEmpty()) {
            throw new IllegalStateException("Cannot dequeue from an empty queue.");
        }

        return nodes.remove(0);
    }

    public int size() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public String toString() {
        return nodes.toString();
    }
}