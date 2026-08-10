package LNS_cvrp_alg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

// NODE_COORD_SECTION
class Node{
    int index;
    double x;
    double y;
    public Node(int i,double x,double y){
        this.index=i;
        this.x=x;
        this.y=y;
    }
}
public class LNS {
    private static int dimension;
    private static int capacity;
    private static int Number_trucks = 28;
    private static double[][] distanceMatrix;
    private static int[] demand;
    public double temperature_max = 300.0;
    public double temperature_min = 0.01;
    public int iteration = 100;
    public double alpha =  0.95;
    private static Node[] nodes;
    private static int[] visited;
    public long evaluation = 1000000000;
    public long eval_count = 0;
    public double destroy_p = 0.20;
    public double worst_removal_p = 0.50;
    public double shaw_removal_p = 0.25;

    public Random rand = new Random();
    public LNS(int d, int c, int[] dd) {
        dimension=d;
        capacity=c;
        demand=dd;
    }

    public double routeCost(ArrayList<Integer> route){
        double cost = 0;
        for(int i = 0; i < route.size() - 1; i++){
            cost += distanceMatrix[route.get(i)][route.get(i+1)];
        }
        return cost;
    }

    public boolean check_customers(ArrayList<ArrayList<Integer>> solution){
        int[] count = new int[dimension];
        for(ArrayList<Integer> route : solution){
            for(int i = 1; i < route.size() - 1; i++){
                int cust = route.get(i);
                if(cust <= 0 || cust >= dimension) {
                    return false;
                }
                count[cust]++;
            }
        }

        for(int i = 1; i < dimension; i++){
            if(count[i] != 1) return false;
        }
        return true;
    }

