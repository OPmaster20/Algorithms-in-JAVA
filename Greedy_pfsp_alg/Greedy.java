package Greedy_pfsp_alg;

import java.util.ArrayList;

class Greedy_algorithm{
    public static long flow_time = 0;
    public static void run_algorithm(int j, int m, int[][] times_set){
        ArrayList<Integer> set = new ArrayList<>();
        for(int i = 0; i < j; i++){
            set.add(i);
        }
        long current_makespan = 0;
        ArrayList<Integer> current_list = new ArrayList<>();
        while(!set.isEmpty()){
            int best_values = 0;
            long best_makespan = Long.MAX_VALUE;
            for(int k = 0; k < set.size(); k++){
                ArrayList<Integer> tmp_makespan = new ArrayList<>(current_list);
                tmp_makespan.add(set.get(k));
                long makespan = computing(tmp_makespan,times_set,m);
                if(makespan < best_makespan){
                    best_makespan = makespan;
                    best_values = set.get(k);
                }
            }
            current_list.add(best_values);
            set.remove(Integer.valueOf(best_values));
            current_makespan = best_makespan;
        }
        System.out.println("flowtime - " + flow_time);
        System.out.println("Makespan - " + current_makespan);
    }

    public static long computing(ArrayList<Integer> array, int[][] times_set, int m){
        long[][] tmp_set = new long[array.size()][m];
        for(int i = 0; i < array.size(); i++){
            int j = array.get(i);
            for(int k = 0; k < m; k++){
                if(i == 0 && k == 0){
                    tmp_set[i][k] = times_set[k][j];
                }else if(i == 0){
                    tmp_set[i][k] = tmp_set[i][k - 1] + times_set[k][j];
                } else if (k == 0) {
                    tmp_set[i][k] = tmp_set[i - 1][k] + times_set[k][j];
                } else{
                    tmp_set[i][k] = Math.max(tmp_set[i - 1][k], tmp_set[i][k - 1]) + times_set[k][j];
                }
            }
        }
        long time = 0;
        for(int i = 0; i < array.size(); i++){
            time += tmp_set[i][m - 1];
        }
        flow_time = time;
        return tmp_set[array.size() - 1][m - 1];
    }
}
