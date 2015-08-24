package main;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class Game {

	Connector connect = null;
	int currentRow = 9;
	static Player currentPlayer = new Player();
	JFrame mainFrame = new JFrame();
	final JFrame guiFrame = new JFrame();

	public void gameGui(Connector connect) throws HeadlessException {
		this.connect = connect;
		mainGui();
	}

	private void mainGui() throws HeadlessException {
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
			}

			@Override
			public void windowClosed(WindowEvent e) {
				connect.disconnect();
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		guiFrame.setTitle("Login and Play");
		guiFrame.setSize(550, 100);

		guiFrame.setLocationRelativeTo(null);

		String[] gamerOptions = {/* currentPlayer.getName() */};

		final JPanel comboPanel = new JPanel();
		JLabel comboLbl = new JLabel("Player:");
		final JComboBox gamer = new JComboBox(gamerOptions);
		gamer.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				String playerName = (String) arg0.getItem();
				currentPlayer.setName(playerName);
			}
		});
		comboPanel.add(comboLbl);
		comboPanel.add(gamer);

		final JPanel listPanel = new JPanel();
		listPanel.setVisible(false);
		JLabel listLbl = new JLabel("Highscores:");
		final Vector<Integer> listModel = new Vector();
		JList highscore = new JList(listModel);
		highscore.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		listPanel.add(listLbl);
		listPanel.add(highscore);
		JButton playHighscore = new JButton("Player or Highscore");
		playHighscore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				listPanel.setVisible(!listPanel.isVisible());
				comboPanel.setVisible(!comboPanel.isVisible());
			}
		});

		guiFrame.add(comboPanel, BorderLayout.NORTH);
		guiFrame.add(listPanel, BorderLayout.CENTER);
		JButton update = new JButton("Update");

		JButton play = new JButton("Play");
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				guiFrame.setEnabled(false);
				String playerName = (String) gamer.getSelectedItem();

				String playerPin = "";
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(1, 2));
				JLabel label = new JLabel("Passwort:");
				JPasswordField pass = new JPasswordField(4);
				((AbstractDocument) pass.getDocument())
						.setDocumentFilter(new LimitDocumentFilter(4));
				panel.add(label);
				panel.add(pass);
				String[] options = new String[] { "OK", "Cancel" };
				int option = JOptionPane.showOptionDialog(null, panel,
						"Passwort eingeben", JOptionPane.NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
				if (option == 0) {
					playerPin = new String(pass.getPassword());
				}

				if (playerPin != null && playerName != null) {
					currentPlayer.setPin(playerPin);
					currentPlayer.setName(playerName);
					if (searchForPlayer() != null) {
						guiFrame.setVisible(false);
						startingGame();
					} else {
						guiFrame.setEnabled(true);
						JOptionPane.showMessageDialog(guiFrame,
								"Spieler konnte nicht gefunden werden!");
					}
				} else {
					guiFrame.setEnabled(true);
					JOptionPane.showMessageDialog(guiFrame,
							"Kein Name oder Passwort eingegeben!");
				}
			}
		});

		JButton create = new JButton("Create new Player");
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String playerName = "";
				String playerPin = "";
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(2, 2));
				JLabel labelName = new JLabel("Name:");
				JTextField name = new JTextField(10);
				((AbstractDocument) name.getDocument())
						.setDocumentFilter(new LimitDocumentFilter(10));
				panel.add(labelName);
				panel.add(name);
				JLabel label = new JLabel("Passwort:");
				JPasswordField pass = new JPasswordField(4);
				((AbstractDocument) pass.getDocument())
						.setDocumentFilter(new LimitDocumentFilter(4));
				panel.add(label);
				panel.add(pass);
				String[] options = new String[] { "OK", "Cancel" };
				int option = JOptionPane.showOptionDialog(null, panel,
						"Spieler eingeben", JOptionPane.NO_OPTION,
						JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
				if (option == 0) {
					playerPin = new String(pass.getPassword());
					playerName = name.getText();

				}

				Player player = new Player(playerName, playerPin);
				if (connect.createNewGamer(player)) {
					gamer.addItem(playerName);
					listModel.addElement(player.getHighscore());
				} else {
					JOptionPane.showMessageDialog(guiFrame,
							"Spieler konnte nicht erstellt werden");
				}
			}
		});

		JPanel buttons = new JPanel();
		buttons.add(play);
		buttons.add(playHighscore);
		buttons.add(update);
		buttons.add(create);
		guiFrame.add(buttons, BorderLayout.SOUTH);

		guiFrame.setVisible(true);
	}

	protected Player searchForPlayer() {
		Player foundPlayer = connect.loginGamer(currentPlayer);
		return foundPlayer;
	}

	public void startingGame() {
		final MasterMindRow[] masterMindRows = new MasterMindRow[10];

		final int[] combination = new int[4];

		Random randomGenerator = new Random();
		for (int idx = 0; idx <= 3; ++idx) {
			int number = randomGenerator.nextInt(7);
			boolean sameNumber = true;
			while (sameNumber) {
				switch (idx) {
				case 1:
					if (combination[0] == number) {
						number = randomGenerator.nextInt(7);
					} else {
						sameNumber = false;
					}
					break;
				case 2:
					if (combination[0] == number || combination[1] == number) {
						number = randomGenerator.nextInt(7);
					} else {
						sameNumber = false;
					}
					break;
				case 3:
					if (combination[0] == number || combination[1] == number
							|| combination[2] == number) {
						number = randomGenerator.nextInt(7);
					} else {
						sameNumber = false;
					}
					break;
				default:
					sameNumber = false;
					break;
				}
			}
			combination[idx] = number;
		}

		GridLayout layout = new GridLayout(10, 1);

		JPanel rows = new JPanel();
		rows.setLayout(layout);
		for (int i = 0; i < masterMindRows.length; i++) {
			masterMindRows[i] = new MasterMindRow();
			if (i < 9) {
				masterMindRows[i].enableRow(false);
			}
			rows.add(masterMindRows[i]);
		}

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("MasterMind");
		mainFrame.setSize(400, 500);

		mainFrame.setLocationRelativeTo(null);

		JButton okay = new JButton("Okay");
		okay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean allRight = checkInput(masterMindRows[currentRow],
						combination);

				if (allRight) {
					mainFrame.setEnabled(false);
					int highscore = currentRow * 1000;
					currentPlayer.setHighscore(highscore);
					int option = JOptionPane.showConfirmDialog(mainFrame,
							"Kombination erraten! \n\n Highscore:  "
									+ highscore);
					if (option == 0) {
						saveScoreToCard(currentPlayer);
						mainFrame.dispose();
						mainFrame = new JFrame();
						guiFrame.setEnabled(true);
						guiFrame.setVisible(true);
						mainGui();
					}
				}

				activateNextLine(masterMindRows);
			}
		});

		mainFrame.add(rows, BorderLayout.NORTH);
		mainFrame.add(okay, BorderLayout.SOUTH);
		mainFrame.setVisible(true);

	}

	protected boolean checkInput(MasterMindRow masterMindRows, int[] combination) {
		int chosenColorIndex1 = masterMindRows.getFirstSelectedIndex();
		int chosenColorIndex2 = masterMindRows.getSecondSelectedIndex();
		int chosenColorIndex3 = masterMindRows.getThirdSelectedIndex();
		int chosenColorIndex4 = masterMindRows.getFourthSelectedIndex();

		int rightPositioned = getRightPositioned(combination,
				chosenColorIndex1, chosenColorIndex2, chosenColorIndex3,
				chosenColorIndex4);
		int falsePositioned = getFalsePositioned(combination,
				chosenColorIndex1, chosenColorIndex2, chosenColorIndex3,
				chosenColorIndex4);
		masterMindRows.setIcon(rightPositioned, falsePositioned);
		if (rightPositioned == 4) {
			return true;
		} else {
			return false;
		}
	}

	private int getFalsePositioned(int[] combination, int chosenColorIndex1,
			int chosenColorIndex2, int chosenColorIndex3, int chosenColorIndex4) {
		int falsePositioned = 0;

		if (chosenColorIndex1 == combination[1]
				|| chosenColorIndex1 == combination[2]
				|| chosenColorIndex1 == combination[3]) {
			falsePositioned++;
		}
		if (chosenColorIndex2 == combination[0]
				|| chosenColorIndex2 == combination[2]
				|| chosenColorIndex2 == combination[3]) {
			falsePositioned++;
		}
		if (chosenColorIndex3 == combination[0]
				|| chosenColorIndex3 == combination[1]
				|| chosenColorIndex3 == combination[3]) {
			falsePositioned++;
		}
		if (chosenColorIndex4 == combination[0]
				|| chosenColorIndex4 == combination[1]
				|| chosenColorIndex4 == combination[2]) {
			falsePositioned++;
		}

		return falsePositioned;
	}

	private int getRightPositioned(int[] combination, int chosenColorIndex1,
			int chosenColorIndex2, int chosenColorIndex3, int chosenColorIndex4) {
		int rightPositioned = 0;
		if (chosenColorIndex1 == combination[0]) {
			rightPositioned++;
		}
		if (chosenColorIndex2 == combination[1]) {
			rightPositioned++;
		}
		if (chosenColorIndex3 == combination[2]) {
			rightPositioned++;
		}
		if (chosenColorIndex4 == combination[3]) {
			rightPositioned++;
		}
		return rightPositioned;
	}

	private void activateNextLine(MasterMindRow[] array) {
		// activate nextLine
		array[currentRow].enableRow(false);
		array[currentRow - 1].enableRow(true);
		currentRow = currentRow - 1;
	}

	private boolean loginPlayer(Player gamer) {
		// Sucht den Spieler auf der OnCard-Applet
		boolean loginSucceded = false;
		connect.connect();
		gamer = connect.loginGamer(gamer);
		if (gamer != null) {
			// Existierendes Spielerprofil gefunden
			loginSucceded = true;
		}
		return loginSucceded;
	}

	private void saveScoreToCard(Player gamer) {
		// Aktualisierung des Spielerprofiles
		boolean updateSucceded = connect.updateGamerData(gamer);
		if (updateSucceded) {
			System.out.println("Spieler update erfolgreich ausgeführt!");
		} else {
			System.out
					.println("Spieler update konnte NICHT ausgeführt werden!");
		}
	}

	public class LimitDocumentFilter extends DocumentFilter {

		private int limit;

		public LimitDocumentFilter(int limit) {
			if (limit <= 0) {
				throw new IllegalArgumentException("Limit can not be <= 0");
			}
			this.limit = limit;
		}

		@Override
		public void replace(FilterBypass fb, int offset, int length,
				String text, AttributeSet attrs) throws BadLocationException {
			int currentLength = fb.getDocument().getLength();
			int overLimit = (currentLength + text.length()) - limit - length;
			if (overLimit > 0) {
				text = text.substring(0, text.length() - overLimit);
			}
			if (text.length() > 0) {
				super.replace(fb, offset, length, text, attrs);
			}
		}

	}

}
