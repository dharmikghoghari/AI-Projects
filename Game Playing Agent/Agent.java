import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import javax.management.Query;

class Pos {

    int row;
    int col;

    Pos(int x, int y) {
        row = x;
        col = y;
    }
}

class state {

    ArrayList<Pos> positions;
    int score;

    state(int sco) {
        positions = new ArrayList<>();
        score = sco;
    }

    state() {
        positions = new ArrayList<>();
    }
}

public class Agent {

    public static int arr_size;
    public int no_fruits;
    public float time;
    public static char matrix_initial[][];
    public static char matrix[][];
    public int scores;
    public int count = 0;
    state track=new state(0);
    public int max_depth;
    public int finalScore;
    public long moves_per_second;
    public int possibleMoves;

    public static void main(String[] args) throws IOException {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        double start_time = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
        
        Agent hw = new Agent();
        hw.read_file();
        hw.initialize_matrix();
        
        ArrayList<state> list=new ArrayList<>();
        list=hw.generate_all_moves(new state(),"MAX" );
        hw.possibleMoves=list.size();
        hw.max_depth =hw.decide_depth(hw.arr_size, hw.no_fruits, hw.time);
        hw.scores = hw.minimax(new state(), hw.max_depth, "MAX", Integer.MAX_VALUE, Integer.MIN_VALUE);
        System.out.println("Depth :"+hw.max_depth);
        System.out.println("Possible_Moves :"+hw.possibleMoves);
        
        System.out.print(" (" + hw.track.positions.get(0).row + "," + hw.track.positions.get(0).col + ") ");
        System.out.println("Score :" + hw.scores);
        
        hw.print_output(hw.track.positions.get(0));
        System.out.println(" Count="+hw.count);
        
        double end_time = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
        System.out.println("time is : " + (end_time - start_time) / 1000000000);
    }

    public int decide_depth(int arr_size, int no_fruits, float time){
        int depth = 3;
        if (time < 1) {
            depth = 1;
        } else if (time < 2) {
            depth = 2;
        } else {
            if (arr_size < 6) {
                depth = 6;
            } else if (arr_size < 17) {
                if(possibleMoves<=30)
                {
                	depth=6;
                }
             	else if (possibleMoves <= 100) {
                    depth = 5;
                } else if (possibleMoves <= 200) {
                    depth = 4;
                }
            } else if (arr_size < 22) {
             	if(possibleMoves<=20){
                	depth=6;
                }
                else if (possibleMoves < 60) {
                    depth = 5;
                } else if (possibleMoves < 100) {
                    depth = 4;
                }
            } else if (arr_size < 27) {
                if (possibleMoves < 40) {
                    depth = 5;
                } else if (possibleMoves < 80) {
                    depth = 4;
                }
            }
        }
        
        return depth;
    }
    
    public void initialize_matrix() {
        for (int i = 0; i < arr_size; i++) {
            for (int j = 0; j < arr_size; j++) {
                matrix[i][j] = matrix_initial[i][j];
            }
        }
    }

    public int minimax(state parent, int depth, String player, int beta, int alpha) {
        count++;
        if (depth == 0) {
            return parent.score;
        }
        
        ArrayList<state> all_moves;
        all_moves = generate_all_moves(parent, player);
        if(all_moves.isEmpty())
        {
            return parent.score;
        }
        all_moves = append_all_moves(all_moves, parent, player);
        
        all_moves=sort_list(all_moves,player);
        int s=all_moves.size()-1;  
        for (int i = 0; i < all_moves.size(); i++) {
            
            if (player.equals("MAX")) {
                int score = minimax(all_moves.get(i), (depth - 1), "MIN", beta, alpha);
                if (score > alpha) {
                    alpha = score;
                    if (depth == max_depth) {
                        track.positions = all_moves.get(i).positions;
                    }
                }
               if (alpha >= beta) {
                   break;
                }
            } else {
                //int score = minimax(all_moves.get(rank[i]), (short) (depth - 1), "MAX", beta, alpha);
                int score = minimax(all_moves.get(i), (depth - 1), "MAX", beta, alpha);
                if (score < beta) {
                    beta = score;
                }
                if (alpha >= beta) {
                    break;
                }
            }
        }
        if (player == "MAX") {
            return alpha;
        } else {
            return beta;
        }
    }
    
    public ArrayList sort_list(ArrayList<state> all_moves, String player )
    {
        Collections.sort(all_moves, new Comparator<state>() {
            public int compare(state o1, state o2){
                if(o1.score==o2.score)
                return 0;
            else if(o1.score>o2.score)
                if(player.equals("MIN"))
                    return 1; 
                else
                    return -1;
            else
                if(player.equals("MIN"))
                    return -1; 
                else
                    return 1;
              //return -1;
            }
            
      });
        return all_moves;
    }
    
