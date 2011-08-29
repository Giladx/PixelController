/**
 * Copyright (C) 2011 Michael Vogt <michu@neophob.com>
 *
 * This file is part of PixelController.
 *
 * PixelController is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.neophob;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import processing.core.PApplet;

import com.neophob.sematrix.glue.Collector;
import com.neophob.sematrix.output.Lpd6803Device;
import com.neophob.sematrix.output.MatrixEmulator;
import com.neophob.sematrix.output.emulatorhelper.NewWindowHelper;
import com.neophob.sematrix.properties.PropertiesHelper;

/**
 * 
 * @author michu
 * 
 * TODO:
 * make image resize option (speed/quality) user selectable
 * make zoom option, usefull for one screen
 *
 */
public class PixelController extends PApplet {

	private static Logger log = Logger.getLogger(PixelController.class.getName());

	private static final long serialVersionUID = -1336765543826338205L;
	
	private static final int DEVICE_SIZE = 8;

	public static final int FPS = 20;
	//96*2*25 = 4800bytes

	//Output rainbowduino;
	Lpd6803Device lpd6803;
	//ArtnetDevice artnet;
	
	NewWindowHelper nwh;
	long lastHeartbeat;
	int error=0;
	int frameCounter=0;
	MatrixEmulator osd;

	/**
	 * prepare
	 */
	public void setup() {
		//		ImageIcon titlebaricon = new ImageIcon(loadBytes("logo.jpg"));
		//		super.frame.setIconImage(titlebaricon.getImage()); 
		//		super.frame.setTitle("This is in the titlebar!");

		Collector col = Collector.getInstance(); 
		col.init(this, FPS, DEVICE_SIZE, DEVICE_SIZE);
		frameRate(FPS);
		noSmooth();
		
		osd = new MatrixEmulator(col.getPixelControllerOutput());
		PropertiesHelper ph = PropertiesHelper.getInstance();
		
/*		try {
			rainbowduino = new RainbowduinoDevice(PropertiesHelper.getAllI2cAddress());
		} catch (Exception e) {
			rainbowduino = null;
		}*/
		try {
			lpd6803 = new Lpd6803Device(
					col.getPixelControllerOutput(), ph.getLpdDevice(), ph.getColorFormat() );
		} catch (Exception e) {
			lpd6803 = null;
		}
		
		//artnet = new ArtnetDevice(col.getPixelControllerOutput() );

		
		if (ph.getProperty("show.debug.window").equalsIgnoreCase("true")) {
			nwh = new NewWindowHelper(true);	
		}
	}

	@SuppressWarnings("deprecation")
	public void draw() { 
		//update all generators
 
		Collector.getInstance().updateSystem();

		if (lpd6803!=null && lpd6803.getArduinoErrorCounter()>0) {
			error=lpd6803.getArduinoErrorCounter();			
			log.log(Level.SEVERE,"error at: {0}, errorcnt: {1}, buffersize: {2}", 
					new Object[] {
						new Date(lpd6803.getLatestHeartbeat()).toGMTString(),
						error,
						lpd6803.getArduinoBufferSize()
					});
		}

		frameCounter++;
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "com.neophob.PixelController" });
	}
}