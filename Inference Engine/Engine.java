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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class Predicate {

    String p_name;
    ArrayList<String> p_arguments;

    Predicate() {
        p_name = new String();
        p_arguments = new ArrayList<>();
    }
}

class Sentence {

    ArrayList<Predicate> preds;

    Sentence() {
        preds = new ArrayList<>();
    }
}

public class Engine {

    public int no_queries;
    public int no_statements;
    public ArrayList<Predicate> queries = new ArrayList<>();
    public ArrayList<Sentence> KbInit = new ArrayList<>();
    public ArrayList<Sentence> Kb = null;
    public HashMap<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
    public HashMap<String, ArrayList<Integer>> copymap = new HashMap<String, ArrayList<Integer>>();
    public ArrayList<String> allConstants = new ArrayList<>();
    public boolean[] queries_boolean;
    public Predicate global_predicate=null;
    public int counters[];
    public String s1;

    public static void main(String[] args) throws IOException 
    {
        Engine hw = new Engine();
        hw.read_file();
        hw.queries_boolean = new boolean[hw.queries.size()];
        for (int i = 0; i < hw.queries.size(); i++) 
        {
            hw.Kb = new ArrayList<>();
            hw.Kb = hw.copyKb();
            Sentence s=new Sentence();
            
            Predicate neg_query = new Predicate();
            neg_query = hw.getNegation(hw.queries.get(i));
            
            s.preds.add(neg_query);
            hw.Kb.add(s);
            
            hw.counters=new int[hw.Kb.size()];
            
            Sentence main_sentence = new Sentence();
            main_sentence.preds.add(neg_query);
            
            hw.copymap=hw.copy_map();
            
            if(!hw.copymap.containsKey(neg_query.p_name)){
                
                hw.copymap.put(neg_query.p_name,new ArrayList<>(Arrays.asList(hw.Kb.size()-1)));
                
            }else{
                
                ArrayList<Integer> temp=hw.copymap.get(neg_query.p_name);
                temp.add(hw.Kb.size()-1);
                hw.copymap.put(neg_query.p_name, temp);
                
            }
            hw.queries_boolean[i] = hw.performResolution(main_sentence,hw.copyCounters(hw.counters));
        }
        hw.print_boolean(hw.queries_boolean);
        hw.print_output(hw.queries_boolean);
    }
    
    public ArrayList copyKb()
    {
        ArrayList<Sentence> copy= new ArrayList<>();
        
        for(int i=0;i<KbInit.size();i++)
        {
            Sentence s_old=KbInit.get(i);
            Sentence s_new=new Sentence();
            for(int j=0;j<s_old.preds.size();j++)
            {
                Predicate p_old=s_old.preds.get(j);
                Predicate p_new=new Predicate();
                
                p_new.p_name=p_old.p_name;
                for(int k=0;k<p_old.p_arguments.size();k++)
                {
                    p_new.p_arguments.add(p_old.p_arguments.get(k));
                }
                s_new.preds.add(p_new);
            }
            copy.add(s_new);
        }
        return copy;
    }
    
    public void print_boolean(boolean[] bool)
    {
        for(int i=0;i<bool.length;i++)
            System.out.println(bool[i]);
    }
    
    public HashMap copy_map()
    {
        HashMap<String, ArrayList<Integer>> copy = new HashMap<String, ArrayList<Integer>>();
        if(map == null)
            return null;
        for (Map.Entry<String, ArrayList<Integer>> entry : map.entrySet()) 
        {
            copy.put(entry.getKey(), entry.getValue());
        }
        return copy;
    }
    
    public boolean performResolution(Sentence main_sentence, int[] counters) 
    {
        HashMap<String, String> substitution_map=new HashMap<>();
        if (main_sentence == null || main_sentence.preds.isEmpty()) 
        {
            return true;
        }
        for (int i = 0; i < main_sentence.preds.size(); i++) 
        {
            Predicate neg_pred = new Predicate();
            neg_pred = getNegation(copyPredicate(main_sentence.preds.get(i)));
            
            ArrayList<Integer> list = new ArrayList<>();
            list = copymap.get(neg_pred.p_name);
            
            if(list == null || list.isEmpty())
                continue;
            for (int j = 0; j < list.size(); j++) 
            {
                counters[list.get(j)]++;
                if(counters[list.get(j)] > 6)
                {
                    continue;
                }
                HashMap<String, String> newsubstitution_map = unifyx(main_sentence, Kb.get(list.get(j)));
                if (newsubstitution_map != null) 
                {
                    Sentence new_sentence = merge_sentences(main_sentence, Kb.get(list.get(j)), newsubstitution_map);
                    if(new_sentence.preds.isEmpty())
                        return true;
                    
                    if(!performResolution(new_sentence,copyCounters(counters)))
                    {
                        continue;
                    }
                    else
                        return true;
                }
            }
        }
        return false;
    }
    
