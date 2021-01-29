
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Soliatre extends JPanel implements MouseListener, MouseMotionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	private static int WIDTH=800;
	private static int HEIGHT=400;
	private static int TOP_SPACE_HEIGHT=120;
	
	private static int CARD_WIDTH=75;
	private static double CARD_RATIO=1.4;
	
	private static int PILE_SIDE_OFFSET=20;
	private static int PILE_TOP_OFFSET=5;
	*/
	private static int WIDTH=625;
	private static int HEIGHT=600;
	private static int TOP_SPACE_HEIGHT=120;
	
	private static int CARD_WIDTH=70;
	private static double CARD_RATIO=1.4;
	private static int BACK_DESIGN_RADIUS = 9;
	
	private static int CARD_STACK_OFFSET=20;
	
	private static int PILE_SIDE_OFFSET=15;
	private static int PILE_TOP_OFFSET=10;
	
	private static int COLLUMN_OFFSET=130;
	
	private int cardHeight=(int)(Soliatre.CARD_WIDTH*Soliatre.CARD_RATIO);
	
	
	private int mouseX=400;
	private int mouseY=200;
	
	private int grabOffsetX=0;
	private int grabOffsetY=0;
	private Collumn previous=null;
	
	
	private Tablaue game;
	
	private Collumn discard;
	private ArrayList<Collumn> foundations;
	private Collumn deck;
	private ArrayList<Collumn> layout = new ArrayList<Collumn>();
	
	private ArrayList<Collumn> topPiles = new ArrayList<Collumn>();
	private Collumn hand=new Collumn();
	
	
	private BufferedImage cardBack = new BufferedImage(Soliatre.CARD_WIDTH,(int)(Soliatre.CARD_WIDTH*Soliatre.CARD_RATIO),2);
	private BufferedImage handCursorImg = null;
	private static String cursorFileName = "HandCursorNormalBig.png";
	
	private JFrame frame;
	
	
	private boolean winMode= false;
	private int throwRow=0;
	private int throwDepth=0;
	
	private int xVelocityMax=5;
	private int xVelocityMin=-5;
	private int yVelocityMax=2;
	private int yVelocityMin=-2;
	
	private int gravity = 1;
	private double bounciness = 0.75;
	//private double bounciness = .97;
	
	private int pause = 3000;
	
	//[suitNumber ][cardNumber Ace->King]
	// contains info about each thrown card/copy [x,y,dX,dY]
	private BufferedImage bouncyCardBackground = new BufferedImage(WIDTH,HEIGHT,2);
	private ArrayList<ArrayList<ArrayList<Integer>>> thrownCards = new ArrayList<ArrayList<ArrayList<Integer>>>();
	private Graphics2D cardBounceG = bouncyCardBackground.createGraphics();
	
	
    public static void main(String[] args) {
        JFrame frame = new JFrame("Soliatre: An game of Paitienting?!");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        //Tablaue game = new Tablaue(false);
        //game.solve();
        
        Tablaue game = new Tablaue(true);
		game.startGame();
        
        
        Soliatre canvas = new Soliatre(game);
        frame.add(canvas);
        frame.setSize(Soliatre.WIDTH, Soliatre.HEIGHT);
        frame.setVisible(true);
        
        frame.addMouseListener(canvas);
        frame.addMouseMotionListener(canvas);
        new Timer().schedule(new GameClock(canvas), 0, 1000/30);
        
        game.displayGame();
        canvas.frame =frame;
        
        canvas.gameUpkeep();
        
    }
    public Soliatre(Tablaue game) {
    	initializeCardBack();
    	//initializeCardFronts();
    	
    	try {
            handCursorImg = ImageIO.read(new File(cursorFileName));
        } catch (IOException e) {
        }
    	Cursor handCursor = Toolkit.getDefaultToolkit().createCustomCursor(
    			handCursorImg, new Point(0, 0), "Hand Cursor");
    	setCursor(handCursor);
    	this.game=game;
    	
    	//Grabs game info to store.
    	discard =game.getDiscard();
    	foundations=game.getFoundation();
    	deck = game.getDeck();
    	
    	layout=game.getTable();
    	
    	
    	//adds info to easy to draw list of piles
    	topPiles.add(discard);
    	topPiles.add(null);
    	for(int i=0;i<foundations.size();i++) {
    		topPiles.add(foundations.get(i));
    	}
    	topPiles.add(deck);
    	
    	
    }
    @Override
	public void paintComponent(Graphics g ) {
    	Graphics2D g2d = (Graphics2D)g;
        int height = getHeight();
        /*
        g2d.setColor(Color.black);
        g2d.drawOval(0, 0, width, height);
    	*/
    	super.paintComponent(g);
    	
    	Font font = g2d.getFont();
    	float fontSize=font.getSize2D();
    	g2d.setFont(font.deriveFont(fontSize+3));
    	
    	//Draws the background details and stuff
    	paintBackground(g2d);
    	
    	
    	//Draws top area piles like the deck and discard
    	for(int i=0;i<topPiles.size();i++) {
    		Collumn collumn = topPiles.get(i);
    		if(collumn!=null) {
    			if(collumn.getSize()!=0) {
    	    		int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH)*i;
    	    		
    	    		paintCollumn(g2d,collumn,x,PILE_TOP_OFFSET,0);
        		}
    		}
    		
    	}
    	
    	//Draws the layout collumns
    	for(int i=0;i<layout.size();i++) {
    		Collumn collumn = layout.get(i);
    		if(collumn!=null) {
    			if(collumn.getSize()!=0) {
    	    		int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH)*i;
    	    		
    	    		paintCollumn(g2d,collumn,x,COLLUMN_OFFSET,CARD_STACK_OFFSET);
        		}
    		}
    		
    	}
    	
    	
    	if(hand.getSize()>0) {
    		paintCollumn(g2d,hand,mouseX-grabOffsetX,mouseY-grabOffsetY,CARD_STACK_OFFSET);
    	}
    	
    	
    	if(winMode) {
    		
    		
    		for(int i=0;i<thrownCards.size();i++) {
    			ArrayList<Integer> cardInfo = new ArrayList<Integer>();
    			cardInfo = thrownCards.get(i).get(0);
    			Collumn collumn= foundations.get(cardInfo.get(0));
    			Card thrownCard = collumn.viewCard(collumn.getSize()-1-cardInfo.get(1));
    			
    			
    			paintCard(cardBounceG,thrownCard,cardInfo.get(2),cardInfo.get(3),false);
    			//paintCard(g2d,thrownCard,cardInfo.get(2),cardInfo.get(3),false);
    			
        			
        		
        			
    			int x=cardInfo.get(2);
    			if(!(x+CARD_WIDTH<0||x>WIDTH)) {
        			x+=cardInfo.get(4);
        			int y= cardInfo.get(3)+cardInfo.get(5);
        			int dy= cardInfo.get(5)+gravity;
        			if(y+cardHeight>height) {
        				y=height-cardHeight;
        				dy= (int) (-dy*bounciness);
        			}
        			cardInfo.set(2,x);
        			cardInfo.set(3,y);
        			cardInfo.set(5,dy);
        			
    			}
    			
    		}
    		
    		
    		
    	}
    	g2d.drawImage(bouncyCardBackground,null,0,0);
    	
    	
    	
    	/*
    	if(discard.getSize()!=0) {
    		paintCollumn(g2d,discard,PILE_SIDE_OFFSET,PILE_TOP_OFFSET,0);
    	}
    	ArrayList<Collumn> foundations=game.getFoundation();
    	for(int i=0;i<foundations.size();i++) {
    		if(foundations.get(i).getSize()!=0) {
	    		int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Drawing.CARD_WIDTH);
	    		
	    		paintCollumn(g2d,foundations.get(i),x,PILE_TOP_OFFSET,0);
    		}
    	}
    	//Draws the deck
    	Collumn deck = game.getDeck();
    	if(deck.getSize()!=0) {
    		paintCollumn(g2d,deck,PILE_SIDE_OFFSET,PILE_TOP_OFFSET,0);
    	}
    	*/
    	
    	
    	/*
    	Card card1 = new Card("♥","10");
        card1.setVis(true);
        int card1_x=PILE_SIDE_OFFSET;
        int card1_y=PILE_TOP_OFFSET;
        
        Card card2 = new Card("♣","7");
        int card2_x=140;
        int card2_y=50;
        
        paintCard(g2d,card1, card1_x,card1_y,false);
        paintCard(g2d,card2, card2_x,card2_y,false);
        moveCard.setVis(true);
        paintCard(g2d,moveCard, moveCard_x,moveCard_y,true);
        */
    }
    public void paintBackground(Graphics2D g2d) {
    	g2d.setColor(new Color(17, 128, 0));
        g2d.fillRect(00, 00, Soliatre.WIDTH, Soliatre.HEIGHT);
        g2d.copyArea(00, 000, 100, 100,300,300);
        g2d.setColor(new Color(194, 160, 8));
        g2d.fill3DRect(0, 0, Soliatre.WIDTH, TOP_SPACE_HEIGHT, true);
        
        
        
        
        int piles_x=PILE_SIDE_OFFSET;
        int piles_y=PILE_TOP_OFFSET;
        g2d.setColor(new Color(184, 122, 0));
        boolean[] pileCount= {true,false,true,true,true,true,true};
        
        //this draws the top bar's graphics
        for( int i=0;i<7;i++) {
        	if(pileCount[i]) {
        		g2d.draw3DRect(piles_x-2, piles_y-2, Soliatre.CARD_WIDTH+4, (int)(Soliatre.CARD_WIDTH*Soliatre.CARD_RATIO+4), true);
        	}
        	piles_x+=PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH;
        }
        
        //this draws the bottom collumn spots
        int collumns_x=PILE_SIDE_OFFSET;
        int collumns_y=COLLUMN_OFFSET;
        g2d.setColor(new Color(194, 160, 8));
        for( int i=0;i<7;i++) {
        	g2d.fill3DRect(collumns_x-2, collumns_y-2, Soliatre.CARD_WIDTH+4, (int)(Soliatre.CARD_WIDTH*Soliatre.CARD_RATIO+4), true);
        	collumns_x+=PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH;
        }
    }
    
    public void paintCollumn(Graphics2D g2d,Collumn collumn,int x,int y,int offset) {
    	for(int i=0; i<collumn.getSize();i++) {
    		Card card = collumn.viewCard(i);
    		int cardX=x;
    		int cardY=y+offset*i;
    		paintCard(g2d,card,cardX,cardY,false);
    	}
    }
    
    public void paintCard(Graphics2D g2d,Card card,int x,int y,boolean middleCalc) {
    	
	    
	    if(card.getVis()) {
	    	if(middleCalc) {
	    		x-=Soliatre.CARD_WIDTH/2+7;
	    		y-=cardHeight/2+35;
	    	}
	    	
	    	
	    	//g2d.drawImage(cardImages[card.getColorNumber()][card.getRankValue()-1],null, x,y);
	    	
	    	g2d.setColor(new Color(255, 255, 255));
		    g2d.fillRoundRect(x,y, Soliatre.CARD_WIDTH, cardHeight, 10, 10);
	        //NOTE Change this part to be based on suit color
	        if(card.getBooleanColor()) {
	        	g2d.setColor(new Color(255, 0, 0));
	        }else {
	        	g2d.setColor(new Color(0, 0, 0));
	        }
	        
	        g2d.drawString(card.getRank()+" "+card.getSuit(), x+5, y+15);
	        
	        int upside_Down_X=x-5+Soliatre.CARD_WIDTH;
	        int upside_Down_Y=y-15+cardHeight;
	        		
	        g2d.translate((float)upside_Down_X,(float)upside_Down_Y);
	        g2d.rotate(Math.PI);
	        
	        g2d.drawString(card.getRank()+" "+card.getSuit(),0, 0);
	
	        g2d.rotate(-Math.PI);
	        g2d.translate(-(float)upside_Down_X,-(float)upside_Down_Y);
	        
    	}else {
    		g2d.drawImage(cardBack,null, x,y);
    	}
    }

    public void initializeCardBack() {
    	Graphics2D g2d =cardBack.createGraphics();
    	g2d.setColor(Color.white);
    	g2d.fillRoundRect(0,0, Soliatre.CARD_WIDTH, cardHeight, 10, 10);
    	g2d.setColor(new Color(255, 77, 77));
    	g2d.fillRect(4,4, Soliatre.CARD_WIDTH-8, cardHeight-8);
    	g2d.setColor(Color.white);
    	int radius = Soliatre.BACK_DESIGN_RADIUS;
    	for(int j=0; j<98;j+=7) {
    		
    		for(int i=0;i<70;i+=7) {
    			//int radius=10;
    			if(!((i==0||i==63)&&(j==0||j==91))) {
    				g2d.drawOval(i, j, radius, radius);
    			}
    		}
    		
    	}
    	g2d.drawArc(0, 0, radius, radius, 0, -90);
    	g2d.drawArc(Soliatre.CARD_WIDTH, 0, radius, radius, 180, 270);
    	g2d.drawArc(0, cardHeight, radius, radius, 90, 0);
    	g2d.drawArc(Soliatre.CARD_WIDTH, cardHeight, radius, radius, 90, 180);
    	
    }
    /*
    public void initializeCardFronts() {
    	
    	for(int i=0;i<cardImages.length;i++) {
    		for(int j=0;j<cardImages[i].length;j++) {
    			cardImages[i][j] =new BufferedImage(Drawing.CARD_WIDTH,
    					(int)(Drawing.CARD_WIDTH*Drawing.CARD_RATIO),3);
    		}
    	}
    	ArrayList<Card> tempDeck = Collumn.getUnopenedDeck();
    	for(int i=0;i<Collumn.getCardAmount();i++) {
    		int suitNum = i/13;
    		int rankNum = i%13;
    		
    		int x=0;
    		int y=0;
    		
    		BufferedImage temp = cardImages[suitNum][rankNum];

    		
    		Graphics2D g2d =temp.createGraphics();
    		Font font = g2d.getFont();
        	float fontSize=font.getSize2D();
        	g2d.setFont(font.deriveFont(fontSize+3));
        	g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        	
        	
        	Card card = tempDeck.get(i);
        	
        	g2d.setColor(new Color(255, 255, 255));
    	    g2d.fillRoundRect(x,y, Drawing.CARD_WIDTH, cardHeight, 10, 10);
            //NOTE Change this part to be based on suit color
            if(card.getBooleanColor()) {
            	g2d.setColor(new Color(255, 0, 0));
            }else {
            	g2d.setColor(new Color(0, 0, 0));
            }
            
            g2d.drawString(card.getRank()+" "+card.getSuit(), x+5, y+15);
            
            int upside_Down_X=x-5+Drawing.CARD_WIDTH;
            int upside_Down_Y=y-15+cardHeight;
            		
            g2d.translate((float)upside_Down_X,(float)upside_Down_Y);
            g2d.rotate(Math.PI);
            
            g2d.drawString(card.getRank()+" "+card.getSuit(),0, 0);

            g2d.rotate(-Math.PI);
            g2d.translate(-(float)upside_Down_X,-(float)upside_Down_Y);
    	}
    	
    }
    */
    
    public void addThrow() {
    	if(throwDepth<13) {
	    	ArrayList<ArrayList<Integer>> cardInfo= new ArrayList<ArrayList<Integer>>();
	    	ArrayList<Integer> firstCard =new ArrayList<Integer>();
	    	Random randomNumGen= new Random();
	    	int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+CARD_WIDTH)*(throwRow+2);
	    	int y=PILE_TOP_OFFSET;
	    	int dx=0;
	    	
	    	while((Math.abs(dx)<1)) {
	    		dx=randomNumGen.nextInt(xVelocityMax - xVelocityMin + 1) + xVelocityMin;
	    	}
	    	
	    	/*
	    	while((Math.abs(dx)<0)) {
	    		dx=randomNumGen.nextInt(xVelocityMax - xVelocityMin + 1) + xVelocityMin;
	    	}
	    	*/
	    	int dy=randomNumGen.nextInt(yVelocityMax - yVelocityMin + 1) + yVelocityMin;
	    	
	    	
	    	firstCard.add(throwRow);
	    	firstCard.add(throwDepth);
	    	firstCard.add(x);
	    	firstCard.add(y);
	    	firstCard.add(dx);
	    	firstCard.add(dy);
	    	
	    	cardInfo.add(firstCard);
	    	thrownCards.add(cardInfo);
	    	throwRow++;
	    	if(throwRow>3) {
	    		
	    		throwRow=0;
	    		throwDepth++;
	    	}
    	}
    }
    
    public void gameUpkeep() {
    	game.flipTopCards();
    	
    	if(game.won()) {
    		winMode=true;
    		new Timer().schedule(new WinClock(this), 0, pause);
    	}
    }

    public Collumn collumnBoxDetection(Point pt) {
    	for(int i=0;i<topPiles.size();i++) {
    		Collumn collumn = topPiles.get(i);
    		if(collumn!=null) {
    	    	int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH)*i;
    	    	int y=PILE_TOP_OFFSET+frame.getInsets().top;
    	    	Rectangle collumnBox = new Rectangle(x,y,Soliatre.CARD_WIDTH,cardHeight);
    	    	if(collumnBox.contains(pt)) {
    	    		return collumn;
    	    	}
    		}
    		
    	}
    	for(int i=0;i<layout.size();i++) {
    		Collumn collumn = layout.get(i);
    		if(collumn!=null) {
    			//Note: add in actual size for box
    			
    	    	int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH)*i;
    	    	int y=COLLUMN_OFFSET+frame.getInsets().top;
    	    	Rectangle collumnBox = new Rectangle(x,y,Soliatre.CARD_WIDTH,HEIGHT-y);
    	    	if(collumnBox.contains(pt)) {
    	    		return collumn;
    	    	}
    		}
    		
    	}
    	
    	return null;
    }
    
    public void cardBoxDetection(Point pt) {
    	for(int i=0;i<topPiles.size();i++) {
    		Collumn collumn = topPiles.get(i);
    		if(collumn!=null) {
    	    	int x=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH)*i;
    	    	int y=PILE_TOP_OFFSET+frame.getInsets().top;
    	    	Rectangle cardBox = new Rectangle(x,y,Soliatre.CARD_WIDTH,cardHeight);
    	    	if(cardBox.contains(pt)) {
    	    		if(collumn==deck) {
    	    			return;
    	    		}
    	    		if(!collumn.viewCard(0).getVis()) {
    	    			continue;
    	    		}

    	    		//System.out.println(i);
    	    		//System.out.println(cardBox);
    	    		grabOffsetX=pt.x-x;
    	    		grabOffsetY=pt.y-y;
    	    		previous=collumn;
    	    		collumn.moveTo(1,hand);
    	    		return;
    	    	}
    		}
    		
    	}
    	for(int i=0;i<layout.size();i++) {
    		Collumn collumn = layout.get(i);
    		if(collumn!=null) {
    			//Note: add in actual size for box
    			
    	    	int baseX=PILE_SIDE_OFFSET+(PILE_SIDE_OFFSET+Soliatre.CARD_WIDTH)*i;
    	    	int baseY=COLLUMN_OFFSET+frame.getInsets().top;
    	    	for(int j=collumn.getSize()-1;j>=0;j--) {
    	    		Rectangle cardBox = new Rectangle(baseX,baseY+j*CARD_STACK_OFFSET,CARD_WIDTH,cardHeight);
        	    	if(cardBox.contains(pt)) {
        	    		if(!collumn.viewCard(j).getVis()) {
        	    			continue;
        	    		}
        	    		grabOffsetX=pt.x-baseX;
        	    		grabOffsetY=pt.y-baseY-j*CARD_STACK_OFFSET;
        	    		previous=collumn;
        	    		System.out.println(pt);
        	    		System.out.println(baseX+","+baseY);
        	    		System.out.println(cardBox);
        	    		System.out.println(collumn.getSize()-j);
        	    		if(collumn.testTopOrder(collumn.getSize()-j)&&collumn.testTopColors(collumn.getSize()-j)) {
        	    			collumn.moveTo(collumn.getSize()-j,hand);
        	    		}
        	    		
        	    		return;
        	    		
        	    	}
    	    	}
    		}
    		
    	}
    }
    
    public boolean validLocationDetector(Collumn collumn, Card bottomCard,int handSize, boolean foundation) {
    	
    	if(collumn==null||collumn==discard) {
    		return false;
    	}
    	Card collumnTopCard=null;
    	if(collumn.getSize()>0) {
    		 collumnTopCard= collumn.viewCard(collumn.getSize()-1);
    	}
    	
    	
    	
		if(foundation) {
			if(handSize>1) {
				return false;
			}
			if(collumn.getSize()==0) {
				if(bottomCard.getRankValue()==1) {
					return true;
				}else {
					return false;
				}
			}else if(collumnTopCard.getSuit().equals(bottomCard.getSuit())&&collumnTopCard.getRankValue()+1==bottomCard.getRankValue()) {
    			return true;
    		}else {
    			return false;
    		}
    	}else {
    		System.out.println();
    		if(collumn.getSize()==0) {
    			if(bottomCard.getRankValue()==13) {
    				return true;
    			}
			}else if(!bottomCard.getColor().equals(collumnTopCard.getColor())&&collumnTopCard.getRankValue()-1==bottomCard.getRankValue()){
    			return true;
    		}
    	}
    	
    	
    	return false;
    }
    
    @Override
	public void mouseMoved(MouseEvent e) {
    	mouseX=e.getPoint().x;
		mouseY=e.getPoint().y-frame.getInsets().top;
    }
    
    
    public void runtime() {
    	this.repaint();
    	
    }
	@Override
	public void mouseClicked(MouseEvent e) {
		
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		Collumn clicked = collumnBoxDetection(e.getPoint());
		
		if(deck==clicked) {
			if(deck.getSize()==0) {
				discard.moveTo(discard.getSize(),deck);
				deck.toggleVisAll();
			}else {
				deck.toggleVis(0);
				deck.moveTo(discard);
			}
			
		}else {
			cardBoxDetection(e.getPoint());
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		Collumn targetCollumn = collumnBoxDetection(e.getPoint());
		if(hand.getSize()>0) {
			if(!validLocationDetector(targetCollumn,hand.viewCard(0),hand.getSize(),foundations.contains(targetCollumn))){
				
				hand.moveTo(hand.getSize(),previous);
			}else {
				hand.moveTo(hand.getSize(),targetCollumn);
				gameUpkeep();
			}
		}
		
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
    	mouseX=e.getPoint().x;
		mouseY=e.getPoint().y-frame.getInsets().top;
	}
}


