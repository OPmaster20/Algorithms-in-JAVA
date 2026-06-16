package max_flow_alg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class edge{
    int start;
    int end;
    int capactity;
    int back_index;
    edge(int start,int end,int capactity,int back_index){
        this.start=start;
        this.end=end;
        this.capactity=capactity;
        this.back_index=back_index;
    }
}
class loading_data{
    public String file_path = "src/task2_data/benchmark.txt";
    public static ArrayList<String[]> data = new ArrayList<String[]>();
    public void read_data(){
        try{BufferedReader br = new BufferedReader(new FileReader(this.file_path));
            String line;
            while ((line = br.readLine()) != null){
                String[] list = line.split("\t");
                for(int i=0;i<list.length;i++){
                    System.out.print(list[i] + " ");
                }
                System.out.println();
                data.add(list);
            }
            System.out.println(data.size());
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<String[]> return_data(){
        return data;
    }
}
public class max_flow {
    public static int source = 1;
    public static int sink = 20;
    public static int max_flow = 0;
    public static int bottleneck = 1000;
    public static ArrayList<edge>[] graph = new ArrayList[sink + 1];
    public static void add_data(int i, int j, int k){
        edge front_edge = new edge(i,j,k,-1);
        edge back_edge = new edge(j,i,0,-1);
        front_edge.back_index = graph[j].size();
        back_edge.back_index = graph[i].size();
        graph[i].add(front_edge);
        graph[j].add(back_edge);

        edge front_back = new edge(j,i,k,-1);
        edge back_back = new edge(i,j,0,-1);
        front_back.back_index = graph[i].size();
        back_back.back_index = graph[j].size();
        graph[i].add(back_back);
        graph[j].add(front_back);
    }
    public static void init_graph(){
        for(int i = 0; i < graph.length; i++){
            graph[i] = new ArrayList<>();
        }
    }
    public static int run(){
        while (true){
            int[] graph_p = new int[sink + 1];
            edge[] graph_e = new edge[sink + 1];
            for(int i = 0; i <= sink; i++){
                graph_p[i] = -1;
            }
            Queue<Integer> q = new LinkedList<>();
            q.add(source);
            graph_p[source] = source;
            boolean if_found = false;
            while (!q.isEmpty()){
                int element = q.poll();
                for(edge e: graph[element]){
                    int index = e.end;
                    if(graph_p[index] == -1 && e.capactity > 0){
                        graph_p[index] = element;
                        graph_e[index] = e;
                        q.add(index);
                        if(index == sink){
                            if_found = !if_found;
                        }
                    }
                }
                if(if_found){
                    break;
                }
            }
            if(graph_p[sink] == -1){
                break;
            }
            int index = sink;
            for(int i = index; i > source; i = graph_p[i]){
                edge e = graph_e[i];
                bottleneck = Math.min(bottleneck, e.capactity);
            }
            index = sink;
            while(index != source){
                edge edge_tmp = graph_e[index];
                edge_tmp.capactity = edge_tmp.capactity - bottleneck;
                edge back_index = graph[edge_tmp.end].get(edge_tmp.back_index);
                back_index.capactity = back_index.capactity + bottleneck;
                index = graph_p[index];
            }
            max_flow = max_flow + bottleneck;
        }
        return max_flow;
    }

    public static void main(String[] args) {
        init_graph();
        loading_data load = new loading_data();
        load.read_data();
        ArrayList<String[]> node_edges = load.return_data();
        for (String[] s: node_edges){
            add_data(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
        }
        long start = System.currentTimeMillis();
        System.out.println("Max_flow - " + run());
        long end = System.currentTimeMillis();
        System.out.println("time cost: " + (end - start) + " ms");
    }
}
