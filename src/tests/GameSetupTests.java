package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.Assert.*;

import java.util.LinkedHashSet;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Solution;


public class GameSetupTests {
	// Constants that I will use to test whether the file was loaded correctly
	public static final int LEGEND_SIZE = 11;
	public static final int NUM_ROWS = 25;
	public static final int NUM_COLUMNS = 28;

	// NOTE: I made Board static because I only want to set it up one
	// time (using @BeforeAll), no need to do setup before each test.
	private static Board board;

	@BeforeEach
	public void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}
	

	// Load people and weapons from ClueSetup.txt and insure the data was loaded properly.
	@Test
	public void testLoadSetup() {
		LinkedHashSet<Card> people = board.getPeople();
		LinkedHashSet<Card> weapons = board.getWeapons();
		LinkedHashSet<Card> rooms = board.getRooms();
		
		assertTrue(people.size()==6);
		assertTrue(weapons.size()==6);
		assertTrue(rooms.size()==9);
	}
	// Create Player class with human and computer child classes. Use people data to instantiate 6 players (1 human and 5 computer)
	@Test
	public void testPlayerCreation() {
		assertTrue(Board.NUM_PLAYERS==6);
		assertTrue(board.getPlayerListSize()==6);
		assertTrue(board.getPlayer(0) instanceof HumanPlayer);							// test if the first Player is a HumanPlayer
		for (int i = 1; i < Board.NUM_PLAYERS; i++) {									// test ComputerPlayers
			assertTrue(board.getPlayer(i) instanceof ComputerPlayer);
		}
	}
	// Create complete deck of cards (weapons, people and rooms)
	@Test
	public void testDeckCreation() {
		// After the solutions are removed, there should be 18
		// cards in the deck.

		// Since they are sets, we know there can't be duplicates.
		Solution solution = board.getSolution();
		assertTrue(board.getDeck().size()==18);
		assertFalse(board.getDeck().contains(solution.person));
		assertFalse(board.getDeck().contains(solution.room));
		assertFalse(board.getDeck().contains(solution.weapon));
		
	}

	
	@Test
	public void testDeckRandomization() {
		// Asserts that if we randomize three different decks, 
		// we get three different orders. The chances of having
		// two equivalent decks, for 21 cards, are 5e-19.
		
		LinkedHashSet<Card> deck1 = board.getDeck();
		board.initialize();
		LinkedHashSet<Card> deck2 = board.getDeck();
		board.initialize();
		LinkedHashSet<Card> deck3 = board.getDeck();
		
		assertFalse(deck1==deck2);
		assertFalse(deck2==deck3);
		assertFalse(deck1==deck3);
}
	
	
	// Deal cards to the Answer and the players (all cards dealt, players have roughly same # of cards, no card dealt twice)
	@Test
	public void testDealAll() {
		assertTrue(board.getPlayer(0).getHand().size()==3);
		assertTrue(board.getPlayer(1).getHand().size()==3);
		assertTrue(board.getPlayer(2).getHand().size()==3);
		assertTrue(board.getPlayer(3).getHand().size()==3);
		assertTrue(board.getPlayer(4).getHand().size()==3);
		assertTrue(board.getPlayer(5).getHand().size()==3);
	}
	
	
	@Test
	public void testSolutionsCreation() {
		// Creates three different solutions, then tests to make sure they each
		// have a person, weapon, and room
		Board board1 = Board.getInstance();
		board1.initialize();
		Solution solution1 = board1.getSolution();
		
		Board board2 = Board.getInstance();
		board2.initialize();
		Solution solution2 = board2.getSolution();
		
		Board board3 = Board.getInstance();
		board3.initialize();
		Solution solution3 = board3.getSolution();
		
		assertTrue(solution1.person.getType() == CardType.PERSON);
		assertTrue(solution1.weapon.getType() == CardType.WEAPON);
		assertTrue(solution1.room.getType() == CardType.ROOM);
		assertTrue(solution2.person.getType() == CardType.PERSON);
		assertTrue(solution2.weapon.getType() == CardType.WEAPON);
		assertTrue(solution2.room.getType() == CardType.ROOM);
		assertTrue(solution3.person.getType() == CardType.PERSON);
		assertTrue(solution3.weapon.getType() == CardType.WEAPON);
		assertTrue(solution3.room.getType() == CardType.ROOM);
	}
	
	@Test
	public void testSolutionsRandomization() {
		// Creates three different solutions, then tests to see if they are all different
		Board board1 = Board.getInstance();
		board1.initialize();
		Solution solution1 = board1.getSolution();
		
		Board board2 = Board.getInstance();
		board2.initialize();
		Solution solution2 = board2.getSolution();
		
		Board board3 = Board.getInstance();
		board3.initialize();
		Solution solution3 = board3.getSolution();
		
		assertFalse(solution1.equals(solution2));
		assertFalse(solution2.equals(solution3));
		assertFalse(solution1.equals(solution3));
	}
	
}
