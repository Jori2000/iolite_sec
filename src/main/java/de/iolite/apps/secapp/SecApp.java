package de.iolite.apps.secapp;

import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iolite.api.IOLITEAPINotResolvableException;
import de.iolite.api.IOLITEAPIProvider;
import de.iolite.api.IOLITEPermissionDeniedException;
import de.iolite.app.AbstractIOLITEApp;
import de.iolite.app.api.device.access.Device;
import de.iolite.app.api.device.access.DeviceAPI;
import de.iolite.app.api.frontend.FrontendAPI;
import de.iolite.app.api.frontend.FrontendAPIException;
import de.iolite.app.api.frontend.util.FrontendAPIUtility;
import de.iolite.apps.secapp.internals.PageWithEmbeddedSessionTokenRequestHandler;
import de.iolite.common.lifecycle.exception.CleanUpFailedException;
import de.iolite.common.lifecycle.exception.InitializeFailedException;
import de.iolite.common.lifecycle.exception.StartFailedException;
import de.iolite.common.lifecycle.exception.StopFailedException;
import de.iolite.common.requesthandler.IOLITEHTTPRequestHandler;
import de.iolite.common.requesthandler.StaticResources;
import de.iolite.common.requesthandler.StaticResources.PathHandlerPair;
import de.iolite.utilities.disposeable.Disposeable;

public class SecApp extends AbstractIOLITEApp {

	@Nonnull
	private static final Logger LOGGER = LoggerFactory.getLogger(SecApp.class);
	private FrontendAPI frontendAPI;
	private DeviceAPI deviceAPI;
	private Disposeable disposeableAssets;
	private SensorReadingRoutine sensorReadingRoutine;

	@Override
	protected void cleanUpHook() throws CleanUpFailedException {
		LOGGER.debug("Cleaning");
		LOGGER.debug("Cleaned");
	}

	@Override
	protected void initializeHook() throws InitializeFailedException {
		LOGGER.debug("Initializing");
		LOGGER.debug("Initialized");
	}

	@Override
	protected void startHook(@Nonnull final IOLITEAPIProvider context) throws StartFailedException {
		LOGGER.debug("Starting");

		try {
			// Frontend API enables the App to expose a user interface
			this.frontendAPI = context.getAPI(FrontendAPI.class);
			initializeWebResources();

			// Device API gives access to devices connected to IOLITE
			this.deviceAPI = context.getAPI(DeviceAPI.class);
			initializeDevices();

			startSensorReadingRoutine();
		} catch (final IOLITEAPINotResolvableException e) {
			throw new StartFailedException(
					MessageFormat.format("Start failed due to required but not resolvable AppAPI: {0}", e.getMessage()),
					e);
		} catch (final IOLITEPermissionDeniedException e) {
			throw new StartFailedException(MessageFormat
					.format("Start failed due to permission denied problems in the examples: {0}", e.getMessage()), e);
		} catch (final FrontendAPIException e) {
			throw new StartFailedException(
					MessageFormat.format("Start failed due to an error in the App API examples: {0}", e.getMessage()),
					e);
		}

		LOGGER.debug("Started");
	}

	@Override
	protected void stopHook() throws StopFailedException {
		LOGGER.debug("Stopping");

		// deregister the static assets
		if (this.disposeableAssets != null) {
			this.disposeableAssets.dispose();
		}

		LOGGER.debug("Stopped");
	}

	/**
	 * Registering web resources.
	 *
	 * @throws FrontendAPIException
	 *             if some resources are not found.
	 */
	private final void initializeWebResources() throws FrontendAPIException {

		// go through static assets and register them
		final Map<URI, PathHandlerPair> assets = StaticResources.scanClasspath("assets", getClass().getClassLoader());
		this.disposeableAssets = FrontendAPIUtility.registerPublicHandlers(this.frontendAPI, assets);

		// index page
		final IOLITEHTTPRequestHandler indexPageRequestHandler = new PageWithEmbeddedSessionTokenRequestHandler(
				loadTemplate("assets/index.html"));
		this.frontendAPI.registerRequestHandler("", indexPageRequestHandler);
		this.frontendAPI.registerRequestHandler("index.html", indexPageRequestHandler);
	}

	/**
	 * Load a HTML template as string.
	 */
	private String loadTemplate(final String templateResource) {
		try {
			return StaticResources.loadResource(templateResource, getClass().getClassLoader());
		} catch (final IOException e) {
			throw new InitializeFailedException("Loading templates for the dummy app failed", e);
		}
	}

	private void initializeDevices() {
		LOGGER.warn("Initializing devices");
		String[] all = { "AlarmSiren", "AlarmSystem", "Blind", "Camera", "carbonDioxideSensor", "ContactSensor",
				"DimmableLamp", "Door", "Doorbell", "HSVLamp", "Lamp", "LuminanceSensor", "MovementSensor", "Socket",
				"Sunblind", "VibrationSensor", "MediaPlayerDevice", "TV" };
		String[] trigger = { "AlarmSystem", "Camera", "carbonDioxideSensor", "ContactSensor", "Door", "LuminanceSensor",
				"Doorbell", "MovementSensor", "VibrationSensor" };
		String[] reaction = { "AlarmSiren", "Blind", "Camera", "DimmableLamp", "HSVLamp", "Lamp", "Socket", "Sunblind",
				"MediaPlayerDevice", "TV" };
		Set<Device> triggerElements = new HashSet<Device>();
		Set<Device> reactionElements = new HashSet<Device>();
		Set<Device> allElements = new HashSet<Device>();

		int triggerCounter = 0;
		int reactionCounter = 0;
		int allCounter = 0;
		LOGGER.warn("Devices: " + this.deviceAPI.getDevices());

		for (final Device device : this.deviceAPI.getDevices()) {
			LOGGER.debug(device.getProfileIdentifier());
			LOGGER.warn(device.getProfileIdentifier());

			for (int i = 0; i < all.length; i++) {
				if (device.getProfileIdentifier().equals("http://iolite.de#" + all[i])) {
					allElements.add(device);
					allCounter++;
					for (int y = 0; i < trigger.length; y++) {
						triggerCounter++;
						triggerElements.add(device);
					}
					for (int y = 0; i < reaction.length; y++) {
						reactionCounter++;
						reactionElements.add(device);
					}
				}
			}
		}
		LOGGER.debug("Number of trigger elements: " + triggerCounter);
		LOGGER.debug("Number of reaction elements: " + reactionCounter);
		LOGGER.debug("Number of relevant elements: " + allCounter);
		LOGGER.warn("Number of trigger elements: " + triggerCounter);
		LOGGER.warn("Number of reaction elements: " + reactionCounter);
		LOGGER.warn("Number of relevant elements: " + allCounter);

		if (triggerElements.isEmpty())
			LOGGER.warn("No trigger element found");
		if (reactionElements.isEmpty())
			LOGGER.warn("No reaction element found");
		if (allElements.isEmpty())
			LOGGER.warn("Element found");

		sensorReadingRoutine = new SensorReadingRoutine(triggerElements, reactionElements, allElements);

		LOGGER.debug("Devices initialized");
	}

	private void startSensorReadingRoutine() {
		LOGGER.debug("Start sensor reading routine");
		Thread temperaturetThread = new Thread(sensorReadingRoutine);
		temperaturetThread.start();
	}

}
