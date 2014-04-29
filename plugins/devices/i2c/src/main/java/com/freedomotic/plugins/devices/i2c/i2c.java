package com.freedomotic.plugins.devices.i2c;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.reactions.Command;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.gpio.extension.pcf.PCF8574GpioProvider;
import com.pi4j.gpio.extension.pcf.PCF8574Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioProvider;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.HashMap;

public class i2c extends Protocol {

    final int POLLING_WAIT = -1;
    private I2CBus bus;
    private int busNum;
    private GpioPinDigitalInput i2c_int;
    private HashMap<String, i2cBoard> boards = new HashMap<String, i2cBoard>();
    private int DEV_NUMBER;
    private GpioController gpio;
    // address = dev_i2c_address: line_number 
    // 0x21:0
    // private String[] address = null;

    public i2c() {
        //every plugin needs a name and a manifest XML file
        super("i2c", "/i2c/i2c-manifest.xml");
        //read a property from the manifest file below which is in
        //POLLING_WAIT is the value of the property "time-between-reads" or 2000 millisecs,
        //default value if the property does not exist in the manifest
        setPollingWait(-1); //millisecs interval between hardware device status reads

    }

    @Override
    protected void onShowGui() {
        /**
         * uncomment the line below to add a GUI to this plugin the GUI can be
         * started with a right-click on plugin list on the desktop frontend
         * (com.freedomotic.jfrontend plugin)
         */
        //bindGuiToPlugin(new HelloWorldGui(this));
    }

    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {
        LOG.info("I2C onRun() logs this message every "
                + "POLLINGWAIT=" + POLLING_WAIT + "milliseconds");
        //at the end of this method the system waits POLLINGTIME 
        //before calling it again. The result is this log message is printed
        //every 2 seconds (2000 millisecs)

    }

