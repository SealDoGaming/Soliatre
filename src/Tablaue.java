import java.util.ArrayList;
import java.util.Scanner;

public class Tablaue {
	private Collumn deck = new Collumn(true);
	private Collumn fakeDeck;
	private ArrayList<Collumn> table=new ArrayList<Collumn>();
	private ArrayList<Collumn> foundations = new ArrayList<Collumn>();
	private Collumn discard = new Collumn();
	
	
	private Scanner input = new Scanner(System.in);
	
	public Tablaue() {
		
		deck.shuffle();
		/*
		deck.toggleCard(0);
		System.out.println(deck);
		for(int i = 0;i< 10;i++) {
			System.out.print(deck.showCard(0));
			deck.removeCard(0);
			deck.toggleCard(0);
		}
		*/
		
		for(var i = 0;i<7;i++) {
			Collumn collumn = new Collumn();
			table.add(collumn);
		}
		
		for(var i = 0;i<4;i++) {
			Collumn collumn = new Collumn();
			foundations.add(collumn);
		}
		
		
	}
public Tablaue(boolean unsolved) {
		
		if(unsolved) {
			deck.shuffle();
		}
		/*
		deck.toggleCard(0);
		System.out.println(deck);
		for(int i = 0;i< 10;i++) {
			System.out.print(deck.showCard(0));
			deck.removeCard(0);
			deck.toggleCard(0);
		}
		*/
		
		for(var i = 0;i<7;i++) {
			Collumn collumn = new Collumn();
			table.add(collumn);
		}
		
		for(var i = 0;i<4;i++) {
			Collumn collumn = new Collumn();
			foundations.add(collumn);
		}
		
		
	}
	public void startGame() {
		distributeCards();
		
	}
	private void distributeCards() {
		for(var i = 0;i<7;i++) {
			for(var j = 0;j<=i;j++) {
				deck.moveTo(table.get(i));
			}
			
		}
		flipTopCards();
		
	}
	public void displayGame() {
		//Temporary placeholder for where drawn cards go.
		
		if(discard.getSize()!=0) {
			System.out.print(discard.viewCard(discard.getSize()-1));
		}
		else {
			printCardSpace();
		}
		emptySpace(1);
		
		//Temporary placeholder for place for stacked cards.
		for(int i=0; i <foundations.size();i++) {
			if(foundations.get(i).getSize()==0) {
				printCardSpace();
			}else {
				System.out.print(""+foundations.get(i).viewCard(foundations.get(i).getSize()-1));
			}
		}
		/*
		printCardSpace();
		printCardSpace();
		printCardSpace();
		printCardSpace();
		*/
		if(deck.getSize()!=0) {
			System.out.print(deck.viewCard(0));
		}
		else {
			printCardSpace();
		}
		System.out.println();
		
		printLine(7);
		for(int i = 0; i<table.get(largestCollumn(table)).getSize();i++) {
			
			for(int c=0;c<7;c++) {
				try {
					System.out.print(""+table.get(c).viewCard(i));
				}catch(Exception e){
					if(i==0) {
						printCardSpace();
					}else {
						emptySpace(1);
					}
				}
				
			}
			System.out.println();
			
		}
	}
	
	public void playRound() {
		//selects the location to move cards from
		flipTopCards();
		System.out.println();
		displayGame();
		Collumn firstLocation = selectLocation("Select first location: ",true,null);
		if(firstLocation==fakeDeck) {
			//Draw from deck
			if(deck.getSize()>0) {
				System.out.println("You draw from the deck");
				deck.toggleVis(0);
				deck.moveTo(discard);
			}else {
				System.out.println("You move cards from discard to the deck");
				discard.moveTo(discard.getSize(),deck);
			}
			
			
			return;
		}
		if(firstLocation.getSize()==0) {
			System.out.println("That Selection is Empty");
		// if location is a collumn
		}else {
			int cardAmount;
			if(table.contains(firstLocation)){
				cardAmount = selectCardAmount(firstLocation);
				if(cardAmount==0) {
					return;
				}
			
			}else {
				cardAmount = 1;
				
			}
			displayGame();
			Collumn secondLocation = selectLocation("Select a location to move cards to. -1 to go back: ",false,firstLocation);
			System.out.println();
			//Determine if a foundation destination or layout destination.
			if(foundations.contains(secondLocation)){
				if(cardAmount==1) {
					Card moveCard=firstLocation.viewCard(firstLocation.getSize()-1);
					if(secondLocation.getSize()>0) {
						
						Card topCard = secondLocation.viewCard(secondLocation.getSize()-1);
						if(topCard.getSuit().equals(moveCard.getSuit())&&topCard.getRankValue()+1==moveCard.getRankValue()) {
							firstLocation.moveTo(cardAmount, secondLocation);
						}
					}else {
						if(moveCard.getRankValue()==1) {
							firstLocation.moveTo(cardAmount, secondLocation);
						}else {
							System.out.println("Not an Ace");
						}
						
					}
				}else {
					System.out.println("Too many cards.");
				}
			
			}else if(table.contains(secondLocation)){
				Card bottomCard = firstLocation.viewCard(firstLocation.getSize()-cardAmount);
				Card topCard = secondLocation.viewCard(secondLocation.getSize()-1);
				if(bottomCard.getRankValue()+1==topCard.getRankValue()) {
					if(!bottomCard.getColor().equals(topCard.getColor())) {
						firstLocation.moveTo(cardAmount, secondLocation);
					}else {
						System.out.println("Cards must alternate in color");
					}
				}else {
					System.out.println("Cards must ascend in value");
				}
				
			}else if(secondLocation==null){
				return;
			} else {
				System.out.println("Can't put cards there.");
				
			}
			
		}
		
		
	}
	
