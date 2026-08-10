package Tabu_pfsp_alg;

import java.util.ArrayList;
import java.util.Collections;

class neighbor_area {
    ArrayList<Integer> Pan;
    int[] move = new int[2];
}
class Tabu_search{
    public static long flow_time = 0;
    public static int max = 5;
    public static int iteration = 0;
    public static int tenure = 1;
    public static ArrayList<neighbor_area> swap(ArrayList<Integer> list, int j){
        ArrayList<neighbor_area> temp = new ArrayList<>();
        for(int i = 0; i < j - 1; i++){
            for(int z = i + 1; z < j; z++ ){
                ArrayList<Integer> temp2 = new ArrayList<>();
                temp2.addAll(list);
                Collections.swap(temp2, i, z);
                neighbor_area n =  new neighbor_area();
                n.Pan = temp2;
                n.move[0] = i;
                n.move[1] = z;
                temp.add(n);

            }
        }
        return temp;
    }
    public static void run_algorithm(int j, int m, int[][] times_set){
        ArrayList<Integer> current_list = new ArrayList<>();
        for(int i = 0; i < j; i++){
            current_list.add(i + 1);
        }
        Collections.shuffle(current_list);
        long current_span = computing(current_list, m, times_set);

        int[][] tabu_list = new int[j][j];
        neighbor_area n = new neighbor_area();
        ArrayList<Integer> b_cost = new ArrayList<>();
        while(iteration < max){
            long best_span = Long.MAX_VALUE;
            ArrayList<neighbor_area> area_list = swap(current_list,j);
            for(neighbor_area area: area_list){
                long cost = computing(area.Pan, m, times_set);
                boolean found = tabu_list[area.move[0]][area.move[1]] > 0;

                if(found && (cost >= current_span)){
                    continue;
                }
                if(cost < best_span){
                    best_span = cost;
                    n = area;
                }
            }
            current_list = n.Pan;
            current_span = best_span;
            for(int i = 0; i < j; i++){
                for(int p = 0; p < j; p++){
                    if(tabu_list[i][p] > 0){
                        tabu_list[i][p] -= 1;
                    }
                }
            }
            tabu_list[n.move[0]][n.move[1]] = tenure;
            if(current_span < best_span){
                b_cost =  new ArrayList<>(current_list);
                best_span = current_span;
                computing(b_cost, m, times_set);
            }
            System.out.println("flowtime - " + flow_time);
            System.out.println("Makespan - " + best_span);
            iteration++;
        }

    }

    public static long computing(ArrayList<Integer> array_list, int m,int[][] times_set){
        int[][] matrix = new int[m][array_list.size()];
        for(int i = 0; i < m; i++){
            for(int j = 0; j < array_list.size(); j++){
                int k = array_list.get(j) - 1;
                if(i == 0 && j == 0){
                    matrix[i][j] = times_set[i][k];
                }else if(i == 0){
                    matrix[i][j] = matrix[i][j - 1] + times_set[i][k];
                }else if(j == 0){
                    matrix[i][j] = matrix[i - 1][j] + times_set[i][k];
                }else{
                    matrix[i][j] = Math.max(matrix[i - 1][j],matrix[i][j - 1]) + times_set[i][k];
                }
            }
        }
        long time = 0;
        for(int i = 0; i < array_list.size(); i++){
            time += matrix[m - 1][i];
        }
        flow_time = time;
        return matrix[m - 1][array_list.size() - 1];
    }
}