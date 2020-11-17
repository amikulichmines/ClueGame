package clueGame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JPanel;

import java.lang.reflect.Field;

public abstract class Player extends JPanel {
	protected int row, column, playerIndex;
	private String name;//, colorName;
	private Color color;
	protected Set<Card> hand = new HashSet<>();
	protected Set<Card> seen = new HashSet<>();
	protected Set<Card> seenPeople = new HashSet<>(), seenRooms = new HashSet<>(), seenWeapons = new HashSet<>();
	protected Set<Card> peopleInHand = new HashSet<>(), roomsInHand = new HashSet<>(), weaponsInHand = new HashSet<>();
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
	
	public void separateCards() {
		for (Card card : seen) {
			if(card.getType()==CardType.PERSON && !hand.contains(card))
				seenPeople.add(card);
			if(card.getType()==CardType.ROOM && !hand.contains(card))
				seenRooms.add(card);
			if(card.getType()==CardType.WEAPON && !hand.contains(card))
				seenWeapons.add(card);
		}
		for (Card card : hand) {
			if(card.getType()==CardType.PERSON)
				peopleInHand.add(card);
			if(card.getType()==CardType.ROOM)
				roomsInHand.add(card);
			if(card.getType()==CardType.WEAPON)
				weaponsInHand.add(card);
		}
	}
	
	

	public void updateHand(Card card) {
		card.setColor(this.getColor()); // sets the card's color to the player's color (this player has this card in hand)
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
	
	public void move(int c, int r) {
		this.setColumn(c);
		this.setRow(r);
	}
	
	public void setColor(String colorName) {
		try {
		    Field field = Color.class.getField(colorName.toLowerCase());
		    color = (Color)field.get(null);
		} catch (Exception e) {
			color = null; // Not defined
		}
	}
	
	public void draw(Graphics g, int cellLength) {
		int x = column * cellLength;
		int y = row * cellLength;
		g.setColor(this.color);
		g.fillOval(x, y, cellLength, cellLength);
		g.setColor(Color.black);
		g.drawOval(x, y, cellLength, cellLength);
	}

	public void setRow(int row) {
		this.row = row;
	}
	public void setColumn(int col) {
		this.column = col;
	}
	public Set<Card> getSeenPeople() {
		return seenPeople;
	}
	
	public Set<Card> getSeenRooms() {
		return seenRooms;
	}
	
	public Set<Card> getSeenWeapons() {
		return seenWeapons;
	}
	
	public Set<Card> getPeopleInHand() {
		return peopleInHand;
	}
	
	public Set<Card> getRoomsInHand() {
		return roomsInHand;
	}
	
	public Set<Card> getWeaponsInHand() {
		return weaponsInHand;
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
	
	public void setHand(Set<Card> hand) {
		this.hand = hand;
	}
	
}
