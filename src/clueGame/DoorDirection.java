package clueGame;

public enum DoorDirection {
	UP('^'), 
	DOWN('v'), 
	LEFT('<'), 
	RIGHT('>'), 
	NONE('0');

	private char direction;
	DoorDirection(char c) {
		this.direction = c;
	}
	
	public char getDirectionCharacter(){
		return direction;
	}
}
