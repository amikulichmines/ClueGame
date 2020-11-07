package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
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
		
	}
	
	@Test
	void handleSuggestion() {
		
	}
	
	@Test
	void testComputerCreateSuggestion() {
		
	}
	
	@Test
	void testComputerSelectTarget() {
		
	}

	
}
