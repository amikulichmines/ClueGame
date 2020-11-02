package clueGame;

import java.awt.Color;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Player {
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
	
	public void setColor(String colorName) {
		try {
		    Field field = Class.forName("java.awt.Color").getField(colorName.toLowerCase());
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = null; // Not defined
		}
	}
	
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
