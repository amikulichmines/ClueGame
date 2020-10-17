package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

public class BoardAdjTargetTest {
	// We make the Board static because we can load it one time and 
	// then do all the tests. 
	private static Board board;
	
	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance
		board = Board.getInstance();
		// set the file names to use my config files
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");		
		// Initialize will load config files 
		board.initialize();
	}

	// Ensure that player does not move around within room
	// These cells are LIGHT ORANGE on the planning spreadsheet
	@Test
	public void testAdjacenciesRooms()
	{
		// we want to test a couple of different rooms.
		// First, the attic that has 2 doors but a secret room
		Set<BoardCell> testList = board.getAdjList(3, 16);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(8, 16)));
		assertTrue(testList.contains(board.getCell(4, 19)));
		assertTrue(testList.contains(board.getCell(16, 16)));
		
		// now test the observatory
		testList = board.getAdjList(3, 23);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(3, 19)));
		
		// one more room, the kitchen
		testList = board.getAdjList(3, 2);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(6, 2)));
		assertTrue(testList.contains(board.getCell(3, 6)));
		
		// test room cell that isn't center, in foyer
		testList = board.getAdjList(18, 10);
		assertEquals(0, testList.size());
		assertFalse(testList.contains(board.getCell(14,9)));
	}

	
	// Ensure door locations include their rooms and also additional walkways
	// These cells are LIGHT ORANGE on the planning spreadsheet
	@Test
	public void testAdjacencyDoor()
	{
		Set<BoardCell> testList = board.getAdjList(6, 4);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(7, 4)));
		assertTrue(testList.contains(board.getCell(6, 5)));
		assertTrue(testList.contains(board.getCell(3, 2)));

		testList = board.getAdjList(11, 4);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(11, 1)));
		assertTrue(testList.contains(board.getCell(10, 4)));
		assertTrue(testList.contains(board.getCell(12, 4)));
		
		testList = board.getAdjList(22, 12);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(19, 8)));
		assertTrue(testList.contains(board.getCell(21, 12)));
		assertTrue(testList.contains(board.getCell(23, 12)));
		assertTrue(testList.contains(board.getCell(22, 13)));
	}
	
	// Test a variety of walkway scenarios
	// These tests are Dark Orange on the planning spreadsheet
	@Test
	public void testAdjacencyWalkways()
	{
		// Test on top edge of board, just one walkway piece
		Set<BoardCell> testList = board.getAdjList(0, 19);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(1, 19)));
		
		//Test on bottom edge
		testList = board.getAdjList(24, 5);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(23, 5)));
		
		//Test on left edge
		testList = board.getAdjList(6, 0);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(6, 1)));
				
		//Test on left edge
		testList = board.getAdjList(9, 27);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(9, 26)));
						
		// Test near a door but not adjacent
		testList = board.getAdjList(9, 19);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(10, 19)));
		assertTrue(testList.contains(board.getCell(8, 19)));
		assertTrue(testList.contains(board.getCell(9, 18)));
		assertTrue(testList.contains(board.getCell(9, 20)));

		// Test next to unused space
		testList = board.getAdjList(9,6);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(9, 7)));
		assertTrue(testList.contains(board.getCell(9, 5)));
		assertTrue(testList.contains(board.getCell(10, 6)));
	
	}
	
	
	// Tests out of room center, 1, 3 and 4
	// These are LIGHT BLUE on the planning spreadsheet
	@Test
	public void testTargetsInLiteratureRoom() {
		// test a roll of 1
		board.calcTargets(board.getCell(16, 24), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(9, 24)));
		assertTrue(targets.contains(board.getCell(11, 1)));
		
		// test a roll of 3
		board.calcTargets(board.getCell(16, 24), 3);
		targets= board.getTargets();
		assertEquals(5, targets.size());
		assertTrue(targets.contains(board.getCell(9, 22)));
		assertTrue(targets.contains(board.getCell(8, 23)));	
		assertTrue(targets.contains(board.getCell(8, 25)));
		assertTrue(targets.contains(board.getCell(9, 26)));	
		
		// test a roll of 4
		board.calcTargets(board.getCell(16, 24), 4);
		targets= board.getTargets();
		assertEquals(8, targets.size());
		assertTrue(targets.contains(board.getCell(9, 21)));
		assertTrue(targets.contains(board.getCell(8, 22)));	
		assertTrue(targets.contains(board.getCell(8, 24)));
		assertTrue(targets.contains(board.getCell(9, 27)));	
	}
	
	@Test
	public void testTargetsInBasement() {
		// test a roll of 1
		board.calcTargets(board.getCell(16, 16), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(10, 16)));
		assertTrue(targets.contains(board.getCell(3, 16)));
		
		// test a roll of 3
		board.calcTargets(board.getCell(16, 16), 3);
		targets= board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(10, 14)));
		assertTrue(targets.contains(board.getCell(9, 15)));	
		assertTrue(targets.contains(board.getCell(9, 17)));
		
	}

	// Tests out of room center, 1, 3 and 4
	// These are LIGHT BLUE on the planning spreadsheet
	@Test
	public void testTargetsAtDoor() {
		// test a roll of 1, at door
		board.calcTargets(board.getCell(2, 6), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(1, 6)));
		assertTrue(targets.contains(board.getCell(3, 6)));	
		assertTrue(targets.contains(board.getCell(3, 9)));	
		
		// test a roll of 3
		board.calcTargets(board.getCell(2, 6), 3);
		targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(5, 6)));
		assertTrue(targets.contains(board.getCell(3, 9)));
		assertTrue(targets.contains(board.getCell(3, 2)));	
		
		// test a roll of 4
		board.calcTargets(board.getCell(2, 6), 4);
		targets= board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(6, 6)));
		assertTrue(targets.contains(board.getCell(3, 9)));
		assertTrue(targets.contains(board.getCell(3, 2)));
	}

	@Test
	public void testTargetsInWalkway1() {
		// test a roll of 1
		board.calcTargets(board.getCell(8, 26), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(8, 25)));
		assertTrue(targets.contains(board.getCell(9, 26)));	
		
		// test a roll of 3
		board.calcTargets(board.getCell(8, 26), 3);
		targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(9, 26)));
		assertTrue(targets.contains(board.getCell(9, 24)));
		assertTrue(targets.contains(board.getCell(8, 23)));	
		
		// test a roll of 4
		board.calcTargets(board.getCell(8, 26), 4);
		targets= board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(9, 27)));
		assertTrue(targets.contains(board.getCell(16, 24)));
		assertTrue(targets.contains(board.getCell(8, 22)));	
	}

	@Test
	public void testTargetsInWalkway2() {
		// test a roll of 1
		board.calcTargets(board.getCell(13, 5), 1);
		Set<BoardCell> targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(13, 6)));
		assertTrue(targets.contains(board.getCell(12, 5)));	
		
		// testing a roll of 3
		board.calcTargets(board.getCell(13, 5), 3);
		targets= board.getTargets();
		assertEquals(11, targets.size());
		assertTrue(targets.contains(board.getCell(10, 5)));
		assertTrue(targets.contains(board.getCell(13, 8)));
		assertTrue(targets.contains(board.getCell(13, 6)));	
		
		// test a roll of 4
		board.calcTargets(board.getCell(13, 5), 4);
		targets= board.getTargets();
		assertEquals(15, targets.size());
		assertTrue(targets.contains(board.getCell(13, 9)));
		assertTrue(targets.contains(board.getCell(16, 4)));
		assertTrue(targets.contains(board.getCell(11, 1)));	
	}

	@Test
	// test to make sure occupied locations do not cause  problems
	public void testTargetsOccupied() {
		// test a roll of 4 blocked 2 down
		board.getCell(19, 13).setOccupied(true);
		board.calcTargets(board.getCell(17, 13), 4);
		board.getCell(19, 13).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(20, 12)));
		assertTrue(targets.contains(board.getCell(16, 12)));
		assertTrue(targets.contains(board.getCell(13, 13)));	
		assertFalse( targets.contains( board.getCell(19, 13))) ;
		assertFalse( targets.contains( board.getCell(21, 13))) ;
	
		// we want to make sure we can get into a room, even if flagged as occupied
		board.getCell(19, 2).setOccupied(true);
		board.calcTargets(board.getCell(16, 4), 1);
		board.getCell(19, 2).setOccupied(false);
		targets= board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(15, 4)));	
		assertTrue(targets.contains(board.getCell(16, 3)));
		assertTrue(targets.contains(board.getCell(16, 5)));
		assertTrue(targets.contains(board.getCell(19, 2)));	
		
		// check leaving a room with a blocked doorway
		board.getCell(9, 24).setOccupied(true);
		board.calcTargets(board.getCell(16, 24), 2);
		board.getCell(9, 24).setOccupied(false);
		targets= board.getTargets();
		assertEquals(1, targets.size());
		assertTrue(targets.contains(board.getCell(11, 1)));
		assertFalse(targets.contains(board.getCell(9, 23)));
		assertFalse(targets.contains(board.getCell(8, 24)));	
		assertFalse(targets.contains(board.getCell(9, 25)));

	}
}
