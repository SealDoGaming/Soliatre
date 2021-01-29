
public class Card {
	private String rank;
	private int rankValue;
	private String suit;
	private boolean visability;
	
	
	public Card(String inputSuit,String inputRank) {
		suit = inputSuit;
		rank = inputRank;
		visability = false;
		rankValue=rankCalc(rank);
	}
	private int rankCalc(String valueOfRank) {
		int num=0;
		try {
			//takes the number inputed and goes to check if it is for a collumn
			num = Integer.valueOf(valueOfRank);
			
		}catch (Exception e){
			if(valueOfRank=="A") {
				num=1;
			}else if(valueOfRank=="J") {
				num=11;
			}else if(valueOfRank=="Q") {
				num=12;
			}else if(valueOfRank=="K") {
				num=13;
			}
		}
		return num;
	}
	public String getSuit() {
		return suit;
	}
	public String getColor() {
		if(suit=="♥"||suit=="♦") {
			return "red";
		}else {
			return "black";
		}
		
	}
	public int getColorNumber() {
		for(int i=0; i<Collumn.getSuits().length;i++) {
			if(Collumn.getSuits()[i].equals(suit)) {
				return i;
			}
		}
		return -1;
	}
	public boolean getBooleanColor() {
		//Red is True
		if(getColor().equals("red")) {
			return true;
		}else {
			return false;
		}
	}
	
	public String getRank(){
		return rank;
	}
	public int getRankValue() {
		return rankValue;
	}
	public boolean getVis() {
		return visability;
	}
	
	public void toggleVis() {
		visability = !visability;
	}
	public void setVis(boolean newVis) {
		visability=newVis;
	}
	@Override
	public String toString() {
		return rank+" "+suit;
	}
}
