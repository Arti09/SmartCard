package main;

public class Player {
	private String name;
	private String pin;
	private int highscore;

	public Player() {
		super();
		this.name = "Default";
		this.pin = "1234";
		this.highscore = 0;
	}

	public Player(String name, String pin) {
		super();
		this.name = name;
		this.pin = pin;
		this.highscore = 0;
	}

	public Player(String name, String pin, int highscore) {
		super();
		this.name = name;
		this.pin = pin;
		this.highscore = highscore;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public int getHighscore() {
		return highscore;
	}

	public void setHighscore(int highscore) {
		this.highscore = highscore;
	}

}
