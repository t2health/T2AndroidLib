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
package org.t2health.lib.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * A class to manage the serial connection to a bluetooth device.
 * @author robbiev
 *
 */
public abstract class SerialBTDevice {
	private static final int MSG_CONNECTED = 1;
	private static final int MSG_CONNECTION_LOST = 2;
	private static final int MSG_MANAGE_SOCKET = 3;
	private static final int MSG_BYTES_RECEIVED = 4;
	
	public static final UUID UUID_RFCOMM_GENERIC = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final BluetoothAdapter mAdapter;
	private final BluetoothDevice mDevice;
	private BluetoothSocket mSsocket;
	private ConnectedThread mConnectedThread;
	private DeviceConnectionListener mConnectionListener;
	
	private boolean mReconnectOnConnectionLost = true;
	
	private ArrayList<Byte[]> mWriteQueue = new ArrayList<Byte[]>();
	
	private Handler mThreadHandler;

	private ConnectThread mConnectThread;
	
	public SerialBTDevice() {
		this.mAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(!BluetoothAdapter.checkBluetoothAddress(this.getDeviceAddress())) {
			this.mDevice = null;
			throw new InvalidBluetoothAddressException("\""+ this.getDeviceAddress() +"\" is an invalid bluetooth device address.");
		}
		
		this.mDevice = this.mAdapter.getRemoteDevice(this.getDeviceAddress());
		
		mThreadHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what) {
					case MSG_CONNECTED:
						deviceConnected();
						break;
					case MSG_CONNECTION_LOST:
						deviceConnectionLost();
						break;
					case MSG_MANAGE_SOCKET:
						manageConnection(mSsocket);
						break;
					case MSG_BYTES_RECEIVED:
						onBytesReceived(msg.getData().getByteArray("bytes"));
						break;
				}
			}
		};
	}
	
	/**
	 * Specifies if the device is currently connecting.
	 * @return
	 */
	public boolean isConnecting() {
		if(!this.isConencted()) {
			if(this.mConnectThread != null && 
					this.mConnectThread.isRunning()) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns true when the device is connected.
	 * @return
	 */
	public boolean isConencted() {
		if(this.mConnectedThread == null) {
			return false;
		}
		return this.mConnectedThread.isRunning();
	}
	
	/**
	 * Get the Android BluetoothDevice object associated with this device.
	 * @return
	 */
	public BluetoothDevice getDevice() {
		return this.mDevice;
	}
	
	/**
	 * Get the address of the device.
	 * @return
	 */
	public String getAddress() {
		return this.mDevice.getAddress();
	}
	
	/**
	 * Get the name of the device.
	 * @return
	 */
	public String getName() {
		return this.mDevice.getName();
	}
	
	/**
	 * Returns true when the device has been initialized.
	 * @return
	 */
	public boolean isInitialized() {
		return this.mDevice != null;
	}
	
	/**
	 * Set the connection lister. The provided object will be notified regarding
	 * device connection events.
	 * @param t
	 */
	public void setDeviceConnectionListener(DeviceConnectionListener t) {
		this.mConnectionListener = t;
	}
	
	/**
	 * Returns true if the device has been bonded.
	 * @return
	 */
	public boolean isBonded() {
		if(!isInitialized()) {
			return false;
		}
		return this.mDevice.getBondState() == BluetoothDevice.BOND_BONDED;
	}
	
	/**
	 * Instructs the system to connect to the device.
	 */
	public void connect() {
		if(!isInitialized()) {
			return;
		}
		
		if(this.isConencted() || this.isConnecting()) {
			return;
		}
		
		this.close(false);
		
		// See if this device is paired.
		if(!this.isBonded()) {
			throw new DeviceNotBondedException("The bluetooth device "+ this.mDevice.getAddress() +" is not bonded (paired).");
		}
		
		// Notify that we have begun the connecting process.
		this.deviceConnecting();
		
//		Log.v(TAG, "BT connect");
		// Begin connecting.
		this.mConnectThread = new ConnectThread(this.mDevice);
		this.mConnectThread.start();
	}
	
	/**
	 * Instructs the system to close the connection to the device.
	 */
	public void close() {
		if(!isInitialized()) {
			return;
		}
		
		this.close(true);
	}
	
	private void close(boolean report) {
		if(!isInitialized()) {
			return;
		}
		
//		Log.v(TAG, "BT close");
		if(report) {
			this.beforeDeviceClosed();
		}
		
		if(this.mConnectedThread != null) {
			this.mConnectedThread.cancel();
			this.mConnectedThread.interrupt();
			this.mConnectedThread = null;
		}
		
		if(this.mConnectThread != null) {
			this.mConnectThread.cancel();
			this.mConnectThread.interrupt();
			this.mConnectThread = null;
		}
		
		if(report) {
			this.deviceClosed();
		}
	}
	
	private void deviceConnecting() {
//		Log.v(TAG, "Device connecting.");
		if(mConnectionListener != null) {
			mConnectionListener.onDeviceConnecting(this);
		}
	}
	
	private void deviceConnected() {
//		Log.v(TAG, "Device connected.");
		if(mConnectionListener != null) {
			mConnectionListener.onDeviceConnected(this);
		}
		this.onDeviceConnected();
	}
	
	private void beforeDeviceClosed() {
		if(this.isConencted()) {
//			Log.v(TAG, "Run before device disconnected.");
			if(mConnectionListener != null) {
				mConnectionListener.onBeforeDeviceClosed(this);
			}
			this.onBeforeConnectionClosed();
		}
	}
	
	private void deviceClosed() {
//		Log.v(TAG, "Device disconnected.");
		if(mConnectionListener != null) {
			mConnectionListener.onDeviceClosed(this);
		}
	}
	
	private void deviceConnectionLost() {
//		Log.v(TAG, "Lost the connection to the device.");
		if(mConnectionListener != null) {
			mConnectionListener.onDeviceConnectionLost(this);
		}
		
		// Try to reconnect.
		if(this.isBonded() && mReconnectOnConnectionLost) {
//			Log.v(TAG, "Trying to reconnect to the device.");
			this.connect();
		}
	}
	
	/**
	 * Returns the address of the device to conenct to.
	 * @return
	 */
	public abstract String getDeviceAddress();
	
	/**
	 * What should occur when data is received from the device.
	 * @param bytes
	 */
	protected abstract void onBytesReceived(byte[] bytes);
	
	/**
	 * Notification that the device has been connected.
	 */
	protected abstract void onDeviceConnected();
	
	/**
	 * Notification before the connection to the device is closed.
	 */
	protected abstract void onBeforeConnectionClosed();
	
	/**
	 * Notification when the device has been closed.
	 */
	protected abstract void onConnectedClosed();
	
	private void manageConnection(BluetoothSocket socket) {
		this.mConnectedThread = new ConnectedThread(socket);
		this.mConnectedThread.start();
	}
	
	private void pushQueuedBytes() {
		// if there are queued Byte[], write them.
		while(mWriteQueue.size() > 0) {
			Byte[] qBytes = mWriteQueue.get(0);
			byte[] bytes = new byte[qBytes.length];
			
			for(int i = 0; i < qBytes.length; i++) {
				bytes[i] = qBytes[i];
			}
			
			mConnectedThread.write(bytes);
			mWriteQueue.remove(0);
		}
	}
	
	protected void write(byte[] bytes) {
		// Immediately write the bytes.
		if(this.mConnectedThread != null && this.mConnectedThread.isRunning()) {
			this.mConnectedThread.write(bytes);
			return;
		}
		
		// Queue the bytes and reconnect.
		Byte[] qBytes = new Byte[bytes.length];
		for(int i = 0; i < bytes.length; i++) {
			qBytes[i] = bytes[i];
		}
		this.mWriteQueue.add(qBytes);
	}
	
	/**
	 * Converts as byte[] to a BitSet.
	 * @param bytes
	 * @return
	 */
	public static BitSet byteArrayToBitSet(byte[] bytes) {
		BitSet bits = new BitSet(bytes.length * 8);
		for (int i = 0; i < bytes.length * 8; i++) {
			if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
				bits.set(i);
			}
		}
	    return bits;
	}

	/**
	 * Converts a BitSet to a Byte[]
	 * @param bs
	 * @return
	 */
	public static byte[] bitSetToByteArray(BitSet bs) {
		byte[] bytes = new byte[(int) Math.ceil(bs.size() / 8)];
		for(int i = 0; i < bs.size(); i++) {
			if(bs.get(i) == true) {
				bytes[i / 8] |= 1 << i;
			}
		}
		return bytes;
	}
	
	/**
	 * Converts a byte[] to an int
	 * @param bytes
	 * @return
	 */
	public static int byteArrayToInt(byte[] bytes) {
		int val = 0;
		
		for(int i = 0; i < bytes.length; i++) {
			int n = (bytes[i] < 0 ? (int)bytes[i] + 256 : (int)bytes[i]) << (8 * i);
			val += n;
		}
		
		return val;
	}
	
	/**
	 * Converts an int to a byte[]
	 * @param value
	 * @param byteArrayLength
	 * @return
	 */
	public static byte[] intToByteArray(int value, int byteArrayLength) {
		byte[] b = new byte[byteArrayLength];
		for (int i = 0; i < b.length; i++) {
			int offset = (b.length - 1 - i) * 8;
			b[i] = (byte) ((value >>> offset) & 0xFF);
		}
		return b;
    }
	
	private class ConnectThread extends Thread {
		private final BluetoothDevice device;
		private boolean isRunning = false;
		private boolean cancelled = false;
		
		
		public ConnectThread(BluetoothDevice device) {
			this.device = device;
		}

		@Override
		public void run() {
//			adapter.cancelDiscovery();
			isRunning = true;
			cancelled = false;
			
			int loopCount = 0;
			BluetoothSocket tmpSocket = null;
			while(true) {
				tmpSocket = null;
				
				// Break out if this was cancelled.
				if(cancelled) {
					break;
				}
				
				// Get the device socket.
				try {
					tmpSocket = device.createRfcommSocketToServiceRecord(UUID_RFCOMM_GENERIC);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// Try to connect to the socket.
				try {
					tmpSocket.connect();
					//threadHandler.sendEmptyMessage(MSG_CONNECTED);
					break;
				} catch(IOException connectException) {
					try {
						tmpSocket.close();
					} catch(IOException closeException) {
					}
				}
				
				// Wait to try to connect to the device again.
				int sleepSeconds = (int) (((loopCount / 5) + 1) * 1.5);
				sleepSeconds = (sleepSeconds > 10)? 10: sleepSeconds;
//				Log.v(TAG, "\tFailed to connect, will try again in "+ sleepSeconds +" sec.");
				try {
					Thread.sleep(sleepSeconds * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				loopCount++;
			}
			
			isRunning = false;
			
			// Tell the main thread to manage the socket.
			if(tmpSocket != null) {
				mSsocket = tmpSocket;
				mThreadHandler.sendEmptyMessage(MSG_MANAGE_SOCKET);
			}
		}
		
		public void cancel() {
			this.cancelled  = true;
		}
		
		public boolean isRunning() {
			return this.isRunning;
		}
	}
	
	private class ConnectedThread extends Thread {
		private final BluetoothSocket socket;
		private final InputStream inputStream;
		private final OutputStream outputStream;
		private boolean isRunning = false;
		private boolean isConnected = false;
		private boolean cancelled = false;
		
		public ConnectedThread(BluetoothSocket s) {
    		this.socket = s;
    		
    		InputStream tmpIs = null;
    		OutputStream tmpOs = null;
    		try {
    			tmpIs = socket.getInputStream();
    			tmpOs = socket.getOutputStream();
    		} catch(IOException e) {
    			
    		}
    		
    		inputStream = tmpIs;
    		outputStream = tmpOs;
    	}
		
		@Override
		public void run() {
			isRunning = true;
			byte[] buffer = new byte[1024];
			int bytes;
			
//			Log.v(TAG, "Receiving data from device.");
			while(true) {
				// Break out if this was cancelled.
				if(cancelled) {
					break;
				}
				
				try {
					bytes = this.inputStream.read(buffer);
					if(!this.isConnected) {
						mThreadHandler.sendEmptyMessage(MSG_CONNECTED);
						pushQueuedBytes();
					}
					this.isConnected = true;
					
					if(bytes > 0) {
						byte[] newBytes = new byte[bytes];
						for(int i = 0; i < bytes; i++) {
							newBytes[i] = buffer[i];
						}
						
						// Call the bytes recieved handler.
						// This call is ran in the main thread.
						Bundle data = new Bundle();
						data.putByteArray("bytes", newBytes);
						
						Message msg = new Message();
						msg.what = MSG_BYTES_RECEIVED;
						msg.setData(data);
						
						mThreadHandler.sendMessage(msg);
					}
					
				// Lost the connection for some reason.
				} catch (IOException e1) {
					if(!this.cancelled) {
						mThreadHandler.sendEmptyMessage(MSG_CONNECTION_LOST);
					}
					break;
				}
			}
			
//			Log.v(TAG, "Stopped receiving data from device.");
			isRunning = false;
		}
		
		public void write(byte[] bytes) {
			try {
				/*Log.v(TAG, "Writing...");
				
				for(int i = 0; i < bytes.length; i++) {
					Log.v(TAG, "\t"+bytes[i]);
				}*/
				
				this.outputStream.write(bytes);
			} catch (IOException e) {
//				Log.v(TAG, "Failed to write to device.");
			}
		}
    	
		public void cancel() {
			this.cancelled = true;
			
			try {
				this.socket.close();
			} catch (IOException e) {}
		}
		
		public boolean isRunning() {
			return this.isRunning;
		}
    }
	
	
	public class DeviceNotBondedException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5585726446729463776L;

		public DeviceNotBondedException(String msg) {
			super(msg);
		}
	}
	
	public class InvalidBluetoothAddressException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = -30604768627158724L;

		public InvalidBluetoothAddressException(String msg) {
			super(msg);
		}
	}
	
	/**
	 * Defines an extension for classes that make them respond to 
	 * status changes on the device. 
	 * @author robbiev
	 *
	 */
	public interface DeviceConnectionListener {
		public void onDeviceConnecting(SerialBTDevice d);
		public void onDeviceConnected(SerialBTDevice d);
		public void onDeviceClosed(SerialBTDevice d);
		public void onBeforeDeviceClosed(SerialBTDevice serialBTDevice);
		public void onDeviceConnectionLost(SerialBTDevice d);
	}
}
