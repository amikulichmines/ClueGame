package clueGame;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ComputerPlayer extends Player{
	private String name;//, colorName;
	private Color color;

	public ComputerPlayer(String name, int row, int column, String color) {
		super(name, row, column, color);
	}

//	public void setColor(String colorName) {
//		try {
//		    Field field = Class.forName("java.awt.Color").getField(colorName.toLowerCase());
//		    color = (Color)field.get(null);
//		} catch (Exception e) {
//		    color = null; // Not defined
//		}
//	}

	public Solution createSuggestion(BoardCell[][] grid) {
		// Starts off by making arrayLists of unseen objects. Unseen 
		// contains all unseen things, so we need to break it apart
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
		
		Card guessedRoom = null;
		BoardCell currentCell = grid[row][column];
		// Goes though each room and finds which one matches the current one
		for(Card card : rooms) {
			if(grid[row][column].getRoomName().equals(card.getCardName())) {
				guessedRoom = card;
				break;
			}
		}
		
		// Randomizes the response of weapons and people with a randomizer as
		// the index
		Card guessedWeapon = unseenWeapons.get(ThreadLocalRandom.current().nextInt(0, unseenWeapons.size()));
		Card guessedPerson = unseenPeople.get(ThreadLocalRandom.current().nextInt(0, unseenPeople.size()));
		
		
		return new Solution(guessedPerson, guessedRoom, guessedWeapon);
				
	}
	
	public BoardCell selectTargets(Set<BoardCell> targets) {
		// Starts by making some ArrayLists for the two types of targets:
		// unseen rooms and walkways
		Set<String> unseenRooms = new HashSet<>();
		ArrayList<BoardCell> targetUnseenRooms = new ArrayList<>();
		ArrayList<BoardCell> targetWalkwaysAndRooms = new ArrayList<>();
		for (Card card : unseen) {
			if(card.getType() == CardType.ROOM) {
				unseenRooms.add(card.getCardName());
			}
		}
		for(BoardCell target : targets) {
			if(unseenRooms.contains(target.getRoomName())) 
				targetUnseenRooms.add(target);
			else 
				targetWalkwaysAndRooms.add(target);
		}
		// Prioritizes unseen rooms, if it isn't empty, return a random one.
		if(!targetUnseenRooms.isEmpty())
			return targetUnseenRooms.get(ThreadLocalRandom.current().nextInt(0, targetUnseenRooms.size()));
		return targetWalkwaysAndRooms.get(ThreadLocalRandom.current().nextInt(0, targetWalkwaysAndRooms.size()));
		
	}

}
