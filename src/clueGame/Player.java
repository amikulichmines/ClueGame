package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
		// The player needs to know all the options to choose from: rooms,
		// people, and weapons. This also initializes the unseen set.
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
		// If you see a card, add it to the seen set and remove from the 
		// unseen set. It removes it based on the name, as a copy will not 
		// be removed with the .remove() function.
		seen.add(seenCard);
		Card cardToRemove = null;
		for(Card u : unseen){
			if(u.getCardName().equals(seenCard.getCardName()))
				cardToRemove=u;
		}
		unseen.remove(cardToRemove);
	}
	
	public Card disproveSuggestion(Solution suggestion) {
		// First add the cards in the solution to a set.
		Set<String> suggestionCards = new HashSet<>();
		suggestionCards.add(suggestion.room.getCardName());
		suggestionCards.add(suggestion.weapon.getCardName());
		suggestionCards.add(suggestion.person.getCardName());
		ArrayList<Card> matchingCards = new ArrayList<>();
		// Find any matching cards
		for(Card card : hand) {
			if (suggestionCards.contains(card.getCardName())) {
				matchingCards.add(card);
			}
		}
		// If there are any, return a random one
		if(!matchingCards.isEmpty())
			return matchingCards.get(ThreadLocalRandom.current().nextInt(0, matchingCards.size()));
		return null;
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
	
	public void setHand(Set<Card> hand) {
		this.hand = hand;
	}
	
}
