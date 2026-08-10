package GA_pfsp_alg;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

class GA_algorithm{
    // size of population
    public static int size = 100;
    // crossover
    public static double Px = 0.7;
    // mutation
    public static double Pm = 0.3;
    public static int max_gen = 100;
    public static int tour = 5;
    public static long flow_time = 0;
    public static Random rand_seed;


    // Initialize population
    public static ArrayList<int[]> init_pop(int N_jobs){
        ArrayList<int[]> pop_list = new ArrayList<>();
        ArrayList<Integer> tmp = new ArrayList<>();
        for(int j = 0; j < size; j++){
            int[] list = new int[N_jobs];
            for (int i = 0; i < N_jobs; i++) {
                tmp.add(i + 1);
            }
            Collections.shuffle(tmp,rand_seed);
            for(int i = 0; i < N_jobs; i++){
                list[i] = tmp.get(i);
            }
            pop_list.add(list);
            tmp.clear();

        }

        return pop_list;
    }
    // Calculate fitness
    public static double computing(int[][] matrix, int[] array){
        int[][] tmp_matrix = new int[matrix.length][matrix[0].length];
        for(int i = 0; i < matrix.length; i++){
            for(int z = 0; z < matrix[0].length; z++){
                int k = array[z] - 1;
                if(i == 0 && z == 0){
                    tmp_matrix[i][z] = matrix[i][k];
                } else if (i == 0) {
                    tmp_matrix[i][z] = tmp_matrix[i][z - 1] + matrix[i][k];
                } else if (z == 0) {
                    tmp_matrix[i][z] = tmp_matrix[i - 1][z] + matrix[i][k];
                } else {
                    tmp_matrix[i][z] = Math.max(tmp_matrix[i - 1][z],tmp_matrix[i][z - 1]) + matrix[i][k];
                }

            }
        }
        long time = 0;
        for(int i = 0; i < array.length; i++){
            time += tmp_matrix[matrix.length - 1][i];
        }
        flow_time = time;
        return computing_fitness(tmp_matrix[matrix.length - 1][matrix[0].length - 1]);
    }

    public static double computing_fitness(int k){
        return 1.0 / k;
    }


    // Selection
    public static int[] selection(ArrayList<int[]> pop_list, int k,int[][] times_set){
        int index = rand_seed.nextInt(pop_list.size());
        double fit_value = computing(times_set,pop_list.get(index));
        for(int i = 1; i < k; i++){
            int new_index = rand_seed.nextInt(pop_list.size());
            double new_fit_value = computing(times_set,pop_list.get(new_index));
            if (fit_value < new_fit_value){
                index = new_index;
                fit_value = new_fit_value;
            }
        }
        return pop_list.get(index);

    }

    public static boolean If_contains(int[] pop_arr, int j){
        for(int i = 0; i < pop_arr.length; i++){
            if(pop_arr[i] == j){
                return true;
            }
        }
        return false;
    }

    public static boolean If_empty(int[] pop_arr){
        for (int i = 0; i < pop_arr.length; i++){
            if(pop_arr[i] == 0){
                return true;
            }
        }
        return false;
    }
    // Cross
    public static int[] cross(int[] parent1,int[] parent2){
        int[] child = new int[parent1.length];
        for(int i = 0; i < parent1.length; i++){
            child[i] = 0;
        }
        int c1;
        int c2;
        while (true) {
            c1 = rand_seed.nextInt(parent1.length);
            c2 = rand_seed.nextInt(parent2.length);
            if(c1 != c2){
                if(c1 > c2){
                    int tmp = c2;
                    c2 = c1;
                    c1 = tmp;
                    break;
                }
            }
        }
        for(int i = c1; i <= c2; i++){
            child[i] = parent1[i];
        }
        int index = (c2 + 1) % parent2.length;
        int scan = (c2 + 1) % parent2.length;

        while (If_empty(child)) {

            if(!(If_contains(child, parent2[scan]))){
                while (child[index] != 0) {
                    index = (index + 1) % parent1.length;
                }
                child[index] = parent2[scan];
                index = (index + 1) % parent1.length;
            }
            scan =  (scan + 1) % parent2.length;

        }
        return child;

    }

    // Mutations
    public static void reverse(int[] pop_list){
        int i;
        int j;
        i = rand_seed.nextInt(pop_list.length);
        j = rand_seed.nextInt(pop_list.length);

        while(i < j){
            int tmp = pop_list[i];
            pop_list[i] = pop_list[j];
            pop_list[j] = tmp;
            i++;
            j--;
        }

    }

