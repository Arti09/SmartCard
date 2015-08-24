package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import opencard.core.event.CTListener;
import opencard.core.event.CardTerminalEvent;
import opencard.core.event.EventGenerator;
import opencard.core.service.CardRequest;
import opencard.core.service.CardServiceException;
import opencard.core.service.SmartCard;
import opencard.core.terminal.CardTerminalException;
import opencard.core.terminal.CommandAPDU;
import opencard.core.terminal.ResponseAPDU;
import opencard.core.util.HexString;
import opencard.core.util.OpenCardPropertyLoadingException;
import opencard.opt.util.PassThruCardService;

public class Connector implements CTListener {
	private SmartCard smartCard = null;

	private byte[] SELECT_APPLET_APDU = { (byte) 0x00, (byte) 0xA4,
			(byte) 0x04, (byte) 0x00, (byte) 0x05, (byte) 0x4D, (byte) 0x4D,
			(byte) 0x4F, (byte) 0x4E, (byte) 0x43, (byte) 0x00 };

	private byte[] SEND_CREATE_APDU = { (byte) 0x80, (byte) 0x01, (byte) 0x00,
			(byte) 0x00, (byte) 0x12 };

	private byte[] SEND_LOGIN_APDU = { (byte) 0x80, (byte) 0x02, (byte) 0x00,
			(byte) 0x00, (byte) 0x0E };

	private byte[] SEND_UPDATE_APDU = { (byte) 0x80, (byte) 0x03, (byte) 0x00,
			(byte) 0x00, (byte) 0x12 };

	final private byte[] createDefaultHighscore = { (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0xFF };

	/**
	 * Verbindung mit Smartcard wird aufgebaut.
	 */
	public void connect() {
		try {
			SmartCard.start();
			EventGenerator.getGenerator().addCTListener(this);
			this.cardInserted(null);
		} catch (OpenCardPropertyLoadingException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		} catch (CardServiceException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		} catch (CardTerminalException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Automatisch erstellter Catch-Block
			e.printStackTrace();
		}
	}

	/**
	 * Neuer Spieler wird auf Smartcard mit Startwerten
	 * 
	 * @param gamer
	 *            -- anzulegender Spieler
	 * @return true -- Spieler wurde angelegt; false -- Spieler konnte nicht
	 *         angelegt werden
	 */
	public boolean createNewGamer(Player gamer) {
		try {
			PassThruCardService passThru = (PassThruCardService) smartCard
					.getCardService(PassThruCardService.class, true);

			CommandAPDU commandAPDU = new CommandAPDU(
					SEND_CREATE_APDU.length + 30);
			commandAPDU.setLength(SEND_CREATE_APDU.length + 30);
			System.arraycopy(SEND_CREATE_APDU, 0, commandAPDU.getBuffer(), 0,
					SEND_CREATE_APDU.length);

			String name = gamer.getName();
			byte[] pin = getMD5FromPassword(gamer.getPin());

			if (name.length() != 10) {
				for (int i = name.length(); i < 10; i++) {
					name += "0";
				}
			}

			byte[] nameArray = Converter.StringToByte(name);
			byte[] pinArray = pin;
			byte[] dataArray = new byte[nameArray.length + pinArray.length
					+ createDefaultHighscore.length];

			System.arraycopy(nameArray, 0, dataArray, 0, nameArray.length);
			System.arraycopy(pinArray, 0, dataArray, nameArray.length,
					pinArray.length);
			System.arraycopy(createDefaultHighscore, 0, dataArray,
					nameArray.length + pinArray.length,
					createDefaultHighscore.length);

			for (int i = 0; i < dataArray.length; i++) {
				commandAPDU.getBuffer()[i + SEND_CREATE_APDU.length] = dataArray[i];
			}

			ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);

			String retCode = HexString.hexifyShort(responseAPDU1.sw1(),
					responseAPDU1.sw2());

			if (retCode.compareTo("9000") == 0) {
				if (responseAPDU1.getBuffer()[0] == 0)
					return true;
			} else {
				System.out.println("ERROR kein 9000 :-/!");
			}

		} catch (Exception e) {
			System.out.println("Exception beim neuen Spieler anlegen: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Spieler wird für Spiel angemeldet. Wenn Spielername auf Smartcard
	 * vorhanden ist und PIN korrekt eingegeben wurde, werden gespeicherte
	 * Spielerdaten zurückgegeben.
	 * 
	 * @param gamer
	 *            -- anzumeldender Spieler
	 * @return Spieler mit gespeicherten Spielerdaten, null bei fehlerhaftem
	 *         Login
	 */
	public Player loginGamer(Player gamer) {
		try {
			PassThruCardService passThru = (PassThruCardService) smartCard
					.getCardService(PassThruCardService.class, true);

			CommandAPDU commandAPDU = new CommandAPDU(
					SEND_LOGIN_APDU.length + 26);

			commandAPDU.setLength(SEND_LOGIN_APDU.length + 26);
			System.arraycopy(SEND_LOGIN_APDU, 0, commandAPDU.getBuffer(), 0,
					SEND_LOGIN_APDU.length);

			String name = gamer.getName();
			byte[] pin = getMD5FromPassword(gamer.getPin());
			if (name.length() != 10) {
				for (int i = name.length(); i < 10; i++) {
					name += "0";
				}
			}

			byte[] nameArray = Converter.StringToByte(name);
			byte[] pinArray = pin;
			byte[] dataArray = new byte[nameArray.length + pinArray.length + 1];

			System.arraycopy(nameArray, 0, dataArray, 0, nameArray.length);
			System.arraycopy(pinArray, 0, dataArray, nameArray.length,
					pinArray.length);
			dataArray[dataArray.length - 1] = (byte) 0x05;

			for (int i = 0; i < dataArray.length; i++) {
				commandAPDU.getBuffer()[i + SEND_LOGIN_APDU.length] = dataArray[i];
			}

			ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);

			String retCode = HexString.hexifyShort(responseAPDU1.sw1(),
					responseAPDU1.sw2());

			if (retCode.compareTo("9000") == 0) {
				if (responseAPDU1.getBuffer()[0] == 0) {
					byte[] creditBuffer = new byte[2];
					creditBuffer[0] = responseAPDU1.getBuffer()[1];
					creditBuffer[1] = responseAPDU1.getBuffer()[2];
				} else if (responseAPDU1.getBuffer()[0] == 1) {
					gamer = null;
				}
			} else {
				System.out.println("ERROR kein 9000!");
			}

		} catch (Exception e) {
			System.out.println("Exception beim login: " + e.getMessage());
			e.printStackTrace();
		}
		return gamer;
	}

	/**
	 * Neue Spielerwerte werde auf Smartcard geschrieben. Alte Spielerwerte
	 * werden überschrieben.
	 * 
	 * @param gamer
	 *            -- zu speichernder Spieler
	 * @return true -- Update erfolgreich; false -- Update fehlerhaft
	 */
	public boolean updateGamerData(Player gamer) {
		boolean updateSucceded = false;
		try {
			PassThruCardService passThru = (PassThruCardService) smartCard
					.getCardService(PassThruCardService.class, true);

			CommandAPDU commandAPDU = new CommandAPDU(
					SEND_UPDATE_APDU.length + 19);
			commandAPDU.setLength(SEND_UPDATE_APDU.length + 19);
			System.arraycopy(SEND_UPDATE_APDU, 0, commandAPDU.getBuffer(), 0,
					SEND_UPDATE_APDU.length);

			String name = gamer.getName();
			String pin = gamer.getPin();

			if (name.length() != 10) {
				for (int i = name.length(); i < 10; i++) {
					name += "0";
				}
			}

			byte[] nameArray = Converter.StringToByte(name);
			byte[] pinArray = Converter.StringToByte(pin);
			byte[] dataArray = new byte[nameArray.length + pinArray.length
					+ createDefaultHighscore.length];

			System.arraycopy(nameArray, 0, dataArray, 0, nameArray.length);
			System.arraycopy(pinArray, 0, dataArray, nameArray.length,
					pinArray.length);
			int dataPos = nameArray.length + pinArray.length;
			byte[] byteArray = Converter.IntegerToByte(gamer.getHighscore(), 2);
			dataArray[dataPos] = byteArray[0];
			dataArray[dataPos + 1] = byteArray[1];
			dataArray[dataPos + 2] = (byte) 0x01;

			for (int i = 0; i < dataArray.length; i++) {
				commandAPDU.getBuffer()[i + SEND_UPDATE_APDU.length] = dataArray[i];
			}

			ResponseAPDU responseAPDU1 = passThru.sendCommandAPDU(commandAPDU);

			String retCode = HexString.hexifyShort(responseAPDU1.sw1(),
					responseAPDU1.sw2());

			if (retCode.compareTo("9000") == 0) {
				if (responseAPDU1.getBuffer()[0] == 0)
					updateSucceded = true;
				else
					updateSucceded = false;
			} else {
				System.out.println("ERROR kein 9000!");
			}

		} catch (Exception e) {
			System.out.println("Exception beim update: " + e.getMessage());
			e.printStackTrace();
		}
		return updateSucceded;
	}

	public void cardInserted(CardTerminalEvent arg0)
			throws CardTerminalException {
		CardRequest cardRequest = new CardRequest(CardRequest.ANYCARD, null,
				PassThruCardService.class);
		cardRequest.setTimeout(1);
		smartCard = SmartCard.waitForCard(cardRequest);
		if (smartCard != null) {
			if (selectApplet(smartCard)) {
				System.out.println("Applet installiert");
			} else {
				System.out.println("Applet nicht installiert");
			}
		} else {
			System.out.println("Bitte SmartCard einfügen");
		}
	}

	/**
	 * Applet auf Smartcard wird ausgewählt. AppletID: |SmPro
	 * 
	 * @param sm
	 *            -- Smartcard
	 * @return true -- ResponseCode 9000 i.O; false -- Fehler bei Übertragung
	 * 
	 */
	private boolean selectApplet(SmartCard sm) {
		if (sm != null) {
			try {
				PassThruCardService passThru = (PassThruCardService) sm
						.getCardService(PassThruCardService.class, true);

				CommandAPDU commandAPDUSelect = new CommandAPDU(
						SELECT_APPLET_APDU.length);
				commandAPDUSelect.setLength(SELECT_APPLET_APDU.length);
				System.arraycopy(SELECT_APPLET_APDU, 0,
						commandAPDUSelect.getBuffer(), 0,
						SELECT_APPLET_APDU.length);

				ResponseAPDU responseAPDU1 = passThru
						.sendCommandAPDU(commandAPDUSelect);

				String ret = HexString.hexifyShort(responseAPDU1.sw1(),
						responseAPDU1.sw2());
				if (ret.compareTo("9000") == 0) {
					return true;
				}

			} catch (ClassNotFoundException cnfe) {
				System.out.println("ClassNotFoundException: ");
				System.out.println(cnfe.getMessage());
			} catch (CardServiceException cse) {
				System.out.println("CardServiceException: ");
				System.out.println(cse.getMessage());
			} catch (CardTerminalException cte) {
				System.out.println("CardTerminalException: ");
				System.out.println(cte.getMessage());
			}
		}
		return false;
	}

	public void cardRemoved(CardTerminalEvent arg0)
			throws CardTerminalException {
		smartCard = null;
	}

	/**
	 * Smartcard Verbindung wird geschlossen, sofern verbunden.
	 */
	public void disconnect() {
		if (SmartCard.isStarted()) {
			try {
				SmartCard.shutdown();
			} catch (CardTerminalException e) {
				e.printStackTrace();
			}
		}
	}

	public static byte[] getMD5FromPassword(String pass)
			throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(pass.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < 8; i++) {
			String hex = Integer.toHexString(0xff & digest[i]);
			if (hex.length() == 1)
				hexString.append('0');

			hexString.append(hex);
		}
		return hexString.toString().getBytes();
	}
}