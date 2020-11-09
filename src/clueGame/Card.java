package clueGame;

import java.awt.Color;

public class Card{
	private String cardName;
	private CardType type;
	private Color color;
	
	public Card(String cardName, CardType type) {
		super();
		this.cardName = cardName;
		this.type = type;
	}

	public String getCardName() {
		return cardName;
	}

	public CardType getType() {
		return type;
	}
	
	public boolean equals(Card target) {
		return (target.cardName.equals(cardName));
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
}