    @Override
    protected void onStart() {
        ArrayList<GpioPinDigitalInput> myInputs = new ArrayList<GpioPinDigitalInput>();
        ArrayList<GpioPinDigitalOutput> myOutputs = new ArrayList<GpioPinDigitalOutput>();

        // create gpio controller
        gpio = GpioFactory.getInstance();
        LOG.info("I2C plugin is started");

        // acquire bus
        this.busNum = I2CBus.BUS_1;

        try {
            bus = I2CFactory.getInstance(I2CBus.BUS_1);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        //configure devices
        DEV_NUMBER = configuration.getTuples().size();
        int devAddr;
        String devTypes;
        String devStrAddr;
        String alias;
        String chip;
        for (int i = 0; i < DEV_NUMBER; i++) {
            devStrAddr = configuration.getTuples().getStringProperty(i, "address", "0x00");
            devAddr = Integer.decode(devStrAddr);
            LOG.info("Reading a new board with address: " + devStrAddr + " read as (" + devAddr + ")");
            devTypes = configuration.getTuples().getStringProperty(i, "lines", "");
            alias = configuration.getTuples().getStringProperty(i, "alias", "");
            chip = configuration.getTuples().getStringProperty(i, "chip", "");
            GpioProvider gpioProvider = null;
            Pin pins[] = null;
            // instantiate the right object type
            if (chip.equalsIgnoreCase("PCF8574T")) {
                try {
                    PCF8574GpioProvider pcf = new PCF8574GpioProvider(busNum, devAddr);
                    gpioProvider = pcf;
                    pins = PCF8574Pin.ALL;
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            } else if (chip.equalsIgnoreCase("MCP23017")) {
                try {
                    MCP23017GpioProvider mcp = new MCP23017GpioProvider(busNum, devAddr);
                    gpioProvider = mcp;
                    pins = MCP23017Pin.ALL;

                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            if (gpioProvider != null) {
                i2cBoard brd = new i2cBoard(gpioProvider, devStrAddr);
                boards.put(devStrAddr, brd);

                //properly configure every line
                int j = 0;
                for (String type : devTypes.split(",")) {
                    if (type.equalsIgnoreCase("i")) {
                        // provision gpio input pins 
                        myInputs.add(gpio.provisionDigitalInputPin(gpioProvider, pins[j]));
                    }
                    if (type.equalsIgnoreCase("o")) {
                        // provision gpio output pins and make sure they are all LOW at startup
                        GpioPinDigitalOutput gpdoPin = gpio.provisionDigitalOutputPin(gpioProvider, pins[j], PinState.LOW);
                        myOutputs.add(gpdoPin);
                        gpioProvider.setState(pins[j], PinState.HIGH);
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                        gpioProvider.setState(pins[j], PinState.LOW);
                        // gpio.setState(false, gpdoPin);
                    }
                    j++;
                }

            } else {
                LOG.warning("Cannot add board #" + (i + 1) + "with unsupported config.");
            }
        }
        
        
        GpioPinDigitalInput inputs[] = {};
        GpioPinDigitalOutput outputs[] = {};

        // create and register gpio pin listener
        GpioPinListenerDigital gpld = new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                LOG.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = "
                        + event.getState());
                // notify event 
                if (event.getSource() instanceof GpioProvider) {
                    GpioProvider source = (GpioProvider) event.getSource();
                    for (i2cBoard b : boards.values()) {
                        if (source == b.getProvider()) {
                            String address = b.getAddress() + ":" + event.getPin().getPin().getAddress();
                            notifyChangeEvent(address, event.getState().getValue());
                            break;
                        }
                    }
                }

            }
        };
        gpio.addListener(gpld, myInputs.toArray(inputs));
        // TODO: check why the next doesn't work
        // gpio.addListener(gpld, myOutputs.toArray(outputs));

        // on program shutdown, set output pins back to their default state: HIGH
        // gpio.setShutdownOptions(true, PinState.HIGH, myOutputs.toArray(outputs));
    }

    @Override
    protected void onStop() {
        LOG.info("I2C plugin is stopped ");

        for (i2cBoard b : boards.values()) {
            b.getProvider().shutdown();
        }
        gpio.shutdown();
        try {
            bus.close();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        boards.clear();
        boards = null;
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        LOG.info("I2C plugin receives a command called " + c.getName()
                + " with parameters " + c.getProperties().toString());
        String delimiter = configuration.getProperty("address-delimiter");
        String objAddress[] = c.getProperty("address").split(delimiter);
        int line = Integer.parseInt(objAddress[1]);
        int devAddr = Integer.parseInt(objAddress[0]);
        int value = 0;
        I2CDevice dev = bus.getDevice(devAddr);
        GpioProvider gpioProvider = boards.get(devAddr).getProvider();
        for (GpioPin pin : gpio.getProvisionedPins()) {
            if (pin.getPin().getAddress() == line && pin.getProvider().equals(gpioProvider)) {

                if (c.getProperty("command").equals("TURN-ON")) {
                    gpioProvider.setState(pin.getPin(), PinState.HIGH);
                    value = 1;
                } else if (c.getProperty("command").equals("TURN-OFF")) {
                    gpioProvider.setState(pin.getPin(), PinState.LOW);
                    value = 0;
                } else if (c.getProperty("command").equals("SWITCH")) {
                    if (gpioProvider.getState(pin.getPin()).equals(PinState.HIGH)) {
                        gpioProvider.setState(pin.getPin(), PinState.LOW);
                    } else {
                        gpioProvider.setState(pin.getPin(), PinState.HIGH);
                    }
                    value = -1;
                }
                // WARINIG: notification is made when the command is passed to the hardware level, 
                // assuming it always succedes. 
                // That's not always true, so we'd better find a way of reading output status elsewhere.
                notifyChangeEvent(c.getProperty("address"), value);
                break;
            }
        }
    }

    private void notifyChangeEvent(String device_address, int val) {

        LOG.log(Level.INFO, "Sending I2C protocol read event for object address ''{0}''. It''s readed status is {1}", new Object[]{device_address, val});
        //building the event
        ProtocolRead event = new ProtocolRead(this, "i2c", device_address); //IP:PORT:RELAYLINE

        if (val == 1) {
            event.addProperty("isOn", "true");
        } else {
            event.addProperty("isOn", "false");
        }

        //adding some optional information to the event
        //event.addProperty("boardIP", board.getIpAddress());
        //event.addProperty("boardPort", new Integer(board.getPort()).toString());
        //event.addProperty("relayLine", new Integer(relayLine).toString());
        //publish the event on the messaging bus
        this.notifyEvent(event);
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

    private static final Logger LOG = Logger.getLogger(i2c.class
            .getName());
}
