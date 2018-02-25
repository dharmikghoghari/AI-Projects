import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class Pos {

    short row;
    short col;

    Pos(short x, short y) {
        row = x;
        col = y;
    }
}

class state {
    ArrayList<Pos> positions;
    short count;
    state() {
        ArrayList<Pos> positions = new ArrayList<>();
        
    }
}

public class Agent {

    public static short arr_size;
    public short no_fruits;
    public float time;
    public static char matrix_initial[][];
    public static char matrix[][];
    public static boolean visited[][];
    public static short count = 0;
    public static short max_count = 0;
    public static short best_move[] = new short[2];
    //public Queue<Pos> queue = new LinkedList<>();
    public static ArrayList<Pos> list = new ArrayList<>();
    public static ArrayList<Pos> best_list = new ArrayList<>();
    public static ArrayList<state> node = new ArrayList<>();
    public static short depth=2;

    public static void main(String[] args) throws IOException {
        Agent hw = new Agent();

        hw.make_copy_matrix();

        hw.read_file();

        //hw.select_best_move();

        //hw.create_levels();
        hw.minimax(null,(short)2);
        for (short i = 0; i < arr_size; i++) {
            for (short j = 0; j < arr_size; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
        //hw.make_stars();
        //hw.apply_gravity();
        System.out.println();
        System.out.println();
        
        for (short i = 0; i < arr_size; i++) {
            for (short j = 0; j < arr_size; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
        hw.print_output();
        System.out.println("Max_Count :" + max_count);
    }

    public void create_levels() {
        for (short i = 0; i < 2; i++) {
            for (short j = 0; j < list.size(); j++) {

            }
        }
    }
    
    public void minimax(ArrayList<state> parent, short depth){
        if(depth == 0)  return;
        list=generate_next_moves(parent);
        for(short i=0;i<list.size();i++){
            
        }     
    }

    public void make_copy_matrix() {
        for (short i = 0; i < arr_size; i++) {
            for (short j = 0; j < arr_size; j++) {
                matrix[i][j] = matrix_initial[i][j];
            }
        }
    }

    public void apply_gravity() 
    {
        Agent hw = new Agent();
        char temp = 0;
        for (short i = 0; i < arr_size; i++) 
        {
            for (short j = (short) (arr_size - 1); j > 0; j--) 
            {
                if (matrix[j][i] == '*') 
                {
                    for (short k = (short) (j - 1); k >= 0; k--) 
                    {
                        if (matrix[k][i] != '*') 
                        {
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

    public void make_stars(ArrayList<Pos> temp) {
        Pos p;
        for (int i = 0; i < temp.size(); i++) {
            p = temp.get(i);
            matrix[p.row][p.col] = '*';
        }
        apply_gravity();
    }

    public void copy_lists() {
        Pos p;
        best_list = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            p = list.get(i);
            best_list.add(p);
        }
        list = new ArrayList<>();
    }

    public void print_list(ArrayList<Pos> temp) {
        Pos p;
        for (int i = 0; i < temp.size(); i++) {
            p = temp.get(i);
            System.out.println("Row :" + p.row + " Col :" + p.col);
        }
        System.out.println();
    }

    public ArrayList generate_next_moves(ArrayList<state> parent) {
        ArrayList<Pos> list=new ArrayList<>();
        
        apply_parent_on_matrix(parent);                 //also generate boolean
        
        state s = null;
        for (short i = 0; i < arr_size; i++) {
            for (short j = 0; j < arr_size; j++) {
                if (visited[i][j] == false) {
                    //iteration(i, j);
                    //hw.create_levels(i,j);
                }
            }
        }
        return list;
    }
    
    public void apply_parent_on_matrix(ArrayList<state> parent)
    {
        state s;
        for(int i=0;i<parent.size();i++)
        {
            s=parent.get(i);
            for(int j=0;j<s.positions.size();j++)
            {
                iteration((short)s.positions.get(j).row,(short)s.positions.get(j).col);
            }
        }
    }
    
    

    public void iteration(short i, short j) {
        
        Queue<Pos> queue = new LinkedList<>();    
        //queue = new LinkedList<>();
        Pos current = new Pos((short) i, (short) j);
        list = new ArrayList<>();
        count = 0;
        queue.add(current);
        visited[current.row][current.col] = true;
        Agent hw = new Agent();
        short r = 0;
        short c = 0, val = 0;
        Pos temp;
        while (queue.peek() != null) {
            temp = queue.poll();
            count++;
            r = temp.row;
            c = temp.col;
            val = (short) matrix[r][c];
            list.add(new Pos((short) (r), (short) (c)));
            if (temp.row + 1 < arr_size) {
                if (check_neighbour(r + 1, c, matrix[r][c])) {
                    queue.add(new Pos((short) (r + 1), (short) (c)));
                    visited[r + 1][c] = true;
                }
            }
            if (temp.col + 1 < arr_size) {
                if (check_neighbour(r, c + 1, matrix[r][c])) {
                    queue.add(new Pos((short) (r), (short) (c + 1)));
                    visited[r][c + 1] = true;
                }
            }
            if (temp.row - 1 >= 0) {
                if (check_neighbour(r - 1, c, matrix[r][c])) {
                    queue.add(new Pos((short) (r - 1), (short) (c)));
                    visited[r - 1][c] = true;
                }
            }
            if (temp.col - 1 >= 0) {
                if (check_neighbour(r, c - 1, matrix[r][c])) {
                    queue.add(new Pos((short) (r), (short) (c - 1)));
                    visited[r][c - 1] = true;
                }
            }
        }
        make_stars(list);
        //node.add(new state(i,j,count));
        if (count > max_count) {
            //best_move[0] = i;
            //best_move[1] = j;
            copy_lists();
            max_count = count;
        }
    }

    public boolean check_neighbour(int row, int col, char value) {
        if (matrix[row][col] == value && visited[row][col] == false) {
            return true;
        } else {
            return false;
        }
    }

    public void read_file() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("input.txt");
        BufferedReader br = new BufferedReader(fr);
        InputStreamReader ir = new InputStreamReader(System.in);
        String line = new String();
        for (short i = 0; (line = br.readLine()) != null; i++) {
            if (i == 0) {
                arr_size = (short) Integer.parseInt(line);
                matrix_initial = new char[arr_size][arr_size];
                visited = new boolean[arr_size][arr_size];
                System.out.println("Size of Array :" + arr_size);
            } else if (i == 1) {
                no_fruits = (short) Integer.parseInt(line);
                System.out.println("Unique Fruits :" + no_fruits);
            } else if (i == 2) {
                time = Float.parseFloat(line);
            } else {
                for (short k = 0; k < arr_size; k++) {
                    char c = line.charAt(k);
                    matrix_initial[i - 3][k] = c;
                    if (matrix_initial[i - 3][k] == '*') {
                        visited[i - 3][k] = true;
                    }
                }
            }
        }
    }

    public void print_output() throws IOException 
    {
        File fl = new File("output.txt");
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fl, false)));
        for (short i = 0; i < arr_size; i++) {
            //output.write("\n");
            for (short j = 0; j < arr_size; j++) {
                output.write((matrix[i][j]));
            }
            output.write("\n");
        }
        output.close();
    }
}
