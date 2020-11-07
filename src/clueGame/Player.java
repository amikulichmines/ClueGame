package clueGame;

import java.awt.Color;

import java.util.HashSet;
import java.util.Set;

public abstract class Player {
	protected int row, column, playerIndex;
	private String name;//, colorName;
	private Color color;
	protected Set<Card> hand = new HashSet<>();
	protected Set<Card> seen = new HashSet<>();
	protected Set<Card> unseen = new HashSet<>();
	
	protected static Set<Card> people, rooms, weapons;
	
	public Player(String name, int row, int column, String color) {
		super();
		this.name=name;
		this.row=row;
		this.column=column;
		setColor(color);
	}
	
	
	
	public int getPlayerIndex() {
		return playerIndex;
	}



	public void setPlayerIndex(int playerIndex) {
		this.playerIndex = playerIndex;
	}



	public void setAllCards(Set<Card> allPeople, Set<Card> allWeapons, Set<Card> allRooms) {
		people = allPeople;
		rooms = allRooms;
		weapons = allWeapons;
		unseen.addAll(people);
		unseen.addAll(weapons);
		unseen.addAll(rooms);
	}
	
	public void updateHand(Card card) {
		hand.add(card);
		updateSeen(card);
	}
	
	public void updateSeen(Card seenCard) {
		seen.add(seenCard);
		unseen.remove(seenCard);
	}
	
	public Card disproveSuggestion(Solution suggestion) {
		Set<Card> suggestionCards = new HashSet<>(suggestion.person, suggestion.room, suggestion.weapon);
		for(Card card : hand) {
			if (suggestionCards.contains(card)) {
				return card;
			}
		}
		System.out.println("Error! Can't disprove");
		return new Card("Invalid card", CardType.PERSON);
	}
	
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