	public boolean won() {
		
		for(Collumn i:foundations) {
			if(i.getSize()<13) {
				
				
				return false;
			}else {
				for(int j =0; j<i.getSize()-1;j++) {
					if(i.viewCard(j).getRankValue()+1!=i.viewCard(j+1).getRankValue()) {
						return false;
					}
				}
			}
			
		}
		return true;
		
	}
	
	private Collumn selectLocation(String words,boolean deckAllowed,Collumn prev) {
		while(true) {
			
			System.out.println();
			System.out.print(words);
			String selection = input.nextLine().toLowerCase();
			Collumn returned = null;
			try {
				//takes the number inputed and goes to check if it is for a collumn
				int num = Integer.valueOf(selection);
				if(num==-1&&!deckAllowed) {
					return null;
				}
				returned = table.get(num-1);
			}catch (Exception e){
				if(selection.equals("")&&deckAllowed) {
					
					return fakeDeck;
				}else if(selection.equals("help")) {
					//Prints Controls
					System.out.println("Controls");
					System.out.println("Enter - Draw Card");
					System.out.println("Numbers 1-9 - Select Collumn");
					System.out.println("a,b,c,d - Select Foundations");
					System.out.println("/ - Select Discard");
				}else if(selection.length()==1){
					char single =selection.charAt(0);
					int alphabetNum = single-'a';
					if(alphabetNum>=0 && alphabetNum<4) {
						returned =foundations.get(alphabetNum);
						
					}else if(single=='/'&&deckAllowed){
						//discard piled
						returned = discard;
					}
				}
			}
			if(returned!=null&&returned!=prev) {
				return returned;
			}else {
				System.out.println("Invalid Input");
			}
		}
	}
	public void solve() {
		for(int i=0;i<foundations.size();i++) {
			for(int j=0;j<13;j++) {
				deck.viewCard(0).toggleVis();
				deck.moveTo(foundations.get(i));
			}
		}
	}
	
	private int selectCardAmount(Collumn pillar){
		while(true) {
			System.out.println();
			System.out.println(pillar);
			System.out.print("How many cards do you take. Take 0 to go back: ");
			int value=0;
			try {
				//takes the number inputed and goes to check if it is for a collumn
				
				value = input.nextInt();
			}catch (Exception e){
				input.nextLine();
				System.out.println("Invalid Input");
				continue;
			}
			if(value>0&&value<=pillar.getSize()) {
				//also add in conditions against cards not alternating or not in order.
				if(pillar.testTopVis(value)) {
					if(pillar.testTopOrder(value)) {
						if(pillar.testTopColors(value)) {
							
							input.nextLine();
							return value;
						}else {
							System.out.println("Cannot take colors that do not follow the color pattern");
						}
						
					}else {
						System.out.println("Cannot take cards out of order");
					}
				}
				
				else {
					System.out.println("Cannot take unrevealed cards");
				}
			}else if(value==0){
				input.nextLine();
				return 0;
			}else {
				System.out.println("Invalid Card Amount");
			}
				
			
		}
	}
	
	
	public void flipTopCards() {
		for(Collumn i:table) {
			if(i.getSize()>0) {
				
				if(!i.viewCard(i.getSize()-1).getVis()) {
					i.viewCard(i.getSize()-1).toggleVis();
				}
			}
		}
	}
	private int largestCollumn(ArrayList<Collumn> collumnArray) {
		int counter = 0;
		for(int i =1;i<collumnArray.size();i++){
			if(collumnArray.get(counter).getSize() < collumnArray.get(i).getSize()) {
				counter = i;
			}
		}
		return counter;
	}
	
	
	private void printCardSpace() {
		System.out.print("|   |");
	}
	private void emptySpace(int repeat) {
		for(int i = 0;i<repeat;i++) {
			System.out.print("     ");
		}
	}
	private void printLine(int repeat) {
		for(int i = 0;i<repeat;i++) {
			System.out.print("-----");
		}
		System.out.println();
	}
	public Collumn getDeck() {
		return deck;
	}
	public ArrayList<Collumn> getTable(){
		return table;
	}
	public Collumn getTableCollumn(int index) {
		return table.get(index);
	}
	public ArrayList<Collumn> getFoundation(){
		return foundations;
	}
	public Collumn getFoundation(int index) {
		return foundations.get(index);
	}
	public Collumn getDiscard() {
		return discard;
	}
	
}


