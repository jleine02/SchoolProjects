import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Card extends JButton {
	
	private ImageIcon frontOfCard;
	private ImageIcon backOfCard;
	private boolean facedUp;
	private int cardID;
	private CardController controller;
	
	public Card(ImageIcon frontOfCard, ImageIcon backOfCard, int cardID, CardController controller) {
		super(backOfCard);
		this.frontOfCard = frontOfCard;
		this.backOfCard = backOfCard;
		this.cardID = cardID;
		this.facedUp = false;
		this.controller = controller;

	}
	
	public void faceUp() {
		if(this.facedUp == false) {
			this.facedUp = true;
			this.setIcon(this.frontOfCard);
		} else {
			return;
		}
	}
	
	public void faceDown() {
		if(this.facedUp == false) {
			return;
		} else {
			this.setIcon(this.backOfCard);
			this.facedUp = false;
		}
	}
	
	public int getCardID() {
		return this.cardID;
	}
	
	public CardController getController() {
		return this.controller;
	}

	/*
	 * @Override public void actionPerformed(ActionEvent e) {
	 * System.out.println("something is happening when you click"); ((Card)
	 * e.getSource()).faceUp();
	 * 
	 * }
	 */

}
