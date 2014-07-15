/*
 Copyright (c) Matteo Mazzoni 2012  
   
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package it.sed.easybox;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.reactions.Command;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class easybox extends Protocol {

    private static int POLLING_TIME;
    private static int BOARD_NUMBER = 1;
    private static String DEBUG_MESSAGES = "";
    private static String STRING_DELIMITER = "-";
    // address = IP_trp: porta_trp: id_nodo: tipo_registro: id I/0
    private String[] address = null;
    private ArrayList< ModbusGateway> trps = new ArrayList<ModbusGateway>();
    private static final Logger LOG = Logger.getLogger(easybox.class.getName());
    private boolean forceNotify;
    private int notifyRule;

    public easybox() {
        //every plugin needs a name and a manifest XML file
        super("EasyBox", "/easybox/easybox-manifest.xml");
        setPollingWait(-1);
        this.notifyRule = configuration.getIntProperty("forcenotify-rule", -1);
    }

    @Override
    protected void onRun() {
        try {

            switch (notifyRule) {
                // seleziona casualmente
                case -2:
                    Random r = new Random();
                    forceNotify = r.nextBoolean();
                    break;
                // sempre
                case -1:
                    forceNotify = true;
                // mai
                case 0:
                    forceNotify = false;
                    break;
                // ogni x secondi
                default:
                    Date now = new Date();
                    if (now.getSeconds() % notifyRule == 0) {
                        forceNotify = true;
                    } else {
                        forceNotify = false;
                    }
            }
            for (ModbusGateway trp : trps) {
                trp.sanityCheck("PRE EXECUTION");
                ModbusAction resultAct = trp.pickDispatchedAction();
                while (resultAct != null) {
                    readboardStatus(trp, resultAct);
                    resultAct = trp.pickDispatchedAction();
                }
                trp.sanityCheck("POST EXECUTION");
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "STOPPING EASYBOX AS WE ENCOUNTERED A PROBLEM AT RUNTIME", e);
            stop();
        }
    }

    @Override
    protected void onStart() {
        POLLING_TIME = configuration.getIntProperty("polling-time", 100);
        DEBUG_MESSAGES = configuration.getStringProperty("debug", "false");
        STRING_DELIMITER = configuration.getProperty("address-delimiter");
        BOARD_NUMBER = configuration.getTuples().size();
        loadBoards();
        for (ModbusGateway trp : trps) {
            LOG.log(Level.INFO, "Starting thread for trp '{'{0}'}'", trp.toString());
            trp.start();
        }
        setPollingWait(POLLING_TIME);

    }

    @Override
    protected void onStop() {
        setDescription("Shutting down...");
        setPollingWait(-1); //disable polling
        unloadBoards(); //release resources
        trps.clear();
        //display the default description
        setDescription(configuration.getStringProperty("description", "EasyBox"));
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        if (DEBUG_MESSAGES.equals("write") || DEBUG_MESSAGES.equals("all")) {
            LOG.log(Level.INFO, "Easybox plugin receives a command called {0} with parameters {1}", new Object[]{c.getName(), c.getProperties().toString()});
        }
        //get connection paramentes address:port from received freedomotic command
        address = c.getProperty("address").split(STRING_DELIMITER);
        byte nodeID = Byte.parseByte(address[2]);
        short lineID = Short.parseShort(address[4]);

        for (ModbusGateway trp : trps) {
            if (trp.getIpAddress().equals(address[0])) {
                Nodo nodo = trp.getNodo(nodeID);
                char type = nodo.getTipoOutDig(lineID);
                short valReg = 0;
                String commandName = c.getProperty("command");
                if (type == 'B') {
                    if (commandName.equals("TURN-ON")) {
                        valReg = 1;
                    } else if (commandName.equals("TURN-OFF")) {
                        valReg = 0;
                    } else if (commandName.equals("SWITCH")) {
                        valReg = (short) (1 - nodo.getOutDig(lineID));
                    }
                } else if (type == 'T') {
                    if (commandName.equals("TURN-ON") || commandName.equals("TURN-OFF") || commandName.equals("SWITCH")) {
                        valReg = 1;
                    }
                } else {
                    LOG.log(Level.WARNING, "CANNOT DEAL WITH COMMAND {0} ON TYPE {1}", new Object[]{commandName, type});
                    return;
                }
                trp.queueWriteOutputAction(nodeID, lineID, valReg);

            }
        }
    }

    @Override
    protected boolean canExecute(Command c) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void loadBoards() {
        setDescription("Reading status changes from"); //empty description
        for (int i = 0; i < BOARD_NUMBER; i++) {
            String ipToQuery;
            int nodeID;
            int inputRegister;
            int outputRegister;
            int portToQuery;
            int digitalInputNumber;
            int relayNumber;
            int socket_timeout;
            boolean initBoard;
            String lineConfig;
            socket_timeout = configuration.getTuples().getIntProperty(i, "socket-timeout", 500);
            ipToQuery = configuration.getTuples().getStringProperty(i, "ip-to-query", "192.168.1.201");
            portToQuery = configuration.getTuples().getIntProperty(i, "port-to-query", 80);
            relayNumber = configuration.getTuples().getIntProperty(i, "relay-number", 8);
            digitalInputNumber = configuration.getTuples().getIntProperty(i, "digital-input-number", 4);
            nodeID = configuration.getTuples().getIntProperty(i, "node-id", 1);
            inputRegister = configuration.getTuples().getIntProperty(i, "input-register", 37);
            outputRegister = configuration.getTuples().getIntProperty(i, "output-register", 14);
            lineConfig = configuration.getTuples().getStringProperty(i, "line-config", "T,T,T,T,T,T,T,T");
            initBoard = configuration.getTuples().getBooleanProperty(i, "relay-init", false);
            String autoConfiguration = configuration.getTuples().getStringProperty(i, "auto-configuration", "false");
            String objectClass = configuration.getTuples().getStringProperty(i, "object.class", "Light");
            boolean trp_exists = false;
            ModbusGateway loc_trp = null;
            for (ModbusGateway trp : trps) {
                if (trp.getIpAddress().equals(ipToQuery)) {
                    trp_exists = true;
                    loc_trp = trp;
                    break;
                }
            }

            if (!trp_exists) {
                loc_trp = new ModbusGateway(ipToQuery, portToQuery, 2, socket_timeout);
                trps.add(loc_trp);
                LOG.info(("{" + loc_trp.toString() + "} " + loc_trp.getReadQueueSize() + ":" + loc_trp.getWriteQueue().size() + ":" + loc_trp.getGlobalQueue().size()));
            }

            loc_trp.sanityCheck("PRE ADD " + nodeID + "|");
            loc_trp.addNodo((byte) nodeID, (byte) digitalInputNumber, (byte) relayNumber, (byte) 4, (byte) 0);
            loc_trp.programmaLinee((byte) nodeID, lineConfig, initBoard);
            loc_trp.sanityCheck("POST ADD");
            LOG.info("Easybox adds a new board ");
            LOG.info(("{" + loc_trp.toString() + "} " + loc_trp.getReadQueueSize() + ":" + loc_trp.getWriteQueue().size() + ":" + loc_trp.getGlobalQueue().size()));
            Nodo loc_nodo = loc_trp.getNodo((byte) nodeID);
            loc_nodo.setAutoconf(autoConfiguration);
            loc_nodo.setObjectType(objectClass);

            setDescription(getDescription() + " " + loc_trp.getIpAddress() + ":" + loc_trp.getTcpPort() + ":" + loc_nodo.getNumero() + "[" + loc_nodo.getNumInDig() + "i][" + loc_nodo.getNumOutDig() + "o]" + loc_nodo.getLinesConfig() + ";");

        }
    }

    private void readboardStatus(ModbusGateway trp, ModbusAction act) {
        Nodo board = trp.getNodo(act.getNode());

        short result[] = act.getData();
        if (act.getAddress() == ModbusGateway.OUTPUT_REGISTER_ADDR) {
            for (short j = 0; j < board.getNumOutDig(); j++) {
                if (board.setOutDig(result[j], j) || DEBUG_MESSAGES.equals("read") || forceNotify) {
                    //this.notifyChangeEvent(trp.getIpAddress() + ":" + trp.getTcpPort() + ":" + act.getNode() + ":" + 'o' + ":" + j, result[j], board);
                    if (board.getTipoOutDig(j) == 'B') {
                        this.notifyChangeEvent(trp.getIpAddress() + ":" + trp.getTcpPort() + ":" + act.getNode() + ":" + 's' + ":" + j, result[j], board);
                    }
                }
            }
        } else if (act.getAddress() == ModbusGateway.INPUT_REGISTER_ADDR) {
            // lettura ingressi
            for (short j = 0; j < board.getNumInDig(); j++) {
                //Logger.getLogger(TestIT.class.getName()).log(Level.INFO, "Current Input: {0}", new Object[]{result});
                if (board.setInDig(result[j], j) || DEBUG_MESSAGES.equals("read") || forceNotify) {
                    //this.notifyChangeEvent(trp.getIpAddress() + ":" + trp.getTcpPort() + ":" + act.getNode() + ":" + 'i' + ":" + j, result[j], board);
                    if (board.getTipoOutDig(j) == 'T') {
                        this.notifyChangeEvent(trp.getIpAddress() + ":" + trp.getTcpPort() + ":" + act.getNode() + ":" + 's' + ":" + j, result[j], board);
                    }
                }
            }
        }
    }

    private void notifyChangeEvent(String device_address, int val, Nodo board) {
        if (DEBUG_MESSAGES.equals("all")) {
            LOG.log(Level.INFO, "Easybox object status - object ''{0}'' = {1}", new Object[]{device_address, val});
        }
        //building the event
        ProtocolRead event = new ProtocolRead(this, "easybox", device_address); //IP:PORT:RELAYLINE

        if (val == 1) {
            event.addProperty("isOn", "true");
        } else {
            event.addProperty("isOn", "false");
        }
        if (board.getAutoConf().equalsIgnoreCase("true")) {
            event.addProperty("object.class", board.getObjectType());
            event.addProperty("object.name", device_address);
        }
        //adding some optional information to the event
        //event.addProperty("boardIP", board.getIpAddress());
        //event.addProperty("boardPort", new Integer(board.getPort()).toString());
        //event.addProperty("relayLine", new Integer(relayLine).toString());
        //publish the event on the messaging bus
        notifyEvent(event);
    }

    private void unloadBoards() {
        for (ModbusGateway trp : trps) {
            try {
                trp.terminate();
                trp.interrupt();
                trp.join(1000);
                trp.getListaNodi().clear();
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, "Error stopping thread for TRP{0}:{1}", new Object[]{trp.getIpAddress(), trp.getTcpPort()});
            }
        }
    }
}
