package SA_cvrp_alg;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
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
public class SA {
    private static int dimension;
    private static int capacity;
    private static int Number_trucks = 7;
    private static double[][] distanceMatrix;
    private static int[] demand;
    public double temperature_max = 1000.0;
    public double temperature_min = 0.1;
    public int iteration = 500;
    public double alpha =  0.995;
    private static Node[] nodes;
    private static int[] visited;
    public static double P1 = 0.3;
    public static double P2 = 0.3;
    public long evaluation = 10000000;
    public long eval_count = 0;
    public Random rand = new Random();
    public SA(int d, int c, int[] dd) {
        dimension=d;
        capacity=c;
        demand=dd;
    }

    public ArrayList<ArrayList<Integer>> cross_recombination(ArrayList<Integer> route_a, ArrayList<Integer> route_b) {
        ArrayList<Integer> original_a = new ArrayList<>(route_a);
        ArrayList<Integer> original_b = new ArrayList<>(route_b);

        if (route_a.size() <= 3 || route_b.size() <= 3) {
            ArrayList<ArrayList<Integer>> set = new ArrayList<>();
            set.add(original_a);
            set.add(original_b);
            return set;
        }

        // Calculate the extraction length
        int lenA = route_a.size() - 2;
        int lenB = route_b.size() - 2;
        int n = Math.min(lenA, lenB) / 2;
        if (n < 1) {
            n = 1;
        }

        // Randomly select sub-paths
        int startA = rand.nextInt(lenA - n + 1) + 1;
        ArrayList<Integer> segA = new ArrayList<>();
        for (int i = 0; i < n; i++) segA.add(route_a.get(startA + i));

        int startB = rand.nextInt(lenB - n + 1) + 1;
        ArrayList<Integer> segB = new ArrayList<>();
        for (int i = 0; i < n; i++) segB.add(route_b.get(startB + i));

        // Merge subpaths
        ArrayList<Integer> merged = new ArrayList<>();
        merged.add(0);
        merged.addAll(segA);
        merged.addAll(segB);
        merged.add(0);

        if (!check_capacity(merged)) {
            ArrayList<ArrayList<Integer>> set = new ArrayList<>();
            set.add(original_a);
            set.add(original_b);
            return set;
        }

        merged = two_opt(merged);

        // Build the remaining path
        ArrayList<Integer> remaining = new ArrayList<>();
        remaining.add(0);
        for (int i = 1; i < route_a.size() - 1; i++) {
            int cust = route_a.get(i);
            if (!segA.contains(cust)) remaining.add(cust);
        }
        for (int i = 1; i < route_b.size() - 1; i++) {
            int cust = route_b.get(i);
            if (!segB.contains(cust)) remaining.add(cust);
        }
        remaining.add(0);

        // If the remaining path exceeds the capacity, return the original path.
        if (!check_capacity(remaining)) {
            ArrayList<ArrayList<Integer>> set = new ArrayList<>();
            set.add(original_a);
            set.add(original_b);
            return set;
        }

        if (remaining.size() > 3) remaining = two_opt(remaining);

        // Compare total costs
        double costNew = routeCost(merged) + routeCost(remaining);
        double costOld = routeCost(route_a) + routeCost(route_b);

        ArrayList<ArrayList<Integer>> set = new ArrayList<>();
        if (costNew < costOld) {
            set.add(merged);
            set.add(remaining);
        } else {
            set.add(original_a);
            set.add(original_b);
        }

        return set;
    }

