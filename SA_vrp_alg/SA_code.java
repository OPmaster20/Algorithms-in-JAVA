package SA_vrp_alg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// NODE_COORD_SECTION
class Node{
    double x;
    double y;
    public Node(double x,double y){
        this.x=x;
        this.y=y;
    }
}
class SA {
    public static int dimension;
    public static int capacity;
    private static double[][] distanceMatrix;
    private static int[] demand;
    private static double temperature_max = 1000.0;
    private static double temperature_min = 0.01;
    private static int iteration = 1000;
    private static double alpha =  0.995;
    private static Node[] nodes;
    private static double P1 = 0.4;
    private static double P2 = 0.3;

    private static final double PENALTY_OVERLOAD = 1000.0;
    private static final double PENALTY_MISSING = dimension - 1;
    private static final double VEHICLE_COST = 500.0;
    public Random rand = new Random();

    public SA(int d, int c, int[] dd) {
        dimension=d;
        capacity=c;
        demand=dd;
    }

    public double get_route_cost(ArrayList<Integer> route){
        double cost = 0;
        for(int i = 0; i < route.size() - 1; i++){
            cost += distanceMatrix[route.get(i)][route.get(i+1)];
        }
        return cost;
    }

    private int get_route_load(ArrayList<Integer> route) {
        int load = 0;
        for (int i = 1; i < route.size() - 1; i++) {
            load += demand[route.get(i)];
        }
        return load;
    }

    private int[] check_customer_appearances(ArrayList<ArrayList<Integer>> solution) {
        int[] count = new int[dimension - 1];
        for (ArrayList<Integer> route : solution) {
            for (int i = 1; i < route.size() - 1; i++) {
                int cust = route.get(i);
                if (cust > 0 && cust < dimension - 1) {
                    count[cust]++;
                }
            }
        }
        return count;
    }

    private int get_route_overload(ArrayList<Integer> route) {
        int load = get_route_load(route);
        return Math.max(0, load - capacity);
    }

