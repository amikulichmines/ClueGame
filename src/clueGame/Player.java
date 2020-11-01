package clueGame;

import java.awt.Color;

import java.lang.reflect.Field;

public class Player {
	protected int row, column;
	private String name;//, colorName;
	private Color color;
	
	public void updateHand(Card card) {
		
	}
	
	public void setColor(String colorName) {
		try {
		    Field field = Class.forName("java.awt.Color").getField(colorName.toLowerCase());
		    color = (Color)field.get(null);
		} catch (Exception e) {
		    color = null; // Not defined
		}
	}
}
