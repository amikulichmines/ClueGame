package clueGame;

import java.awt.Graphics;

import javax.swing.JFrame;


public class ClueFrame extends JFrame{
	public void setSize() {
		
	}
	public static void main(String[] args) {
		Graphics g
		Board board = Board.getInstance();
		board.paintComponent();
		JFrame frame = new JFrame();
		// Create a JPanel (MyDrawing), we'll draw on this
//		frame.setContentPane(new MyDrawing());
//		// Program will end when the window is closed
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		// Set the size (otherwise only title bar displays)
//		frame.setSize(400, 400);
//		
//		// Frame will not display until you set visible true
//		frame.setVisible(true);
	}

	}
}
