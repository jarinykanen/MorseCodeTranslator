/**
 *
 */
package fi.jari.morsecodetranslator;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/**
 * @author Nakki
 *
 */
public class Motion implements Runnable {

	private boolean running = true;

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public void run() {

		I2CBus bus = getBus();
		if (bus != null) {
			while (isRunning()) {

				try {
					I2CDevice device = bus.getDevice(0x29);
					System.out.println(device + "asd");
					device.write((byte) 0x00);
					int value = device.read();
					System.out.println(value);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				sleep(1000);
			}
		}

	}

	private I2CBus getBus() {
		I2CBus bus = null;
		try {
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
		} catch (UnsupportedBusNumberException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bus;
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
