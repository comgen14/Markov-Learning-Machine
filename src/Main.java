import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	static MemoArray memoizationReward = new MemoArray();
	static MemoArray memoizationAction = new MemoArray();
	static MemoArray memoCheck = new MemoArray();
	
	public static void main (String[] args) throws FileNotFoundException{
		ArrayList<State> data = getInfo("C:\\\\Users\\Daniel\\Documents\\Programming Projects\\Java\\Workspace\\CS4375HW3P1\\src\\test2.in");
		Answer[][] x = new Answer[data.size()][20];
		
		for(int i = 0; i < 20; i++){
			for(int j = 0; j < data.size(); j++){
				x[j][i] = bellmanEquation(data, j, i, .9);
			}
		}
		
		for(int i = 0; i < 20; i++){
			System.out.print((i+1) + " ");
			for(int j = 0; j < data.size(); j++){
				x[j][i].print();
			}
			System.out.println("");
		}
	}
	
	public static Answer bellmanEquation(ArrayList<State> data, int state, int iteration, double discountFactor){
		/*How the Bellman equation works
		 * 
		 * we compare the possible rewards given for each action, choosing the most rewarding using the loops defined be variable k
		 * 
		 * The first time an answer is calculated, it is added to the memoization arrays to prevent repetitive calculations.
		 * 
		 * loops defined by the variable j iterate through the the different possible stages available to each action. Options with a probability of 0.0 are skipped. 
		 * 
		 */
		double answer = 0.0;
		int answerAction = 0;
		double tempAnswer = 0.0;
		int tempAnswerAction = 0;
		
		
		if(memoCheck.get(state, iteration) == 1.0){ //we've calculated this before
			return new Answer(data.get(state).getName(), data.get(state).getAction(memoizationAction.get(state, iteration).intValue()).getName(), memoizationReward.get(state, iteration));
		}else{										//we need to calculate this
			if(iteration == 0){ 					//calculates the first iteration
				for(int k = 0; k < data.get(state).getActionsLength(); k++){ //finds max value among actions
					for(int j = 0; j < data.size(); j++){	//sums the total reward for a given action
						if(data.get(state).getAction(k).getProbability(data.get(j).getName()) == 0.0){
							continue;
						}
						tempAnswer += data.get(state).getAction(k).getProbability(data.get(j).getName()) * data.get(j).getReward();
					}
					tempAnswer *= discountFactor;
					tempAnswer += data.get(state).getReward();
					tempAnswerAction = k;
					if(k == 0){		//This statement exist to intialize answer to the very first value. That way negative values don't get rooted out for being less than zero.
						answer = tempAnswer;
						answerAction = tempAnswerAction;
					}
					if(tempAnswer > answer){
						answer = tempAnswer;
						answerAction = tempAnswerAction;
					}
				}
				memoizationReward.add(state, iteration, (double) data.get(state).getReward());
				memoizationAction.add(state, iteration, (double) answerAction);
				memoCheck.add(state, iteration, 1.0);
				
				return new Answer(data.get(state).getName(), data.get(state).getAction(memoizationAction.get(state, iteration).intValue()).getName(), memoizationReward.get(state, iteration));
			}else{
				for(int k = 0; k < data.get(state).getActionsLength(); k++){
					tempAnswer = 0.0;
					
					for(int j = 0; j < data.size(); j++){
						tempAnswer += data.get(state).getAction(k).getProbability(data.get(j).getName()) * bellmanEquation(data, j, iteration - 1, discountFactor).getReward();
					}
					tempAnswer *= discountFactor;
					tempAnswer += data.get(state).getReward();
					tempAnswerAction = k;
					if(k == 0){
						answer = tempAnswer;
						answerAction = tempAnswerAction;
					}
					if(tempAnswer > answer){
						answer = tempAnswer;
						answerAction = tempAnswerAction;
					}
				}
				memoizationReward.add(state, iteration, answer);
				memoizationAction.add(state, iteration, (double) answerAction);
				memoCheck.add(state, iteration, 1.0);
			}
		}
		
		return new Answer(data.get(state).getName(), data.get(state).getAction(answerAction).getName(), answer);
	}
	
	public static String removeChar(String s, char c){
		StringBuffer r = new StringBuffer( s.length() );
		r.setLength( s.length() );
		int current = 0;
		for (int i = 0; i < s.length(); i ++) {
			char cur = s.charAt(i);
			if (cur != c) r.setCharAt( current++, cur );
		}
		return r.toString();
	}
	
	public static ArrayList<State> getInfo(String file) throws FileNotFoundException{
		ArrayList<State> x = new ArrayList<State>();
		
		File f = new File(file);
		Scanner sc1 = new Scanner(f);
		Scanner sc2;
		String temp = "";
		String tempName;
		int tempReward;
		String tempAction;
		String tempProb;
		String tempDest;
		int i = 0;
		
		
		while(sc1.hasNext()){
			temp = sc1.nextLine();
			sc2 = new Scanner(temp);
			
			tempName = sc2.next();
			tempReward = sc2.nextInt();
			
			x.add(new State(tempReward, tempName));
			
			while(sc2.hasNext()){
				tempAction = sc2.next();
				tempAction = removeChar(tempAction, '(');
				tempDest = sc2.next();
				tempProb = sc2.next();
				tempProb = removeChar(tempProb, ')');
				
				if(!x.get(i).addAction(new Action(tempAction, Double.parseDouble(tempProb), tempDest))){
					x.get(i).getAction(tempAction).add(Double.parseDouble(tempProb), tempDest);
				}
			}
			
			sc2.close();
			i++;
		}
		
		sc1.close();
		return x;
	}
	
	public static class Answer{
		String state = "";
		String action = "";
		double reward = 0.0;
		
		public Answer(){
			state = "";
			action = "";
			reward = 0.0;
		}
		
		public void print() {
			System.out.format("(%s %s %.4f) ", state, action, reward); 
		}

		public Answer(String name, String name2, Double double1) {
			state = name;
			action = name2;
			reward = double1;
		}

		public Double getReward() {
			return reward;
		}
		
	}
	public static class MemoArray{
		private Double[][] array;
		
		public MemoArray(){
			array = new Double[10][10];
			
			for (int row = 0; row < 10; row ++)
			    for (int col = 0; col < 10; col++)
			        array[row][col] = 0.0;
		}
		private void checkSize(int i, int j){
			Double[][] temp;
			
			if(i > j){
				if(i >= array.length){
					temp = new Double[i+5][i+5];
					for(int x = 0; x < temp.length; x++){
						for(int y = 0; y < temp[x].length; y++){
							temp[x][y] = 0.0;
						}
					}
					for(int k = 0; k < array.length; k++){
						for(int l = 0; l < array[k].length; l++){
							temp[k][l] = array[k][l];
						}
					}
					array = temp;
				}
			}else{
				if(j >= array.length){
					temp = new Double[j+5][j+5];
					for(int x = 0; x < temp.length; x++){
						for(int y = 0; y < temp[x].length; y++){
							temp[x][y] = 0.0;
						}
					}
					for(int k = 0; k < array.length; k++){
						for(int l = 0; l < array[k].length; l++){
							temp[k][l] = array[k][l];
						}
					}
					array = temp;
				}
			}
		}
		public void add(int i, int j, Double d){
			checkSize(i, j);
			
			array[i][j] = d;
		}
		public Double get(int i, int j){
			checkSize(i,j);
			
			return array[i][j];
		}
	}
	public static class State {
		private String name;
		private int reward;
		private ArrayList<Action> actions;
		
		public State(int r, String n){
			name = n;
			reward =r;
			actions = new ArrayList<Action>();
		}
		
		public int getActionsLength() {
			return actions.size();
		}

		public void print() {
			System.out.format("%s %d ", name, reward);
			for(int i = 0; i < actions.size(); i++){
				actions.get(i).print();
			}
			
		}

		public String getName(){
			return name;
		}
		public int getReward(){
			return reward;
		}
		public Action getAction(int i){
			return actions.get(i);
			
		}
		
		public Action getAction(String s){ //returns null if action isn't present return null
			for(int i = 0; i < actions.size(); i++){
				if(s.compareTo(actions.get(i).getName())==0){
					return actions.get(i);
				}
			}
			return null;
			
		}
		public boolean addAction(Action a){
			for(int i = 0; i < actions.size(); i++){
				if(actions.get(i).getName().equals(a.getName())){
					return false;
				}
			}
			actions.add(a);
			return true;
		}
	}
	public static class Action {
		private String name;
		private ArrayList<String> destination;
		private ArrayList<Double> probability;
		
		public Action(String s){
			name = s;
			destination = new ArrayList<String>();
			probability = new ArrayList<Double>();
		}

		public Action(String s, double d, String t){
			name = s;
			destination = new ArrayList<String>();
			probability = new ArrayList<Double>();
			destination.add(t);
			probability.add(d);
		}
		
		public void add(double d, String s){
			destination.add(s);
			probability.add(d);
		}
		
		public Double getProbability(int i){
			return probability.get(i);
		}
		
		public Double getProbability(String s){
			for(int i = 0; i < probability.size(); i++){
				if(s.compareTo(destination.get(i))==0){
					return probability.get(i);
				}
			}
			return 0.0;
		}
		
		public String getName(){
			return name;
		}
		
		public void print(){
			for(int i = 0; (i < destination.size() && i < probability.size()); i++){
				System.out.format("(%s %s %f) ", name, destination.get(i),probability.get(i));
			}
			
		}
	}
}
