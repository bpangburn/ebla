/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003, Brian E. Pangburn & Prasanth R. Pasala
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  The names of its contributors may not be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



package com.greatmindsworking.EBLA.Interfaces;



import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;
import java.beans.PropertyVetoException;
import com.nqadmin.Utils.DBConnector;
import com.greatmindsworking.EBLA.EBLA;
import com.greatmindsworking.EBLA.SessionData;
import com.greatmindsworking.EBLA.ImageComponent;
import java.awt.image.*;
import java.awt.geom.AffineTransform;



/**
 * StatusScreen.java
 *<p>
 * EBLA - Experience-Based Language Acquisition
 *<p>
 * Screen to display calculation status and intermediate results
 * for the EBLA graphical user interface (GUI).
 *<p>
 * @author	$Author$
 * @version	$Revision$
 */
public class StatusScreen extends JInternalFrame {

	ImageComponent ic1 = null;
	ImageComponent ic2 = null;
	ImageComponent ic3 = null;

	DBConnector dbc = null;
	SessionData sd = null;
	EBLA ebla = null;

	boolean eblaCanceled = false;

	Container desktop = null;
    JLabel statusField = new JLabel("Initializing EBLA...", JLabel.CENTER);
    JProgressBar progressBar = new JProgressBar();
    JButton interruptButton;
    Border spaceBelow = BorderFactory.createEmptyBorder(0, 0, 5, 0);



	public StatusScreen(Container _desktop, SessionData _sd, DBConnector _dbc){
		super("EBLA Calculation Status", false,true,true,true);
		setSize(550,400);

		desktop = _desktop;
		sd = _sd;
		dbc = _dbc;

        //setBorder(BorderFactory.createTitledBorder(
        //              BorderFactory.createLineBorder(Color.black),
        //              "EBLA Calculation Status"));

        //progressBar.setMaximum(NUMLOOPS);

        interruptButton = new JButton("Cancel");
        interruptButton.addActionListener(interruptListener);
        interruptButton.setEnabled(true);

        JComponent buttonBox = new JPanel();
        buttonBox.add(interruptButton);

        JComponent imageBox = new JPanel();
        imageBox.setLayout(new GridBagLayout());


		GridBagConstraints constraints = new GridBagConstraints();


        //BufferedImage tmpImage = new BufferedImage(160, 140, BufferedImage.TYPE_INT_RGB);
        BufferedImage tmpImage = new BufferedImage(160, 140, BufferedImage.TYPE_INT_RGB);

		constraints.gridx = 0;
		constraints.gridy = 0;
		ic1 = new ImageComponent(tmpImage);
		imageBox.add(ic1, constraints);

		constraints.gridx = 1;
		ic2 = new ImageComponent(tmpImage);
		imageBox.add(ic2, constraints);

		constraints.gridx = 2;
		ic3 = new ImageComponent(tmpImage);
		imageBox.add(ic3, constraints);


		Container contentPane = getContentPane();



		contentPane.setLayout(new GridBagLayout());
		constraints.gridx = 0;
		constraints.gridy = 3;
		contentPane.add(imageBox,constraints);
		constraints.gridy = 2;
		contentPane.add(buttonBox,constraints);
		constraints.gridy = 1;
		contentPane.add(progressBar,constraints);
		constraints.gridy = 0;
		contentPane.add(statusField,constraints);

        buttonBox.setBorder(spaceBelow);
        Border pbBorder = progressBar.getBorder();
        progressBar.setBorder(BorderFactory.createCompoundBorder(
                                        spaceBelow,
                                        pbBorder));



    }


