package com.freedomotic.plugins.devices.modbus.gateways;

import jssc.*;
import com.serotonin.modbus4j.serial.SerialPortWrapper;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortWrapperImpl implements SerialPortWrapper {

    private SerialPort serialPort;
    private String commPortId;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;
    private int flowControlIn;
    private int flowControlOut;
    private InputStream inputStream;
    private OutputStream outputStream;

    public SerialPortWrapperImpl(String commPortId, int baudRate, int dataBits, int stopBits, int parity, int flowControlIn,
            int flowControlOut) {

        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
        this.flowControlIn = flowControlIn;
        this.flowControlOut = flowControlOut;
        
        serialPort = new SerialPort(this.commPortId);
        inputStream = new SerialInputStream(serialPort);
        outputStream = new BufferedOutputStream(new SerialOutputStream(serialPort));

    }

    @Override
    public void close() throws Exception {
        try {
            serialPort.closePort();
        } catch (SerialPortException ex) {
            //LOG.log(Level.WARNING, "Error while closing serial port " + serialPort.getPortName(), ex);
                    }
    }

    @Override
    public void open() throws Exception {
        boolean isOpen = serialPort.openPort();
    }

    @Override
    public InputStream getInputStream() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getBaudRate() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getStopBits() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc) @see
     * com.serotonin.modbus4j.serial.SerialPortWrapper#getParity()
     */
    @Override
    public int getParity() {
        // TODO Auto-generated method stub
        return 0;
    }


    /*
     * (non-Javadoc) @see
     * com.serotonin.modbus4j.serial.SerialPortWrapper#getFlowControlIn()
     */
    @Override
    public int getFlowControlIn() {
        // TODO Auto-generated method stub
        return 0;
    }


    /*
     * (non-Javadoc) @see
     * com.serotonin.modbus4j.serial.SerialPortWrapper#getFlowControlOut()
     */
    @Override
    public int getFlowControlOut() {
        // TODO Auto-generated method stub
        return 0;
    }


    /*
     * (non-Javadoc) @see
     * com.serotonin.modbus4j.serial.SerialPortWrapper#getDataBits()
     */
    @Override
    public int getDataBits() {
        // TODO Auto-generated method stub
        return 0;
    }
}
