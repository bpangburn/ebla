/* $Id$
 *
 * Tab Spacing = 4
 *
 * Copyright (c) 2003-2004, Brian E. Pangburn & Prasanth R. Pasala
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
import javax.swing.event.*;
import javax.swing.border.*;
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

	// INITIALIZE CONTAINER (APPLICATION WINDOW) FOR STATUS SCREEN
		Container desktop = null;

	// INITIALIZE DATABASE CONNECTIVITY COMPONENT
		DBConnector dbc = null;

	// INITIALIZE EBLA CALCULATION CLASSES
		SessionData sd = null;
		EBLA ebla = null;

	// INITIALIZE FLAG TO INDICATE IF USER HAS CANCELED EBLA CALCULATIONS
		boolean eblaCanceled = false;

	// TEMP IMAGE TO BE USED AS PLACE HOLDER
		BufferedImage tmpImage = null;

	// DEFAULT IMAGE DIMENSIONS
		int imageX = 192;
		int imageY = 140;

	// INITIALIZE MISC WIDGETS USED FOR DISPLAYING PROGRESS/INTERMEDIATE RESULTS
		// INITIALIZE IMAGE COMPONENTS TO DISPLAY INTERMEDIATE RESULTS
			EBLAPanel imagePanel  = new EBLAPanel();
			ImageComponent ic1 = null;
			ImageComponent ic2 = null;
			ImageComponent ic3 = null;

		// LABELS, PROGRESS BARS, BUTTONS, ETC.
			JLabel statusText1 = new JLabel("Initializing EBLA...");
			JLabel statusText2 = new JLabel("");
			JLabel statusText3 = new JLabel("");

			JLabel imageLabel1 = new JLabel("Original Image");
			JLabel imageLabel2 = new JLabel("Segmented Image");
			JLabel imageLabel3 = new JLabel("Polygon Image");


			JProgressBar statusBar1 = new JProgressBar();
			JProgressBar statusBar2 = new JProgressBar();
			JProgressBar statusBar3 = new JProgressBar();


			JButton cancelButton = new JButton("Cancel");


	/**
	 * StatusScreen constructor.
	 *
	 * @param the container in which the screen has to showup.
	 * @param the SessionData object need to initialize the EBLA calc engine.
	 * @param the database connection to the ebla_data database
	 */
	public StatusScreen(Container _desktop, SessionData _sd, DBConnector _dbc) {
		// CALL JINTERNALFRAME CONSTRUCTOR TO INITIALIZE EXPERIENCE SCREEN
			super("EBLA - Calculation Status Screen",false,false,false,false);

		// SET SIZE
			setSize(640,480);

		// SET APPLICATION WINDOW THAT WILL SERVE AS PARENT
			desktop = _desktop;

		// SET SESSION OBJECT
			sd = _sd;

		// SET DATABASE CONNECTION
			dbc = _dbc;


        // SET ACTION LISTENER FOR CANCEL BUTTON
        	cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (eblaCanceled) {
					// CLOSE WINDOW
						try {
							setClosed(true);
						} catch(PropertyVetoException pve) {
							pve.printStackTrace();
						}

					} else {
					// RESET CANCELED FLAG
						eblaCanceled = true;

					// CHANGE BUTTON TEXT
						cancelButton.setText("Close");

					}

				} // end actionPerformed()
			});
        	//cancelButton.setEnabled(true);


		// ADD INTERNAL FRAME LISTENER TO FORM TO CHECK FOR LOSS OF FOCUS
			addInternalFrameListener(new InternalFrameAdapter() {
			// FRAME DEACTIVATED
				public void internalFrameDeactivated(InternalFrameEvent ife) {
					SwingUtilities.invokeLater(new Thread() {
						public void run() {
							StatusScreen.this.moveToFront();
							StatusScreen.this.requestFocus();
							try {
								StatusScreen.this.setSelected(true);
							} catch(PropertyVetoException pve) {
								System.out.println(pve.getMessage());
							}

						}
					});
				} // end internalFrameDeactivated()

			});


		// INITIALIZE VARIABLES NEEDED FOR LAYOUT
			GridBagConstraints constraints = new GridBagConstraints();
			Border emptySpace = BorderFactory.createEmptyBorder(0, 0, 10, 0);


		// CREATE/LAYOUT PANEL FOR INTERMEDIATE RESULT IMAGES
			imagePanel.setLayout(new GridBagLayout());

			tmpImage = new BufferedImage(imageX, imageY, BufferedImage.TYPE_INT_RGB);

			Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2);

			constraints.gridx = 0;
			constraints.gridy = 0;
			EBLAPanel tmpPanel1 = new EBLAPanel();
			ic1 = new ImageComponent(tmpImage);
			tmpPanel1.add(ic1);
			tmpPanel1.setBorder(blackBorder);
			imagePanel.add(tmpPanel1, constraints);

			constraints.gridy = 1;
			imagePanel.add(imageLabel1, constraints, false);

			constraints.gridx = 1;
			constraints.gridy = 0;
			EBLAPanel tmpPanel2 = new EBLAPanel();
			ic2 = new ImageComponent(tmpImage);
			tmpPanel2.add(ic2);
			tmpPanel2.setBorder(blackBorder);
			imagePanel.add(tmpPanel2, constraints);

			constraints.gridy = 1;
			imagePanel.add(imageLabel2, constraints, false);

			constraints.gridx = 2;
			constraints.gridy = 0;
			EBLAPanel tmpPanel3 = new EBLAPanel();
			ic3 = new ImageComponent(tmpImage);
			tmpPanel3.add(ic3);
			tmpPanel3.setBorder(blackBorder);
			imagePanel.add(tmpPanel3, constraints);

			constraints.gridy = 1;
			imagePanel.add(imageLabel3, constraints, false);

		// ADD IMAGE PANEL, CANCEL BUTTON, PROGRESS BAR, & STATUS LABEL TO STATUS SCREEN
			// SET CONTAINER AND LAYOUT
				Container contentPane = getContentPane();
				contentPane.setLayout(new GridBagLayout());

			// ROW 1
				constraints.anchor = GridBagConstraints.WEST;
				constraints.gridx = 0;
				constraints.gridy = 0;
				contentPane.add(statusText1, constraints);

				constraints.anchor = GridBagConstraints.EAST;
				constraints.gridx = 1;
				contentPane.add(statusBar1, constraints);

			// ROW 2 (BLANK)
				constraints.anchor = GridBagConstraints.WEST;
				constraints.gridx = 0;
				constraints.gridy = 1;
				contentPane.add(new JLabel("   "), constraints);

			// ROW 3
				constraints.anchor = GridBagConstraints.WEST;
				constraints.gridx = 0;
				constraints.gridy = 2;
				contentPane.add(statusText2, constraints);

				constraints.anchor = GridBagConstraints.EAST;
				constraints.gridx = 1;
				contentPane.add(statusBar2, constraints);

			// ROW 4
				constraints.anchor = GridBagConstraints.WEST;
				constraints.gridx = 0;
				constraints.gridy = 3;
				contentPane.add(statusText3, constraints);

				constraints.anchor = GridBagConstraints.EAST;
				constraints.gridx = 1;
				contentPane.add(statusBar3, constraints);

			// IMAGE PANEL
				constraints.anchor = GridBagConstraints.CENTER;
				constraints.gridx = 0;
				constraints.gridy = 4;
				constraints.gridwidth = 2;
				contentPane.add(imagePanel, constraints);

			// BUTTON PANEL
				EBLAPanel buttonPanel = new EBLAPanel();
				buttonPanel.setBorder(emptySpace);
				buttonPanel.add(cancelButton);

				constraints.gridy = 5;
				contentPane.add(buttonPanel, constraints);

	} // end of StatusScreen constructor



	/**
	 * Returns flag indicating if the EBLA calc engine should be stopped.
	 */
	public boolean getEBLACanceled() {
		return eblaCanceled;
	}



	/**
	 * Changes "Cancel" button to "Close" button upon completion of EBLA calcs
	 * and updates calculation status flag.
	 */
	public void indicateEBLACompletion() {
		// RESET CANCELED FLAG
			eblaCanceled = true;

		// CHANGE BUTTON TEXT
			cancelButton.setText("Close");

	} // end indicateEBLACompletion()



	/**
	 * from http://forum.java.sun.com/thread.jsp?forum=20&thread=260711&message=982907
	 */
	public static BufferedImage scaleToSize(int nMaxWidth, int nMaxHeight, BufferedImage imgSrc) {
		int nHeight = imgSrc.getHeight();
		int nWidth = imgSrc.getWidth();
		double scaleX = (double)nMaxWidth / (double)nWidth;
		double scaleY = (double)nMaxHeight / (double)nHeight;
		double fScale = Math.min(scaleX, scaleY);
		return scale(fScale, imgSrc);
	} // end scaleToSize()



	/**
	 * from http://forum.java.sun.com/thread.jsp?forum=20&thread=260711&message=982907
	 */
	public static BufferedImage scale(double scale, BufferedImage srcImg) {
		if (scale == 1 ) {
			return srcImg;
		}
		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), null);
		return op.filter(srcImg, null);
	} // end scale()



    /**
     * Sets the max value for the specified progress bar.
     */
    public void setBarMax(final int barIndex, final int max) {
        Runnable doSetBarMax = new Runnable() {
            public void run() {
				if (barIndex==1) {
                	statusBar1.setMaximum(max);
                	statusBar1.setVisible(true);
				} else if (barIndex==2) {
					statusBar2.setMaximum(max);
					statusBar2.setVisible(true);
				} else {
					statusBar3.setMaximum(max);
					statusBar3.setVisible(true);
				}
            }
        };
        SwingUtilities.invokeLater(doSetBarMax);
    } // end setBarMax()


    /**
     * Updates the specified progress bar.
     */
    public void updateBar(final int barIndex, final int i) {
        Runnable doUpdateBar = new Runnable() {
            public void run() {
				if (barIndex==1) {
					statusBar1.setValue(i);
				} else if (barIndex==2) {
					statusBar2.setValue(i);
				} else {
					statusBar3.setValue(i);
				}
            }
        };
        SwingUtilities.invokeLater(doUpdateBar);
    } // end updateBar()



    /**
     * Hides the specified progress bar.
     */
    public void hideBar(final int barIndex) {
        Runnable doHideBar = new Runnable() {
            public void run() {
				if (barIndex==1) {
					statusBar1.setVisible(false);
				} else if (barIndex==2) {
					statusBar2.setVisible(false);
				} else {
					statusBar3.setVisible(false);
				}
            }
        };
        SwingUtilities.invokeLater(doHideBar);
    } // end hideBar()


    /**
     * Hides the entire intermediate image panel.
     */
    public void hideImagePanel() {
        Runnable doHideImagePanel = new Runnable() {
            public void run() {
				imagePanel.setVisible(false);
            }
        };
        SwingUtilities.invokeLater(doHideImagePanel);
    } // end hideImagePanel()




    /**
     * Updates the text for the specified status label.
     */
    public void updateStatus(final int statusIndex, final String s) {
        Runnable doUpdateStatus = new Runnable() {
            public void run() {
				if (statusIndex==1) {
					statusText1.setText(s);
				} else if (statusIndex==2) {
					statusText2.setText(s);
				} else {
					statusText3.setText(s);
				}
            }
        };
        SwingUtilities.invokeLater(doUpdateStatus);
    } // end updateStatus()



    /**
     * Updates intermediate images for the EBLA vision system.
     */
    public void updateImage(final int imageIndex, final BufferedImage bi) {
        Runnable doUpdateImage = new Runnable() {
            public void run() {
				if (imageIndex==1) {
					ic1.updateImage(scaleToSize(imageX, imageY, bi));
					ic1.setVisible(true);
					ic1.repaint();
				} else if (imageIndex==2) {
					ic2.updateImage(scaleToSize(imageX, imageY, bi));
					ic2.setVisible(true);
					ic2.repaint();
				} else {
					ic3.updateImage(scaleToSize(imageX, imageY, bi));
					ic3.setVisible(true);
					ic3.repaint();
				}

			}
        };
        SwingUtilities.invokeLater(doUpdateImage);
    } // end updateImage()



    /**
     * Hides intermediate images for the EBLA vision system.
     */
    public void hideImage(final int imageIndex) {
        Runnable doHideImage = new Runnable() {
            public void run() {
				if (imageIndex==1) {
					ic1.updateImage(tmpImage);
					ic1.repaint();
				} else if (imageIndex==2) {
					ic2.updateImage(tmpImage);
					ic2.repaint();
				} else {
					ic3.updateImage(tmpImage);
					ic3.repaint();
				}

			}
        };
        SwingUtilities.invokeLater(doHideImage);
    } // end hideImage()



	/**
	 * Adds the status screen to the specified container at the specified position.
	 *
	 * @param the container in which the screen has to showup.
	 * @param the x co-ordinate of the position where the screen has to showup.
	 * @param the y co-ordinate of the position where the screen has to showup.
	 */
	public void showUp(Container container,double positionX, double positionY){

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

		// MAKE SCREEN VISIBLE, MOVE TO FRONT, & REQUEST FOCUS
			this.setVisible(true);
			this.moveToFront();
			this.requestFocus();

		// MAKE THE SCREEN SELECTED SCREEN
			try{
				this.setClosed(false);
				this.setSelected(true);
			} catch(PropertyVetoException pve) {
				pve.printStackTrace();
			}

		// START EBLA
			try {
				if (ebla == null) {
					ebla = new EBLA(sd, dbc, this);
					ebla.start();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}

	} // end showUp()



	/**
	 * Shows the experience screen at the default location on the specified container.
	 *
	 * @param the container in which the screen has to showup.
	 */
	public void showUp(Container container) {
		showUp(container, 30,30);
	} // end showUp()


} // end of StatusScreen class



/*
 * $Log$
 * Revision 1.7  2003/12/31 19:38:08  yoda2
 * Fixed various thread synchronization issues.
 *
 * Revision 1.6  2003/12/30 23:20:30  yoda2
 * Added more details of EBLA processing and cleaned up layout.
 *
 * Revision 1.5  2003/12/29 23:19:42  yoda2
 * Finished JavaDoc and code cleanup.
 * Simulated modal behavior by retaking focus when lost.
 * Added close button.
 *
 * Revision 1.4  2003/12/26 22:16:21  yoda2
 * General code cleanup and addition of JavaDoc.
 *
 * Revision 1.3  2003/12/26 20:48:45  yoda2
 * Reflected renaming of Session.java to SessionData.java and removed unnecessary import statements.
 *
 * Revision 1.2  2003/09/25 23:07:46  yoda2
 * Updates GUI code to use new SwingSet toolkit and latest Java RowSet reference implementation.
 *
 * Revision 1.1  2003/08/08 20:09:21  yoda2
 * Added preliminary version of new GUI for EBLA to SourceForge.
 *
 */