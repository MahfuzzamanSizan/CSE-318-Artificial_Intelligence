import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {

        long nodes;
        long edges;
        long u, v, w;

        File inputFile = new File("input.txt");
        FileReader fileReader = new FileReader(inputFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String firstLine = bufferedReader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(firstLine);
        nodes = Integer.parseInt(tokenizer.nextToken());
        edges = Integer.parseInt(tokenizer.nextToken());

        System.out.print(nodes + ",");
        System.out.println(edges);

        Graph graphVector = new Graph(nodes, edges);

        for (long i = 0; i < edges; i++) {

            String line = bufferedReader.readLine();
            tokenizer = new StringTokenizer(line);
            long v1 = Integer.parseInt(tokenizer.nextToken());
            long v2 = Integer.parseInt(tokenizer.nextToken());
            long weight = Integer.parseInt(tokenizer.nextToken());

            // System.out.print(v1+ " ");
            // System.out.print(v2+ " ");
            // System.out.println(weight);

            graphVector.edges[(int) i] = new Edges(v1 - 1, v2 - 1, weight);
            graphVector.addEdge(v1 - 1, v2 - 1, weight);
            graphVector.addEdge(v2 - 1, v1 - 1, weight); // for an undirected graph

        }

        graphVector.GRASP();
        graphVector.printValues();


        bufferedReader.close();
        fileReader.close();

        System.out.println("The End");

    }
}
