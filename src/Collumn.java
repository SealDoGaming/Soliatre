import java.util.ArrayList;
import java.util.Collections;
public class Collumn {
	private ArrayList<Card> cards = new ArrayList<Card>();
	private static ArrayList<Card> deck=new ArrayList<Card>();
	private static int cardAmount = 52;
	
	
	private static String[] suits= {"♥","♦","♠","♣"};
	private static String[] ranks= {"A","2","3","4","5","6","7","8","9","10","J","Q","K",};
	
	public Collumn() {
	}
	public Collumn(boolean isDeck){
		if(isDeck) {
			for(String s: suits) {
				for(String r : ranks) {
					Card nextCard = new Card(s,r);
					deck.add(nextCard);
				}
			}
			cards.addAll(deck);
		}
	}
	
	public void addCards(Card addedCard) {
		cards.add(cards.size(),addedCard);
	}
	/*
	public void addCards(Card[] addedCards) {
		for(Card i: addedCards) {
			cards.add(0,i);
		}
	}
	*/
	public Card removeCard(int index) {
		return cards.remove(index);
	}
	public void removeCard(Card card) {
		cards.remove(card);
	}
	
	public int getSize() {
		return cards.size();
	}
	public Card viewCard(int index) {
		
		return cards.get(index);
	}
	public void moveTo(Collumn collumn) {
		collumn.addCards(cards.get(0));
		removeCard(0);
		
	}
	/*
	public void moveTo(Deck collumn) {
		collumn.addCards(cards.get(0));
		removeCard(0);
		
	}
	*/
	public void moveTo(int cardCount,Collumn collumn) {
		int cardStart =cards.size()-cardCount;
		int startSize = cards.size();
		for(int i =cardStart;i<startSize;i++) {
			/*
 * 			System.out.println(cards.get(i).getRank());
			System.out.println(cards.get(i).getSuit());
			System.out.println(cards.get(i).getVis());
			*/
			Card important =cards.get(cardStart);
			collumn.addCards(important);
			
			cards.remove(important);
		}
	}
	/*
	public void moveTo(int cardCount,Deck collumn) {
		int cardStart =cards.size()-cardCount;
		int startSize = cards.size();
		for(int i =cardStart;i<startSize;i++) {
			
			Card important =cards.get(cardStart);
			important.setVis(false);
			collumn.addCards(important);
			
			cards.remove(important);
		}
	}
	*/
	public boolean testTopVis(int topCards) {
		for( int i =cards.size()-1; i>=cards.size()-topCards;i--) {
			
			Card testCard = cards.get(i);
			if(!testCard.getVis()) {
				return false;
			}
		}
		return true;
	}
	public boolean testTopOrder(int topCards) {
		int prevCardValue=cards.get(cards.size()-1).getRankValue();
		for( int i =cards.size()-2; i>=cards.size()-topCards;i--) {
			
			Card testCard = cards.get(i);
			/*
			System.out.println(prevCardValue);
			System.out.println(testCard.getRankValue());
			*/
			if(prevCardValue+1!=testCard.getRankValue()) {
				return false;
			}
			prevCardValue=testCard.getRankValue();
		}
		return true;
	}
	
	public boolean testTopColors(int topCards) {
		boolean prevCardValue=cards.get(cards.size()-1).getBooleanColor();
		for( int i =cards.size()-2; i>=cards.size()-topCards;i--) {
			
			Card testCard = cards.get(i);
			/*
			System.out.println(prevCardValue);
			System.out.println(testCard.getBooleanColor());
			System.out.println(prevCardValue^testCard.getBooleanColor());
			*/
			//boolean logic = (prevCardValue^testCard.getBooleanColor())&&(!prevCardValue^!testCard.getBooleanColor())
			
			boolean logic=!(prevCardValue^testCard.getBooleanColor());
			
			if(logic) {
				return false;
			}
			
			prevCardValue=testCard.getBooleanColor();
		}
		return true;
	}
	public void toggleVis(int index) {
		cards.get(index).toggleVis();
	}
	public void toggleVisAll() {
		for(Card i:cards) {
			i.toggleVis();
		}
	}
	public static String[] getSuits() {
		return suits;
	}
	public static String[] getRanks() {
		return ranks;
	}

	public static int getCardAmount() {
		return cardAmount;
	}
	
	public static ArrayList<Card> getUnopenedDeck(){
		ArrayList<Card> unopened = new ArrayList<Card>();
		
		for(String s: suits) {
			for(String r : ranks) {
				Card nextCard = new Card(s,r);
				unopened.add(nextCard);
			}
		}
		return unopened;
	}
	
	
	@Override
	public String toString() {
		String cardString = "";
		for(Card i:cards) {
			cardString = cardString+i+"\n";
		}
		return cardString;
	}
	public void shuffle() {
		Collections.shuffle(cards);

	}
}
