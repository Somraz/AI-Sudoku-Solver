import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.border.Border;
import javax.swing.SwingWorker;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

//Sudoku solver using Constraing propogation and backtracking
class Sudoku extends JFrame implements ActionListener{
    
static Sudoku sudo;
Dimension d;
JPanel pl=new JPanel();
static JPanel pan = new JPanel();
JPanel back = new JPanel();
static MyButton bt[][] = new MyButton[9][9];
ArrayList<JLabel> show=new ArrayList<JLabel>();
JScrollPane scrollPane = new JScrollPane(pl);
JLabel msg=new JLabel("Messages");
 
//PLAY Button
static JButton but=new JButton("PLAY");
static JLabel lb=new JLabel("SUDOKU");
static JPanel pan1[]=new JPanel[9];
public Integer boardSize;

//When input is taken from file
static JButton in=new JButton("INPUT");

//When input is taken from the GUI
static JButton cl=new JButton("GUI INPUT");

public static ArrayList<MyButton> checked=new ArrayList<MyButton>();
    
//All the tiles of sudoku
public List<MyButton> tiles;
    
//All the constraints of a sudoku
public List<AllDiffConstraint> all_constraints;
    
//Initial state of the board
public State base_state;
    
//Map to relate a cell to its constraints
public Map<MyButton, List<AllDiffConstraint>> cell_constraints;
 
//Creating border around the Sudoku board
Border bor =BorderFactory.createLineBorder(new Color(200,80,80),10,true);
Border bor1 =BorderFactory.createLineBorder(Color.black,3,true);
   Sudoku(){
     //Creating GUI for the game
     super("GAME OF SUDOKU");
     Dimension d=Toolkit.getDefaultToolkit().getScreenSize();
     setSize(d.width,d.height);
     setResizable(false);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setLayout(null);
     addButtons();
     
     //Adding the buttons
     but.setFont(new Font("cooper black", 2,32));
     but.setBounds(d.width-400,200,150,60);
     but.setBackground(Color.cyan);
     add(but);
     in.setFont(new Font("cooper black", 2,32));
     in.setBounds(d.width-400,400,150,60);
     in.setBackground(Color.GREEN);
     add(in);
     cl.setFont(new Font("cooper black", 2,32));
     cl.setBounds(d.width-400,600,250,60);
     cl.setBackground(Color.GREEN);
     add(cl);
     add(lb);
     lb.setBounds((d.width-200)/2,30,250,80);
     lb.setForeground(Color.black);
     lb.setFont(new Font("algerian", 1,50));
     but.addActionListener(this);
     in.addActionListener(this);
     cl.addActionListener(this);
scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

msg.setBounds(0,10,200,100);
back.add(msg);
msg.setFont(new Font("Arial",1,28));
scrollPane.setBorder(bor);
scrollPane.setBounds(0,(d.height-850)/2,400,900);
pl.setBackground(Color.white);
pl.setLayout(new BoxLayout(pl, BoxLayout.Y_AXIS));
back.add(scrollPane);
pan.setBorder(bor);
pan.setBounds((d.width-900)/2,(d.height-850)/2,900,900);
pan.setLayout(new GridLayout(3,3));
back.setLayout(null);
back.setSize(d.width,d.height);
back.add(pan);
add(back);
back.setVisible(true);
setVisible(true);

//Initializing all the class variables
base_state = new State();
boardSize = 9;
all_constraints = new ArrayList<AllDiffConstraint>(boardSize * 3);
tiles = new ArrayList<MyButton>(boardSize * boardSize);
cell_constraints = null;

//Initializing the list of Sudoku tiles
for (int i = 0; i < boardSize; i++) {
    for(int j=0;j<boardSize;j++)
            tiles.add(bt[i][j]);
        }

//This method creates a list of all the constraints in the sudoku
        ini_constraints();
    }
   
//This method is invoked when input is taken from a file
void input_values(){
    checked.clear();
    //Path for the input file is taken from the user
    String path=JOptionPane.showInputDialog("Enter file path");
    try{
    String pathToCsv=path;
    BufferedReader csvReader = new BufferedReader(new FileReader(pathToCsv));
    String row;
    int count=0;
while ((row= csvReader.readLine()) != null) {
    int cell_id, cell_val;
    //Contains all the values in a particular row
    String []data = row.split(",");
     
    for (int y = 0; y <boardSize; y++) {
        //Computing cell id
            cell_id = count * 9 + y;
            if (!data[y].equals("0")) {
                   cell_val = Integer.parseInt(data[y]);
                   MyButton cell = tiles.get(cell_id);
                   cell.value = cell_val;
                   cell.domain = new LinkedList<Object>();
                   tiles.set(cell_id, cell);
 
                   //Assigning value to the corresponding Sudoku tile
                   base_state = base_state.assign(cell, cell_val);
                   checked.add(cell);
                                    }
         
                                }
       count++;                      
}
//To catch exceptions while file reading
csvReader.close();
    }
    catch(Exception e){
    e.printStackTrace();}

    //Displaying the assigned cell values on the Sudoku board
     for (Map.Entry<MyButton,Object> entry : base_state.assignments.entrySet()) {
                MyButton bt1=entry.getKey();
                Object curr_value=entry.getValue();
                if(curr_value!=null)
                bt1.setText(Integer.toString((Integer)curr_value));          
            }
     
}

/*This method removes the assigned values from the domains 
of other cells to satisfy the constraints of this cell and 
then start recursive backtracking*/
public State start() {
                  
    for(int i=0;i<checked.size();i++){
        base_state=forward_checking(base_state,checked.get(i));
    }
        State ans = recursive_backtrack(base_state);
        return ans;
    }
//It checks if the Sudoku is solved in a given state.
public boolean solved(State state) {
        if (tiles.size() > state.assignments.size()) {
            return false;
        }
   //It returns false if all the constraints are not satisfied
        for (AllDiffConstraint adc : all_constraints) {
            if (!adc.satisfied(state)) {
                return false;
            }
        }
        return true;
    }

//It adds all the buttons to the GUI
void addButtons()
{
    int count=0,count1=0;
    for(int i=0;i<9;i++){
        //Creating panel to add the buttons
        pan1[i]=new JPanel();
            pan1[i]=new JPanel();
            pan1[i].setLayout(new GridLayout(3,3));
            pan1[i].setBorder(bor1);
            pan.add(pan1[i]);
    }
for(int i=0;i<9;i++){
    count1=0;
    count=(int)Math.pow(2,i/3);
    if(count==1)
        count=0;
    for(int j=0;j<3;j++){
        for(int k=0;k<3;k++){
            bt[i][count1]=new MyButton(i,count1,null);
            bt[i][count1].addActionListener(this);
            bt[i][count1].setFont(new Font("cooper black", 2,32));
            pan1[(i/3)+j+count].add(bt[i][count1]);
            
            count1++;
        }
    }
    
}

}
/*This method is invoked when input is to
be taken from the GUI*/
void put_it(){
    checked.clear();
    for(int i=0;i<boardSize;i++) {
    int cell_id, cell_val;  
    for (int y = 0; y <boardSize; y++) {
            cell_id = i*9 + y;
            if(!bt[i][y].getText().equals("")){
                   cell_val = Integer.parseInt(bt[i][y].getText());
                   MyButton cell = tiles.get(cell_id);
                   cell.value = cell_val;
                   cell.domain = new LinkedList<Object>();
                   tiles.set(cell_id, cell);

                   base_state = base_state.assign(cell, cell_val);
                   checked.add(cell);
                                    }                   
    }
}   
}
//This method is invoked to start solving the sudoku
void play(){
     State solution = sudo.start();
           if (solution == null) 
             System.out.println("BAD");
           System.out.println("OK");   
}

/*This class represents the all different values in a
row, column and grid Constraint.*/
public class AllDiffConstraint {

/*List of cells related to the all different constraint of a particular unit i.e
    row, cell or the grids. There will be total 27 such constraints for entire Sudoku*/
     public List<MyButton> tiles;

