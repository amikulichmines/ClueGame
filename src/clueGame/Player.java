package clueGame;

import java.awt.Color;

import java.util.HashSet;
import java.util.Set;

public abstract class Player {
	protected int row, column;
	private String name;//, colorName;
	private Color color;
	private Set<Card> hand = new HashSet<>();
	
	public Player(String name, int row, int column, String color) {
		super();
		this.name=name;
		this.row=row;
		this.column=column;
		setColor(color);
	}
	
	public void updateHand(Card card) {
		hand.add(card);
	}
	
	public void updateSeen()
	
	public abstract void setColor(String colorName);
	// There will likely be a different way to set colors to humans vs computers.
	// We will make humans able to choose color, while the computers will be 
	// random. Thus, this is implemented in child classes.
	
	/**************************************************
	 * For Testing
	 **************************************************/

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	public String getName() {
		return name;
	}

	public Color getColor() {
		return color;
	}

	public Set<Card> getHand() {
		return hand;
	}
	
}
