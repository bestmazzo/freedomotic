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

public class ModbusAction {

    private boolean write = false;
    private short address;
    private short size = 1;
    private short[] data = null;
    private boolean[] mask = null;
    private byte node;
    /*
     Most used for write actions
     */

    public ModbusAction(boolean write, byte node, short address, short size, short[] data) {
        this.write = write;
        this.address = address;
        this.size = size;
        this.data = data;
        this.node = node;
    }
    /*Read or write for size bytes. If write is true, data is extracted at dispatch time */

    public ModbusAction(boolean write, byte node, short address, short size) {
        this.write = write;
        this.address = address;
        this.size = size;
        this.node = node;
    }
    /*
     Read from address for size bytes
     */

    public ModbusAction(byte node, short address, short size) {
        this.address = address;
        this.size = size;
        this.node = node;
    }

    /*
     Read a single byre from address
     */
    public ModbusAction(byte node, short address) {
        this.address = address;
        this.node = node;
    }

    public boolean isWrite() {
        return this.write;
    }

    public short getAddress() {
        return this.address;
    }

    public short getSize() {
        return this.size;
    }

    public short[] getData() {
        return this.data;
    }

    public byte getNode() {
        return this.node;
    }

    public boolean setData(short[] data) {
        if (!this.isWrite() || this.data == null) {
            this.data = new short[data.length];
            System.arraycopy(data, 0, this.data, 0, data.length);
            return true;
        }
        return false;
    }

    public boolean equals(ModbusAction other) {
        if (other.isWrite() == isWrite()) {
            if (other.getNode() == getNode()) {
                if (other.getAddress() == getAddress()) {
                    if (other.getSize() == getSize()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String ws = write ?  "W":"R";
        String output = "(" + ws + ",N:" + node + ",Reg:" + address + ",S:" + size + ",D:";
        if (data != null) {
            for (short b : data) {
                output += b + ".";
            }
        }
        return output + ")";
    }
}