     public AllDiffConstraint() {
            tiles = new LinkedList<MyButton>();
        }

   //This method checks if all the constraints for a particular tile are satisfied
     public boolean satisfied(State state) {
            boolean[] visited = new boolean[boardSize + 1];
            for (MyButton tile : tiles) {
                Integer value = (Integer) state.assignments.get(tile);
                if (value == null || visited[value]) {
                    return false;
                }
                visited[value] = true;
            }
            return true;
        }
//This method checks if current state is consistent and no constraint is being violated
        public boolean consistent(State state) {
            //To check that all different constraint is not violated 
            boolean[] visited = new boolean[boardSize + 1];
            
            //To check that there is no tile left with zero domain values
            boolean[] free = new boolean[boardSize + 1];
            int num = 0;

            for (MyButton tile : tiles) {
                for (Object value : domain_tile(state, tile)) {
                    if (!free[(Integer) value]) {
                        num++;
                        free[(Integer) value] = true;
                    }
                }

                Integer value = (Integer) state.assignments.get(tile);
                if (value != null) {
                    if (visited[value]) {
                        return false;
                    }
                    visited[value] = true;
                }
            }

            if (tiles.size() > num) {
                return false;
            }
            return true;
        }
// To check if the list of tiles for a particular unit 
//of all different constraint contains this cell
        public boolean contains_tile(int row,int col) {
            for (MyButton tile : tiles) {
                if (tile.row== row && tile.column==col) {
                    return true;
                }
            }
            return false;
        }
    }

//A customised class for creating Buttons
class MyButton extends JButton{
    public int row;
    public int column;
    public java.util.List<Object> domain;
    public Object value;
    
