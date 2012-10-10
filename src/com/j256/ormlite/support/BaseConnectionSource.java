/*
 * 
 * T2AndroidLib
 * 
 * Copyright © 2009-2012 United States Government as represented by 
 * the Chief Information Officer of the National Center for Telehealth 
 * and Technology. All Rights Reserved.
 * 
 * Copyright © 2009-2012 Contributors. All Rights Reserved. 
 * 
 * THIS OPEN SOURCE AGREEMENT ("AGREEMENT") DEFINES THE RIGHTS OF USE, 
 * REPRODUCTION, DISTRIBUTION, MODIFICATION AND REDISTRIBUTION OF CERTAIN 
 * COMPUTER SOFTWARE ORIGINALLY RELEASED BY THE UNITED STATES GOVERNMENT 
 * AS REPRESENTED BY THE GOVERNMENT AGENCY LISTED BELOW ("GOVERNMENT AGENCY"). 
 * THE UNITED STATES GOVERNMENT, AS REPRESENTED BY GOVERNMENT AGENCY, IS AN 
 * INTENDED THIRD-PARTY BENEFICIARY OF ALL SUBSEQUENT DISTRIBUTIONS OR 
 * REDISTRIBUTIONS OF THE SUBJECT SOFTWARE. ANYONE WHO USES, REPRODUCES, 
 * DISTRIBUTES, MODIFIES OR REDISTRIBUTES THE SUBJECT SOFTWARE, AS DEFINED 
 * HEREIN, OR ANY PART THEREOF, IS, BY THAT ACTION, ACCEPTING IN FULL THE 
 * RESPONSIBILITIES AND OBLIGATIONS CONTAINED IN THIS AGREEMENT.
 * 
 * Government Agency: The National Center for Telehealth and Technology
 * Government Agency Original Software Designation: T2AndroidLib001
 * Government Agency Original Software Title: T2AndroidLib
 * User Registration Requested. Please send email 
 * with your contact information to: robert.kayl2@us.army.mil
 * Government Agency Point of Contact for Original Software: robert.kayl2@us.army.mil
 * 
 */
package com.j256.ormlite.support;

import java.sql.SQLException;

import com.j256.ormlite.logger.Logger;

/**
 * Connection source base class which provides the save/clear mechanism using a thread local.
 * 
 * @author graywatson
 */
public abstract class BaseConnectionSource implements ConnectionSource {

	protected boolean usedSpecialConnection = false;
	private ThreadLocal<NestedConnection> specialConnection = new ThreadLocal<NestedConnection>();

	public DatabaseConnection getSpecialConnection() {
		return getSpecial();
	}

	/**
	 * Returns the connection that has been saved or null if none.
	 */
	protected DatabaseConnection getSavedConnection() throws SQLException {
		if (!usedSpecialConnection) {
			return null;
		}
		NestedConnection nested = specialConnection.get();
		if (nested == null) {
			return null;
		} else {
			return nested.connection;
		}
	}

	/**
	 * Return true if the connection being released is the one that has been saved.
	 */
	protected boolean isSavedConnection(DatabaseConnection connection) throws SQLException {
		if (!usedSpecialConnection) {
			return false;
		}
		NestedConnection currentSaved = specialConnection.get();
		if (currentSaved == null) {
			return false;
		} else if (currentSaved.connection == connection) {
			// ignore the release when we have a saved connection
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Save this connection as our special connection to be returned by the {@link #getSavedConnection()} method.
	 * 
	 * @return True if the connection was saved or false if it was already saved.
	 */
	protected boolean saveSpecial(DatabaseConnection connection) throws SQLException {
		// check for a connection already saved
		NestedConnection currentSaved = specialConnection.get();
		if (currentSaved == null) {
			/*
			 * This is fine to not be synchronized since it is only this thread we care about. Other threads will set
			 * this or have it synchronized in over time.
			 */
			usedSpecialConnection = true;
			specialConnection.set(new NestedConnection(connection));
			return true;
		} else {
			if (currentSaved.connection != connection) {
				throw new SQLException("trying to save connection " + connection
						+ " but already have saved connection " + currentSaved.connection);
			}
			// we must have a save call within another save
			currentSaved.increment();
			return false;
		}
	}

	/**
	 * Clear the connection that was previously saved.
	 * 
	 * @return True if the connection argument had been saved.
	 */
	protected boolean clearSpecial(DatabaseConnection connection, Logger logger) {
		NestedConnection currentSaved = specialConnection.get();
		boolean cleared = false;
		if (currentSaved == null) {
			logger.error("no connection has been saved when clear() called");
		} else if (currentSaved.connection == connection) {
			if (currentSaved.decrementAndGet() == 0) {
				// we only clear the connection if nested counter is 0
				specialConnection.set(null);
			}
			cleared = true;
		} else {
			logger.error("connection saved {} is not the one being cleared {}", currentSaved.connection, connection);
		}
		// release should then be called after clear
		return cleared;
	}

	/**
	 * Get the currently saved.
	 */
	protected DatabaseConnection getSpecial() {
		NestedConnection currentSaved = specialConnection.get();
		if (currentSaved == null) {
			return null;
		} else {
			return currentSaved.connection;
		}
	}

	private class NestedConnection {
		public final DatabaseConnection connection;
		private int nestedC;

		public NestedConnection(DatabaseConnection connection) {
			this.connection = connection;
			this.nestedC = 1;
		}

		public void increment() {
			nestedC++;
		}

		public int decrementAndGet() {
			nestedC--;
			return nestedC;
		}
	}
}