    public boolean check_customers(ArrayList<ArrayList<Integer>> solution){
        boolean[] visited = new boolean[dimension];
        visited[0] = true;

        int countVisited = 0;
        for (ArrayList<Integer> route : solution) {
            if (route.size() < 2){
                return false;
            }
            if (route.get(0) != 0 || route.get(route.size() - 1) != 0){
                return false;
            }
            for (int i = 1; i < route.size() - 1; i++) {
                int cust = route.get(i);
                if (cust <= 0 || cust >= dimension){
                    return false;
                }
                if (cust == 0){
                    return false;
                }
                if (visited[cust]){
                    return false;
                }
                visited[cust] = true;
                countVisited++;
            }
        }

        return countVisited == dimension - 1;
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

    public Boolean check_capacity(ArrayList<Integer> route){
        int current_route_load = 0;
        for(int i=1;i<route.size()-1;i++){
            current_route_load += demand[route.get(i)];
            if(current_route_load > capacity){
                return false;
            }
        }
        return true;
    }

    public ArrayList<ArrayList<Integer>> Similarity_swap_operator(ArrayList<Integer> route_a, ArrayList<Integer> route_b) {
        ArrayList<Integer> original_a = new ArrayList<>(route_a);
        ArrayList<Integer> original_b = new ArrayList<>(route_b);
        if (route_a.size() <= 3 || route_b.size() <= 3) {
            ArrayList<ArrayList<Integer>> set = new ArrayList<>();
            set.add(original_a);
            set.add(original_b);
            return set;
        }

        // Remove the most costly customer from either of the two paths.
        int maxCostNodeA = -1;
        int maxCostNodeB = -1;
        double maxCostA = Double.MIN_VALUE;
        double maxCostB = Double.MIN_VALUE;
        for (int i = 1; i < route_a.size() - 1; i++) {
            int prev = route_a.get(i - 1);
            int curr = route_a.get(i);
            int next = route_a.get(i + 1);
            double removalCost = distanceMatrix[prev][curr] + distanceMatrix[curr][next] - distanceMatrix[prev][next];
            if (removalCost > maxCostA) {
                maxCostA = removalCost;
                maxCostNodeA = i;
            }
        }

        for (int i = 1; i < route_b.size() - 1; i++) {
            int prev = route_b.get(i - 1);
            int curr = route_b.get(i);
            int next = route_b.get(i + 1);
            double removalCost = distanceMatrix[prev][curr] + distanceMatrix[curr][next] - distanceMatrix[prev][next];
            if (removalCost > maxCostB) {
                maxCostB = removalCost;
                maxCostNodeB = i;
            }
        }
        if (maxCostNodeA == -1 || maxCostNodeB == -1) {
            ArrayList<ArrayList<Integer>> set = new ArrayList<>();
            set.add(original_a);
            set.add(original_b);
            return set;
        }

        int removedCustomerA = route_a.get(maxCostNodeA);
        int removedCustomerB = route_b.get(maxCostNodeB);
        route_a.remove(maxCostNodeA);
        route_b.remove(maxCostNodeB);

        // Randomly exchange remaining customers
        int routeALength = route_a.size() - 2;
        int routeBLength = route_b.size() - 2;
        int n = Math.min(routeALength, routeBLength) / 2;
        if (n == 0 && routeALength >= 1 && routeBLength >= 1) {
            n = 1;
        }

        if (n > 0) {
            ArrayList<Integer> indicesA = new ArrayList<>();
            ArrayList<Integer> indicesB = new ArrayList<>();

            for (int i = 1; i <= routeALength; i++) {
                indicesA.add(i);
            }
            for (int i = 1; i <= routeBLength; i++) {
                indicesB.add(i);
            }

            Collections.shuffle(indicesA, rand);
            Collections.shuffle(indicesB, rand);

            for (int i = 0; i < n; i++) {
                int posA = indicesA.get(i);
                int posB = indicesB.get(i);
                int temp = route_a.get(posA);
                route_a.set(posA, route_b.get(posB));
                route_b.set(posB, temp);
            }
        }

        // Optimal position for re-insertion of removed customer
        int bestPosA = findBestInsertionPosition(route_b, removedCustomerA);
        route_b.add(bestPosA, removedCustomerA);
        int bestPosB = findBestInsertionPosition(route_a, removedCustomerB);
        route_a.add(bestPosB, removedCustomerB);

        if (!check_capacity(route_a) || !check_capacity(route_b)) {
            ArrayList<ArrayList<Integer>> set = new ArrayList<>();
            set.add(original_a);
            set.add(original_b);
            return set;
        }

        ArrayList<ArrayList<Integer>> set = new ArrayList<>();
        set.add(route_a);
        set.add(route_b);
        return set;
    }

    public ArrayList<ArrayList<Integer>> relocate(ArrayList<Integer> route_a, ArrayList<Integer> route_b){
        int position_a;
        int position_b;
        while (true){
            position_a = rand.nextInt(route_a.size() - 2) + 1;
            position_b = rand.nextInt(route_b.size() - 2) + 1;
            if(position_a != 0 && position_a != route_a.size()-1){
                if(position_b != 0 && position_b != route_b.size()-1){
                    break;
                }
            }
        }

        int customer = route_a.get(position_a);

        route_a.remove(position_a);
        route_b.add(position_b, customer);
        ArrayList<ArrayList<Integer>> set = new ArrayList<>();
        set.add(route_a);
        set.add(route_b);
        return set;
    }

    private int findBestInsertionPosition(ArrayList<Integer> route, int customer){
        int bestPos = 1;
        double minIncrease = Double.MAX_VALUE;

        for(int i = 1; i < route.size(); i++){
            int prev = route.get(i - 1);
            int next = route.get(i);
            double increase = distanceMatrix[prev][customer] + distanceMatrix[customer][next] - distanceMatrix[prev][next];

            if(increase < minIncrease){
                minIncrease = increase;
                bestPos = i;
            }
        }

        return bestPos;
    }

    public ArrayList<ArrayList<Integer>> swap(ArrayList<Integer> route_a, ArrayList<Integer> route_b){
        int Rn1 = rand.nextInt(route_a.size() - 2) + 1;
        int Rn2 = rand.nextInt(route_b.size() - 2) + 1;
        Integer tmp = route_a.get(Rn1);
        route_a.set(Rn1, route_b.get(Rn2));
        route_b.set(Rn2, tmp);

        ArrayList<ArrayList<Integer>> set = new ArrayList<>();
        set.add(route_a);
        set.add(route_b);
        return set;
    }

    public ArrayList<Integer> two_opt(ArrayList<Integer> route){
        double bestCost = routeCost(route);
        for(int i = 1; i < route.size() - 2; i++){
            for(int j = i + 1; j < route.size() - 1; j++){
                ArrayList<Integer> newRoute = new ArrayList<>(route);
                int a = i, b = j;
                while(a < b){
                    int temp = newRoute.get(a);
                    newRoute.set(a, newRoute.get(b));
                    newRoute.set(b, temp);
                    a++;
                    b--;
                }

                double newCost = routeCost(newRoute);
                if(newCost < bestCost){
                    route = newRoute;
                    bestCost = newCost;
                }
            }
        }
        return route;

    }
    public double routeCost(ArrayList<Integer> route){
        double cost = 0;
        for(int i = 0; i < route.size() - 1; i++){
            cost += distanceMatrix[route.get(i)][route.get(i+1)];
        }
        return cost;
    }


    public ArrayList<ArrayList<Integer>> find_neighbors(ArrayList<ArrayList<Integer>> init_solution){
        ArrayList<ArrayList<Integer>> new_solution = new ArrayList<>();
        ArrayList<Integer> route_a;
        ArrayList<Integer> route_b;
        for(ArrayList<Integer> route: init_solution){
            new_solution.add(new ArrayList<>(route));
        }
        int random_a;
        int random_b;
        while (true){
            random_a = rand.nextInt(new_solution.size());
            random_b = rand.nextInt(new_solution.size());
            if(random_a!=random_b){
                break;
            }
        }

        route_a = new_solution.get(random_a);
        route_b = new_solution.get(random_b);
        if(route_a.size() <= 2 ||  route_b.size() <= 2){
            return new_solution;
        }

        double p = rand.nextDouble();
        if(p < P1){
            ArrayList<ArrayList<Integer>> set = swap(route_a, route_b);
            new_solution.set(random_a, set.get(0));
            new_solution.set(random_b, set.get(1));
        }else if(p < P2 + P1){
            ArrayList<ArrayList<Integer>> set = relocate(route_a, route_b);
            new_solution.set(random_a, set.get(0));
            new_solution.set(random_b, set.get(1));
        }else{
            // 2-opt
            if(rand.nextBoolean()){
                ArrayList<Integer> optimized_route_a = two_opt(route_a);
                new_solution.set(random_a, optimized_route_a);
            }else{
                ArrayList<Integer> optimized_route_b = two_opt(route_b);
                new_solution.set(random_b, optimized_route_b);
            }
        }


        if (check_capacity(route_b) && check_capacity(route_a) && check_customers(new_solution)){
            return new_solution;
        }
        return init_solution;
    }

    public ArrayList<ArrayList<Integer>> SA_CVRP(){
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
                ArrayList<ArrayList<Integer>> new_solution = find_neighbors(solution);
                double new_cost = get_fitness(new_solution);
                double cost_difference = new_cost - cost;
                if(cost_difference < 0){
                    solution = new_solution;
                    cost = new_cost;
                }else{
                    double p = Math.exp(-1 * cost_difference / current_temperature);
                    if (rand.nextDouble() < p){
                        solution = new_solution;
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
            record_result.write_data(best_cost);
        }
        record_result.write_data(0);
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