    MyButton(int row, int column, Object value){
        this.row=row;
        this.column=column;
        domain=new LinkedList<Object>();
        
        if(value == null){
        for (int i = 1; i < 10; i++) {
	      domain.add(i);
	}
        }
    }

  }

//It selects all the blank or unassigned cells in a given state.
public List<MyButton> all_blank(State state) {
        List<MyButton> blanks = new LinkedList<MyButton>();
        for (MyButton c : tiles) {
            if (state.assignments.get(c) == null) {
                blanks.add(c);
            }
        }
        return blanks;
    }  

//Use of the heuristic Most Constrained Variable (MCedV) 
//to find the blank cell with smaller domain
   
    public MyButton cell_heuristic_MCV(State state) {
        int min = Integer.MAX_VALUE;
        MyButton minTile = null;

        if (tiles.size() == state.assignments.size()) {
            return null;
        }

        for (MyButton t : tiles) {
            if (state.assignments.get(t) == null) {
                int val = domain_tile(state, t).size();
                if (val < min) {
                    min = val;
                    minTile = t;
                }
            }
        }

        return minTile;
    }

//Use of the heuristic Least Constraining Value (LCV) to find the best
//value to being assigned for a blank cell.
public Object value_heuristic_LCV(State state, List<Object> domain, MyButton tile) {
        List<MyButton> blanks = all_blank(state);
        int count, min = Integer.MAX_VALUE;
        Object best = null;
        for (Object v : domain) {
            count = 0;
            for (MyButton c : blanks) {
                if ((c.row != tile.row) || (c.column!=tile.column)) {
                    if (state.domains.get(c).contains(v) && isConstrained(c, tile)) {
                        count++;
                    }
                }
            }
            if (count < min) {
                min = count;
                best = v;
            }
        }
        return best;
    }


//It evaluates how an assignment over a cell affects the rest of the cells.
public State forward_checking(State state, MyButton tile) {
        List<AllDiffConstraint> rul = rules_tile(tile);
        Object value = state.assignments.get(tile);

        for (AllDiffConstraint adc : rul) {
            for (MyButton c : adc.tiles) {
                if (c == tile) {
                    continue;
                }

                List<Object> values = domain_tile(state, c);
                if (values.contains(value)) {
                    values = new LinkedList<Object>(values);
                    values.remove(value);
                    state.domains.put(c, values);
                    /*
                    */
                    if (state.assignments.get(c) == null) {
                        if (values.size() == 1) {
                            state = state.assign(c, values.get(0));
                        } else if (values.size() == 0) {
                            continue;
                        }
                    }
                }
            }
        }

        return state;
    }
  
//Returns the rules that apply/affect a given cell.
public List<AllDiffConstraint> rules_tile(MyButton tile) {
        if (cell_constraints != null) {
            return cell_constraints.get(tile);
        }
        cell_constraints = new HashMap<MyButton, List<AllDiffConstraint>>();

        for (AllDiffConstraint adc : all_constraints) {
            for (MyButton c : adc.tiles) {
                if (cell_constraints.containsKey(c)) {
                    cell_constraints.get(c).add(adc);
                } else {
                    List<AllDiffConstraint> rul = new LinkedList<AllDiffConstraint>();
                    rul.add(adc);
                    cell_constraints.put(c, rul);
                }
            }
        }
        return cell_constraints.get(tile);
    }

//Returns the values in the domain of a given cell in a given state.
public List<Object> domain_tile(State state, MyButton tile) {
        List<Object> values = state.domains.get(tile);
        if (values != null) {
            return values;
        }
        return tile.domain;
    }

//Verifies the consistency of a new state.
public boolean consistent_state(State state) {
        for (AllDiffConstraint adc : all_constraints) {
            if (!adc.consistent(state)) {
                return false;
            }
        }
        return true;
    }
 
/* Backtracking Recursive Algorithm. It selects add blank cell, 
   assign one of the values in the domain of the cell, executes forward 
   checking, and repeats this process until there is no remaining 
   candidate value or a solution has been found.
     */
public State recursive_backtrack(State state) {
        if (solved(state)) {
            return state;
        }

        MyButton tile;
 
        tile = cell_heuristic_MCV(state);
        if (tile == null) {
            return null;
        }

        List<Object> values = domain_tile(state, tile);
        Object value1 = value_heuristic_LCV(state, values, tile);
            values.remove(value1);
            values.add(0, value1);

        for (Object value : values) {
            State a2 = state.assign(tile, value);
            a2 = forward_checking(a2, tile);
            
            if (!consistent_state(a2)) {
                continue;
            }
            for (Map.Entry<MyButton,Object> entry : a2.assignments.entrySet()) {
                MyButton bt1=entry.getKey();
                Object curr_value=entry.getValue();
                if(curr_value!=null && bt1.getText()==""){
                bt1.setText(Integer.toString((Integer)curr_value));
                bt1.setForeground(Color.RED);
                JLabel temp=new JLabel("Cell("+bt1.row+","+bt1.column+") is only cell that can host "+curr_value);
                temp.setBorder(bor1);
                temp.setFont(new Font("Arial",2,25));
                pl.add(temp);

                try{
                TimeUnit.MILLISECONDS.sleep(120);
                }
                catch(Exception e){}
                }
                
            }
           
            a2 = recursive_backtrack(a2);
            if (a2 != null) {
                return a2;
            }
        }
        return null;
    }
//It initializes all the constraints for the Sudoku board.
public void ini_constraints() {
        if (all_constraints.size() == 0) {

            // ROW constraints
            for (int row = 0; row < boardSize; row++) {
                AllDiffConstraint rule = new AllDiffConstraint();
                for (int col = 0; col < boardSize; col++) {
                    rule.tiles.add(tiles.get(row * boardSize + col));
                }
                all_constraints.add(rule);
            }

            // COLUMN constraints
            for (int col = 0; col < boardSize; col++) {
                AllDiffConstraint rule = new AllDiffConstraint();
                for (int row = 0; row < boardSize; row++) {
                    rule.tiles.add(tiles.get(row * boardSize + col));
                }
                all_constraints.add(rule);
            }

            // GRID constraints
            int gridSize = (int) Math.sqrt(boardSize);
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    AllDiffConstraint rule = new AllDiffConstraint();
                    for (int rowSS = 0; rowSS < gridSize; rowSS++) {
                        for (int colSS = 0; colSS < gridSize; colSS++) {
                            rule.tiles.add(all_constraints.get(rowSS + row * gridSize).tiles.get(colSS + col * gridSize));
                        }
                    }
                    all_constraints.add(rule);
                }
            }
        }
    } 


