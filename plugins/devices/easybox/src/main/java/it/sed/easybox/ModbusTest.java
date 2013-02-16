/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.sed.easybox;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.wimpi.modbus.ModbusException;
import net.wimpi.modbus.ModbusIOException;
import net.wimpi.modbus.ModbusSlaveException;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.msg.ModbusResponse;
import net.wimpi.modbus.msg.ReadMultipleRegistersRequest;
import net.wimpi.modbus.msg.WriteMultipleCoilsRequest;
import net.wimpi.modbus.net.TCPMasterConnection;

/**
 *
 * @author Matteo Mazzoni <matteo@bestmazzo.it>
 */
public class ModbusTest {

    private TCPMasterConnection conn;

    public ModbusTest() {
        byte[] addr = {(byte) 192, (byte) 168, (byte) 0, (byte) 2};
        try {
            conn = new TCPMasterConnection(InetAddress.getByAddress(addr));
            conn.setPort(4001);




        } catch (UnknownHostException ex) {
        } catch (Exception e) {
        }
    }

    public void connect() {
        try {
            conn.connect();
        } catch (Exception ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void disconnect() {
        if (conn.isConnected()) {
            conn.close();
        }
    }

    public ModbusResponse Write(int node, int reg, int len) {
        WriteMultipleCoilsRequest wreq = new WriteMultipleCoilsRequest();
        wreq.setUnitID(node);
        wreq.setCoilStatus(node, true);
        ModbusTCPTransaction wtrans = new ModbusTCPTransaction(conn);
        wtrans.setRequest(wreq);
        try {
            wtrans.execute();
            return wtrans.getResponse();

        } catch (ModbusIOException ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModbusSlaveException ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModbusException ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public ModbusResponse Read(int node, int ref, int len) {

        ModbusTCPTransaction trans = new ModbusTCPTransaction(conn);

        ReadMultipleRegistersRequest req = new ReadMultipleRegistersRequest();
        req.setDataLength(8);
        req.setUnitID(node);
        req.setReference(37);
        trans.setRequest(req);
        try {
            trans.execute();
            return trans.getResponse();
        } catch (ModbusIOException ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModbusSlaveException ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ModbusException ex) {
            Logger.getLogger(ModbusTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
