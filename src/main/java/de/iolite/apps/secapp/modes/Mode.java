package de.iolite.apps.secapp.modes;

import java.util.ArrayList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;

public class Mode {
	private static final Logger LOGGER = LoggerFactory.getLogger(Mode.class);

	private Set<Device> trigger;
	private ArrayList<Reaction> reactions;

	public Mode(Set<Device> trigger, ArrayList<Reaction> reactions) {

		this.trigger = trigger;
		this.reactions = reactions;
	}
}