    public static void get_distance_cost(){
        distanceMatrix = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                } else {
                    double dx = Math.abs(nodes[i].x - nodes[j].x);
                    double dy = Math.abs(nodes[i].y - nodes[j].y);
                    distanceMatrix[i][j] = Math.round(dx + dy);
                }
            }
        }
    }

    private int get_nearest_solution(int current, int load, boolean[] visited){
        int best_solution = -1;
        double best_distance = Double.MAX_VALUE;
        for(int i=1;i<dimension - 1;i++){
            if(!visited[i] && load + demand[i] <= capacity){
                if(distanceMatrix[current][i] < best_distance){
                    best_solution = i;
                    best_distance = distanceMatrix[current][i];
                }
            }
        }
        return best_solution;
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

    private ArrayList<ArrayList<Integer>> clean_empty_route(ArrayList<ArrayList<Integer>> solution) {
        ArrayList<ArrayList<Integer>> cleaned = new ArrayList<>();
        for (ArrayList<Integer> route : solution) {
            if (route.size() > 2) {
                cleaned.add(route);
            }
        }
        if(cleaned.isEmpty()){
            return solution;
        }
        return cleaned;
    }

    public ArrayList<Integer> two_opt(ArrayList<Integer> route){
        double bestCost = get_route_cost(route);
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

                double newCost = get_route_cost(newRoute);
                if(newCost < bestCost){
                    route = newRoute;
                    bestCost = newCost;
                }
            }
        }

        return route;

    }

    private ArrayList<ArrayList<Integer>> get_initial_solution(){
        ArrayList<ArrayList<Integer>> solution = new ArrayList<>();
        boolean[] visited = new boolean[dimension - 1];
        visited[0] = true;

        while (true) {
            int start = -1;
            for (int i = 1; i < dimension - 1; i++) {
                if (!visited[i]) {
                    start = i;
                    break;
                }
            }
            if (start == -1) break;

            ArrayList<Integer> route = new ArrayList<>();
            route.add(0);
            int current_load = 0;
            int current = 0;

            int first = get_nearest_solution(0, current_load, visited);
            if (first == -1) {
                double minDist = Double.MAX_VALUE;
                for (int i = 1; i < dimension - 1; i++) {
                    if (!visited[i] && distanceMatrix[0][i] < minDist) {
                        minDist = distanceMatrix[0][i];
                        first = i;
                    }
                }
            }

            route.add(first);
            visited[first] = true;
            current_load += demand[first];
            current = first;

            while (true) {
                int next = get_nearest_solution(current, current_load, visited);
                if (next == -1) break;
                route.add(next);
                visited[next] = true;
                current_load += demand[next];
                current = next;
            }
            route.add(0);
            solution.add(route);
        }

        return solution;
    }



    private double get_fitness(ArrayList<ArrayList<Integer>> solution){
        double totalDistance = 0;
        double totalOverload = 0;
        int vehicleCount = solution.size();

        for (ArrayList<Integer> route : solution) {
            totalDistance += get_route_cost(route);
            totalOverload += get_route_overload(route);
        }

        int[] appearances = check_customer_appearances(solution);
        int missingCount = 0;
        int duplicateCount = 0;
        for (int i = 1; i < dimension - 1; i++) {
            if (appearances[i] == 0) missingCount++;
            else if (appearances[i] > 1) duplicateCount += (appearances[i] - 1);
        }

        return totalDistance + VEHICLE_COST * vehicleCount + PENALTY_OVERLOAD * totalOverload + PENALTY_MISSING * (missingCount + duplicateCount);
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
        }else {
            // 2-opt
            if(rand.nextBoolean()){
                ArrayList<Integer> optimized_route_a = two_opt(route_a);
                new_solution.set(random_a, optimized_route_a);
            }else{
                ArrayList<Integer> optimized_route_b = two_opt(route_b);
                new_solution.set(random_b, optimized_route_b);
            }
        }

        return clean_empty_route(new_solution);

    }

    public ArrayList<ArrayList<Integer>> SA_VRP(){
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
        System.out.println("initial number of trucks: " + solution.size() + " total vehicle distance:" + (cost - VEHICLE_COST * solution.size()) + " cost:" + best_cost);
        double current_temperature = temperature_max;
        while (current_temperature > temperature_min) {
            for(int i=0;i<iteration;i++){
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
            double current_distance = best_cost - VEHICLE_COST * best_solution.size() - calculate_penalty(best_solution);
            System.out.println(" T - " + current_temperature+ " N_trucks - " + best_solution.size() + " total vehicle distance - " + current_distance + " fitness/cost - " + best_cost);
        }
        return best_solution;
    }

    private double calculate_penalty(ArrayList<ArrayList<Integer>> solution) {
        double overload = 0;
        for (ArrayList<Integer> route : solution) {
            overload += get_route_overload(route);
        }
        int[] appearances = check_customer_appearances(solution);
        int missing = 0;
        int duplicate = 0;
        for (int i = 1; i < dimension - 1; i++) {
            if (appearances[i] == 0) missing++;
            else if (appearances[i] > 1) duplicate += (appearances[i] - 1);
        }
        return PENALTY_OVERLOAD * overload + PENALTY_MISSING * (missing + duplicate);
    }

    public void init_node_data(double[][] node_coordinate){
        nodes = new Node[dimension];
        for (int i = 0; i < dimension; i++) {
            nodes[i] = new Node(node_coordinate[i][0], node_coordinate[i][1]);
        }
    }

    public static void DataCheck(){
        System.out.println("dimension="+dimension);
        System.out.println("capacity="+capacity);
        System.out.println("demand="+demand.length);
    }

    public void validateSolution(ArrayList<ArrayList<Integer>> solution) {
        // 1. 检查客户覆盖
        int[] appearances = check_customer_appearances(solution);
        List<Integer> missing = new ArrayList<>();
        List<Integer> duplicate = new ArrayList<>();

        for (int i = 1; i <= 199; i++) {
            if (appearances[i] == 0) {
                missing.add(i);
            } else if (appearances[i] > 1) {
                duplicate.add(i);
            }
        }

        if (!missing.isEmpty()) {
            System.out.println("❌ 缺失客户(" + missing.size() + "个): " + missing);
        }
        if (!duplicate.isEmpty()) {
            System.out.println("❌ 重复客户(" + duplicate.size() + "个): " + duplicate);
        }
        if (missing.isEmpty() && duplicate.isEmpty()) {
            System.out.println("✅ 所有客户恰好访问一次");
        }

        // 2. 检查容量约束
        boolean overloaded = false;
        for (int v = 0; v < solution.size(); v++) {
            ArrayList<Integer> route = solution.get(v);
            int load = get_route_load(route);
            if (load > capacity) {
                System.out.println("❌ 车辆" + (v+1) + "超载: " + load + "/" + capacity);
                overloaded = true;
            }
        }
        if (!overloaded) {
            System.out.println("✅ 所有车辆满足容量约束");
        }

        // 3. 输出详细统计
        System.out.println("\n车辆负载详情:");
        for (int v = 0; v < solution.size(); v++) {
            ArrayList<Integer> route = solution.get(v);
            int load = get_route_load(route);
            double dist = get_route_cost(route);
            System.out.printf("车辆%2d: %2d个客户, 负载=%3d, 距离=%6.0f\n",
                    v+1, route.size()-2, load, dist);
        }
    }

}


class Read_data{
    String file_path = "src/task6_data/VRP_data.txt";
    int dimension = 201;
    int capacity = 100;
    int[] demand;
    double[][] node_coordinate;
    public void read_data(){
        int d = dimension;
        double[][] nd = new double[d][2];
        int[] dd = new int[d - 1];
        try{
            BufferedReader br = new BufferedReader(new FileReader(this.file_path));
            String line;
            int index = 0;
            int i = 0;
            int j = 0;
            while ((line = br.readLine()) != null){
                if(index >= 2){
                    nd[i][0] =  Double.parseDouble(line.split(" ")[0]);
                    nd[i][1] =  Double.parseDouble(line.split(" ")[1]);
                    i++;
                }
                if(index >= 3){
                    dd[j] =  Integer.parseInt(line.split(" ")[2]);
                    j++;
                }
                index++;
            }
            demand = dd;
            node_coordinate = nd;

        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


public class SA_code {
    public static void main(String[] args) {
        Read_data read_data = new Read_data();
        read_data.read_data();
        SA sa = new SA(read_data.dimension, read_data.capacity, read_data.demand);
        sa.init_node_data(read_data.node_coordinate);
        sa.DataCheck();
        sa.get_distance_cost();
        ArrayList<ArrayList<Integer>> solution = sa.SA_VRP();
        System.out.println(solution.size());
        for(int i=0;i<solution.size();i++){
            for(int j=0;j<solution.get(i).size();j++){
                System.out.print(solution.get(i).get(j) + " ");
            }
            System.out.println();
        }
        sa.validateSolution(solution);
    }
}



