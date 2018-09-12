package br.edu.uni7.ia.ttt.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class TTTApplication {

	public TTTApplication() {
		SwingUtilities.invokeLater(() -> {
			Mainframe mainframe = new Mainframe();
			mainframe.setVisible(true);
		});
	}

	public static void main(String[] args) {
		new TTTApplication();
	}

}
