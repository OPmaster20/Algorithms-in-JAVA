package A_star_alg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

class point{
    public int node;
    public double current_cost;
    public double heuristic_cost;
    public double total_cost;
    public point parent_node;
}
class algorithm {
    // number of row and col
    public static int row = 50;
    public static int col = 50;
    // coordinate
    public static ArrayList<int[]> coordinate = new ArrayList<>();
    // define start A_star_alg.point and final A_star_alg.point
    public static int start_point = 0;
    public static int final_point = 26;
    // map
    public static double[][] map = new double[row][col];
    // Manhattan function
    public static int manhattan_fun(int a, int b){
        return Math.abs(coordinate.get(a)[0] - coordinate.get(b)[0]) + Math.abs(coordinate.get(a)[1] - coordinate.get(b)[1]);
    }
    // Euclidean function
    public static double euclidean_fun(int a, int b){
        return Math.sqrt((coordinate.get(a)[0] - coordinate.get(b)[0]) * (coordinate.get(a)[0] - coordinate.get(b)[0]) + (coordinate.get(a)[1] - coordinate.get(b)[1]) * (coordinate.get(a)[1] - coordinate.get(b)[1]));
    }
    // Check if current node in close-set
    public static boolean check_close_list(ArrayList<point> tmp_set, int node_id){
        for(point p: tmp_set){
            if(p.node == node_id){
                return true;
            }
        }
        return false;
    }
    //
    public static point check_open_list(ArrayList<point> list, int id){
        for (point p: list){
            if(p.node == id){
                return p;
            }
        }
        return null;
    }
    // Get neighbor node from open-set
    public static ArrayList<Integer> get_neighbor_node(int node_id){
        ArrayList<Integer> neighbor = new ArrayList<>();
        for(int i = 0; i < col;i++){
            // check the values > 0.0
            if(map[node_id][i] > 0){
                neighbor.add(i);
            }
        }
        return neighbor;
    }

    public static void start(){
        point start_node = new point();
        start_node.node = start_point;

        point end_node = new point();
        end_node.node = final_point;

        start_node.current_cost = 0;
        start_node.heuristic_cost = manhattan_fun(start_node.node, end_node.node);
        start_node.total_cost = start_node.current_cost + start_node.heuristic_cost;

        ArrayList<point> open_list = new ArrayList<>();
        ArrayList<point> close_list = new ArrayList<>();

        open_list.add(start_node);

        while (! open_list.isEmpty()){
            point get_current_node = open_list.get(0);
            for(point nodes: open_list){
                if (get_current_node.total_cost > nodes.total_cost){
                    get_current_node = nodes;
                }
            }
            if(get_current_node.node == end_node.node){
                System.out.println("We found final node");
                print_path(get_current_node);
                return;
            }
            open_list.remove(get_current_node);
            close_list.add(get_current_node);
            for (int i: get_neighbor_node(get_current_node.node)){
                double cost = map[get_current_node.node][i];
                double new_current_cost = get_current_node.current_cost + cost;
                if (check_close_list(close_list, i)){
                    continue;
                }

                point neighbor_node = check_open_list(open_list,i);

                if(neighbor_node == null){
                    neighbor_node = new point();
                    neighbor_node.node = i;
                    neighbor_node.current_cost = new_current_cost;
                    neighbor_node.heuristic_cost = manhattan_fun(neighbor_node.node, end_node.node);
                    neighbor_node.total_cost = neighbor_node.current_cost + neighbor_node.heuristic_cost;
                    open_list.add(neighbor_node);
                    neighbor_node.parent_node = get_current_node;
                } else if (new_current_cost < neighbor_node.current_cost) {
                    neighbor_node.current_cost = new_current_cost;
                    neighbor_node.total_cost = new_current_cost + neighbor_node.heuristic_cost;
                    neighbor_node.parent_node = get_current_node;

                }


            }
        }
        System.out.println("No path");

    }
    // Print path
    public static void print_path(point final_node){
        ArrayList<Integer> path = new ArrayList<>();
        while (final_node != null){
            path.add(final_node.node + 1);
            final_node = final_node.parent_node;
        }
        for (int i = path.size() - 1; i >= 0; i--){
            System.out.print(path.get(i));
            System.out.println();
        }
    }

    // Read data from the txt file and Initialize map
    public static void init_map() throws IOException {
        String path = "src/task1_data/benchmark.txt";
        List<String> lines = Files.readAllLines(Path.of(path));
        for(String s: lines){
            String[] p = s.strip().split("\t");
            if (p.length > 1){
                coordinate.add(new int[]{Integer.parseInt(p[0]),Integer.parseInt(p[1])});
            }else {
                p = s.strip().split(" ");
                coordinate.add(new int[]{Integer.parseInt(p[0]),Integer.parseInt(p[1])});
            }

        }
        for (int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                map[i][j] = 0;
            }
        }

        String path2 = "src/task1_data/rounts.txt";
        lines = Files.readAllLines(Path.of(path2));
        for(String s: lines){
            String[] p = s.strip().split(" ");
            if (p.length > 2){
                map[Integer.parseInt(p[0]) - 1][Integer.parseInt(p[1]) - 1] = Double.parseDouble(p[2]);
            }
        }
    }
}


// run code
public class A_star{
    public static void main(String[] args) throws IOException {
        algorithm.init_map();
        for (int i = 0; i < algorithm.row; i++) {
            for (int j = 0; j < algorithm.col; j++){
                System.out.print(algorithm.map[i][j]);
                System.out.print(" ");
            }
            System.out.print('\n');
        }
        long start = System.nanoTime();
        algorithm.start();
        long end = System.nanoTime();
        double timeMs = (end - start) / 1_000_000.0;

        System.out.println("runtime - " + timeMs + " ms");

    }
}