    public int[] copyCounters(int[] c)
    {
        int cc[]=new int[c.length];
        for(int i=0;i<c.length;i++)
            cc[i]=c[i];
        return cc;
    }
    
    public void initCounters()
    {
        counters=new int[Kb.size()];
        for(int i=0;i<counters.length;i++)
            counters[i]=0;
    }
    
    public int getRand(ArrayList<Integer> list)
    {
        int temp=list.size();
        Random r = new Random();
        return r.nextInt(temp);
    }
    
    public boolean check_predicate_available(Sentence s, Predicate p)
    {
        for(int i=0;i<s.preds.size();i++)
        {
            if(s.preds.get(i).p_name.equals(p.p_name))
                return true;
        }
        return false;
    }
    
    public HashMap unifyx(Sentence main_sentence, Sentence sentence_from_kb) 
    {
        HashMap<String, String> substitution_map= null;
        for(int j=0;j<main_sentence.preds.size();j++)
        {
            Predicate main_pred=main_sentence.preds.get(j);
            Predicate neg_pred=getNegation(copyPredicate(main_pred));
            ArrayList<Predicate> kb_pred = get_neg_predicate(sentence_from_kb, neg_pred);

            if(kb_pred == null || kb_pred.isEmpty())
            {
                continue;
            }
            for(int k=0;k<kb_pred.size();k++)
            {    
                boolean check=false;
                if(substitution_map == null)
                    substitution_map=new HashMap<>();

                for(int i=0;i<main_pred.p_arguments.size() ;i++)
                {
                    String s_main=main_pred.p_arguments.get(i);
                    String s_kb=kb_pred.get(k).p_arguments.get(i);
                    if(is_constant(s_main) && is_variable(s_kb))
                    {
                        if(!substitution_map.containsKey(s_kb))
                        {
                            substitution_map.put(s_kb, s_main);
                        }
                        else if(substitution_map.containsKey(s_kb))    
                        {
                            String  v_s_kb = substitution_map.get(s_kb);
                            
                            if(is_constant(v_s_kb) && s_main.equals(v_s_kb))
                            {
                                if(i==(main_pred.p_arguments.size()-1))
                                    check=true;
                                continue; 
                            }
                            else if(is_constant(v_s_kb) && !s_main.equals(v_s_kb))
                            {
                                substitution_map = null;
                                break;
                            }
                            else if(is_variable(v_s_kb) && substitution_map.containsKey(v_s_kb))
                            {
                                String  v_s_kb_cst = substitution_map.get(v_s_kb);
                                if((v_s_kb_cst).equals(s_main))
                                {
                                    if(i==(main_pred.p_arguments.size()-1))
                                        check=true;
                                    continue; 
                                }
                                else
                                {
                                    substitution_map = null;
                                    break;
                                }
                            }
                            else if(is_variable(v_s_kb) && !substitution_map.containsKey(v_s_kb))
                            {
                                substitution_map.put(v_s_kb, s_main);
                            }
                        }
                    } 
                    //s_main=variable && s_kb=constant 
                    else if(is_variable(s_main) && is_constant(s_kb))
                    {
                        if(!substitution_map.containsKey(s_main))
                            substitution_map.put(s_main, s_kb);
                        else
                        {
                            String  v_s_main = substitution_map.get(s_main);
                            if(is_constant(v_s_main) && s_kb.equals(v_s_main))
                            {
                                if(i==(main_pred.p_arguments.size()-1))
                                    check=true;
                                continue; 
                            }
                            else if(is_constant(v_s_main) && !s_kb.equals(v_s_main))
                            {
                                substitution_map = null;
                                break;
                            }
                            else if(is_variable(v_s_main) && substitution_map.containsKey(v_s_main))
                            {
                                String  v_s_main_cst = substitution_map.get(v_s_main);
                                if((v_s_main_cst).equals(s_kb))
                                {
                                    if(i==(main_pred.p_arguments.size()-1))
                                        check=true;
                                    continue; 
                                }
                                else
                                {
                                    substitution_map = null;
                                    break;
                                }
                            }
                            else if(is_variable(v_s_main) && !substitution_map.containsKey(v_s_main))
                            {
                                substitution_map.put(v_s_main, s_kb);
                            }
                        }
                    }
                    //s_main=variable && s_kb=variable
                    else if(is_variable(s_main) && is_variable(s_kb))
                    {
                        if(substitution_map.containsKey(s_kb) && substitution_map.containsKey(s_main))
                        {
                            if(substitution_map.get(s_main).equals(substitution_map.get(s_kb)))
                            {
                                if(i==(main_pred.p_arguments.size()-1))
                                    check=true;
                                continue;
                            }
                            else
                            {
                                String v1=substitution_map.get(s_main);
                                String v2=substitution_map.get(s_kb);
                                
                                if(is_variable(v1) && is_constant(v2))
                                {
                                    substitution_map.put(s_main, v2);
                                }
                                else if(is_constant(v1) && is_variable(v2))
                                {
                                    substitution_map.put(s_kb, v1);
                                }
                                else if(is_variable(v1) && is_variable(v2))
                                {
                                    substitution_map.put(s_kb, s_main);
//                                    substitution_map.put(s_kb, s_kb);
                                }
                                else if(is_constant(v1) && is_constant(v2) && !v1.equals(v2))
                                { 
                                    substitution_map=null;
                                    break;
                                }
                            }
                        }
                        else if(substitution_map.containsKey(s_kb))
                        {
                            substitution_map.put(s_main, substitution_map.get(s_kb));
                        }
                        else if(substitution_map.containsKey(s_main))
                        {
                            substitution_map.put(s_kb, substitution_map.get(s_main));
                        }
                        else
                        {
                            substitution_map.put(s_kb, s_main);
                        }
                    }
                    //s_main=constant && s_kb=constant
                    else if(is_constant(s_main) && is_constant(s_kb))
                    {
                        if(s_main.equals(s_kb))
                        {
                                if(i==(main_pred.p_arguments.size()-1))
                                    check=true;
                                continue; 
                        }
                        else 
                        {
                            substitution_map=null;
                            break;
                        }
                    }   
                }
                if(check == true)
                    break;
            }
                
        }
        return substitution_map;
    }
    
