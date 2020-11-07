package clueGame;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashSet;
import java.util.Set;


public class ComputerPlayer extends Player{

	protected int row, column;
	private String name;//, colorName;
	private Color color;

	public ComputerPlayer(String name, int row, int column, String color) {
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

	public Solution createSuggestion() {
		ArrayList<Card> unseenWeapons = new ArrayList<>();
		ArrayList<Card> unseenRooms= new ArrayList<>();
		ArrayList<Card> unseenPeople = new ArrayList<>();
		for(Card card : unseen) {
			if(card.getType() == CardType.PERSON)
				unseenPeople.add(card);
			if(card.getType() == CardType.WEAPON)
				unseenWeapons.add(card);
			if(card.getType() == CardType.ROOM)
				unseenRooms.add(card);
		}
		Card guessedWeapon = unseenWeapons.get(ThreadLocalRandom.current().nextInt(0, unseenWeapons.size()));
		Card guessedRoom = unseenWeapons.get(ThreadLocalRandom.current().nextInt(0, unseenRooms.size()));
		Card guessedPerson = unseenWeapons.get(ThreadLocalRandom.current().nextInt(0, unseenPeople.size()));
		
		return new Solution(guessedPerson, guessedRoom, guessedWeapon);
				
	}
	
	public BoardCell selectTargets(Set<BoardCell> targets) {
		Set<String> unseenRooms = new HashSet<>();
		ArrayList<BoardCell> targetRooms = new ArrayList<>();
		ArrayList<BoardCell> targetWalkways = new ArrayList<>();
		for (Card card : unseen) {
			if(card.getType() == CardType.ROOM) {
				unseenRooms.add(card.getCardName());
			}
		}
		for(BoardCell target : targets) {
			if(target.isRoom() && unseen.contains(target.getRoomName())) 
				targetRooms.add(target);
			else if(!target.isRoom()) 
				targetWalkways.add(target);
		}
		if(!targetRooms.isEmpty())
			return targetRooms.get(ThreadLocalRandom.current().nextInt(0, targetRooms.size()));
		return targetWalkways.get(ThreadLocalRandom.current().nextInt(0, targetWalkways.size()));
		
	}

}
