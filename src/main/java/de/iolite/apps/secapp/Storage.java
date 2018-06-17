package de.iolite.apps.secapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.app.api.device.access.Device;
import de.iolite.apps.secapp.modes.Mode;

/*
 * Has to be static so every class can fetch Infos from it
 */
public class Storage {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(Storage.class);

	// final???
	private static Set<Device> triggerElements;
	private static Set<Device> reactionElements;
	private static Set<Device> allElements;

	private static ArrayList<Event> eventList = new ArrayList<Event>();

	private final HashMap<String, LinkedList<Double>> classicModeConnections = new HashMap<String, LinkedList<Double>>();
	private final static HashMap<String, Mode> customModes = new HashMap<String, Mode>();

	public Storage(Set<Device> trigger, Set<Device> reaction, Set<Device> all) {
		this.triggerElements = trigger;
		this.reactionElements = reaction;
		this.allElements = all;

	}

	public void addTriggerElements(Set<Device> values) {

		triggerElements = values;
	}

	public void addReactionElements(Set<Device> values) {
		reactionElements = values;
	}

	public void addAllElements(Set<Device> values) {
		allElements = values;
	}

	public static void addEvent(Device device) {
		Event event = new Event(device);
		eventList.add(event);
	}

}