    public Sentence merge_sentences(Sentence main_sentence, Sentence sentence_from_kb, HashMap<String, String> substitution_map) 
    {
        Sentence new_sentence = new Sentence();
        int size = main_sentence.preds.size() + sentence_from_kb.preds.size();
        for (int i = 0; i < main_sentence.preds.size(); i++) 
        {
            Predicate p = main_sentence.preds.get(i);
            new_sentence.preds.add(p);
        }
        for (int i = 0; i < sentence_from_kb.preds.size(); i++) 
        {
            Predicate p = sentence_from_kb.preds.get(i);
            new_sentence.preds.add(p);
        }
        
        new_sentence=substitute(copySentence(new_sentence),substitution_map);
        for(int i=0;i<new_sentence.preds.size();)
        {
            boolean check=false;
            
            Predicate p1 = new_sentence.preds.get(i);
            Predicate p3= getNegation(copyPredicate(p1));
            for(int j=i+1;j<new_sentence.preds.size();j++)
            {
                Predicate p2=new_sentence.preds.get(j);
                if(p3.p_name.equals(p2.p_name))
                {
                    for(int k=0;k<p2.p_arguments.size();k++)
                    {
                        if(p3.p_arguments.get(k).equals(p2.p_arguments.get(k)))
                        {
                            check=true;
                        }
                        else
                        {
                            check=false;
                            break;
                        }   
                    }
                }
                if(check==true)
                {
                    new_sentence.preds.remove(p2);
                    new_sentence.preds.remove(p1);
                    
                }
                
            }
            if(check==false)
                i++;
        }
        return new_sentence;
    }
    
    public Sentence substitute(Sentence new_sentence, HashMap<String,String> substitution_map)
    {
        
        for(int i=0;i<new_sentence.preds.size();i++)
        {
            Predicate p=new_sentence.preds.get(i);
            for(int j=0;j<p.p_arguments.size();j++)
            {
                if(substitution_map.containsKey(p.p_arguments.get(j)))
                {
                    if(!is_variable(substitution_map.get(p.p_arguments.get(j))))
                    {
                        p.p_arguments.set(j,substitution_map.get(p.p_arguments.get(j)));
                    }
                    else if(! p.p_arguments.get(j).equals(substitution_map.get(p.p_arguments.get(j))))
                    {
                        p.p_arguments.set(j,substitution_map.get(p.p_arguments.get(j)));
                        j--;
                    }
                }
            }
            
        }
        return new_sentence;
    }
    