    public boolean is_terminal_state(){
        for(int i=0;i<arr_size;i++)
            for(int j=0;j<arr_size;j++)
                if(matrix[i][j]!='*')
                    return false;
        return true;
    }

    public ArrayList append_all_moves(ArrayList<state> all_moves, state parent, String player) {
        for (int j = 0; j < all_moves.size(); j++) {
            state temp = new state();
            for (int i = 0; i < parent.positions.size(); i++) {
                temp.positions.add(parent.positions.get(i));
            }
            temp.positions.add(all_moves.get(j).positions.get(0));
            if (player.equals("MAX")) {
                temp.score = parent.score + all_moves.get(j).score;
            } else {
                temp.score = parent.score - all_moves.get(j).score;
            }
            all_moves.get(j).positions = temp.positions;
            all_moves.get(j).score=temp.score;
        }
        return all_moves;
    }

    public ArrayList generate_all_moves(state parent, String player) {
        ArrayList<state> list = new ArrayList<>();
        boolean bool[][] = new boolean[arr_size][arr_size];
        apply_parent_on_matrix(parent);
        bool = set_boolean(bool);
        for (int i = 0; i < arr_size; i++) {
            for (int j = 0; j < arr_size; j++) {
                if (bool[i][j] == false) {
                    list.add(new state(0));
                    list.get(list.size() - 1).positions.add(new Pos(i, j));
                    list.get(list.size() - 1).score = (int) Math.pow((iteration(i, j, bool)), 2);//iteration(i,j,bool);//
                }
            }
        }
        return list;
    }
    

    public boolean[][] set_boolean(boolean[][] bool) {
        for (int i = 0; i < arr_size; i++) 
            for (int j = 0; j < arr_size; j++) 
                if (matrix[i][j] == '*') 
                    bool[i][j] = true;
        return bool;
    }

    public void apply_parent_on_matrix(state parent) {
        boolean bool[][]= new boolean[arr_size][arr_size];
        Pos p;
        initialize_matrix();
        for (int i = 0; i < parent.positions.size(); i++) {
            p = parent.positions.get(i);
            iterations(p.row, p.col);
        }
    }

    public int iteration(int i, int j, boolean bool[][]) {
        Queue<Pos> queue = new LinkedList<>();
        Pos current = new Pos(i, j);
        int count = 0;
        queue.add(current);
        bool[current.row][current.col] = true;
        int r = 0;
        int c = 0;
        //char val = 0;
        Pos temp;
        while (queue.peek() != null) {
            temp = queue.poll();
            count++;
            r = temp.row;
            c = temp.col;
            //val = matrix[r][c];
            if (temp.row + 1 < arr_size) {
                if (check_neighbour(r + 1, c, matrix[r][c], bool)) {
                    queue.add(new Pos(r + 1,c));
                    bool[r + 1][c] = true;
                }
            }
            if (temp.col + 1 < arr_size) {
                if (check_neighbour(r, c + 1, matrix[r][c], bool)) {
                    queue.add(new Pos(r,c+1));
                    bool[r][c + 1] = true;
                }
            }
            if (temp.row - 1 >= 0) {
                if (check_neighbour(r - 1, c,matrix[r][c], bool)) {
                    queue.add(new Pos(r-1,c));
                    bool[r - 1][c] = true;
                }
            }
            if (temp.col - 1 >= 0) {
                if (check_neighbour(r, c - 1, matrix[r][c], bool)) {
                    queue.add(new Pos(r,c-1));
                    bool[r][c - 1] = true;
                }
            }
        }
        return count;
    }

    public void iterations(int i, int j) {
        Queue<Pos> queue = new LinkedList<>();
        boolean[][] bool=new boolean[arr_size][arr_size];
        bool=set_boolean(bool);
        Pos current = new Pos(i,j);
        int count = 0;
        queue.add(current);
        bool[current.row][current.col] = true;
        int r = 0;
        int c = 0; 
        char val = 0;
        Pos temp;
        while (queue.peek() != null) {
            temp = queue.poll();
            count++;
            r = temp.row;
            c = temp.col;
            
            val =matrix[r][c];
            matrix[r][c]='*';
            if (temp.row + 1 < arr_size) {
                if (check_neighbour(r + 1, c,val, bool)) {
                    queue.add(new Pos(r+1,c));
                    bool[r + 1][c] = true;
                }
            }
            if (temp.col + 1 < arr_size) {
                if (check_neighbour(r, c + 1,val, bool)) {
                    queue.add(new Pos(r,c+1));
                    bool[r][c + 1] = true;
                }
            }
            if (temp.row - 1 >= 0) {
                if (check_neighbour(r - 1, c,val, bool)) {
                    queue.add(new Pos(r-1,c));
                    bool[r - 1][c] = true;
                }
            }
            if (temp.col - 1 >= 0) {
                if (check_neighbour(r, c - 1, val, bool)) {
                    queue.add(new Pos(r,c-1));
                    bool[r][c - 1] = true;
                }
            }
        }
        apply_gravity();
    }

