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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

class struct {

    short no_queens;
    Vector<position> state;
    short row;

    struct() {
        this.state = new Vector<position>();
        this.no_queens = 0;
    }

    public struct(struct s1) {
        this.no_queens = s1.no_queens;
        this.state = (Vector<position>) s1.state.clone();
    }
}

class position {

    short row;
    short col;

    position(short x, short y) {
        row = x;
        col = y;
    }
}

public class Techniques{

    public static short size = 0;
    public static short lizards = 0;
    public static String algo_type;
    public static short[][] matrix = new short[size][size];
    public static short[][] copy;
    public static Queue<struct> queue = null;      
    public static Stack<struct> stack = null;

    ////////////////////////SA//////////////////
    public short conflicts = 0;
    public short new_conflicts = 0;
    public static short lizards_SA=0;
    public short delta = 0;
    public short counter = 3;
    public double temperature = 1.00;
    public double cooling_factor = 0.0005;
    public short copy_SA[][] = new short[size][size];

    public Pos curr = null;
    public ArrayList<Pos> list = new ArrayList<>();
    public ArrayList<Pos> free = new ArrayList<>();

    public void bfs_function() throws IOException {
        queue = new LinkedList<struct>();
        struct current = new struct();
        init(current);
        queue.add(current);         //queue impemwntation
        
        while (queue.peek() != null) //queue impemwntation
        {
            current = queue.poll();           //queue impemwntation
            init(current);
            if (current.no_queens == lizards) {
                break;
            }
            if (current.no_queens == 0) {
                add_first_bfs(current, (short) 0, (short) 0);
            } else {
                add_children_bfs(current, current.state.lastElement().row, current.state.lastElement().col);

            }
        }
        if (current.no_queens == lizards) {
            print_output("OK");
        } else {
            print_output("FAIL");
        }
        init(current);
    }

    public void dfs_function() throws IOException {
        stack = new Stack<struct>();
        struct current = new struct();
        init(current);
        stack.push(current);
        //int counter=0;
        while (stack.size() > 0) {

            current = stack.pop();
            init(current);
            //counter++;
            if (current.no_queens == lizards) {
                break;
            }
            if (current.no_queens == 0) {
                add_first_dfs(current, (short) 0, (short) 0);
            } else {
                add_children_dfs(current, current.state.lastElement().row, current.state.lastElement().col);

            }
        }
        init(current);
        if (current.no_queens == lizards) {
            print_output("OK");
        } else {
            print_output("FAIL");
        }
    }

    public void sa_function() throws IOException {
        long start_time = System.currentTimeMillis();
        homework tc = new homework();
            
        boolean flag = true;
        tc.init_SA();
        tc.place_liz();
        tc.add_free();
        int count = 0;
        while (true) {
            if (System.currentTimeMillis() - start_time > 270000) {
                flag = false;
             	break;
            }
            tc.conflict_mat();
            if (tc.conflicts == 0) {
                break;
            }
            counter++;
            tc.move_lizzard();
        }
        if (flag == false) {
            print_output("FAIL");
        } else {
            print_output("OK");
        }
    }

    public static void main(String[] args) throws IOException {
        read_file();

        homework hw = new homework();

        if (algo_type.equals("BFS")) {
            hw.bfs_function();
        } else if (algo_type.equals("DFS")) {
            hw.dfs_function();
        } else if(algo_type.equals("SA")){
            hw.sa_function();
        }
    }

