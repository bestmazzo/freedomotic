/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freedomotic.plugins.devices.i2c.support;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.component.temperature.TemperatureSensorBase;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.temperature.TemperatureScale;
import java.io.IOException;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author matteo
 */
public class lm77 extends TemperatureSensorBase implements TemperatureSensor  {
    public static final String NAME = "com.freedomotic.plugins.devices.i2c.chip.lm77";
    public static final String DESCRIPTION = "LM77 Temperature Sensor Provider";
    public static final int DEFAULT_ADDRESS = 0x20;

    private static final int REGISTER_TEMPERATURE = 0x00;
    private static final int REGISTER_CONFIG = 0x01;
    private static final int REGISTER_T_HIST = 0x02;
    private static final int REGISTER_T_CRIT = 0x03;
    private static final int REGISTER_T_LOW = 0x04;
    private static final int REGISTER_T_HIGH = 0x05;  
    
    
    private final I2CBus bus;
    private final I2CDevice device;
    
    public lm77(int busNumber, int address) throws IOException{
        // create I2C communications bus instance
        bus = I2CFactory.getInstance(busNumber);
        // create I2C device instance
        device = bus.getDevice(address);
    }
    
    @Override
    public double getTemperature() {
        try {
            // read bytes from REGISTER_TEMPERATURE
            byte buffer[] = new byte[2];
            device.read(REGISTER_TEMPERATURE, buffer,0 , 2);
            // convert and return
            
            
        } catch (IOException ex) {
            Logger.getLogger(lm77.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    public TemperatureScale getScale() {
        return TemperatureScale.CELSIUS;
    }

   
    
}
