import java.util.ArrayList;

public class Graph {

    ArrayList<ArrayList<Integer>> matrix;

    Graph(int size, ArrayList<Node> nodes){

        matrix = new ArrayList<>();

        for(int i = 0; i < size; i++){

            matrix.add(new ArrayList<>());

        }

        for(int i = 0; i < nodes.size(); i++){

            matrix.get(nodes.get(i).start).add(nodes.get(i).end);

        }


    }

}
