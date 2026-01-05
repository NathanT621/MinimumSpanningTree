import java.util.ArrayList;

public class MSTQueue{
	private ArrayList<MSTNode> nodes;
	public MSTQueue() {
		nodes = new ArrayList<MSTNode>();
		
	}
	public void enqueue(MSTNode node) {
		nodes.add(node);
	}
	public MSTNode deque() {
		return nodes.remove(0);
	}
	public int size() {
		return nodes.size();
	}
	public boolean isEmpty() {
		if (nodes.isEmpty()) {
			return true;
		}
		return false;
	}
	public String toString() {
		return nodes.toString();
	}
}
