public class MSTEdge<DataType> {

    private double edgeWeight;
    private MSTNode<DataType> pred;
    private MSTNode<DataType> succ;

    public MSTEdge(double edgeWeight, MSTNode<DataType> pred, MSTNode<DataType> succ) {
        if (pred == null || succ == null) {
            throw new IllegalArgumentException("Edge endpoints cannot be null.");
        }

        this.edgeWeight = edgeWeight;
        this.pred = pred;
        this.succ = succ;
    }

    public double getEdgeWeight() {
        return edgeWeight;
    }

    public MSTNode<DataType> getPred() {
        return pred;
    }

    public MSTNode<DataType> getSucc() {
        return succ;
    }

    public MSTNode<DataType> getOtherNode(MSTNode<DataType> node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null.");
        }

        if (node == pred) {
            return succ;
        }

        if (node == succ) {
            return pred;
        }

        throw new IllegalArgumentException("Node is not connected to this edge.");
    }

    @Override
    public String toString() {
        return "[Start Node: " + pred.getData()
                + ", End Node: " + succ.getData()
                + ", Weight: " + edgeWeight + "]";
    }
}