//Verifies it exits at least one constraint that relates between two given cell.
public boolean isConstrained(MyButton c1, MyButton c2) {
        for (AllDiffConstraint adc : all_constraints) {
            if (adc.contains_tile(c1.row,c1.column) && adc.contains_tile(c2.row,c2.column)) {
                return true;
            }
        }
        return false;
    }

//This class represents a state of the Sudoku board.

    public class State {
        
        public Map<MyButton, Object> assignments = null;
        public Map<MyButton, List<Object>> domains = null;

        public State() {
            assignments = new HashMap<MyButton, Object>();
            domains = new HashMap<MyButton, List<Object>>();
        }

  //Assigns a value to a cell and returns the new state
         
        public State assign(MyButton tile, Object value) {
            State s2 = new State();
            s2.assignments = new HashMap<MyButton, Object>(assignments);
            s2.assignments.put(tile, value);
            s2.domains = new HashMap<MyButton, List<Object>>(domains);

            // Restrict the domain to only a single value
            List<Object> newDomain = new LinkedList<Object>();
            newDomain.add(value);
            s2.domains.put(tile, newDomain);

            return s2;
        }

    }
public static void main(String args[]){

//Creating Sudoku object
sudo=new Sudoku();

}
//Swing Worker object to perform computation task
//in the background
SwingWorker myWorker= new SwingWorker<String, Void>() {
    @Override
    protected String doInBackground() throws Exception {
        play();
        return null;
    }
};

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton bc=(JButton)e.getSource();
        if(bc.getText().equals("PLAY")){
            
            myWorker.execute();
        }
        else if(bc.getText().equals("GUI INPUT")){
            put_it();
        }
        else if(bc.getText().equals("INPUT")){
            input_values();
        }
        else{
        if(bc.getText().equals("")){
            bc.setText("1");
        }
            else if(bc.getText().equals("9")){
                bc.setText("");
                    
                    }
            else{
                int txt=Integer.parseInt(bc.getText());
                bc.setText(Integer.toString(txt+1));
            }
        }
    }
    }

