package greedy_alg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class greedy_p1{
    public int items_number = 20;
    public int knapsack_capacity = 38;
    public int[] items = new int[items_number];
    public int[] items_cost = new int[items_number];
    public int[] items_size = new int[items_number];
    public double[] cost_size = new double[items_number];
    public double[] mark_items = new double[items_number];
    public void init_items(){
        for(int i = 0; i < items_number; i++){
            items[i] = i + 1;
            mark_items[i] = 0;
        }

    }
    public void calculate_items(){
        for(int i = 0; i < items_number; i++){
            cost_size[i] = (double)items_cost[i] / items_size[i];
        }
    }

    public void sort_items(){
        for(int i = 0; i < items_number; i++){
            for(int j = i + 1; j < items_number; j++){
                if(cost_size[i] < cost_size[j]){
                    int temp1 = items[i];
                    items[i] = items[j];
                    items[j] = temp1;

                    int temp2 = items_cost[i];
                    items_cost[i] = items_cost[j];
                    items_cost[j] = temp2;

                    int temp3 = items_size[i];
                    items_size[i] = items_size[j];
                    items_size[j] = temp3;

                    double temp4 = cost_size[i];
                    cost_size[i] = cost_size[j];
                    cost_size[j] = temp4;
                }
            }
        }
    }

    public void greedy(){
        int capacity = knapsack_capacity;
        for(int i = 0; i < items_number; i++){
            if(items_size[i] <= capacity){
                mark_items[i] = 1;
                capacity -= items_size[i];
            }
            //else{
                //double temp = (double) capacity / items_size[i];
                //mark_items[i] = temp;
                //capacity = 0;
            //}
            //if(capacity == 0){
                //break;
            //}
        }
    }

    public void calculate_cost(){
        double cost = 0;
        for(int i = 0; i < items_number; i++){
            double k = mark_items[i] * items_cost[i];
            cost += k;
        }
        System.out.println("Total cost: " + cost);
    }
}

class get_data{
    String file_path = "src/task4_data/benchmark1.txt";
    int[] arr1 = new int[20];
    int[] arr2 = new int[20];
    public void read_data(){
        ArrayList<Integer> k = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(this.file_path));
            String line;
            int i = 0;
            while ((line = br.readLine()) != null){
                String[] arr = line.strip().split("\t");
                System.out.println(line);
                arr1[i] = Integer.parseInt(arr[0]);
                arr2[i] = Integer.parseInt(arr[1]);
                i++;
            }
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}



public class greedy {
    public static void main(String[] args) {
        get_data g = new get_data();
        g.read_data();
        greedy_p1 p = new greedy_p1();
        p.init_items();
        p.items_cost = g.arr1;
        p.items_size = g.arr2;
        p.calculate_items();
        p.sort_items();
        p.greedy();
        System.out.println();
        for(int i = 0; i < p.items_number; i++){
            if(p.mark_items[i] != 0.0){
                System.out.print(p.items[i] + " ");
            }
        }
        System.out.println();
        p.calculate_cost();

    }
}
