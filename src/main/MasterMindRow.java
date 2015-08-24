package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class MasterMindRow extends JPanel {

	static String[] colorOptions = { "R", "G", "B", "S", "Br", "Gb", "W" };
	JComboBox first = new JComboBox(colorOptions);
	JComboBox second = new JComboBox(colorOptions);
	JComboBox third = new JComboBox(colorOptions);
	JComboBox fourth = new JComboBox(colorOptions);
	JLabel icon = new JLabel(new ImageIcon());

	int firstSelectedIndex = 0;
	int secondSelectedIndex = 0;
	int thirdSelectedIndex = 0;
	int fourthSelectedIndex = 0;

	ImageIcon oneRight;
	ImageIcon twoRight;
	ImageIcon threeRight;
	ImageIcon fourRight;
	ImageIcon oneWrong;
	ImageIcon twoWrong;
	ImageIcon threeWrong;
	ImageIcon fourWrong;
	ImageIcon twoTwo;
	ImageIcon nothing;

	public MasterMindRow() {
		super();
		setLayout(new FlowLayout());
		first.setRenderer(new MyCellRenderer());
		second.setRenderer(new MyCellRenderer());
		third.setRenderer(new MyCellRenderer());
		fourth.setRenderer(new MyCellRenderer());

		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				firstSelectedIndex = first.getSelectedIndex();
			}
		};
		first.addActionListener(actionListener);

		ActionListener actionListener2 = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				secondSelectedIndex = second.getSelectedIndex();
			}
		};
		second.addActionListener(actionListener2);

		ActionListener actionListener3 = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				thirdSelectedIndex = third.getSelectedIndex();
			}
		};
		third.addActionListener(actionListener3);

		ActionListener actionListener4 = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				fourthSelectedIndex = fourth.getSelectedIndex();
			}
		};
		fourth.addActionListener(actionListener4);

		add(first);
		add(second);
		add(third);
		add(fourth);
		add(icon);

		oneRight = new ImageIcon(loadImage(1));
		twoRight = new ImageIcon(loadImage(2));
		threeRight = new ImageIcon(loadImage(3));
		fourRight = new ImageIcon(loadImage(4));
		oneWrong = new ImageIcon(loadImage(5));
		twoWrong = new ImageIcon(loadImage(6));
		threeWrong = new ImageIcon(loadImage(7));
		fourWrong = new ImageIcon(loadImage(8));
		twoTwo = new ImageIcon(loadImage(9));
		nothing = new ImageIcon(loadImage(0));
	}

	// 0 alle falsch
	// 1 - 4 richtige Positionen
	// 5 - 8 falsche Positionen
	// 9 zwei richtig, zwei falsch
	private Image loadImage(int number) {
		Image img = null;
		switch (number) {
		case 0:
			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/AllWrong.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 1:
			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/OnePositionRight.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 2:
			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/TwoPositionRight.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 3:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/ThreePositionRight.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 4:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/FourPositionRight.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 5:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/OneWrongPosition.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 6:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/TwoWrongPosition.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 7:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/ThreeWrongPosition.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 8:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/FourWrong.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		case 9:

			try {
				img = ImageIO.read(getClass().getResource(
						"/resources/TwoWrongTwoPositionRight.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return img.getScaledInstance(30, 30, 0);
		default:
			return null;
		}

	}

	public MasterMindRow(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public MasterMindRow(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		// TODO Auto-generated constructor stub
	}

	public MasterMindRow(LayoutManager layout) {
		super(layout);
		// TODO Auto-generated constructor stub
	}

	public void enableRow(boolean enable) {
		first.setEnabled(enable);
		second.setEnabled(enable);
		third.setEnabled(enable);
		fourth.setEnabled(enable);
	}

	public void setIcon(int amountRight, int amountFalseRight) {
		if (amountRight == 4) {
			icon.setIcon(fourRight);
		} else if (amountRight == 1 && amountFalseRight == 0) {
			icon.setIcon(oneRight);
		} else if (amountRight == 2 && amountFalseRight == 0) {
			icon.setIcon(twoRight);
		} else if (amountRight == 2 && amountFalseRight == 2) {
			icon.setIcon(twoTwo);
		} else if (amountRight == 3 && amountFalseRight == 0) {
			icon.setIcon(threeRight);
		} else if (amountRight == 0 && amountFalseRight == 1) {
			icon.setIcon(oneWrong);
		} else if (amountRight == 0 && amountFalseRight == 2) {
			icon.setIcon(twoWrong);
		} else if (amountRight == 0 && amountFalseRight == 3) {
			icon.setIcon(threeWrong);
		} else if (amountRight == 0 && amountFalseRight == 4) {
			icon.setIcon(fourWrong);
		} else {
			// chosing right icons
			icon.setIcon(nothing);
		}
	}

	class MyCellRenderer extends JLabel implements ListCellRenderer<Object> {
		public MyCellRenderer() {
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {

			setText(value.toString());

			Color background = Color.white;
			Color foreground = Color.black;

			// check if this cell represents the current DnD drop location
			JList.DropLocation dropLocation = list.getDropLocation();

			if (dropLocation != null && !dropLocation.isInsert()
					&& dropLocation.getIndex() == index) {

				// check if this cell is selected
			} else if (isSelected) {
				if (value.toString().compareTo(colorOptions[0]) == 0) {
					background = Color.RED;
					foreground = Color.black;
				}
				if (value.toString().compareTo(colorOptions[1]) == 0) {
					background = Color.green;
					foreground = Color.black;
				}
				if (value.toString().compareTo(colorOptions[2]) == 0) {
					background = Color.blue;
					foreground = Color.black;
				}
				if (value.toString().compareTo(colorOptions[3]) == 0) {
					background = Color.lightGray;
					foreground = Color.black;
				}
				if (value.toString().compareTo(colorOptions[4]) == 0) {
					background = Color.orange;
					foreground = Color.black;
				}
				if (value.toString().compareTo(colorOptions[5]) == 0) {
					background = Color.yellow;
					foreground = Color.black;
				}
				if (value.toString().compareTo(colorOptions[6]) == 0) {
					background = Color.white;
					foreground = Color.black;
				}

				// unselected, and not the DnD drop location
			} else {
			}
			;

			setBackground(background);
			setForeground(foreground);

			return this;
		}
	}

	public int getFirstSelectedIndex() {
		return firstSelectedIndex;
	}

	public int getSecondSelectedIndex() {
		return secondSelectedIndex;
	}

	public int getThirdSelectedIndex() {
		return thirdSelectedIndex;
	}

	public int getFourthSelectedIndex() {
		return fourthSelectedIndex;
	}

}
