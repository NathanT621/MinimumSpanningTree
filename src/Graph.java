import java.util.ArrayList;
import java.util.HashSet;
public class Graph<DataType> {
	private ArrayList<MSTNode> nodes;
	
	public Graph(ArrayList<MSTNode> nodes) {
		this.nodes = nodes;
		
	}

	public int getGraphSize() {
		return nodes.size();
	}

	public boolean containsNode(MSTNode node) {

		for (MSTNode n : nodes) {
			if (n.data.equals(node.data) && n.edgesEntering.equals(node.edgesEntering)
					&& n.edgesLeaving.equals(node.edgesLeaving)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public void addNode(DataType value, ArrayList<MSTNode> refBy, ArrayList<MSTNode> refTo) {
	
		
		MSTNode newNode = new MSTNode(value);
		newNode.edgesEntering = refBy;
		newNode.edgesLeaving = refTo;
		if (this.containsNode(newNode)) {
			return;
		}
		for (MSTNode n : refBy) {
			n.edgesLeaving.add(newNode);
		}
		for (MSTNode n : refTo) {
			n.edgesEntering.add(newNode);
		}

	}
	public void removeNode(MSTNode node) {
		if (!this.containsNode(node)) {
			return;
		}
		ArrayList<MSTEdge> outgoingEdges = node.edgesLeaving;
		ArrayList<MSTEdge> incomingEdges = node.edgesEntering;
		for (MSTEdge outgoingEdge : outgoingEdges) {
			outgoingEdge.succ.edgesEntering.remove(outgoingEdge);
		}
		for (MSTEdge incomingEdge : incomingEdges) {
			incomingEdge.pred.edgesLeaving.remove(incomingEdge);
		}
		nodes.remove(node);
	}
	public String prim(MSTNode startNode) {
		ArrayList<MSTEdge> treeEdges = new ArrayList<MSTEdge>();
		ArrayList<MSTNode> treeNodes = new ArrayList<MSTNode>();
		treeNodes.add(startNode);
		// Add all outgoing edges from tree nodes to contenders 
		ArrayList<MSTEdge> contenderEdges;
		while (!this.isSpanningTree(treeEdges, treeNodes)) {
			contenderEdges = new ArrayList<MSTEdge>();
			for (int x = 0; x<treeNodes.size(); x++) {
				for (int y = 0; y<treeNodes.get(x).edgesLeaving.size();y++) {
					contenderEdges.add((MSTEdge) treeNodes.get(x).edgesLeaving.get(y));
				}
			}
			for (int m = 0; m<contenderEdges.size(); m++) {
				if (treeNodes.contains(contenderEdges.get(m).succ)) {
					contenderEdges.remove(m);
					m--;
				}
			}
//			for (int treeNode = 0; treeNode<treeNodes.size(); treeNode++) {
//				for (int contenderEdge = 0; contenderEdge< contenderEdges.size(); contenderEdge++) {
//					if (pathExists(treeNodes.get(treeNode), contenderEdges.get(contenderEdge).succ)) {
//						// We remove an element from the list, decrease the index to offset
//						contenderEdge--;
//						contenderEdges.remove(contenderEdge);
//					}
//				}
//			}
			MSTEdge smallest = contenderEdges.get(0);
				
			
			for (MSTEdge m : contenderEdges) {
				if (m.edgeWeight < smallest.edgeWeight) {
					smallest = m;
				}
			}
			treeEdges.add(smallest);
			treeNodes.add(smallest.succ);
		}
		String ret = "";
		for (MSTEdge m : treeEdges) {
			ret += m.toString() + "\n";
		}
		return ret;
	}
	
	// Make use of BFS
	private boolean PathExists(MSTNode start, MSTNode end) {
		if (start.equals(end)) {
			return true;
		}
		MSTQueue queue = new MSTQueue();
		HashSet<MSTNode> visited = new HashSet<MSTNode>();
		queue.enqueue(start);
		visited.add(start);
		while (!queue.isEmpty()) {
			MSTNode visitedNode = queue.deque();
			
			ArrayList<MSTNode> neighbors = new ArrayList<MSTNode>();
			for (int x = 0; x<visitedNode.edgesLeaving.size();x++) {
				neighbors.add(((MSTEdge) visitedNode.edgesLeaving.get(x)).succ);
			}
			// Neighbors created, use a hash set to check if each neighbor has been visited and should be explored further
			for (int x = 0; x<neighbors.size(); x++) {
				MSTNode currentNeighbor = neighbors.get(x);
			if (!visited.contains(currentNeighbor)) {
				if (currentNeighbor.equals(end)) {
					return true;
				}
				else {
					visited.add(currentNeighbor);
					queue.enqueue(currentNeighbor);
				}
			}
		}
		}
		return false;
	
	}
	private boolean isSpanningTree(ArrayList<MSTEdge> treeEdges, ArrayList<MSTNode> treeNodes) {
		if (treeNodes.size()==nodes.size() && treeEdges.size() == nodes.size()-1) {
			return true;
		}
		return false;
	}
}
