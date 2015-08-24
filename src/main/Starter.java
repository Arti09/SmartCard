package main;

public class Starter {
	static boolean playAgain = true;

	public static void main(String[] args) {
		Connector connector = new Connector();
		connector.connect();

		Game game = new Game();
		game.gameGui(connector);
	}

}
