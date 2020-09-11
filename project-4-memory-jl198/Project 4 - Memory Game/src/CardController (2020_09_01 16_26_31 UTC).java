
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;


public class CardController implements ActionListener {
	private ArrayList<Card> currentCards;
	private Timer delay; // this allows the user to see both incorrect cards for a second before facing them down again

	
	public CardController() {
		currentCards = new ArrayList();
		delay = new Timer(4000, this);
	}
	
	// adds cards to the current cards arraylist and checks to see if the card ids match when the arraylist
	// holds two cards.  It clears the arraylist after checking the ids
	private boolean addCard(Card card) {
		this.currentCards.add(card);
		if (currentCards.size() == 2) {
			Card firstCard = (Card) this.currentCards.get(0);
			if (firstCard.getCardID() == card.getCardID()) {
				System.out.println("You made a match!!!!");
				this.currentCards.clear();
			} else {
			// pause before clearing the cards from the arraylist
				this.delay.start();				
			}
		}

		return true;
	}
	
	// turns the card selected face up if the size of the array is less than two
	public boolean faceUp(Card card) {
		if (this.currentCards.size() < 2) {
			card.faceUp();
			return this.addCard(card);
		}
		return false;
	}

	@Override
	// this checks the arraylist continuously to turn the cards face down once the arraylist size is 2 and
	// there is not a match yet
	public void actionPerformed(ActionEvent e) {
		for(int i = 0; i < this.currentCards.size(); i++) {
			if(this.currentCards.size() == 2) {
				Card card = (Card) this.currentCards.get(i);
				card.faceDown();
			}
		}
		this.currentCards.clear();
	}

}