    public static void init(struct t) {
        short x = 0, y = 0;
        matrix = new short[size][size];
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                matrix[i][j] = copy[i][j];
            }
        }
        Iterator<position> i = t.state.iterator();
        while (i.hasNext()) {
            position p = i.next();
            x = p.row;
            y = p.col;
            matrix[x][y] = 1;
        }
    }

    public static void add_first_bfs(struct t, short row, short col) {
        boolean check = false;
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                if (matrix[i][j] == 0 && is_safe(i, j)) {
                    struct temp = new struct(t);
                    temp.state.add(new position(i, j));
                    temp.no_queens = (short) (t.no_queens + 1);
                    queue.add(temp);            //queue impemwntation
                    check = true;
                }
            }
            if (check == true) {
                break;
            }
        }
    }

    public static void add_first_dfs(struct t, short row, short col) {
        boolean check = false;
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                if (matrix[i][j] == 0 && is_safe(i, j)) {
                    struct temp = new struct(t);
                    temp.state.add(new position(i, j));
                    temp.no_queens = (short) (t.no_queens + 1);
                    stack.push(temp);
                    check = true;
                }
            }
            if (check == true) {
                break;
            }
        }
    }

    public static void add_children_bfs(struct t, short row, short col) {
        short x = row;
        boolean check = false;
        if (col < size) {
            for (short i = (short) (col + 1); i < size; i++) {
                if (matrix[row][i] == 0 && is_safe(row, i)) {
                    struct temp = new struct(t);
                    temp.state.add(new position(row, i));
                    temp.no_queens = (short) (t.no_queens + 1);
                    queue.add(temp);            //queue impemwntation
                    check = true;
                }
            }
        }
        if (check) {
            return;
        }
        for (short i = (short) (row + 1); i < size; i++) {
            for (short j = 0; j < size; j++) {
                if (matrix[i][j] == 0 && is_safe(i, j)) {
                    struct temp = new struct(t);
                    temp.state.add(new position(i, j));
                    temp.no_queens = (short) (t.no_queens + 1);
                    queue.add(temp);            //queue impemwntation
                    check = true;
                }
            }
            if (check == true) {
                break;
            }
        }
    }

    public static void add_children_dfs(struct t, short row, short col) {
        short x = row;
        boolean check = false;
        if (col < size) {
            for (short i = (short) (col + 1); i < size; i++) {
                if (matrix[row][i] == 0 && is_safe(row, i)) {
                    struct temp = new struct(t);
                    temp.state.add(new position(row, i));
                    temp.no_queens = (short) (t.no_queens + 1);
                    stack.push(temp);
                    check = true;
                }
            }
        }
        if (check) {
            return;
        }

        for (short i = (short) (row + 1); i < size; i++) {
            for (short j = 0; j < size; j++) {
                if (matrix[i][j] == 0 && is_safe(i, j)) {
                    struct temp = new struct(t);
                    temp.state.add(new position(i, j));
                    temp.no_queens = (short) (t.no_queens + 1);
                    stack.push(temp);
                    check = true;
                }
            }
            if (check == true) {
                break;
            }
        }
    }

    public static boolean is_safe(short row, short column) {
        for (short c_a = (short) (column + 1); c_a < size; c_a++) {
            if (matrix[row][c_a] == 1) {
                return false;
            } else if (matrix[row][c_a] == 2) {
                break;
            }
        }
        for (short c_s = (short) (column - 1); c_s >= 0; c_s--) {
            if (matrix[row][c_s] == 1) {
                return false;
            } else if (matrix[row][c_s] == 2) {
                break;
            }
        }

        for (short r_s = (short) (row - 1); r_s >= 0; r_s--) {
            if (matrix[r_s][column] == 1) {
                return false;
            } else if (matrix[r_s][column] == 2) {
                break;
            }
        }
        for (short r_a = (short) (row + 1); r_a < size; r_a++) {
            if (matrix[r_a][column] == 1) {
                return false;
            } else if (matrix[r_a][column] == 2) {
                break;
            }
        }

        for (short r_s = (short) (row - 1), c_a = (short) (column + 1); r_s >= 0 && c_a < size; r_s--, c_a++) {
            if (matrix[r_s][c_a] == 1) {
                return false;
            } else if (matrix[r_s][c_a] == 2) {
                break;
            }
        }
        for (short r_a = (short) (row + 1), c_s = (short) (column - 1); r_a < size && c_s >= 0; r_a++, c_s--) {
            if (matrix[r_a][c_s] == 1) {
                return false;
            } else if (matrix[r_a][c_s] == 2) {
                break;
            }
        }

        for (short r_s = (short) (row - 1), c_s = (short) (column - 1); r_s >= 0 && c_s >= 0; r_s--, c_s--) {
            if (matrix[r_s][c_s] == 1) {
                return false;
            } else if (matrix[r_s][c_s] == 2) {
                break;
            }
        }
        for (short r_a = (short) (row + 1), c_a = (short) (column + 1); r_a < size && c_a < size; r_a++, c_a++) {
            if (matrix[r_a][c_a] == 1) {
                return false;
            } else if (matrix[r_a][c_a] == 2) {
                break;
            }
        }
        return true;
    }

    public static void read_file() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("/Users/dharmik/NetBeansProjects/Homework1a/src/homework1a/input.txt");
        BufferedReader br = new BufferedReader(fr);
        InputStreamReader ir = new InputStreamReader(System.in);
        String line = new String();
        short line_number = 1;
		short two_counter=0;
        short count = 0;

        for (short i = 0; (line = br.readLine()) != null; i++) {
            if (i == 0) {
                algo_type = line;
                System.out.println("Algorithm :" + line);
            } else if (i == 1) //number of rows
            {
                size = (short) Integer.parseInt(line);
                
                copy = new short[size][size];
                System.out.println("Size of Array :" + size);
            } else if (i == 2) //number of columns
            {
                if (algo_type.equals("BFS") || algo_type.equals("DFS")) {
                    lizards = (short) Integer.parseInt(line);
                    System.out.println("No. of Lizards :" + lizards);
                }

                if (algo_type.equals("SA")) {
                    lizards_SA = (short) Integer.parseInt(line);
                    System.out.println("No. of Lizards :" + lizards_SA);
                }
                //System.out.println("No. of Lizards :" + lizards_SA);
            } else {

                for (short k = 0; k < size; k++) {
                    char c = line.charAt(k);
                    //System.out.println("k="+k + " i="+(i-3));
                    copy[i - 3][k] = (short) (c - '0');
                    if (copy[i - 3][k] == 2) {
                        two_counter++;
                    }
                    //else if(copy[i-])
                    //System.out.print(copy[i - 3][k]);
                }
                //System.out.println();
            }
        }
        if ((size * size - two_counter) < lizards || (size * size - two_counter) < lizards_SA) {
            print_output("FAIL");
            System.exit(0);
        }

        if(two_counter == 0 && lizards>size )
        {
            print_output("FAIL");
            System.exit(0);
        }
    }

    public static void print_output(String result) throws IOException {
        File fl = new File("/Users/dharmik/NetBeansProjects/Homework1a/src/homework1a/output.txt");
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fl, false)));

        if (result.equals("OK")) {
            output.print("OK");
            for (short i = 0; i < size; i++) {
                output.write("\n");
                for (short j = 0; j < size; j++) {
                    output.write(new Integer(matrix[i][j]).toString());
                }
            }
        } else {
            output.print("FAIL");
        }
        output.close();
    }

    public static void create_rows(String line, short row) {
        for (short i = 0; i < size; i++) {
            char c = line.charAt(i);
            copy[row - 3][i] = (short) (c - '0');
            System.out.print(copy[row - 3][i]);
        }
        System.out.println();
    }

    /////////////////////////////////////////////////SA//////////////////////////////////////////////////////////
    public void add_free() {
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                //System.out.print("i:" + i + " j:" + j);
                if (matrix[i][j] == 0) {
                    free.add(new Pos(i, j));
                }
                //System.out.println();
            }
        }
    }

    public void init_SA() {
        short x = 0, y = 0;
        matrix = new short[size][size];
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                matrix[i][j] = copy[i][j];
            }
        }
    }

    public boolean probability() {
        if (delta < 0) {
            return true;
        }
        double C = Math.exp(-delta / temperature);

        double r = Math.random();
        if (r < C) {
            return true;
        }
        return false;
    }

    public void move_lizzard() {
        while (true) {
            Random r = new Random();

            copy_matrix();
            Pos to_be_moved = new Pos((short) 0, (short) 0);
            Pos moved_here = new Pos((short) 0, (short) 0);

            short to_be_moved_curr = (short) r.nextInt(list.size());

            to_be_moved = list.get(to_be_moved_curr);
            short to_be_moved_row = to_be_moved.row;
            short to_be_moved_col = to_be_moved.col;

            short free_curr = (short) r.nextInt(free.size());
            moved_here = free.get(free_curr);
            short moved_here_row = moved_here.row;
            short moved_here_col = moved_here.col;

            copy_SA[to_be_moved_row][to_be_moved_col] = 0;
            copy_SA[moved_here_row][moved_here_col] = 1;

            conflict_mat();
            conflict_copy();

            delta = (short) (new_conflicts - conflicts);
            if (probability()) {

                matrix[to_be_moved_row][to_be_moved_col] = 0;
                matrix[moved_here_row][moved_here_col] = 1;

                list.remove(to_be_moved_curr);
                list.add(new Pos(moved_here_row, moved_here_col));
                //print_list();

                free.remove(free_curr);
                free.add(new Pos(to_be_moved_row, to_be_moved_col));
                //temperature = (temperature - cooling_factor);
				temperature = 1/Math.log(counter);
                break;
            }
        }
    }

    public void print_list() {
        for (int i = 0; i < list.size(); i++) {
            System.out.print("[" + list.get(i).row + "," + list.get(i).col + "] ");
        }
        System.out.println();
    }

    public void print_free() {
        for (int i = 0; i < free.size(); i++) {
            System.out.print("[" + free.get(i).row + "," + free.get(i).col + "] ");
        }
        System.out.println();
        System.out.println();
    }

    public void copy_matrix() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                copy_SA[i][j] = matrix[i][j];
            }
        }
    }

    public void print_mat() {
        //System.out.println("Matrix :");
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void print_copy() {
        System.out.println("Copy :");
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                System.out.print(copy_SA[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    public void place_liz() {
        short row = 0;//=rando();
        short col = 0;//rando();
        short x = 0;
        while (x < lizards_SA) {
            row = rando();
            //System.out.println("row :" + row);
            col = rando();
            //System.out.println("col :" + col);
            if (matrix[row][col] == 0) {
                matrix[row][col] = 1;
                list.add(new Pos(row, col));
                x++;
            }
        }
    }

    public void conflict_mat() {
        conflicts = 0;
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                if (matrix[i][j] == 1) {
                    short row = i;
                    short column = j;
                    for (short c_a = (short) (column + 1); c_a < size; c_a++) {
                        if (matrix[row][c_a] == 1) {
                            conflicts++;
                        } else if (matrix[row][c_a] == 2) {
                            break;
                        }
                    }
                    for (short c_s = (short) (column - 1); c_s >= 0; c_s--) {
                        if (matrix[row][c_s] == 1) {
                            conflicts++;
                        } else if (matrix[row][c_s] == 2) {
                            break;
                        }
                    }

                    for (short r_s = (short) (row - 1); r_s >= 0; r_s--) {
                        if (matrix[r_s][column] == 1) {
                            conflicts++;
                        } else if (matrix[r_s][column] == 2) {
                            break;
                        }
                    }
                    for (short r_a = (short) (row + 1); r_a < size; r_a++) {
                        if (matrix[r_a][column] == 1) {
                            conflicts++;
                        } else if (matrix[r_a][column] == 2) {
                            break;
                        }
                    }

                    for (short r_s = (short) (row - 1), c_a = (short) (column + 1); r_s >= 0 && c_a < size; r_s--, c_a++) {
                        if (matrix[r_s][c_a] == 1) {
                            conflicts++;
                        } else if (matrix[r_s][c_a] == 2) {
                            break;
                        }
                    }
                    for (short r_a = (short) (row + 1), c_s = (short) (column - 1); r_a < size && c_s >= 0; r_a++, c_s--) {
                        if (matrix[r_a][c_s] == 1) {
                            conflicts++;
                        } else if (matrix[r_a][c_s] == 2) {
                            break;
                        }
                    }

                    for (short r_s = (short) (row - 1), c_s = (short) (column - 1); r_s >= 0 && c_s >= 0; r_s--, c_s--) {
                        if (matrix[r_s][c_s] == 1) {
                            conflicts++;
                        } else if (matrix[r_s][c_s] == 2) {
                            break;
                        }
                    }
                    for (short r_a = (short) (row + 1), c_a = (short) (column + 1); r_a < size && c_a < size; r_a++, c_a++) {
                        if (matrix[r_a][c_a] == 1) {
                            conflicts++;
                        } else if (matrix[r_a][c_a] == 2) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void conflict_copy() {
        new_conflicts = 0;
        for (short i = 0; i < size; i++) {
            for (short j = 0; j < size; j++) {
                if (copy_SA[i][j] == 1) {
                    short row = i;
                    short column = j;
                    for (short c_a = (short) (column + 1); c_a < size; c_a++) {
                        if (copy_SA[row][c_a] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[row][c_a] == 2) {
                            break;
                        }
                    }
                    for (short c_s = (short) (column - 1); c_s >= 0; c_s--) {
                        if (copy_SA[row][c_s] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[row][c_s] == 2) {
                            break;
                        }
                    }

                    for (short r_s = (short) (row - 1); r_s >= 0; r_s--) {
                        if (copy_SA[r_s][column] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[r_s][column] == 2) {
                            break;
                        }
                    }
                    for (short r_a = (short) (row + 1); r_a < size; r_a++) {
                        if (copy_SA[r_a][column] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[r_a][column] == 2) {
                            break;
                        }
                    }

                    for (short r_s = (short) (row - 1), c_a = (short) (column + 1); r_s >= 0 && c_a < size; r_s--, c_a++) {
                        if (copy_SA[r_s][c_a] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[r_s][c_a] == 2) {
                            break;
                        }
                    }
                    for (short r_a = (short) (row + 1), c_s = (short) (column - 1); r_a < size && c_s >= 0; r_a++, c_s--) {
                        if (copy_SA[r_a][c_s] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[r_a][c_s] == 2) {
                            break;
                        }
                    }

                    for (short r_s = (short) (row - 1), c_s = (short) (column - 1); r_s >= 0 && c_s >= 0; r_s--, c_s--) {
                        if (copy_SA[r_s][c_s] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[r_s][c_s] == 2) {
                            break;
                        }
                    }
                    for (short r_a = (short) (row + 1), c_a = (short) (column + 1); r_a < size && c_a < size; r_a++, c_a++) {
                        if (copy_SA[r_a][c_a] == 1) {
                            new_conflicts++;
                        } else if (copy_SA[r_a][c_a] == 2) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public void init() {

    }

    public short rando() {
        Random r = new Random();
        return (short) r.nextInt(size);
    }

    public short rando_list(int high) {
        Random r = new Random();
        short low = 1;
        return (short) (r.nextInt(high - low) + low);
    }

}

class Pos {

    short row;
    short col;

    Pos(short x, short y) {
        row = x;
        col = y;
    }
}

