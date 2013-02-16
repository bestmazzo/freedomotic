package it.sed.easybox;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Matteo Mazzoni <matteo@bestmazzo.it>
 */
public class TestTRP {

    ModbusGateway trp;
    static byte nodeID = 1;

    public TestTRP() {
        trp = new ModbusGateway("192.168.128.156", 4001, 1, 500);
        trp.addNodo(nodeID, (byte) 8, (byte) 8, (byte) 4, (byte) 0);
        trp.programmaLinee((byte) 1, "B,B,B,B,B,B,B,B", true);
    }

    //@Test
    public void doTests() {
        ModbusAction act = trp.pickDispatchedAction();
        for (short i = 0; i < 8; i++) {
           testLine(i,(short) 1);
           testLine(i,(short) 0);
        }
    }
    
    private void testLine(short line, short value){
            trp.queueWriteOutputAction(nodeID, line, value);
            ModbusAction act = trp.pickDispatchedAction();
            
            
            if (!act.isWrite()) {
                if ((act.getAddress() == ModbusGateway.OUTPUT_REGISTER_ADDR && trp.getNodo(nodeID).getTipoOutDig(line) == 'B')){
                    assertEquals(value, act.getData()[line]);
                }
                if ((act.getAddress() == ModbusGateway.INPUT_REGISTER_ADDR && trp.getNodo(nodeID).getTipoOutDig(line) == 'T')){
                    assertEquals(value, act.getData()[line]);
                }
            }
    }
}
