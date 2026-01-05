
public class MSTEdge<DataType>{
public double edgeWeight;
public MSTNode pred;
public MSTNode succ;
public MSTEdge(double weight, MSTNode pred, MSTNode succ) {
	this.edgeWeight = weight;
	this.pred = pred;
	this.pred.edgesLeaving.add(this);
	this.succ = succ;
	this.succ.edgesEntering.add(this);
}
public String toString() {
	return "[Start Node: " + pred.data.toString() + ", End Node: " + succ.data.toString() + ", Weight: " + edgeWeight + "], ";
}
}
