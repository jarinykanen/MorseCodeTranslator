package fi.jari.morsecodetranslator;

import java.util.HashMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;

public class Activator implements BundleActivator {

	private HashMap<Integer, Object> map = new HashMap<Integer, Object>();
	private GpioController gpio;
	private GpioPinDigitalInput soundSensor;
	private GpioPinDigitalOutput buzzer;

	@Override
	public void start(BundleContext context) {
		System.out.println("Starting the bundle");
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
		gpio = GpioFactory.getInstance();
		soundSensor = gpio.provisionDigitalInputPin(RaspiBcmPin.GPIO_17, "SoundSensor");
		buzzer = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_23, "buzzer");

		MorseCoder coder = new MorseCoder(buzzer);
		Thread thread = new Thread(coder);
		thread.start();

//		TestReader reader = new TestReader(soundSensor, buzzer);
//		Thread thread = new Thread(reader);
//		thread.start();
//		map.put(1, reader);

//		Motion motion = new Motion();
//		Thread motionThread = new Thread(motion);
//		motionThread.start();
//		map.put(1, motion);

	}

	@Override
	public void stop(BundleContext context) {
		System.out.println("Stopping the bundle");
		gpio.shutdown();
		System.out.println(gpio.isShutdown());

	}

}