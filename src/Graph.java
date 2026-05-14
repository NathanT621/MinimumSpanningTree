import java.util.ArrayList;
import java.util.HashSet;

public class Graph<DataType> {

    private ArrayList<MSTNode<DataType>> nodes;
    private int idCounter;

    public Graph() {
        nodes = new ArrayList<>();
        idCounter = 0;
    }

    public int getGraphSize() {
        return nodes.size();
    }
    public int getId() {
    	return idCounter;
    }

    public ArrayList<MSTNode<DataType>> getNodes() {
        return nodes;
    }

    public MSTNode<DataType> addNode(DataType value) {
        MSTNode<DataType> newNode = new MSTNode<>(value, idCounter);
        idCounter++;

        nodes.add(newNode);
        return newNode;
    }

    public boolean containsNode(MSTNode<DataType> node) {
        return nodes.contains(node);
    }

    public MSTEdge<DataType> addEdge(double weight, MSTNode<DataType> pred, MSTNode<DataType> succ) {
        if (pred == null || succ == null) {
            throw new IllegalArgumentException("Edge endpoints cannot be null.");
        }

        if (!containsNode(pred) || !containsNode(succ)) {
            throw new IllegalArgumentException("Both nodes must already be in the graph.");
        }

        MSTEdge<DataType> edge = new MSTEdge<>(weight, pred, succ);

        pred.addEdgeLeaving(edge);
        succ.addEdgeEntering(edge);

        return edge;
    }
    public void removeNode(MSTNode<DataType> node) {
        if (node == null || !containsNode(node)) {
            return;
        }

        ArrayList<MSTEdge<DataType>> outgoingEdges = new ArrayList<>(node.getEdgesLeaving());
        ArrayList<MSTEdge<DataType>> incomingEdges = new ArrayList<>(node.getEdgesEntering());

        for (MSTEdge<DataType> edge : outgoingEdges) {
            edge.getSucc().removeEdgeEntering(edge);
        }

        for (MSTEdge<DataType> edge : incomingEdges) {
            edge.getPred().removeEdgeLeaving(edge);
        }

        nodes.remove(node);
    }

//    public ArrayList<MSTEdge<DataType>> primEdges(MSTNode<DataType> startNode) {
//        if (startNode == null) {
//            throw new NullPointerException("Starting node cannot be null.");
//        }
//
//        if (!containsNode(startNode)) {
//            throw new IllegalArgumentException("Starting node must be in the graph.");
//        }
//
//        ArrayList<MSTEdge<DataType>> treeEdges = new ArrayList<>();
//        ArrayList<MSTNode<DataType>> treeNodes = new ArrayList<>();
//
//        treeNodes.add(startNode);
//
//        while (!isSpanningTree(treeEdges, treeNodes)) {
//            MSTEdge<DataType> smallestEdge = null;
//            MSTNode<DataType> nextNode = null;
//
//            for (MSTNode<DataType> treeNode : treeNodes) {
//                for (MSTEdge<DataType> edge : getAllEdgesForNode(treeNode)) {
//                    MSTNode<DataType> otherNode = edge.getOtherNode(treeNode);
//
//                    if (!treeNodes.contains(otherNode)) {
//                        if (smallestEdge == null
//                                || edge.getEdgeWeight() < smallestEdge.getEdgeWeight()) {
//                            smallestEdge = edge;
//                            nextNode = otherNode;
//                        }
//                    }
//                }
//            }
//
//            if (smallestEdge == null || nextNode == null) {
//                return new ArrayList<>();
//            }
//
//            treeEdges.add(smallestEdge);
//            treeNodes.add(nextNode);
//        }
//
//        return treeEdges;
//    }
    public ArrayList<MSTEdge<DataType>> primEdges(MSTNode<DataType> startNode) {
        if (startNode == null) {
            throw new NullPointerException("Starting node cannot be null.");
        }

        if (!containsNode(startNode)) {
            throw new IllegalArgumentException("Starting node must be in the graph.");
        }

        ArrayList<MSTEdge<DataType>> treeEdges = new ArrayList<>();
        ArrayList<MSTNode<DataType>> treeNodes = new ArrayList<>();

        treeNodes.add(startNode);

        System.out.println("Starting Prim's at: " + startNode);

        while (!isSpanningTree(treeEdges, treeNodes)) {
            MSTEdge<DataType> smallestEdge = null;
            MSTNode<DataType> nextNode = null;

            System.out.println("\nTree currently has " + treeNodes.size() + " nodes.");

            for (MSTNode<DataType> treeNode : treeNodes) {
                ArrayList<MSTEdge<DataType>> edges = getAllEdgesForNode(treeNode);

                System.out.println("Checking node: " + treeNode + ", edges found: " + edges.size());

                for (MSTEdge<DataType> edge : edges) {
                    MSTNode<DataType> otherNode = edge.getOtherNode(treeNode);

                    if (!treeNodes.contains(otherNode)) {
                        if (smallestEdge == null
                                || edge.getEdgeWeight() < smallestEdge.getEdgeWeight()) {
                            smallestEdge = edge;
                            nextNode = otherNode;
                        }
                    }
                }
            }

            if (smallestEdge == null || nextNode == null) {
                System.out.println("FAILED: Could not find an edge leaving the current tree.");

                System.out.println("Reached nodes:");
                for (MSTNode<DataType> node : treeNodes) {
                    System.out.println(node);
                }

                System.out.println("Unreached nodes:");
                for (MSTNode<DataType> node : nodes) {
                    if (!treeNodes.contains(node)) {
                        System.out.println(node);
                    }
                }

                return new ArrayList<>();
            }

            System.out.println("Chosen edge: " + smallestEdge);
            System.out.println("Adding node: " + nextNode);

            treeEdges.add(smallestEdge);
            treeNodes.add(nextNode);
        }

        System.out.println("Prim's completed. Edges in MST: " + treeEdges.size());

        return treeEdges;
    }
    public String prim(MSTNode<DataType> startNode) {
        if (nodes.isEmpty()) {
            return "Graph is empty.";
        }

        ArrayList<MSTEdge<DataType>> treeEdges = primEdges(startNode);

        if (nodes.size() > 1 && treeEdges.isEmpty()) {
            return "Graph is not connected. MST cannot be completed.";
        }

        String result = "";
        double total = 0.0;

        for (MSTEdge<DataType> edge : treeEdges) {
            result += edge.toString() + "\n";
            total += edge.getEdgeWeight();
        }

        result += "Total MST Weight: " + total;
        return result;
    }

