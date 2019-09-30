/**
 *
 */
package fi.jari.morsecodetranslator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

/**
 * @author Nakki
 *
 */
public class MorseCoder implements Runnable {

	private boolean running = true;
	private GpioPinDigitalOutput buzzer;
	private List<String> charList = new ArrayList<String>();
	private Map<String, String> codeMap = new HashMap<String, String>();

	public MorseCoder(GpioPinDigitalOutput buzzer) {
		this.buzzer = buzzer;
		readCodes();
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {

		String thisToMorse = "Does this thing work at all";

		while (isRunning()) {
			for (char character : thisToMorse.toCharArray()) {
				String code = codeMap.get(String.valueOf(character).toLowerCase());
				System.out.println(character);
				if (code != null) {
//					System.out.println(code);
					for (Character morseCode : code.toCharArray()) {
						if (morseCode.equals(".".charAt(0))) {
//							System.out.println(morseCode);
							buzz(100);
							sleep(100);
						} else if (morseCode.equals("-".charAt(0))) {
							buzz(200);
							sleep(100);
						}
					}

				} else {
					sleep(700);
				}

			}
			sleep(5000);
		}

	}

	private void buzz(long duration) {
		buzzer.setState(PinState.HIGH);
		sleep(duration);
		buzzer.setState(PinState.LOW);
	}

	private void readCodes() {
		String path = "/home/jari/felix/etc/codes.json";
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Gson gson = new Gson();
		Alphabets json = gson.fromJson(bufferedReader, Alphabets.class);

		List<CodeMapAttrs> alphabets = json.getalphabet();
		List<CodeMapAttrs> digits = json.getDigit();
		List<CodeMapAttrs> marks = json.getPunctuation_mark();
		addToMap(alphabets);
		addToMap(digits);
		addToMap(marks);
	}

	private void addToMap(List<CodeMapAttrs> list) {
		for (CodeMapAttrs value : list) {
			String morseCode = value.getVal();
			String character = value.getKey().toLowerCase();
			codeMap.put(character, morseCode);
		}
	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {

		}
	}

	public void stop() {
		setRunning(false);
	}

}
