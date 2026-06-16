package paging_alg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class FIFO{
    public int frames = 10;
    public ArrayList<Integer> input = new ArrayList<Integer>();
    public Queue<Integer> ram_set = new LinkedList<>();
    public int hit = 0;
    public ArrayList<Integer> output = new ArrayList<>();
    public boolean exist(Queue<Integer> previous_set, int k){
        return previous_set.contains(k);
    }
    public void run(){
        for(int j = 0; j < input.size();j++){
            if(exist(ram_set,input.get(j))){
                hit++;
                continue;
            }
            if(ram_set.size() < frames){
                ram_set.add(input.get(j));
            }else{
                output.add(ram_set.poll());
                ram_set.add(input.get(j));
            }
        }
        System.out.print("paging_alg.FIFO NUMBER OF PAGE FAULTS - ");
        System.out.println(input.size() - hit);
    }
}

class LRU{
    public int frames = 10;
    public ArrayList<Integer> input = new ArrayList<Integer>();
    public ArrayList<Integer> output = new ArrayList<>();
    public Integer[] ram_set = new Integer[frames];
    public int hit = 0;
    public int len = 0;
    public boolean exist(Integer[] previous_set, Integer k){
        for(int j = 0; j < len;j++){
            if(Objects.equals(previous_set[j], k)){
                return true;
            }
        }
        return false;
    }

    public void insert(Integer k){
        for (int i = len; i > 0; i--) {
            ram_set[i] = ram_set[i - 1];
        }
        ram_set[0] = k;
        len++;
    }

    public void insert_mix(Integer k){
        int index = -1;
        for (int i = 0; i < frames; i++) {
            if (Objects.equals(ram_set[i], k)) {
                index = i;
                break;
            }
        }
        if (index == -1) return;
        for (int i = index; i > 0; i--) {
            ram_set[i] = ram_set[i - 1];
        }
        ram_set[0] = k;
    }

    public void insert_remove(Integer k){
        output.add(ram_set[frames - 1]);
        for (int i = frames - 1; i > 0; i--) {
            ram_set[i] = ram_set[i - 1];
        }
        ram_set[0] = k;
    }

    public void run(){
        for(int j = 0; j < input.size();j++){
            if(len < frames){
                if(exist(ram_set,input.get(j))){
                    insert_mix(input.get(j));
                    hit++;
                }else{
                    insert(input.get(j));
                }
            }else{
                if(exist(ram_set,input.get(j))){
                    insert_mix(input.get(j));
                    hit++;
                }else{
                    insert_remove(input.get(j));
                }
            }
        }
        System.out.print("paging_alg.LRU NUMBER OF PAGE FAULTS - ");
        System.out.println(input.size() - hit);
    }
}

class OPT{
    public int frames = 10;
    public ArrayList<Integer> input = new ArrayList<Integer>();
    public ArrayList<Integer> ram_set = new  ArrayList<>();
    public ArrayList<Integer> output = new ArrayList<>();
    public int hit = 0;
    public boolean exist(Integer k){
        for(int j = 0; j < ram_set.size();j++){
            if(Objects.equals(ram_set.get(j), k)){
                return true;
            }
        }
        return false;
    }

    public int find_future(int index, int p){
        int page = ram_set.get(p);
        for(int j = index + 1; j < input.size(); j++){
            if(page == input.get(j)){
                return j;
            }
        }
        return Integer.MAX_VALUE;
    }
    public void replace(Integer k, int index){
        int victimIndex = -1;
        int farthest = -1;
        for(int i = 0; i < ram_set.size(); i++){
            int nextUse = find_future(index, i);
            if(nextUse > farthest){
                farthest = nextUse;
                victimIndex = i;
            }
        }
        output.add(ram_set.get(victimIndex));
        ram_set.set(victimIndex, k);
    }

    public void run(){
        ram_set.add(input.get(0));
        for(int i = 1; i < input.size();i++){
            if(ram_set.size() < frames){
                if(!(exist(input.get(i)))){
                    ram_set.add(input.get(i));
                }else{
                    hit++;
                }
            }else{
                if(!(exist(input.get(i)))){
                    replace(input.get(i),i);
                }else{
                    hit++;
                }
            }
        }
        System.out.print("paging_alg.OPT NUMBER OF PAGE FAULTS - ");
        System.out.println(input.size() - hit);
    }
}


class init_data{
    String file_path = "src/task3_data/benchmark paging_alg.paging.txt";
    public ArrayList<Integer> read_data(){
        ArrayList<Integer> k = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(this.file_path));
            String line;
            int line_index = 1;
            while ((line = br.readLine()) != null){
                if(line_index >= 8 && line.strip() != " "){
                    k.add(Integer.parseInt(line.strip()));
                }
                line_index++;
            }
            System.out.println(k.size());
            return k;
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

public class paging {
    public static void main(String[] args) {
        init_data init = new init_data();
        FIFO fifo = new FIFO();
        LRU lru = new LRU();
        OPT opt = new OPT();
        lru.input = init.read_data();
        fifo.input = init.read_data();
        opt.input = init.read_data();
        fifo.run();
        lru.run();
        opt.run();

        for(int i: fifo.output){
            System.out.print(i);
            System.out.print(",");
        }
        System.out.println();
        for(int i: lru.output){
            System.out.print(i);
            System.out.print(",");
        }
        System.out.println();
        for(int i: opt.output){
            System.out.print(i);
            System.out.print(",");
        }
    }
}
