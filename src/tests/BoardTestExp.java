package tests;

import experiment.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



public class BoardTestExp {
	
	private TestBoard board;
	
	@BeforeEach
	public void setup() {
		board = new TestBoard();
	}
	
	/*
	 * The four following tests check the adjacency lists of cells in the top left, bottom right, right edge, and left edge. 
	 */
	
	
	@Test
	public void testTopLeft() {
		TestBoardCell cell = board.getCell(0, 0);
		Set<TestBoardCell> testList = cell.getAdjList();
		cell = board.getCell(1,0);
		boolean b = testList.contains(board.getCell(1,0));
		assertTrue(testList.contains(board.getCell(1,0)));
		assertTrue(testList.contains(board.getCell(0,1)));
		assertEquals(2,testList.size());
	}
	
	@Test
	public void testBottomRight() {
		TestBoardCell cell = board.getCell(3, 3);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(board.getCell(2,3)));
		assertTrue(testList.contains(board.getCell(3,2)));
		assertEquals(2,testList.size());
	}
	
	@Test
	public void testRightEdge() {
		TestBoardCell cell = board.getCell(1,3);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(board.getCell(0,3)));
		assertTrue(testList.contains(board.getCell(2,3)));
		assertTrue(testList.contains(board.getCell(1,2)));
		assertEquals(3,testList.size());
	}
	
	@Test
	public void testLeftEdge() {
		TestBoardCell cell = board.getCell(2,0);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(board.getCell(2,1)));
		assertTrue(testList.contains(board.getCell(1,0)));
		assertTrue(testList.contains(board.getCell(3,0)));
		assertEquals(3,testList.size());
	}
	
	@Test
	public void testMiddle() {
		TestBoardCell cell = board.getCell(2,2);
		Set<TestBoardCell> testList = cell.getAdjList();
		assertTrue(testList.contains(board.getCell(2,1)));
		assertTrue(testList.contains(board.getCell(1,2)));
		assertTrue(testList.contains(board.getCell(3,2)));
		assertTrue(testList.contains(board.getCell(2,3)));
		assertEquals(4,testList.size());
	}
	
	/*
	 * The five following tests
	 */
	@Test
	public void testPathlengthOneFromCenter() {
		// 2,2
		Set<TestBoardCell> expectedTargets = new HashSet<TestBoardCell>();
		board.calcTargets(board.getCell(2,2), 1);
		expectedTargets.add(board.getCell(1,2));
		expectedTargets.add(board.getCell(3,2));
		expectedTargets.add(board.getCell(2,1));
		expectedTargets.add(board.getCell(2,3));
		Set<TestBoardCell> actualTargets = board.getTargets();
		assertEquals(expectedTargets,actualTargets);
		
	}
	
	@Test
	public void testPathlengthOneFromCorner() {
		// 0,0
		Set<TestBoardCell> expectedTargets = new HashSet<TestBoardCell>();
		board.calcTargets(board.getCell(0, 0), 1);
		expectedTargets.add(board.getCell(1,0));
		expectedTargets.add(board.getCell(0,1));
		Set<TestBoardCell> actualTargets = board.getTargets();
		assertEquals(expectedTargets,actualTargets);
	}
	
	@Test
	public void testPathlengthTwoFromCorner() {
		// 0,0
		Set<TestBoardCell> expectedTargets = new HashSet<TestBoardCell>();
		board.calcTargets(board.getCell(0, 0), 2);
		expectedTargets.add(board.getCell(0,2));
		expectedTargets.add(board.getCell(1,1));
		expectedTargets.add(board.getCell(2,0));
		Set<TestBoardCell> actualTargets = board.getTargets();
		assertEquals(expectedTargets, actualTargets);
	}
	
	@Test
	public void testPathlengthOneFromCornerWithOccupiedSpace() {
		// 0,0
		// Also assume space (0,1) is occupied.
		board.getCell(0, 1).setOccupied(true);;
		Set<TestBoardCell> expectedTargets = new HashSet<TestBoardCell>();
		board.calcTargets(board.getCell(0,0), 1);
		expectedTargets.add(board.getCell(1,0));
		Set<TestBoardCell> actualTargets = board.getTargets();
		assertEquals(expectedTargets,actualTargets);
	}
	
	@Test
	public void testPathlengthTwoFromCornerWithRoom() {
		// 0,0
		// Assume (0,1), (0,2) and (0,3) is a room and door is at (0,1)
		Set<TestBoardCell> expectedTargets = new HashSet<TestBoardCell>();
		board.getCell(0, 1).setRoom(true);
		board.getCell(0, 2).setRoom(true);
		board.getCell(0, 3).setRoom(true);
		board.calcTargets(board.getCell(0,0), 2);
		expectedTargets.add(board.getCell(1,1));
		expectedTargets.add(board.getCell(2,0));
		Set<TestBoardCell> actualTargets = board.getTargets();
		assertEquals(expectedTargets,actualTargets);
	}
}