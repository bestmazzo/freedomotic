/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.freedomotic.plugins.devices.i2c;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.i2c.I2CDevice;
import java.util.Map;

/**
 *
 * @author matteo
 */
public class i2cBoard {
    private final String address;
    private final GpioProvider gP;
    private Map<Integer, GpioPinDigitalInput> inputs;
    private Map<Integer, GpioPinDigitalOutput> outputs;

    public i2cBoard(GpioProvider gp, String address) {
        this.gP=gp;
        this.address= address;
        
    }
    
    public String getAddress(){
        return address;
    }
    
    public GpioProvider getProvider(){
        return gP;
    }
    
    
    
}
