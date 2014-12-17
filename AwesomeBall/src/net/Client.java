package net;

import gui.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Client is a Runnable that will try to bind a socket to a given port until it
 * is actually bound every 200 microsecond.
 * 
 * When it binds, it continuously reads data from the socket.
 * 
 * If the socket gets disconnected, a dialog with an exit button will show and
 * exit the program.
 * 
 * @author youri
 *
 */
public class Client implements Runnable {
	private Socket socket;
	private InetAddress address;
	private int port;
	private String message;

	public Client(InetAddress addr, int inport) {
		socket = null;
		port = inport;
		address = addr;
	}

	public String getMessage() {
		return this.message;
	}

	public InetAddress getAddr() {
		return socket.getLocalAddress();
	}
	
	public void run() {
		try {
			Thread.sleep(2);
		} catch (Exception e) {
		}

		while (true) {
			try {
				socket = new Socket(address, port);
				if (socket != null && socket.isBound()) {
					System.out.println("connected");
					break;
				} else {
					System.out.println("no server detected");
					try {
						Thread.sleep(200);
					} catch (Exception e) {
					}
				}
			}

			catch (ConnectException ce) {
				// System.out.println("Trying again");
				try {
					Thread.sleep(20);
				} catch (Exception e) {
				}
				continue;
			}

			catch (SocketTimeoutException ex) {
				System.out.println("Trying to connect to " + address + "...");
			}

			catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		while (!socket.isClosed()) {

			if (socket != null && socket.isConnected()) {
				try {
					BufferedReader entree = new BufferedReader(
							new InputStreamReader(socket.getInputStream()));
					String mes = entree.readLine();
					if (mes != null) {
						this.message = mes;
					}
				} catch (SocketException se) {
					Dialog d = new Dialog("Player 2 Left");
					ActionListener exit = new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							System.exit(0);
						}
					};
					Button ex = new Button("exit", exit);
					ex.setPreferredSize(new Dimension(d.getWidth(), 50));
					d.add(ex, BorderLayout.SOUTH);
					d.setVisible(true);

				} catch (IOException ioex) {
					continue;
				}
			}
		}
	}
}