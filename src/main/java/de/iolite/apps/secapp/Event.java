package de.iolite.apps.secapp;

import java.sql.Timestamp;
import java.util.Calendar;

import de.iolite.app.api.device.access.Device;

public class Event {

	private Device device;
	private Timestamp timestamp;
	private String message;

	public Event(Device device, String message) {
		this.device = device;
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		this.timestamp = currentTimestamp;
		this.message = message;
	}

	public Event(Device device) {
		this.device = device;
		Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
		this.timestamp = currentTimestamp;
	}

	public Device getDevice() {
		return device;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
		return message;
	}

}
