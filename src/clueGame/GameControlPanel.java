package clueGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel {
	private String turn;
	private JTextField theGuess = new JTextField(20);
	private JTextField theResult = new JTextField(20), currentTurn;
	private JTextField theRoll = new JTextField(5);
	
	public GameControlPanel()
	{
		setLayout(new GridLayout(2,0));
        createLayout();
	}

	private JPanel createGuessPanel() {
		JPanel guessPanel = new JPanel();
		guessPanel.setBorder(new TitledBorder (new EtchedBorder(), "Guess"));
		guessPanel.add(theGuess);
		return guessPanel;
	}
	
	private JPanel createGuessResultPanel() {
		JPanel resultPanel = new JPanel();
		theResult = new JTextField(20);
		resultPanel.setBorder(new TitledBorder (new EtchedBorder(), "Guess result"));
		resultPanel.add(theResult);
		return resultPanel;
	}
	
	private JPanel createWhoseTurnPanel() {
		JPanel whoseTurnPanel = new JPanel(new GridLayout(4,0));
		JLabel whoseTurn = new JLabel("Whose turn?");
		whoseTurnPanel.add(whoseTurn);
		currentTurn = new JTextField(10);
		whoseTurnPanel.add(currentTurn);
		return whoseTurnPanel;
	}
	
	private JPanel createRollPanel() {
		JPanel rollPanel = new JPanel();
		JLabel roll = new JLabel("Roll");
		rollPanel.add(roll);
		rollPanel.add(theRoll);
		return rollPanel;
	}


	private JButton createAccusationButtonPanel() {
		return new JButton("Make Accusation");
	}
	
	private JButton createNextButtonPanel() {
		return new JButton("NEXT!");
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
	}
}
