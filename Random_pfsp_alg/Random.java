package Random_pfsp_alg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Rand_algorithm{
    public static int threshold = 30000;
    public static long flow_time = 0;
    public static Random rand_seed;
    public static void run_algorithm(int j, int m, int[][] times_set){
        while(true){
            ArrayList<Integer> rand_list = new ArrayList<>();
            for(int i = 0; i < j; i++){
                rand_list.add(i);
            }
            Collections.shuffle(rand_list, rand_seed);
            long new_set_makespan = computing(rand_list,times_set,m);
            if(new_set_makespan <= threshold){
                System.out.println("flowtime - " + flow_time);
                System.out.println("final makespan - " + new_set_makespan);
                return;
            }

        }
    }

    public static long computing(ArrayList<Integer> rand_list, int[][] times_set, int m){
        long[][] rand_set = new long[rand_list.size() + 1][m + 1];
        for(int i = 0; i <= rand_list.size(); i++){
            rand_set[i][0] = 0;
        }
        for(int j = 0; j <= m; j++){
            rand_set[0][j] = 0;
        }
        for(int k = 1; k <= rand_list.size(); k++){
            int j = rand_list.get(k - 1);
            for(int z = 1; z <= m; z++){
                rand_set[k][z] = Math.max(rand_set[k-1][z], rand_set[k][z-1]) + times_set[z-1][j];
            }
        }
        long time = 0;
        for(int i = 1; i <= rand_list.size(); i++){
            time += rand_set[i][m];
        }
        flow_time = time;
        return rand_set[rand_list.size()][m];

    }

}