	// from http://forum.java.sun.com/thread.jsp?forum=20&thread=260711&message=982907
	public static BufferedImage scaleToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc) {
		int nHeight = imgSrc.getHeight();
		int nWidth = imgSrc.getWidth();
		double scaleX = (double)nMaxWidth / (double)nWidth;
		double scaleY = (double)nMaxHeight / (double)nHeight;
		double fScale = Math.min(scaleX, scaleY);
		return scale(fScale, imgSrc);
	}

	// from http://forum.java.sun.com/thread.jsp?forum=20&thread=260711&message=982907
	public static BufferedImage scale(double scale, BufferedImage srcImg) {
		if (scale == 1 ) {
			return srcImg;
		}
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), null);
		return op.filter(srcImg, null);
	}


    /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     */
    public void setBarMax(final int i) {
        Runnable doSetBarMax = new Runnable() {
            public void run() {
                progressBar.setMaximum(i);
            }
        };
        SwingUtilities.invokeLater(doSetBarMax);
    }


    /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     */
    public void updateStatus(final int i) {
        Runnable doSetProgressBarValue = new Runnable() {
            public void run() {
                progressBar.setValue(i);
            }
        };
        SwingUtilities.invokeLater(doSetProgressBarValue);
    }

    /**
     * When the worker needs to update the GUI we do so by queuing
     * a Runnable for the event dispatching thread with
     * SwingUtilities.invokeLater().  In this case we're just
     * changing the progress bars value.
     */
    public void updateStatus(final String s) {
        Runnable doSetStatusField = new Runnable() {
            public void run() {
                statusField.setText(s);
            }
        };
        SwingUtilities.invokeLater(doSetStatusField);
    }

    /**
     * Add a component to the status screen to display intermediate images from EBLA
     *
     */
    public void updateImage(final BufferedImage bi, final int xCoord) {
        Runnable doUpdateImage = new Runnable() {
            public void run() {
				if (xCoord==1) {
					ic1.updateImage(scaleToSize(160, 140, bi));
					ic1.repaint();
				} else if (xCoord==2) {
					ic2.updateImage(scaleToSize(160, 140, bi));
					ic2.repaint();
				} else {
					ic3.updateImage(scaleToSize(160, 140, bi));
					ic3.repaint();
				}

			}
        };
        SwingUtilities.invokeLater(doUpdateImage);
    }


    /**
     * This action listener, called by the "Cancel" button, interrupts
     * the worker thread which is running this.doWork().  Note that
     * the doWork() method handles InterruptedExceptions cleanly.
     */
    ActionListener interruptListener = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
            if (eblaCanceled) {
			// close window
				dispose();

			} else {
			// interrupt EBLA
				ebla.interrupt();

			// change button text
				interruptButton.setText("Close");

			// reset cancled flag
				eblaCanceled = true;
			}

        }
    };



	/**
	 *	adds the census screen to the specified container at the specified position.
	 *@param container the container in which the screen has to showup.
	 *@param positionX the x co-ordinate of the position where the screen has to showup.
	 *@param positionY the y co-ordinate of the position where the screen has to showup.
	 */
	public void showUp(Container container,double positionX, double positionY){

		int optionChoosen = -1;
		// SET THE POSITION OF THE SCREEN.
		this.setLocation((int)positionX, (int)positionY);

		// IF THE USER WANTS TO ADD A RECORD OR IF THERE ARE RECORDS IN DB SHOW THE SCREEN

			Component[] components = container.getComponents();
			int i=0;
			for(i=0; i< components.length;i++){
				if(components[i] instanceof StatusScreen ) {
					System.out.println("Already on desktop");
					break;
				}
			}
			// IF IT IS NOT THERE ADD THE SCREEN TO THE CONTAINER
			if(i == components.length) {
				container.add(this);
			}
			this.setVisible(true);
			// MOVE THE SCREEN TO THE FRONT
			this.moveToFront();

			// REQUEST FOCUS FOR THE SCREEN
			this.requestFocus();
			// MAKE THE SCREEN SELECTED SCREEN
			try{
				this.setClosed(false);
				this.setSelected(true);
			}catch(PropertyVetoException pve){
				pve.printStackTrace();
			}



			try {
				ebla = new EBLA(sd, dbc, this);
				ebla.start();
	//			ebla.finalize();
			} catch(Exception e) {
				e.printStackTrace();
			}

	}

	/**
	 * shows the census screen at the default location on the specified container.
	 *@param container the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	}

} // end of StatusScreen class



/*
 * $Log$
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */