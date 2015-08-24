package test;

import java.security.NoSuchAlgorithmException;

import main.Connector;

import org.junit.Test;

public class MainTest {

	@Test
	public void test() {
		try {
			String pass = "1234";
			byte[] passMD5 = Connector.getMD5FromPassword(pass);

			System.out.println("original:   \t\t" + pass);
			System.out.println("Digest(in hex format): " + passMD5.length);

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void test2() {
		byte x = (byte) 0x00;
		byte y = (byte) 0;
		System.out.println(x == y);
	}
}
