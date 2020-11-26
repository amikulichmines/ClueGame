package clueGame;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class CardsKnownPanel extends JPanel {
	private Set<Card> peopleSeen;
	private Set<Card> peopleInHand;
	private Set<Card> weaponsSeen;
	private Set<Card> weaponsInHand;
	private Set<Card> roomsSeen;
	private Set<Card> roomsInHand;

	public CardsKnownPanel(Player player) {
		initializeSets(player);
		setLayout(new GridLayout(3,1));
		add(createTopBox());
		add(createMiddleBox());
		add(createBottomBox());
	}
	
	private void initializeSets(Player player) {
		player.separateCards();
		peopleSeen=player.getSeenPeople();
		peopleInHand = player.getPeopleInHand();
		weaponsSeen = player.getSeenWeapons();
		weaponsInHand = player.getWeaponsInHand();
		roomsSeen = player.getSeenRooms();
		roomsInHand = player.getRoomsInHand();
	}
	
	private JPanel createBottomBox() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(new TitledBorder (new EtchedBorder(), "Weapons"));
		panel.add(new JLabel("In Hand"));
		panel.add(populatePanel(weaponsInHand));
		panel.add(new JLabel("Seen"));
		panel.add(populatePanel(weaponsSeen));
		return panel;
	}

	private JPanel createMiddleBox() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(new TitledBorder (new EtchedBorder(), "Rooms"));
		panel.add(new JLabel("In Hand"));
		panel.add(populatePanel(roomsInHand));
		panel.add(new JLabel("Seen"));
		panel.add(populatePanel(roomsSeen));
		return panel;
	}

	private JPanel createTopBox() {
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.setBorder(new TitledBorder (new EtchedBorder(), "People"));
		panel.add(new JLabel("In Hand"));
		panel.add(populatePanel(peopleInHand));
		panel.add(new JLabel("Seen"));
		panel.add(populatePanel(peopleSeen));
		return panel;
	}
	
	private JPanel populatePanel(Set<Card> set) {
		// Sets up panel and populates it with a for loop
		JTextField theCard;
		JPanel panel = new JPanel(new GridLayout(0,1));
		if (set.isEmpty()) {
			theCard = new JTextField(20);
			theCard.setText("None");
			theCard.setEditable(false);
			panel.add(theCard);
		}
		
		else for (Card card : set) {
			theCard = new JTextField(20);
			theCard.setText(card.getCardName());
			theCard.setEditable(false);
			theCard.setBackground(card.getColor());
			panel.add(theCard);
		}
		return panel;
	}

}