    public boolean check_possible(HashMap<String,String> hm)
    {
//        HashMap<String,String> temp=new HashMap<>();
            for (Map.Entry<String, String> entry : hm.entrySet()) 
            {
                if(is_variable(entry.getValue()))
                    if(hm.containsKey(entry.getValue()) && is_constant(hm.get(entry.getValue())))
                        return true;
//                temp.put(entry.getKey(), entry.getValue());
            }
        return false;
    }
    
    public Sentence copySentence(Sentence sentence)
    {
        Sentence temp=new Sentence();
        for(int i=0;i<sentence.preds.size();i++)
        {
            Predicate p=new Predicate();
            p.p_name=sentence.preds.get(i).p_name;
            for(int j=0;j<sentence.preds.get(i).p_arguments.size();j++)
            {
                p.p_arguments.add(sentence.preds.get(i).p_arguments.get(j));
            }
            temp.preds.add(p);
        }
        
        return temp;
    }
    
    public Predicate copyPredicate(Predicate pd)
    {
        Predicate temp=new Predicate();
        temp.p_name=pd.p_name;
        for(int i=0;i<pd.p_arguments.size();i++)
        {
            temp.p_arguments.add(pd.p_arguments.get(i));
        }
        return temp;
    }
    
    public HashMap copyMap(HashMap<String,String> hm)
    {
        if(hm != null)
        {
            HashMap<String,String> temp=new HashMap<>();
            for (Map.Entry<String, String> entry : hm.entrySet()) 
            {
                temp.put(entry.getKey(), entry.getValue());
            }
            return temp; 
        }
        else
            return null;
    }

    public Predicate get_predicate(Sentence sent, Predicate s) 
    {
        Predicate p = null;
        for (int i = 0; i < sent.preds.size(); i++) 
        {
            if ((sent.preds.get(i).p_name).equals(s.p_name)) 
            {
                p = sent.preds.get(i);
                break;
            }
        }
        return p;
    }
    
    public ArrayList get_neg_predicate(Sentence sent, Predicate s)
    {
        ArrayList<Predicate> pls=new ArrayList<>();
        for (int i = 0; i < sent.preds.size(); i++) 
        {
            if ((sent.preds.get(i).p_name).equals(s.p_name)) 
            {
                pls.add(sent.preds.get(i));
            }
        }
        return pls;
    }

    public Predicate getNegation(Predicate temp) {
        if (temp.p_name.charAt(0) == '~') {
            temp.p_name = temp.p_name.substring(1);
        } else {
            StringBuilder string = new StringBuilder("~");
            string.append(temp.p_name);
            temp.p_name = string.toString();
        }
        return temp;
    }

