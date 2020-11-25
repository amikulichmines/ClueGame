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

	@Override
	protected Solution createSuggestion(BoardCell[][] grid) {
		// TODO Auto-generated method stub
		return null;
		
	}
}