    public static void get_distance_cost(){
        distanceMatrix = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    double dx = nodes[i].x - nodes[j].x;
                    double dy = nodes[i].y - nodes[j].y;
                    distanceMatrix[i][j] = Math.round(Math.sqrt(dx * dx + dy * dy));
                }
            }
        }
    }

    private int get_nearest_solution(int current, int load, int[] visited){
        int best_solution = -1;
        double best_distance = Double.MAX_VALUE;
        for(int i=1;i<dimension;i++){
            if(visited[i]==0 && load + demand[i] <= capacity){
                if(distanceMatrix[current][i] < best_distance){
                    best_solution = i;
                    best_distance = distanceMatrix[current][i];
                }
            }
        }
        return best_solution;
    }
    private ArrayList<ArrayList<Integer>> get_initial_solution(){
        ArrayList<ArrayList<Integer>> solution = new ArrayList<>();
        visited = new int[dimension];
        for (int i = 0; i < dimension; i++) visited[i] = 0;
        visited[0] = 1;
        for (int t = 0; t < Number_trucks; t++) {
            ArrayList<Integer> route = new ArrayList<>();
            route.add(0);
            int current_load = 0;
            int current_index = 0;
            while (true) {
                int next = get_nearest_solution(current_index, current_load, visited);
                if (next == -1) break;

                route.add(next);
                visited[next] = 1;
                current_load += demand[next];
                current_index = next;
            }
            route.add(0);
            solution.add(route);
        }
        for (int cust = 1; cust < dimension; cust++) {
            if (visited[cust] == 1) {
                continue;
            }

            int bestRoute = -1;
            int bestPos = -1;
            double bestIncrease = Double.MAX_VALUE;

            for (int r = 0; r < solution.size(); r++) {
                ArrayList<Integer> route = solution.get(r);
                for (int pos = 1; pos < route.size(); pos++) {

                    int load = 0;
                    for (int k = 1; k < route.size() - 1; k++) load += demand[route.get(k)];
                    if (load + demand[cust] > capacity) continue;

                    int prev = route.get(pos - 1);
                    int next = route.get(pos);
                    double oldCost = distanceMatrix[prev][next];
                    double newCost = distanceMatrix[prev][cust] + distanceMatrix[cust][next];
                    double increase = newCost - oldCost;

                    if (increase < bestIncrease) {
                        bestIncrease = increase;
                        bestRoute = r;
                        bestPos = pos;
                    }
                }
            }

            if (bestRoute == -1) {
                throw new RuntimeException("Cannot insert customer " + cust + " into any route under capacity constraints");
            }

            solution.get(bestRoute).add(bestPos, cust);
            visited[cust] = 1;
        }

        return solution;
    }

    public double get_fitness(ArrayList<ArrayList<Integer>> solution){
        eval_count++;
        double fitness = 0;
        for(ArrayList<Integer> route: solution){
            for(int i=0;i<route.size() - 1;i++){
                int x = route.get(i);
                int y = route.get(i+1);
                fitness += distanceMatrix[x][y];
            }
        }
        return fitness;
    }

    public ArrayList<Integer> destroy(ArrayList<ArrayList<Integer>> solution, ArrayList<ArrayList<Integer>> destroyed_solution) {
        ArrayList<int[]> customer_positions = new ArrayList<>();
        for (int r = 0; r < solution.size(); r++) {
            ArrayList<Integer> route = solution.get(r);
            for (int p = 1; p < route.size() - 1; p++) {
                customer_positions.add(new int[]{route.get(p), r, p});
            }
        }

        if (customer_positions.isEmpty()) {
            return new ArrayList<>();
        }
        int total_customers = customer_positions.size();
        int q = Math.max(2, (int)(total_customers * destroy_p));
        q = Math.min(q, total_customers);

        ArrayList<Integer> removed_customers = new ArrayList<>();
        double p = rand.nextDouble();
        if(p < worst_removal_p){
            worst_remove(solution,customer_positions,q,removed_customers);
        } else if (p < worst_removal_p + shaw_removal_p) {
            same_remove(solution,customer_positions,q,removed_customers);
        } else {
            random_remove(customer_positions,q,removed_customers);
        }

        destroyed_solution.clear();
        for (ArrayList<Integer> route : solution) {
            ArrayList<Integer> newRoute = new ArrayList<>();
            for (int cust : route) {
                if (cust == 0 || !removed_customers.contains(cust)) {
                    newRoute.add(cust);
                }
            }
            if (newRoute.size() >= 2) {
                if (newRoute.get(newRoute.size() - 1) != 0) newRoute.add(0);
                if (newRoute.size() > 2 || newRoute.get(1) != 0) {
                    destroyed_solution.add(newRoute);
                }
            }
        }

        return removed_customers;
    }

    public void random_remove(ArrayList<int[]> positions, int q, ArrayList<Integer> removed) {
        ArrayList<int[]> copy = new ArrayList<>(positions);
        for (int i = 0; i < q && !copy.isEmpty(); i++) {
            int idx = rand.nextInt(copy.size());
            removed.add(copy.get(idx)[0]);
            copy.remove(idx);
        }
    }
    public void worst_remove(ArrayList<ArrayList<Integer>> solution, ArrayList<int[]> positions, int q, ArrayList<Integer> removed) {
        ArrayList<double[]> savings = new ArrayList<>();
        for (int[] pos : positions) {
            int customer = pos[0];
            int r = pos[1];
            int p = pos[2];
            ArrayList<Integer> route = solution.get(r);

            double distWith = distanceMatrix[route.get(p-1)][customer] + distanceMatrix[customer][route.get(p+1)];
            eval_count++;
            double distWithout = distanceMatrix[route.get(p-1)][route.get(p+1)];
            eval_count++;
            double saving = distWith - distWithout;
            savings.add(new double[]{customer, saving});
        }

        savings.sort((a, b) -> Double.compare(b[1], a[1]));

        for (int i = 0; i < q && i < savings.size(); i++) {
            if (rand.nextDouble() < 0.8) {
                removed.add((int) savings.get(i)[0]);
            }
        }

        if (removed.size() < q) {
            ArrayList<int[]> copy = new ArrayList<>(positions);
            copy.removeIf(pos -> removed.contains(pos[0]));
            while (removed.size() < q && !copy.isEmpty()) {
                int idx = rand.nextInt(copy.size());
                removed.add(copy.get(idx)[0]);
                copy.remove(idx);
            }
        }
    }

    private void same_remove(ArrayList<ArrayList<Integer>> solution, ArrayList<int[]> positions, int q, ArrayList<Integer> removed) {
        if (positions.isEmpty()) {
            return;
        }

        int seedIdx = rand.nextInt(positions.size());
        int seed = positions.get(seedIdx)[0];
        removed.add(seed);

        ArrayList<double[]> distToSeed = new ArrayList<>();
        for (int[] pos : positions) {
            if (pos[0] != seed) {
                distToSeed.add(new double[]{pos[0], distanceMatrix[seed][pos[0]]});
                eval_count++;
            }
        }
        distToSeed.sort((a, b) -> Double.compare(a[1], b[1]));

        for (int i = 0; i < q - 1 && i < distToSeed.size(); i++) {
            if (rand.nextDouble() < 0.85) {
                removed.add((int) distToSeed.get(i)[0]);
            }
        }

        if (removed.size() < q) {
            ArrayList<int[]> copy = new ArrayList<>(positions);
            copy.removeIf(pos -> removed.contains(pos[0]));
            while (removed.size() < q && !copy.isEmpty()) {
                int idx = rand.nextInt(copy.size());
                removed.add(copy.get(idx)[0]);
                copy.remove(idx);
            }
        }
    }

    public ArrayList<ArrayList<Integer>> repair(ArrayList<ArrayList<Integer>> partialSolution, ArrayList<Integer> removedCustomers) {
        ArrayList<ArrayList<Integer>> newSolution = new ArrayList<>();
        for (ArrayList<Integer> route : partialSolution) {
            newSolution.add(new ArrayList<>(route));
        }

        for (int customer : removedCustomers) {
            double bestCost = Double.MAX_VALUE;
            int bestRoute = -1;
            int bestPosition = -1;

            for (int r = 0; r < newSolution.size(); r++) {
                ArrayList<Integer> route = newSolution.get(r);

                int routeLoad = 0;
                for (int i = 1; i < route.size() - 1; i++) {
                    routeLoad += demand[route.get(i)];
                }
                if (routeLoad + demand[customer] > capacity) continue;

                for (int pos = 1; pos < route.size(); pos++) {
                    double costBefore = routeCost(route);
                    eval_count++;
                    ArrayList<Integer> testRoute = new ArrayList<>(route);
                    testRoute.add(pos, customer);
                    double costAfter = routeCost(testRoute);
                    eval_count++;
                    double delta = costAfter - costBefore;

                    if (delta < bestCost) {
                        bestCost = delta;
                        bestRoute = r;
                        bestPosition = pos;
                    }
                }
            }

            if (bestRoute != -1) {
                newSolution.get(bestRoute).add(bestPosition, customer);
            } else {
                ArrayList<Integer> newRoute = new ArrayList<>();
                newRoute.add(0);
                newRoute.add(customer);
                newRoute.add(0);
                newSolution.add(newRoute);
            }
        }

        newSolution.removeIf(route -> route.size() <= 2);
        return newSolution;
    }


    public ArrayList<ArrayList<Integer>> LNS_CVRP(){
        ArrayList<ArrayList<Integer>> solution = get_initial_solution();
        double cost = get_fitness(solution);

        ArrayList<ArrayList<Integer>> best_solution = new ArrayList<>();
        for(ArrayList<Integer> route: solution){
            for(Integer i: route){
                System.out.print(i +" ");
            }
            System.out.println();
            best_solution.add(new ArrayList<>(route));
        }

        double best_cost = cost;

        double current_temperature = temperature_max;
        while (current_temperature > temperature_min && eval_count < evaluation) {
            for(int i=0;i<iteration && eval_count < evaluation;i++){
                ArrayList<ArrayList<Integer>> new_solution = new ArrayList<>();
                ArrayList<Integer> destroyed_solution = destroy(solution, new_solution);

                if(destroyed_solution.isEmpty()){
                    continue;
                }

                ArrayList<ArrayList<Integer>> repaired_solution = repair(new_solution, destroyed_solution);
                if(!(check_customers(repaired_solution))){
                    continue;
                }

                double new_cost = get_fitness(repaired_solution);
                double cost_difference = new_cost - cost;
                if(cost_difference < 0){
                    solution = repaired_solution;
                    cost = new_cost;
                }else{
                    double p = Math.exp(-1 * cost_difference / current_temperature);
                    if (rand.nextDouble() < p){
                        solution = repaired_solution;
                        cost = new_cost;
                    }
                }
                if(cost < best_cost){
                    best_solution.clear();
                    for(ArrayList<Integer> route: solution){
                        best_solution.add(new ArrayList<>(route));
                    }
                    best_cost = cost;
                }
            }
            current_temperature *= alpha;
            System.out.println(" T - " + current_temperature+ " fitness/cost - " + best_cost);
            //record_result.write_data(best_cost);
        }
        //record_result.write_data(0);
        return best_solution;
    }

    public static void init_node_data(double[][] node_coordinate){
        nodes = new Node[dimension];
        for (int i = 0; i < dimension; i++) {
            nodes[i] = new Node(i + 1, node_coordinate[i][0], node_coordinate[i][1]);
        }
    }
    public static void DataCheck(){
        System.out.println("dimension="+dimension);
        System.out.println("capacity="+capacity);
        System.out.println("number_trucks="+Number_trucks);
        System.out.println("demand="+demand.length);
    }

}

class record_result{
    public static String file_path = "";
    public static String file_name = "";
    public static boolean if_file(){
        if(!(file_name.equals("")) || !(file_path.equals(""))){
            return true;
        }
        return false;
    }
    public static void write_data(double cost){
        if(if_file()){
            try(BufferedWriter w = new BufferedWriter(new FileWriter(file_path,true))) {
                if(cost == 0){
                    w.write("\n");
                    return;
                }
                w.write(cost + "\n");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}