    public boolean check_neighbour(int row, int col, char value, boolean[][] bool) {
        if (matrix[row][col] == value && bool[row][col] == false) {
            return true;
        } else {
            return false;
        }
    }

    public void make_stars(ArrayList<Pos> temp) {
        Pos p;
        for (int i = 0; i < temp.size(); i++) {
            p = temp.get(i);
            matrix[p.row][p.col] = '*';
        }
    }
    
    public void make_stars(boolean[][] bool)
    {
        for(int i=0;i<arr_size;i++)
        {
            for(int j=0;j<arr_size;j++)
            {
                if(bool[i][j]==true)
                    matrix[i][j]='*';
            }
        }
    }

    public void apply_gravity() {
        char temp = 0;
        for (int i = 0; i < arr_size; i++) 
        {
            for (int j = (arr_size - 1); j > 0; j--) 
            {
                if (matrix[j][i] == '*') {
                    for (int k = (j - 1); k >= 0; k--) 
                    {
                        if (matrix[k][i] != '*') {
                            temp = matrix[j][i];
                            matrix[j][i] = matrix[k][i];
                            matrix[k][i] = temp;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void read_file() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("input.txt");
        BufferedReader br = new BufferedReader(fr);
        InputStreamReader ir = new InputStreamReader(System.in);
        String line = new String();
        for (int i = 0; (line = br.readLine()) != null; i++) {
            if (i == 0) {
                arr_size = Integer.parseInt(line);
                matrix_initial = new char[arr_size][arr_size];
                matrix = new char[arr_size][arr_size];
            } else if (i == 1) {
                
                no_fruits = Integer.parseInt(line);
            } else if (i == 2) {
                time = Float.parseFloat(line);
            } else {
                for (int k = 0; k < arr_size; k++) {
                    char c = line.charAt(k);
                    matrix_initial[i - 3][k] = c;
                }
            }
        }
    }

    public void print_output(Pos p) throws IOException {
        File fl = new File("output.txt");
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fl, false)));
        initialize_matrix();
        iterations(p.row,p.col);
        output.write((char)(p.col+65)+""+(p.row+1)+"");
        output.write("\n");
        for (int i = 0; i < arr_size; i++) {
            for (int j = 0; j < arr_size; j++) {
                output.write((matrix[i][j]));
            }
            output.write("\n");
        }
        output.flush();
        output.close();
        
    }

    public void print_list(ArrayList<Pos> temp) {
        Pos p;
        for (int i = 0; i < temp.size(); i++) {
            p = temp.get(i);
            System.out.println("Row :" + p.row + " Col :" + p.col);
        }
        System.out.println();
    }

    public void print_states(ArrayList<state> temp) {
        state s;
        for (int i = 0; i < temp.size(); i++) {
            s = temp.get(i);
            for (int j = 0; j < s.positions.size(); j++) {
                System.out.print("(" + s.positions.get(j).row + "," + s.positions.get(j).col + ")");
            }
            System.out.println(" Score:" + s.score);
        }
        System.out.println();
    }

    public void print_matrix() {
        for (short i = 0; i < arr_size; i++) {
            for (short j = 0; j < arr_size; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void print_boolean(boolean[][] bool) {
        for (short i = 0; i < arr_size; i++) {
            for (short j = 0; j < arr_size; j++) {
                System.out.print(bool[i][j]);
            }
            System.out.println();
        }
        System.out.println();
    }
    
   public void iteration_once(int i, int j) {
        Queue<Pos> queue = new LinkedList<>();
        boolean[][] bool=new boolean[arr_size][arr_size];
        bool=set_boolean(bool);
        Pos current = new Pos(i,j);
        int count = 0;
        queue.add(current);
        bool[current.row][current.col] = true;
        int r = 0;
        int c = 0; 
        char val = 0;
        Pos temp;
        while (queue.peek() != null) {
            temp = queue.poll();
            count++;
            r = temp.row;
            c = temp.col;
            
            val =matrix[r][c];
            matrix[r][c]='*';
            if (temp.row + 1 < arr_size) {
                if (check_neighbour(r + 1, c,val, bool)) {
                    queue.add(new Pos(r+1,c));
                    bool[r + 1][c] = true;
                }
            }
            if (temp.col + 1 < arr_size) {
                if (check_neighbour(r, c + 1,val, bool)) {
                    queue.add(new Pos(r,c+1));
                    bool[r][c + 1] = true;
                }
            }
            if (temp.row - 1 >= 0) {
                if (check_neighbour(r - 1, c,val, bool)) {
                    queue.add(new Pos(r-1,c));
                    bool[r - 1][c] = true;
                }
            }
            if (temp.col - 1 >= 0) {
                if (check_neighbour(r, c - 1, val, bool)) {
                    queue.add(new Pos(r,c-1));
                    bool[r][c - 1] = true;
                }
            }
        }
        apply_gravity();
    }

}