    public Predicate store_queries(String line) {
        StringBuilder sb = new StringBuilder();
        Predicate temp = new Predicate();
        char current;
        for (int i = 0; i < line.length(); i++) {
            current = line.charAt(i);
            if(current == ' ')
            {
                //System.out.println("Found Space....");
                continue;
            }
            if (current == '(') {
                temp.p_name = sb.toString();
                sb = new StringBuilder();
                continue;
            }
            if (line.charAt(i) == ',') {
                temp.p_arguments.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            if (current == ')') {
                temp.p_arguments.add(sb.toString());
                sb = new StringBuilder();
                continue;
            }
            sb.append(current);
        }
        return temp;
    }

    public Predicate store_sentences(String line, int row) {
        StringBuilder sb = new StringBuilder();
        Predicate temp = new Predicate();
        char current;
        for (int i = 0; i < line.length(); i++) 
        {
            current = line.charAt(i);
            if(current == ' ')
            {
                //System.out.println("Found Space...");
                continue;
            }
                
            if (current == '(') {
                temp.p_name = sb.toString();
                add_map((temp.p_name).trim(), row);
                sb = new StringBuilder();
                continue;
            }

            if (line.charAt(i) == ',') {
                if (is_variable(sb.toString())) {
                    temp.p_arguments.add((sb.toString()).trim() + "" + row);
                } else {
                    temp.p_arguments.add((sb.toString()).trim());
                }
                sb = new StringBuilder();
                continue;
            }

            if (current == ')') {
                if (is_variable(sb.toString())) {
                    temp.p_arguments.add(sb.toString() + "" + row);
                } else {
                    temp.p_arguments.add(sb.toString());
                }
                sb = new StringBuilder();
                continue;
            }

            sb.append(current);
        }
        return temp;
    }

    public boolean is_variable(String s) {
        if (s.charAt(0) >= 65 && s.charAt(0) <= 90) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean is_constant(String s) {
        if (s.charAt(0) >= 65 && s.charAt(0) <= 90) {
            return true;
        } else {
            return false;
        }
    }

    public void add_map(String key, int row) {
        if (map.containsKey(key)) {
            ArrayList<Integer> list = map.get(key);
            list.add(row);
            map.put(key, list);
        } else {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(row);
            map.put(key, list);
        }
    }
    
    public void print_hashmap() 
    {
        for (Map.Entry<String, ArrayList<Integer>> entry : map.entrySet()) 
        {
            String key = entry.getKey().toString();
            System.out.print(key + "(");
            ArrayList<Integer> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) 
            {
                System.out.print(value.get(i));
                if (i < value.size() - 1) 
                {
                    System.out.print(",");
                }
            }
            System.out.print(")");
            System.out.println();
        }
    }
    
    public void print_map(HashMap<String, String> hm)
    {
        if(hm != null)
        {
            for (Map.Entry<String, String> entry : hm.entrySet()) 
            {
                String key = entry.getKey().toString();
                System.out.print("(" + key+",");

                String value = entry.getValue().toString();
                System.out.print(value + ") ");
                //System.out.print();
            }
        }
    }

    public void print_queries() {
        Predicate temp;
        for (int i = 0; i < queries.size(); i++) {
            temp = queries.get(i);
            System.out.print(temp.p_name + "(");
            for (int j = 0; j < temp.p_arguments.size(); j++) {
                System.out.print(temp.p_arguments.get(j));
                if (j < temp.p_arguments.size() - 1) {
                    System.out.print(",");
                }
            }
            System.out.print(")");
        }
    }
    
    public void print_sentence(Sentence s)
    {
        for(int i=0;i<s.preds.size();i++)
        {
            print_queries(s.preds.get(i));
            if (i < s.preds.size() - 1) 
            {
                System.out.print(" | ");
            }
        }
    }

    public void print_queries(Predicate temp) 
    {
        System.out.print(temp.p_name + "(");
        for (int j = 0; j < temp.p_arguments.size(); j++) 
        {
            System.out.print(temp.p_arguments.get(j));
            if (j < temp.p_arguments.size() - 1) 
            {
                System.out.print(",");
            }
        }
        System.out.print(")");
    }

    public void print_KB() {
        Sentence temp;
        for (int i = 0; i < Kb.size(); i++) {
            temp = Kb.get(i);
            for (int j = 0; j < temp.preds.size(); j++) {
                print_queries(temp.preds.get(j));
                if (j < temp.preds.size() - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
    }
    
    public void print_KBInit() {
        Sentence temp;
        for (int i = 0; i < KbInit.size(); i++) {
            temp = KbInit.get(i);
            for (int j = 0; j < temp.preds.size(); j++) {
                print_queries(temp.preds.get(j));
                if (j < temp.preds.size() - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
    }

    public Sentence store_statements_in_KB(String line, int row) {
        Sentence temp = new Sentence();
        String[] terms = line.split("\\|");
        Predicate pt;
        for (int i = 0; i < terms.length; i++) {
            //System.out.println(terms[i]);
            pt = store_sentences(terms[i].trim(), row);
            temp.preds.add(pt);
        }
        return temp;
    }

    public void read_file() throws FileNotFoundException, IOException {
        FileReader fr = new FileReader("/Users/dharmik/NetBeansProjects/Hw3/src/hw3/input.txt");
        BufferedReader br = new BufferedReader(fr);
        InputStreamReader ir = new InputStreamReader(System.in);
        String line = new String();
        for (int i = 0; (line = br.readLine()) != null; i++) {
            if (i == 0) {
                no_queries = Integer.parseInt(line);
            } else if (i > 0 && i < (no_queries + 1)) {
                Predicate temp = store_queries(line);
                queries.add(temp);
            } else if (i == (no_queries + 1)) {
                no_statements = Integer.parseInt(line);
            } else {
                Sentence st = store_statements_in_KB(line, (i - (no_queries + 2)));
                KbInit.add(st);
            }
        }
    }
    
    public void print_output(boolean[] bool) throws IOException {
        File fl = new File("/Users/dharmik/NetBeansProjects/Hw3/src/hw3/output.txt");
        PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fl, false)));
            for (short i = 0; i < bool.length; i++) 
            {
                if(i>0)
                    output.write("\n");
                if(bool[i]==true)
                    output.write("TRUE");
                else
                    output.write("FALSE");
            }
        output.close();
    }
}
    
