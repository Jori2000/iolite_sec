package de.iolite.apps.secapp.modes;

import java.text.MessageFormat;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.Device;
import de.iolite.app.api.device.DeviceAPIException;
import de.iolite.app.api.device.DeviceBooleanProperty;
import de.iolite.drivers.basic.DriverConstants;

/**
 * 
 * @author Jori
 *
 *         Single reaction - can be either the function of device or a software
 *         function
 */
public class Reaction {
	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(Reaction.class);

	private Device device;
	private int specification;
	private Functionality functionality;

	public Reaction(Device device, int specification) {
		this.device = device;
		this.specification = specification;
	}

	public Reaction(Functionality functionality) {

	}

	public void startReaction() {
		final DeviceBooleanProperty onProperty = device.getBooleanProperty(DriverConstants.PROPERTY_on_ID);
		if (onProperty != null) {
			final boolean isDeviceOn = onProperty.getValue();
			LOGGER.debug(
					MessageFormat.format("Device ''{0}'' is '1'", device.getIdentifier(), isDeviceOn ? "on" : "off"));
			try {
				onProperty.requestValueUpdate(!isDeviceOn);
			} catch (DeviceAPIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