    private ArrayList<MSTEdge<DataType>> getAllEdgesForNode(MSTNode<DataType> node) {
        ArrayList<MSTEdge<DataType>> allEdges = new ArrayList<>();

        allEdges.addAll(node.getEdgesLeaving());
        allEdges.addAll(node.getEdgesEntering());

        return allEdges;
    }

    private boolean pathExists(MSTNode<DataType> start, MSTNode<DataType> end) {
        if (start.equals(end)) {
            return true;
        }

        MSTQueue<DataType> queue = new MSTQueue<>();
        HashSet<MSTNode<DataType>> visited = new HashSet<>();

        queue.enqueue(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            MSTNode<DataType> currentNode = queue.dequeue();

            for (MSTEdge<DataType> edge : getAllEdgesForNode(currentNode)) {
                MSTNode<DataType> neighbor = edge.getOtherNode(currentNode);

                if (!visited.contains(neighbor)) {
                    if (neighbor.equals(end)) {
                        return true;
                    }

                    visited.add(neighbor);
                    queue.enqueue(neighbor);
                }
            }
        }

        return false;
    }
    public MSTNode<DataType> nodeLookup(int id){
    	return nodes.get(id);
    }

    private boolean isSpanningTree(
            ArrayList<MSTEdge<DataType>> treeEdges,
            ArrayList<MSTNode<DataType>> treeNodes
    ) {
        return treeNodes.size() == nodes.size()
                && treeEdges.size() == nodes.size() - 1;
    }
}