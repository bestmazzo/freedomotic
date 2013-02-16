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

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

interface QueueStrategy {
    /* choose a action dto dispatch and removes it from the queue*/

    ModbusAction pick(ModbusGateway trp);

    void init(ModbusGateway trp);
}

class FIFOQueue implements QueueStrategy {

    @Override
    public ModbusAction pick(ModbusGateway trp) {
        List<ModbusAction> queue = trp.getGlobalQueue();
        return queue.get(0);
    }

    @Override
    public void init(ModbusGateway trp) {
        trp.setQueues(new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>());

    }
}

class RandomQueue implements QueueStrategy {

    private Random generator = new Random();

    @Override
    public ModbusAction pick(ModbusGateway trp) {
        List<ModbusAction> queue = trp.getGlobalQueue();
        return queue.get(generator.nextInt(queue.size()));
    }

    @Override
    public void init(ModbusGateway trp) {
        trp.setQueues(new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>());

    }
}

class WriteFirstQueue implements QueueStrategy {

    @Override
    public ModbusAction pick(ModbusGateway trp) {

        if (!trp.getWriteQueue().isEmpty()) {
            return trp.getWriteQueue().get(0);
        } else if (!trp.getGlobalQueue().isEmpty()) {
            return trp.getGlobalQueue().get(0);
        }
        return null;
    }

    @Override
    public void init(ModbusGateway trp) {
        trp.setQueues(new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>(), new LinkedList<ModbusAction>());

    }
}
