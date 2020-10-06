package tests;

import experiment.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import com.sun.tools.javac.util.Assert;



public class BoardTestExp {
	
	private TestBoard board;
	
	@BeforeEach
	public void setup() {
		board = new TestBoard(4,4);
	}
	
	/*
	 * The four following tests check the adjacency lists
	 */
	
	@Test
	public void testTopLeft() {
		TestBoardCell cell = board.getCell(0, 0);
		Set<TestBoardCell> testList = cell.getAdjList();
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
}