    public static void swap(int[] pop_list){
        int i;
        int j;
        i = rand_seed.nextInt(pop_list.length);
        j = rand_seed.nextInt(pop_list.length);
        int tmp =  pop_list[i];
        pop_list[i] = pop_list[j];
        pop_list[j] = tmp;
    }

    public static void mutate(int[] pop_list){
        if(rand_seed.nextDouble() < Pm){
            swap(pop_list);
        }else {
            reverse(pop_list);
        }
    }

    public static int[] best(ArrayList<int[]> pop_list,int[][] tmp_matrix){
        int[] best = pop_list.get(0);
        double best_value = computing(tmp_matrix,best);
        for(int i = 0; i < pop_list.size(); i++){
            double current_value = computing(tmp_matrix,pop_list.get(i));
            if(current_value > best_value){
                best_value = current_value;
                best = pop_list.get(i);
            }
        }
        return best;

    }

    public static void replace(ArrayList<int[]> pop_list,int[] best,int[][] tmp_matrix){
        double low_fit = computing(tmp_matrix,pop_list.get(0));
        int index = 0;
        for(int i = 0; i < pop_list.size(); i++){
            double current_value = computing(tmp_matrix,pop_list.get(i));
            if(current_value < low_fit){
                low_fit = current_value;
                index = i;
            }
        }
        int[] tmp = new int[best.length];
        for(int i = 0; i < tmp.length; i++){
            tmp[i] = best[i];
        }
        pop_list.set(index, tmp);
    }

    public static void gen_new_pop() throws IOException {
        int[][] times_set = init_data.read_file();
        System.out.println("Number of machines " + init_data.m);
        System.out.println("Number of jobs " + init_data.j);

        ArrayList<int[]> pop = GA_algorithm.init_pop(init_data.j);
        int[] best = best(pop,times_set);
        for(int i = 1; i <= max_gen; i++){
            ArrayList<int[]> new_pop = new ArrayList<>();
            while (new_pop.size() < size){
                int[] p1 = selection(pop, tour, times_set);
                int[] p2 = selection(pop, tour, times_set);
                int[] child;
                if(Math.random() < Px){
                    child = cross(p1,p2);
                }else {
                    child = new int[p1.length];
                    for(int p = 0; p < p1.length; p++){
                        child[p] = p1[p];
                    }
                }
                if(Math.random() < Pm){
                    mutate(child);
                }
                new_pop.add(child);

            }
            int[] best_elite = best(pop,times_set);
            replace(new_pop,best_elite,times_set);
            pop = new_pop;
            if(computing(times_set,best_elite) > computing(times_set,best)){
                best =  best_elite;
            }
            System.out.println("flowtime - " + flow_time);
            System.out.println("Generation " + i + " makespan - " + (1.0/computing(times_set,best)));

        }
        System.out.println("final makespan - " + (1.0/computing(times_set,best)));
    }
}

class init_data{
    // Local file path
    public static String file_path = "tai20_5_0.fsp";
    // Number of job
    public static int j;
    // Number of machines
    public static int m;


    // Seed
    public static long s;
    // Upper bound
    public static long up_bound;
    // Lower bound
    public static long low_bound;
    // Read instance from fsp file
    public static int[][] read_file() throws IOException {
        List<String> lines = Files.readAllLines(Path.of(file_path));
        List<String> get_values = new ArrayList<>();
        String tmp_string = "";
        for(char i : lines.get(1).toCharArray()){
            if(i != ' '){
                tmp_string += i;
            }
            if(i == ' ' && tmp_string != ""){
                get_values.add(tmp_string);
                tmp_string = "";
            }
        }
        get_values.add(tmp_string);
        j = Integer.parseInt(get_values.get(0));
        m = Integer.parseInt(get_values.get(1));
        s = Long.parseLong(get_values.get(2));
        up_bound = Long.parseLong(get_values.get(3));
        low_bound = Long.parseLong(get_values.get(4));

        // processing times
        int[][] times_set = new int[m][j];
        int x = 0;
        int y = 0;
        for(int i = 3; i < lines.size(); i++){
            String[] s = lines.get(i).strip().split(" ");
            for(String time: s){
                if(!Objects.equals(time, "")){
                    times_set[x][y] = Integer.parseInt(time);
                    y++;
                }

            }
            y = 0;
            x++;
        }
        System.out.println("rows - " + times_set.length);
        System.out.println("cols - " + times_set[0].length);
        return times_set;

    }
}