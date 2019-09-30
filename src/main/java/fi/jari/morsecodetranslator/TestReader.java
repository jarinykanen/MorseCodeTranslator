/**
 *
 */
package fi.jari.morsecodetranslator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Gpio;

/**
 * @author Nakki
 *
 */
public class TestReader implements Runnable {

	private boolean running = true;
	private GpioPinDigitalInput input;
	private GpioPinDigitalOutput buzzer;
	private Instant buttonPressedFirstTime;
	private Instant buttonReleased;
	private List<String> charList = new ArrayList<String>();
	private Map<String, String> codeMap = new HashMap<String, String>();
	private StringBuilder message = new StringBuilder();

	public TestReader(GpioPinDigitalInput soundSensor, GpioPinDigitalOutput buzzer) {
		this.input = soundSensor;
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

//		input.addListener(new GpioPinListenerDigital() {
//
//			@Override
//			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//				while (event.getState() == PinState.HIGH) {
//					System.out.println("Button is pressed");
//					buzz();
//				}
//			}
//
//
//		});
		boolean firstPress = true;
		boolean wasNewLetter = false;
		boolean wasNewWord = false;
		while (isRunning()) {

			firstPress = whilePressed(firstPress);
			if (buttonPressedFirstTime != null) {
				buttonReleased = Instant.now();
				long delta = Duration.between(buttonPressedFirstTime, buttonReleased).toMillis();
				String character = processPressDuration(delta);
				if (character != null) {
					charList.add(character);
				} else {
					charList.clear();
				}

			}
			if (charList.size() > 6) {
				charList.clear();
			}
			unBuzz();
			buttonPressedFirstTime = null;
			firstPress = true;
		}

	}

	private boolean whilePressed(boolean firstPress) {
		while (Gpio.digitalRead(17) == 1) {

			if (buttonReleased != null && firstPress) {
				buttonPressedFirstTime = Instant.now();
				long delta = Duration.between(buttonReleased, buttonPressedFirstTime).toMillis();
				if (checkLetter(delta)) {
					printLetter(charList);
					charList.clear();
					System.out.println(message);
				} else if (checkWord(delta)) {
					charList.clear();
					message.append(" ");
					System.out.println(message);
				}

			} else if (firstPress) {
				buttonPressedFirstTime = Instant.now();
			}
			firstPress = false;
			buzz();
		}
		return firstPress;
	}

	private void printLetter(List<String> charList) {
		StringBuilder builder = new StringBuilder();
		for (String character : charList) {
			builder.append(character);
		}
		String asd = codeMap.get(builder.toString());
		if (asd != null) {
			message.append(asd);
		}
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
			String character = value.getKey();
			codeMap.put(morseCode, character);
		}
	}

	private boolean checkLetter(long time) {
		if (time > 1500 && time <= 2500) {
			System.out.println("New letter starts");
			return true;
		} else {
//			System.out.println("Too short or long");
		}
		return false;
	}

	private boolean checkWord(long time) {
		if (time > 2500) {
			System.out.println("New word starts");
			return true;
		} else {
//			System.out.println("Too short or long");
		}
		return false;
	}

	private String processPressDuration(long time) {

		if (time > 0 && time <= 500) {
			System.out.println("This was .");
			return ".";
		} else if (time > 500 && time <= 1500) {
			System.out.println("This was -");
			return "-";
		} else {
//			System.out.println("Too short or long");
		}
		return null;

	}

	private void buzz() {
		buzzer.setState(PinState.HIGH);
	}

	private void unBuzz() {
		buzzer.setState(PinState.LOW);

	}

//	private I2CBus getBus() {
//		try {
//			return I2CFactory.getInstance(I2CBus.BUS_1);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	private void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {

		}
	}

	public void stop() {
		setRunning(false);
		input.removeAllListeners();
	}

}
