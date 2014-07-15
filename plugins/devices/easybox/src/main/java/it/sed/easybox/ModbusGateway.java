/*
 Copyright (c) Dario Pieracci, Matteo Mazzoni 2012  
   
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModbusGateway extends Thread {
    // Definizione costanti per Errori

    static final int MODBUS_ERRORE_ADDR = -1;
    static final int MODBUS_ERRORE_TIMEOUT_TX = -2;
    static final int MODBUS_ERRORE_TIMEOUT_RX = -3;
    // Definizione costanti per Comandi
    static final int MODBUS_READHOLDREG = 3;
    static final int MODBUS_WRITEMULTIREG = 16;
    static short OUTPUT_REGISTER_ADDR = 14;
    static short INPUT_REGISTER_ADDR = 37;
    static short CONFIG_REGISTER_ADDR = 6;
    static short AFTER_TX_WAIT = 250;
    static short DISPATCH_INTERVAL = 150;
    private int SOCKET_TIMEOUT = 500;
    private Socket socketStream;
    private DataInputStream sockIn;
    private DataOutputStream sockOut;
    private String ipAddress;
    private int tcpPort;
    private int nProveTx;
    private BufferModbus bufIn;
    private byte comando;
    private int sizeRx, lenRx;
    private HashMap<Byte, Nodo> listaNodi = new HashMap<Byte, Nodo>();
    private QueueStrategy queueStrategy;
    private List<ModbusAction> ReadActionQueue;
    private List<ModbusAction> WriteActionQueue;
    private List<ModbusAction> GlobalActionQueue;
    private List<ModbusAction> DispatchedActionQueue;
    private List<ModbusAction> ProcessingActionQueue;
    private boolean inited = false;
    private volatile boolean running = true;
    private static final Logger LOG = Logger.getLogger(ModbusGateway.class.getName());

    public void terminate() {
        running = false;
        try {
            socketStream.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        while (running) {
            dispatchAction();
            try {
                sleep(DISPATCH_INTERVAL);
            } catch (InterruptedException ex) {
                LOG.severe(ex.getLocalizedMessage());
                running = false;
            }
        }

    }

    public synchronized ModbusAction pickDispatchedAction() {
        if (DispatchedActionQueue.size() > 0) {
            return DispatchedActionQueue.remove(0);
        }
        return null;
    }

    public List<ModbusAction> getGlobalQueue() {
        return this.GlobalActionQueue;
    }

    public int getReadQueueSize() {
        return this.ReadActionQueue.size();
    }

    public int getDispatchedQueueSize() {
        return this.DispatchedActionQueue.size();
    }

    public List<ModbusAction> getWriteQueue() {
        return this.WriteActionQueue;
    }

    public final void setQueueStrategy(QueueStrategy strat) {
        this.queueStrategy = strat;
        queueStrategy.init(this);
    }

    public void setQueues(List<ModbusAction> ReadQueue, List<ModbusAction> WriteQueue, List<ModbusAction> GlobalQueue, List<ModbusAction> DispatchedQueue, List<ModbusAction> ProcessingQueue) {
        this.ReadActionQueue = ReadQueue;
        this.WriteActionQueue = WriteQueue;
        this.GlobalActionQueue = GlobalQueue;
        this.DispatchedActionQueue = DispatchedQueue;
        this.ProcessingActionQueue = ProcessingQueue;

    }
    // costruttore

    public ModbusGateway(String ipAddress, int tcpPort, int nProveTx, QueueStrategy strat, int sockTimeOut) {
        this.bufIn = new BufferModbus((short) 256);
        setQueueStrategy(strat);
        init(ipAddress, tcpPort, nProveTx, sockTimeOut);
    }

    public ModbusGateway(String ipAddress, int tcpPort, int nProveTx, int sockTimeOut) {
        this.bufIn = new BufferModbus((short) 256);
        setQueueStrategy(new WriteFirstQueue());
        init(ipAddress, tcpPort, nProveTx, sockTimeOut);
    }

    public final void init(String ipAddress, int tcpPort, int nProveTx, int sockTimeOut) {
        socketStream = null;

        this.ipAddress = ipAddress;
        this.tcpPort = tcpPort;
        this.nProveTx = nProveTx;
        this.SOCKET_TIMEOUT = sockTimeOut;


        try {
            socketStream = new Socket(ipAddress, tcpPort);
            sockIn = new DataInputStream(socketStream.getInputStream());
            sockOut = new DataOutputStream(socketStream.getOutputStream());
            socketStream.setSoTimeout(SOCKET_TIMEOUT);
            this.inited = true;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Modbus gateway init failed:{0}:{1} : {2}", new Object[]{ipAddress, new Integer(tcpPort).toString(), ex.getMessage()});
            this.inited = false;
        }


    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    private BufferModbus getBufIn() {
        return bufIn;
    }

    public void addNodo(byte numero, byte numIngDig, byte numOutDig, byte numIngAna, byte numOutAna) {
        listaNodi.put(numero, new Nodo(numero, numIngDig, numOutDig, numIngAna, numOutAna));
    }

    public Nodo getNodo(byte numero) {
        return listaNodi.get(numero);
    }

    public HashMap<Byte, Nodo> getListaNodi() {
        return this.listaNodi;
    }

    /**
     * *************************************************************************************
     * Implementa il comando MODBUS per leggere 'n' registri a ritenzione a
     * partire dall'indirizzo specificato. Ritorna: >0 = Comando eseguito
     * correttamente. Il valore di ritorno indica il numero di byte della
     * risposta Altrimenti il codice di errore verificatosi.
     * **************************************************************************************
     */
    public int Read(int slave, int addrStart, int nReg) {
        if (this.inited == false) {
            init(this.ipAddress, this.tcpPort, this.nProveTx, this.SOCKET_TIMEOUT);
        }
        int ris;

        sizeRx = 0;

        if (addrStart == 0) {
            return MODBUS_ERRORE_ADDR;
        }

        sizeRx = 3 + (nReg * 2) + 2;

        this.comando = MODBUS_READHOLDREG;

        ris = Tx(slave, addrStart - 1, nReg, null);

        return ris;
    }

    /**
     * *************************************************************************************
     * ModBus_WriteMultiReg
     *
     * Implementa il comando MODBUS per scrivere un numero di registri di output
     * a partire dall'indirizzo specificato. Ritorna: 0= Comando eseguito
     * correttamente Altrimenti il codice di errore verificatosi.
     * **************************************************************************************
     */
    public int Write(int slave, int addrStart, int nReg, short[] valReg) {
        if (this.inited == false) {
            init(this.ipAddress, this.tcpPort, this.nProveTx, this.SOCKET_TIMEOUT);
        }
        byte[] bufferVal = new byte[(nReg * 2) + 1];

        int ris;
        short j = 1;

        if (addrStart == 0) {
            return MODBUS_ERRORE_ADDR;
        }

        bufferVal[0] = (byte) (nReg * 2);
        for (short i = 0; i < nReg; i++) {
            // bufferVal[j] = (byte) ((valReg[i] & 0xFF00 )>> 8 ); // 1000 = 0b0000001111101000 = 0x03E8; := 03
            bufferVal[j++] = (byte) ((valReg[i] >> 8) & 0x00FF); // := 03
            bufferVal[j++] = (byte) (valReg[i] & 0x00FF); // 
            //j += 2;
        }

        comando = MODBUS_WRITEMULTIREG;

        sizeRx = 8;
        ris = Tx(slave, addrStart - 1, nReg, bufferVal);

        sizeRx = 5;

        return ris;
    }

    /**
     * ************************************************************************
     * Invia un comando MODBUS allo slave specificato.
     * ************************************************************************
     */
    private int Tx(int slave, int addrStart, int nReg, byte[] bufferVal) {
        boolean flagTx;

        byte AddH, AddL;
        byte nByteH, nByteL;
        byte[] bufferOut = new byte[256];

        short sizeTx;
        short nProve;

        AddH = (byte) ((addrStart & 0xFF00) >> 8);
        AddL = (byte) ((addrStart & 0x00FF));
        nByteH = (byte) ((nReg & 0xFF00) >> 8);
        nByteL = (byte) ((nReg & 0x00FF));

        bufferOut[0] = (byte) slave;
        bufferOut[1] = comando;
        bufferOut[2] = AddH;				//in alcuni comandi il parametro addr assume un'altro significato in modo da
        bufferOut[3] = AddL;				//poter utilizzare comunque la funzione ModBus_tx
        bufferOut[4] = nByteH;			//in alcuni comandi il parametro nByte assume un altro significato in modo da 
        bufferOut[5] = nByteL;			//poter utilizzare comunque la funzione ModBus_tx
        sizeTx = 6;

        // si controlla se c'è da inviare niente. Se sì, si aggiorna il bufferOut
        if (bufferVal != null) {

            /* for (int i = 0; i < bufferVal.length; i++) {
             *   bufferOut[i + sizeTx] = bufferVal[i];
             * }
             */

            System.arraycopy(bufferVal, 0, bufferOut, sizeTx, bufferVal.length);

            sizeTx += (short) bufferVal.length;
        }

        // aggiunge alla stringa la crc

        CRC16 crc = new CRC16();
        crc.update(bufferOut, 0, sizeTx);
        int crcValue = crc.get16Value();
        // calcolo con crc16
       /* crc16 = crc16_Calcolo(bufferOut, 0, sizeTx);
        
         bufferOut[sizeTx++] = (byte) ((crc16 & 0xff00) >> 8);
         bufferOut[sizeTx++] = (byte) ((crc16 & 0x00ff));
         */
        bufferOut[sizeTx++] = (byte) ((crcValue & 0x00ff));
        bufferOut[sizeTx++] = (byte) ((crcValue & 0xff00) >> 8);


        //   if (  crcValue != crc16) System.out.println("CRC class: 0x" + Integer.toHexString(crcValue) + " CRC func: 0x"+ Integer.toHexString(crc16));

        nProve = 0;
        lenRx = 0;
        do {
            if (nProve > 0) {
                LOG.log(Level.WARNING, "{0} RETRYING OPERATION ({1}) ON SLAVE {2}", new Object[]{this, nProve, slave});
            }
            try {
                sockOut.write(bufferOut, 0, sizeTx);
                flagTx = true;
            } catch (java.io.IOException exio) {
                try {
                    socketStream.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                LOG.log(Level.WARNING, "{0} REINIT SOCKET AFTER ERROR in TX", this);
                flagTx = false;
                init(this.ipAddress, this.tcpPort, this.nProveTx, this.SOCKET_TIMEOUT);
            } catch (Exception ex) {
                // System.out.println(ex.getMessage() + " C:" + comando + " S:" + slave + " A:" + addrStart);
                LOG.severe(ex.getMessage());
                flagTx = false;
            }

            try {
                Thread.sleep(AFTER_TX_WAIT);
            } catch (InterruptedException ex) {
                // LOG.log(Level.SEVERE, null, ex);
            }

            if (flagTx) {
                try {
                    lenRx = sockIn.read(bufIn.getBuffer(), 0, sizeRx);
                    crc.reset();
                    crc.update(bufIn.getBuffer(), 0, sizeRx - 2);
                    crcValue = crc.get16Value();
                    if ((byte) ((crcValue & 0xff00) >> 8) != bufIn.getBuffer()[sizeRx - 1]
                            || (byte) (crcValue & 0x00ff) != bufIn.getBuffer()[sizeRx - 2]) {
                        lenRx = 0;
                    }

                } catch (SocketTimeoutException ex) {
                    LOG.log(Level.SEVERE, "{0} Timeout waiting answer from: {1}", new Object[]{this, slave});
                    //LOG.severe(ex.getMessage());
                    lenRx = 0;
                } catch (Exception ex) {
                    LOG.severe(ex.getMessage());
                    //System.out.println(ex.getMessage() + " C:" + comando + " S:" + slave + " A:" + addrStart);
                    lenRx = 0;
                }
            }
            nProve++;
        } while (lenRx == 0 && nProve < nProveTx);

        if (lenRx == 0) {
            if (flagTx) {
                return MODBUS_ERRORE_TIMEOUT_TX;
            }
            return MODBUS_ERRORE_TIMEOUT_RX;
        }
        return lenRx;
    }

    /*
     public void setOutput(byte nodeID, short line, int val) {
     Nodo board = getNodo(nodeID);


     short valArray[] = {(short) val};
     if (board != null) {
     Write(nodeID, OUTPUT_REGISTER_ADDR + line, 1, valArray);
     }

     }
     */
    private void setRelayTimeout(byte nodeID, short line, short val) {
        Nodo board = getNodo(nodeID);

        short[] valArray = {val};
        if (board != null) {
            Write(nodeID, CONFIG_REGISTER_ADDR + line, 1, valArray);
        }

    }

    public void programmaLinee(byte nodeID, String lineConfig, boolean doIt) {

        Nodo board = getNodo(nodeID);
        if (board != null) {
            String tipilinee[] = lineConfig.split(",");
            // l'inizializzazione porta le linee a 0
            if (doIt) {
                for (short j = 0; j < board.getNumOutDig(); j++) {
                    //setOutput(nodeID, j, 0);
                    Write(nodeID, OUTPUT_REGISTER_ADDR + j, 1, new short[]{(short) 0});
                }
            }

            for (short j = 0; j < board.getNumOutDig(); j++) {
                char curChar = tipilinee[j].charAt(0);
                short valReg = 0;
                // si salva il tipo di linea
                board.setTipoOutDig(curChar, j);
                if (curChar == 'T') {
                    valReg = 300;
                }
                if (doIt) {
                    try {

                        // Thread.sleep(200);
                        // si configura il timeout del relay
                        setRelayTimeout(nodeID, j, valReg);
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
            }

            board.setProgrammata(true);
            queueReadAction(nodeID, INPUT_REGISTER_ADDR);
            // useless to read output register: we use it only for writing
            //queueReadAction(nodeID, OUTPUT_REGISTER_ADDR);

        }
    }

    public short[] getResult(short size) {
        short result[] = new short[size];
        for (short j = 0; j < size; j++) {
            result[j] = getBufIn().getRegister((short) (3 + (j * 2)));
        }
        return result;
    }

    private synchronized void queueAction(ModbusAction act) {
        if (act.isWrite()) {
            //if (!WriteActionQueue.contains(act)) {
            WriteActionQueue.add(act);
            //}
        } else {
            //if (!ReadActionQueue.contains(act)) {
            ReadActionQueue.add(act);
            //}
        }
        //if (!GlobalActionQueue.contains(act)) {
        GlobalActionQueue.add(act);
        // }

    }

    public void queueReadAction(byte node, short register) {
        Nodo curr = getNodo(node);
        if (curr != null) {
            queueAction(new ModbusAction(node, register, curr.getNumOutDig()));
        } else {
            LOG.log(Level.SEVERE, "ERROR QUEUEING READ ACTION FOR NODE {0}", node);
        }
    }

    public void queueWriteOutputAction(byte nodeId, short lineID, short data) {
        Nodo curr = getNodo(nodeId);
        if (curr != null) {
            curr.setOutToBeWritten(lineID, data);
            queueAction(new ModbusAction(true, nodeId, OUTPUT_REGISTER_ADDR, curr.getNumOutDig()));
        }
    }

    private synchronized void dispatchAction() {
        // pick should just select an action NOT REMOVE IT FROM ANY QUEUE
        ModbusAction act = this.queueStrategy.pick(this);
        if (act != null) {
            ProcessingActionQueue.add(act);
            GlobalActionQueue.remove(act);
            if (act.isWrite()) {
                try {
                    WriteActionQueue.remove(act);
                    Nodo curr = getNodo(act.getNode());
                    // usually data is set to null, in order to optimize writings at runtime
                    if (act.getData() == null) {
                        if (curr != null) {
                            act.setData(curr.getNewOutDigs());
                        }
                    }
                    try {
                        this.Write(act.getNode(), act.getAddress(), act.getSize(), act.getData());
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "ERROR EXECUTIN WRITE ACTION{0}", act.toString());
                    }
                    curr.resetToBeWritten();

                } catch (Exception e) {
                    LOG.log(Level.WARNING, "{0} ACTION {1} GOT ERROR ON RESPONSE", new Object[]{this, toString(), act.toString()});
                } finally {
                    ProcessingActionQueue.remove(act);
                }
            } else {
                ReadActionQueue.remove(act);
                // read response 
                try {
                    int res = this.Read(act.getNode(), act.getAddress(), act.getSize());
                    if (res > 0) {
                        act.setData(getResult(act.getSize()));
                         DispatchedActionQueue.add(act);
                    } else {   
                        LOG.log(Level.WARNING, "{1} ACTION {2} DID NOT SUCCEDE: {0}", new Object[]{ res, this, act.toString()});
                    }
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "{0} ACTION {1} GOT ERROR ON RESPONSE", new Object[]{this,act.toString()});
                } finally {
                    ProcessingActionQueue.remove(act);
                    // queue a new read request
                    queueReadAction(act.getNode(), act.getAddress());
                }
            }
        }
        // act is null : probabily no action was queued yet
    }

    @Override
    public String toString() {
        String str = "{" + this.ipAddress + ":" + this.tcpPort + ": ";
        for (byte n : listaNodi.keySet()) {
            str += n + " ";
        }
        return str + "}";
    }

    @Override
    public void finalize() throws Throwable {


        socketStream.shutdownInput();
        socketStream.shutdownOutput();
        socketStream.close();

        super.finalize();
    }

    public synchronized void sanityCheck(String msg) {
        if (((ReadActionQueue.size() + WriteActionQueue.size() != GlobalActionQueue.size()) || (ReadActionQueue.size() + ProcessingActionQueue.size() != listaNodi.size()))) {
            LOG.log(Level.SEVERE, "{0} {1} ERROR! QUEUES MISMATCH! N:{7} R:{2} W:{3} D:{5} G:{4} P:{6}", new Object[]{msg, this.toString(), ReadActionQueue.size(), WriteActionQueue.size(), GlobalActionQueue.size(), DispatchedActionQueue.size(), ProcessingActionQueue.size(), listaNodi.size()});
            for (final ModbusAction act : DispatchedActionQueue) {
                LOG.log(Level.INFO, "{0} DISPATCHED QUEUE: {1}", new Object[]{this.toString(), act.toString()});
            }
            for (final ModbusAction act : GlobalActionQueue) {
                LOG.log(Level.INFO, "{0} ACTION QUEUE: {1}", new Object[]{this.toString(), act.toString()});
            }
            for (final ModbusAction act : ProcessingActionQueue) {
                LOG.log(Level.INFO, "{0} PROCESSING QUEUE: {1}", new Object[]{this.toString(), act.toString()});
            }
        }
        if (DispatchedActionQueue.size() > 100) {
            LOG.log(Level.WARNING, "{0} DISPATCHED QUEUE EXPLODING: {1}", new Object[]{this.toString(), DispatchedActionQueue.size()});
            DispatchedActionQueue.clear();
        }
    }

    List<ModbusAction> getProcessingQueue() {
        return this.ProcessingActionQueue;
    }
}