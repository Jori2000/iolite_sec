package de.iolite.apps.secapp;

import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;

/**
 * @author Jori
 *
 */
public class SensorReadingRoutine implements Runnable {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SensorReadingRoutine.class);
	private final Set<Device> triggerElements;
	private final Set<Device> reactionElements;
	private final Set<Device> allElements;
	private static final String TEMPERATURE = "http://iolite.de#currentEnvironmentTemperature";
	private static final String CO2 = "http://iolite.de#carbonDioxidePPM";
	private static final String HUMIDITY = "http://iolite.de#humidityLevel";

	public SensorReadingRoutine(Set<Device> triggerElements, Set<Device> reactionElements, Set<Device> allElements) {
		this.triggerElements = triggerElements;
		this.reactionElements = reactionElements;
		this.allElements = allElements;
	}

	@Override
	public void run() {
		try {
			Thread currentThread = Thread.currentThread();
			while (currentThread.isAlive()) {
				if (!allElements.isEmpty())
					LOGGER.debug("check if something was triggered: " + checkForTrigger());
				currentThread.sleep(30000);
			}
		} catch (Exception e) {
			LOGGER.error("Exception while thread trying to wait 30 secs");
		}
	}

	private double checkForTrigger() {
		String[] all = { "AlarmSiren", "AlarmSystem", "Blind", "Camera", "carbonDioxideSensor", "ContactSensor",
				"DimmableLamp", "Door", "Doorbell", "HSVLamp", "Lamp", "LuminanceSensor", "MovementSensor", "Socket",
				"Sunblind", "VibrationSensor", "MediaPlayerDevice", "TV" };
		String[] trigger = { "Camera", "carbonDioxideSensor", "ContactSensor", "Door", "LuminanceSensor", "Doorbell",
				"MovementSensor", "VibrationSensor" };
		String[] reaction = { "AlarmSiren", "Blind", "Camera", "DimmableLamp", "HSVLamp", "Lamp", "Socket", "Sunblind",
				"MediaPlayerDevice", "TV" };

		for (Device module : triggerElements) {
			switch (module.getIdentifier()) {
			case "Camera":
				// This has to be done manually
				if (false)
					Storage.addEvent(module);
				break;
			case "carbonDioxideSensor":
				// This has to be done manually
				if (false)
					Storage.addEvent(module);
				break;
			case "ContactSensor":
				if (module.getProperty("http://iolite.de#contactDetected").getValue().toString() == "false")
					Storage.addEvent(module);
				break;
			case "Door":
				if (module.getProperty("http://iolite.de#open").getValue().toString() == "true")
					Storage.addEvent(module);
				break;
			case "LuminanceSensor":
				// This has to be done manually
				if (false)
					Storage.addEvent(module);
				break;
			case "Doorbell":
				if (module.getProperty("http://iolite.de#ringing").getValue().toString() == "true")
					Storage.addEvent(module);
				break;
			case "MovementSensor":
				if (module.getProperty("http://iolite.de#movementDetected").getValue().toString() == "true")
					Storage.addEvent(module);
				break;
			case "VibrationSensor":
				if (module.getProperty("http://iolite.de#vibrationDetected").getValue().toString() == "true")
					Storage.addEvent(module);
				break;
			}
		}
		;

		return 0;

		// if finds something
		// return something
		// double averageHumidity = 0;
		// for (Device humiditySensor : allElements) {
		// averageHumidity +=
		// Double.valueOf(humiditySensor.getProperty(HUMIDITY).getValue().toString());
		// }
		// averageHumidity = averageHumidity / allElements.size();
		// return averageHumidity;
	}

}
