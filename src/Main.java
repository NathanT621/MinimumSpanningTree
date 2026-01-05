import java.util.ArrayList;

public class Main {
public static void main(String[] args) {
	MSTNode<Integer> two = new MSTNode<Integer>(2);
	MSTNode<Integer> seven = new MSTNode<Integer>(7);
	MSTNode<Integer> five = new MSTNode<Integer>(5);
	MSTNode<Integer> six = new MSTNode<Integer>(6);
	MSTNode<Integer> four = new MSTNode<Integer>(4);
	MSTNode<Integer> three = new MSTNode<Integer>(3);
	MSTEdge<Integer> twoSeven = new MSTEdge<Integer>(1,two, seven);
	MSTEdge<Integer> twoFive = new MSTEdge<Integer>(1,two,five);
	MSTEdge<Integer> sevenFour = new MSTEdge<Integer>(1,seven,four);
	MSTEdge<Integer> sevenSix = new MSTEdge<Integer>(1,seven,six);
	MSTEdge<Integer> fourThree = new MSTEdge<Integer>(1,four,three);
	MSTEdge<Integer> sixFour = new MSTEdge<Integer>(1,six,four);
	ArrayList<MSTNode> nodes = new ArrayList<MSTNode>();
	ArrayList<MSTEdge> edges = new ArrayList<MSTEdge>();
	nodes.add(two);
	nodes.add(three);
	nodes.add(five);
	nodes.add(six);
	nodes.add(four);
	nodes.add(seven);
	
//	edges.add(fourThree);
//	edges.add(sevenSix);
//	edges.add(sevenFour);
//	edges.add(twoFive);
//	edges.add(twoSeven);
//	edges.add(sixFour);
	Graph graph = new Graph(nodes);
	System.out.println(graph.prim(two));
}

}
