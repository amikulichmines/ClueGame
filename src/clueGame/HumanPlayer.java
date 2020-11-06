package clueGame;

import java.lang.reflect.Field;
import java.awt.Color;


public class HumanPlayer extends Player{
	protected int row, column;
	private String name;//, colorName;
	private Color color;

	public HumanPlayer(String name, int row, int column, String color) {
		super(name, row, column, color);
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
