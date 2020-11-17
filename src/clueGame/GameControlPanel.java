package clueGame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel implements ActionListener{
	private JTextField theGuess = new JTextField(20);
	private JTextField theResult = new JTextField(20), currentTurn;
	private JTextField theRoll = new JTextField(5);
	private JButton nextButton = new JButton("NEXT!"), accusationButton = new JButton("Make Accusation");
	
	private ClueGUI clueGUI;
	
	public GameControlPanel(ClueGUI clueGUI)
	{
		this.clueGUI = clueGUI;
		setLayout(new GridLayout(2,0));
        createLayout();
	}

	private JPanel createGuessPanel() {
		// returns a panel with one entry
		JPanel guessPanel = new JPanel();
		guessPanel.setBorder(new TitledBorder (new EtchedBorder(), "Guess"));
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
		JPanel whoseTurnPanel = new JPanel(new GridLayout(4,0));
		JLabel whoseTurn = new JLabel("Whose turn?");
		whoseTurnPanel.add(whoseTurn);
		currentTurn = new JTextField(10);
		currentTurn.setEditable(false);
		whoseTurnPanel.add(currentTurn);
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
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==nextButton) {
			// go to next turn if player has moved
			if (clueGUI.currentPlayer.hasMoved == false) {
				System.out.println("You need to move first");
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
			// make accusation
		}
	}
}
