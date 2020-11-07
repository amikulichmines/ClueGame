package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.Player;
import clueGame.Solution;

class GameSolutionTest {

	private static Board board;

	
	@BeforeEach
	void setUp() throws Exception {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	@Test
	void testAccusations() {
		Card culprit = new Card("Derek Baum", CardType.PERSON);
		Card deathRoom = new Card("Kitchen", CardType.ROOM);
		Card murderWeapon = new Card("Gun", CardType.WEAPON);
		Solution testAnswer = new Solution(culprit, deathRoom, murderWeapon);
		board.setTheAnswer(testAnswer);
		assertTrue(board.checkAccusation("Derek Baum", "Kitchen", "Gun"));
		assertFalse(board.checkAccusation("Peter", "Kitchen", "Gun"));
		assertFalse(board.checkAccusation("Derek Baum", "Restroom", "Gun"));
		assertFalse(board.checkAccusation("Derek Baum", "Kitchen", "Grenade"));
	}
	
	@Test
	void testDisprovenSuggestion() {
		Card person = new Card("Derek Baum", CardType.PERSON);
		Card room = new Card("Kitchen", CardType.ROOM);
		Card weapon = new Card("Gun", CardType.WEAPON);
		Solution suggestion = new Solution(person, room, weapon);  
		Player computer1 = new ComputerPlayer("Comp1", 0,0, "Blue");
		
		// Tests to see if no cards, it returns null
		assertTrue(computer1.disproveSuggestion(suggestion) == null);
		
		// Tests to see if one card, it returns that card
		computer1.updateHand(person);
		assertTrue(computer1.disproveSuggestion(suggestion) == person);
		
		// Tests to see if multiple cards, it randomized it.
		computer1.updateHand(room);
		computer1.updateHand(weapon);
		Player computer2 = new ComputerPlayer("Comp2",0,0,"Green");
		computer2.updateHand(person);
		computer2.updateHand(room);
		computer2.updateHand(weapon);
		Card[] suggestions1 =  new Card[100];
		Card[] suggestions2 =  new Card[100];
		
		for(int i = 0; i<100; i++) {
			suggestions1[i]=computer1.disproveSuggestion(suggestion);
			suggestions2[i]=computer2.disproveSuggestion(suggestion);
		}
		assertFalse(suggestions1.equals(suggestions2));
	}
		


	@Test
	void handleSuggestion() {
		Solution theAnswer = board.getSolution();
		String peopleAnswer = theAnswer.getPerson().getCardName(), roomAnswer = theAnswer.getPerson().getCardName(), weaponAnswer = theAnswer.getPerson().getCardName();
		
		board.getPlayer(1).setHand(new HashSet<Card>());
		board.getPlayer(1).updateHand(new Card("Derek Baum", CardType.PERSON));
		board.getPlayer(1).updateHand(new Card("Kitchen", CardType.ROOM));
		board.getPlayer(1).updateHand(new Card("Gun", CardType.WEAPON));
		board.getPlayer(0).updateHand(new Card("Dog House", CardType.ROOM));		// Player 0 is the human player; all other players are computer players
		board.getPlayer(2).updateHand(new Card("Pool Noodle", CardType.WEAPON));
		
		// tests that handling a suggestion that no one can disprove (the answer to the game) returns null
		assertTrue(board.handleSuggestion(peopleAnswer, roomAnswer, weaponAnswer, board.getPlayer(1)) == null);
		// tests that handling a suggestion that only the accusing player can disprove returns null
		assertTrue(board.handleSuggestion("Derek Baum", "Kitchen", "Gun", board.getPlayer(1)) == null);
		// tests that handling a suggestion that only the human can disprove returns a disproving card
		assertTrue(board.handleSuggestion("Derek Baum", "Dog House", "Gun", board.getPlayer(1)).equals(new Card("Dog House", CardType.ROOM)));
		// tests that handling a suggestion that two players can disprove is disproved by the correct player
		assertTrue(board.handleSuggestion("Derek Baum", "Dog House", "Pool Noodle", board.getPlayer(1)).equals(new Card("Pool Noodle", CardType.WEAPON)));
		assertFalse(board.handleSuggestion("Derek Baum", "Dog House", "Pool Noodle", board.getPlayer(1)).equals(new Card("Dog House", CardType.ROOM)));
	}
	
	@Test
	void testComputerCreateSuggestion() {
		Card person = new Card("Derek Baum", CardType.PERSON);
		Card room = new Card("Cookplace", CardType.ROOM);
		Card weapon = new Card("Gun", CardType.WEAPON);
		
		ComputerPlayer computer1 = new ComputerPlayer("Comp1", 3, 2, "Blue");
		Set<Card> allPeople = new HashSet<>(); 
		Set<Card> allWeapons = new HashSet<>(); 
		Set<Card> allRooms = new HashSet<>(); 
		
		allPeople.add(person);
		allWeapons.add(weapon);
		allRooms.add(room);
		
		computer1.setAllCards(allPeople, allWeapons, allRooms);
		Solution solution = computer1.createSuggestion(board.getGrid());
		
		// We put the player in the conservatory, so room should be conservatory
		
		assertTrue(solution.room.getCardName().equals("Cookplace"));
		
		// Now if we add a knife, but make the knife seen, gun should be guessed.
		Card knife = new Card("Knife", CardType.WEAPON);
		allWeapons.add(knife);
		computer1.setAllCards(allPeople, allWeapons, allRooms);
		computer1.updateSeen(knife);
		assertTrue(solution.weapon.getCardName().equals("Gun"));
		
		// Same with a person. We add a person, "Phil Swift", but make him seen
		Card phil = new Card("Phil Swift", CardType.PERSON);
		allPeople.add(phil);
		computer1.setAllCards(allPeople, allWeapons, allRooms);
		computer1.updateSeen(phil);
		solution = computer1.createSuggestion(board.getGrid());
		assertTrue(solution.person.getCardName().equals("Derek Baum"));
		
		// If multiple unseen weapons, randomize it
		ComputerPlayer computer2 = new ComputerPlayer("Comp2",3,2,"Green");
		computer2.setAllCards(allPeople, allWeapons, allRooms);
		Card[] suggestions1 =  new Card[100];
		Card[] suggestions2 =  new Card[100];
		for(int i = 0; i<100; i++) {
			suggestions1[i]=computer1.createSuggestion(board.getGrid()).weapon;
			suggestions2[i]=computer2.createSuggestion(board.getGrid()).weapon;
		}
		assertFalse(suggestions1 == suggestions2);
		

	}
	
	@Test
	void testComputerSelectTarget() {
		Card person = new Card("Derek Baum", CardType.PERSON);
		Card room = new Card("Cookplace", CardType.ROOM);
		Card weapon = new Card("Gun", CardType.WEAPON);
		
		ComputerPlayer computer1 = new ComputerPlayer("Comp1", 3, 2, "Blue");
		ComputerPlayer computer2 = new ComputerPlayer("Comp2", 3, 2, "Green");
		Set<Card> allPeople = new HashSet<>(); 
		Set<Card> allWeapons = new HashSet<>(); 
		Set<Card> allRooms = new HashSet<>(); 
		
		allPeople.add(person);
		allWeapons.add(weapon);
		allRooms.add(room);
		
		computer1.setAllCards(allPeople, allWeapons, allRooms);
		computer2.setAllCards(allPeople, allWeapons, allRooms);
		
		board.calcTargets(board.getGrid()[10][10], 1);
		Set<BoardCell> targets = board.getTargets();
		
		// If there are no rooms, pick randomly
		String[] suggestions1 =  new String[100];
		String[] suggestions2 =  new String[100];
		for(int i = 0; i<100; i++) {
			suggestions1[i]= computer1.selectTargets(targets).toString();
			suggestions2[i]= computer2.selectTargets(targets).toString();
		}
		assertFalse(suggestions1 == suggestions2);
		
		List<String> targetList = Arrays.asList(suggestions1);
		assertTrue(targetList.contains("Row: 9, Col: 10"));
		assertTrue(targetList.contains("Row: 11, Col: 10"));
		assertTrue(targetList.contains("Row: 10, Col: 9"));
		assertTrue(targetList.contains("Row: 10, Col: 11"));
		
		// if room in list that has not been seen, select it
		board.calcTargets(board.getGrid()[10][16], 1);
		Set<BoardCell> targetsWithRoom = board.getTargets();
		String result = computer1.selectTargets(targetsWithRoom).toString();
		assertTrue(result.equals("Row: 16, Col: 16"));
		
		// if room in list that has been seen, each target (including room) selected randomly
		
		board.calcTargets(board.getGrid()[10][16], 1);
		targets = board.getTargets();
		computer1.updateSeen(new Card("Basement", CardType.ROOM));
		// If there are no rooms, pick randomly
		suggestions1 =  new String[100];
		suggestions2 =  new String[100];
		for(int i = 0; i<100; i++) {
			suggestions1[i]= computer1.selectTargets(targets).toString();
			suggestions2[i]= computer2.selectTargets(targets).toString();
		}
		assertFalse(suggestions1 == suggestions2);
		
		targetList = Arrays.asList(suggestions1);
		assertTrue(targetList.contains("Row: 9, Col: 16"));
		assertTrue(targetList.contains("Row: 16, Col: 16"));
		assertTrue(targetList.contains("Row: 10, Col: 15"));
		assertTrue(targetList.contains("Row: 10, Col: 17"));
	}

	
}
