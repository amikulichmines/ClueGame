package clueGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel implements ActionListener{
	private JTextArea theGuess = new JTextArea(2,15);
	private JTextField theResult = new JTextField(20), currentTurn;
	private JTextField theRoll = new JTextField(5);
	private JButton nextButton = new JButton("NEXT!"), accusationButton = new JButton("Make Accusation");
	private String guess, result, filename;
	private JPanel imagePanel;
	
	private ClueGUI clueGUI;
	
	public GameControlPanel(ClueGUI clueGUI, String filename)
	{
		this.filename=filename;
		this.clueGUI = clueGUI;
		setLayout(new GridLayout(2,0));
		setPreferredSize(new Dimension(10000,200));
        createLayout();
	}

	private JPanel createGuessPanel() {
		// returns a panel with one entry
		JPanel guessPanel = new JPanel();
		guessPanel.setBorder(new TitledBorder (new EtchedBorder(), "Guess"));
		theGuess.setEditable(false);
		theGuess.setLineWrap(true);
		theGuess.setWrapStyleWord(true);
		guessPanel.add(theGuess);
		return guessPanel;
	}
	
	private JPanel createGuessResultPanel() {
		// returns a panel with one entry
		JPanel resultPanel = new JPanel();
		resultPanel.setBorder(new TitledBorder (new EtchedBorder(), "Guess result"));
		theResult.setEditable(false);
		resultPanel.add(theResult);
		return resultPanel;
	}
	
	private JPanel createWhoseTurnPanel() {
		// Makes a panel with two entries, the label and text
		JPanel whoseTurnPanel = new JPanel(new GridLayout(1,2));
		
		JPanel labelAndName = new JPanel(new GridLayout(2,0));
		JLabel whoseTurn = new JLabel("Whose turn?");
		labelAndName.add(whoseTurn);
		currentTurn = new JTextField(10);
		currentTurn.setEditable(false);
		labelAndName.add(currentTurn);
		whoseTurnPanel.add(labelAndName);
		
		imagePanel = new ImagePanel(filename);
		imagePanel.setSize(200, 200);
		// put in the images //
		
		whoseTurnPanel.add(imagePanel);
		return whoseTurnPanel;
	}
	
	private JPanel createRollPanel() {
		// Makes two things, a label for the roll and the text box.
		// it adds them side by side
		JPanel rollPanel = new JPanel();
		JLabel roll = new JLabel("Roll");
		rollPanel.add(roll);
		theRoll.setEditable(false);
		rollPanel.add(theRoll);
		return rollPanel;
	}


	private JButton createAccusationButtonPanel() {
		accusationButton.addActionListener(this);
		return accusationButton;
	}
	
	private JButton createNextButtonPanel() {
		nextButton.addActionListener(this);
		return nextButton;
	}
	
	private JPanel createTopBox() {
		// 1 row, 4 columns
		JPanel panel = new JPanel(new GridLayout(1,4));
		panel.add(createWhoseTurnPanel());
		panel.add(createRollPanel());
		panel.add(createAccusationButtonPanel());
		panel.add(createNextButtonPanel());
		return panel;
	}
	
	private JPanel createBottomBox() {
		// variable rows, 2 columns
		JPanel panel = new JPanel(new GridLayout(1,2));
		panel.add(createGuessPanel());
		panel.add(createGuessResultPanel());
		return panel;
	}
	
	void createLayout() {
		add(createTopBox());
		add(createBottomBox());		
	}
	
	void setGuessResult(String result) {
		theResult.setText(result);
	}

	void setGuess(String guess) {
		theGuess.setText(guess);
	}

	void setTurn(Player player, int i) {
		this.currentTurn.setText(player.getName());
		this.currentTurn.setBackground(player.getColor());
		this.theRoll.setText(""+i);
		this.theGuess.setText(guess);
		this.theResult.setText(result);
	}
	
	public void setGuess(String person, String room, String weapon) {
		guess = "Person: " + person + "\nRoom: " + room + "\nWeapon: " + weapon; 
		this.theGuess.setText(guess);
	}
	
	public void setGuess(Solution solution) {
		guess = "Person: " + solution.person.getCardName() + "\nRoom: " + solution.room.getCardName() + "\nWeapon: " + solution.weapon.getCardName(); 
		this.theGuess.setText(guess);
	}
	
	public void setResult(boolean disproven) {
		if(disproven) {
			result="Disproven";
			this.theResult.setText(result);
			this.theResult.setBackground(Color.red);
		} else {
			result = "Was not disproven";
			this.theResult.setText(result);
			this.theResult.setBackground(Color.green);
		}
	}
	
	public void resetGuessAndResult() {
		guess = "";
		theGuess.setText(guess);
		result = "";
		theResult.setText(result);
		theResult.setBackground(null);
	}
	
	public void setFilename(String filename) {
		this.filename=filename;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==nextButton) {
			// go to next turn if player has moved
			if (clueGUI.currentPlayer.hasMoved() == false) {
				Object[] options = {"OK"};
				String message = "You need to move first";
				JOptionPane.showOptionDialog(null, message, "Error",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, options, options[0]);
			} else {				
				clueGUI.nextTurn(clueGUI);
			}
		}
		if (e.getSource()==accusationButton) {
			if(clueGUI.currentPlayer instanceof ComputerPlayer || clueGUI.currentPlayer.hasMoved()) {
				Object[] options = {"OK"};
				String message = "Accusations can only be made at the start of your turn.";
				JOptionPane.showOptionDialog(null, message, "Error",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
						null, options, options[0]);
			}else {
			clueGUI.promptForAccusation();}
		}
	}
}
