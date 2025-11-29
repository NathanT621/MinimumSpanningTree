
public class MSTEdge<DataType>{
public DataType edgeWeight;
public MSTNode pred;
public MSTNode succ;
public MSTEdge(DataType weight, MSTNode pred, MSTNode succ) {
	this.edgeWeight = weight;
	this.pred = pred;
	this.succ = succ;
}
}
