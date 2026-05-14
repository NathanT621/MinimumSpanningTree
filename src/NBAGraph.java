import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class NBAGraph {
	private Graph<String> graph;
	 public NBAGraph() {
	         graph = new Graph<>();
	        try {
	        	File nbaTeams = new File("NBATeams.txt");
	        	Scanner teamScanner = new Scanner(nbaTeams);
	        	while (teamScanner.hasNextLine()) {
	        	    String currTeam = teamScanner.nextLine().trim();

	        	    if (!currTeam.isEmpty()) {
	        	        graph.addNode(currTeam);
	        	    }
	        	}

	        	teamScanner.close();
	        	System.out.println("Number of teams/nodes: " + graph.getGraphSize());
	        }
	        catch (FileNotFoundException e) {
	            System.out.println("File not found.");
	            return;
	        }
	        int n = 30;
	        double[][] distances = new double[n][n];

	        try {
	            File file = new File("distanceMatrix.txt");
	            Scanner scanner = new Scanner(file);
	            scanner.useDelimiter("[,\\s]+");
	            // Read the 30x30 matrix
	            for (int i = 0; i < n; i++) {
	                for (int j = 0; j < n; j++) {
	                    distances[i][j] = scanner.nextDouble();
	                }
	            }

	            scanner.close();

	        } catch (FileNotFoundException e) {
	            System.out.println("File not found.");
	            return;
	        }

	        // Create edges from the matrix
	        for (int i = 0; i < n; i++) {
	            for (int j = i + 1; j < n; j++) {
	                double weight = distances[i][j];
	                MSTEdge<String> newEdge = graph.addEdge(weight, graph.nodeLookup(i), graph.nodeLookup(j));
	                // Add edge between team i and team j
	                //System.out.println(newEdge.toString());
	            }
	        }
	        System.out.println("Graph size: " + graph.getGraphSize());

	        for (int i = 0; i < graph.getGraphSize(); i++) {
	            MSTNode<String> node = graph.nodeLookup(i);
	            System.out.println(
	                i + " " + node
	                + ": leaving=" + node.getEdgesLeaving().size()
	                + ", entering=" + node.getEdgesEntering().size()
	            );
	        }
	        // Execute Prim's on each team
	        System.out.println(graph.prim(graph.nodeLookup(3)));
	 }
	 public static void main(String args[]) {
		 NBAGraph graph = new NBAGraph();
		 
	 }
	       
	
}
