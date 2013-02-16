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

final class Nodo {

    private boolean nodoProgrammato;
    private byte numero;
    private byte numInDig;
    private byte numOutDig;
    private byte numInAna;
    private byte numOutAna;
    private int[] statoInDig;
    private short[] statoInAna;
    private short[] statoOutDig;
    private short[] statoOutAna;
    private byte[] statoInDigOld;
    private short[] statoInAnaOld;
    private short[] statoOutDigNew;
    private short[] statoOutAnaOld;
    private char[] tipoOutDig;
    private boolean lineaProgrammata;
    private boolean toBeWritten = false;
    private String autoConfiguration;
    private String objectType;

    public Nodo() {
    }

    public Nodo(byte numero, byte numIngDig, byte numOutDig, byte numIngAna, byte numOutAna) {
        Inizializza(numero, numIngDig, numOutDig, numIngAna, numOutAna);
    }

    public void Inizializza(byte numero, byte numIngDig, byte numOutDig, byte numIngAna, byte numOutAna) {
        this.numero = numero;
        this.numInDig = numIngDig;
        this.numOutDig = numOutDig;
        this.numInAna = numIngAna;
        this.numOutAna = numOutAna;

        this.statoInAna = new short[numIngAna];
        this.statoOutAna = new short[numOutAna];
        this.statoInDig = new int[numIngDig];

        this.statoOutDig = new short[numOutDig];

        this.statoInAnaOld = new short[numIngAna];
        this.statoOutAnaOld = new short[numOutAna];
        this.statoInDigOld = new byte[numIngDig];
        this.statoOutDigNew = new short[numOutDig];

        this.tipoOutDig = new char[numOutDig];
        this.lineaProgrammata = false;


    }

    public byte getNumero() {
        return numero;
    }

    public byte getNumInDig() {
        return numInDig;
    }

    public byte getNumOutDig() {
        return numOutDig;
    }

    public byte getNumInAna() {
        return numInAna;
    }

    public byte getNumOutAna() {
        return numOutAna;
    }

    public int setInAna(short val, short idx) {
        short oldval = statoInAna[idx];
        statoInAna[idx] = val;
        if (oldval != val) {
            return 1;
        } else {
            return 0;
        }

    }

    public boolean setInDig(int val, short idx) {
        int oldval = statoInDig[idx];
        statoInDig[idx] = val;
        if (oldval != val) {
            //toBeWritten=false;
            return true;
        } else {
            return false;
        }
    }

    public boolean setOutAna(short val, short idx) {
        short oldval = statoOutAna[idx];
        statoOutAna[idx] = val;
        if (oldval != val) {
            return true;
        } else {
            return false;
        }
    }

    public boolean setOutDig(int val, short idx) {
        int oldval = statoOutDig[idx];
        statoOutDig[idx] = (short) val;
        if (oldval != val) {
            toBeWritten = true;
            return true;
        } else {
            return false;
        }
    }

    public short getInAna(short idx) {
        return statoInAna[idx];
    }

    public short getOutAna(short idx) {
        return statoOutAna[idx];
    }

    public int getInDig(short idx) {
        return statoInDig[idx];
    }

    public int getOutDig(short idx) {
        return statoOutDig[idx];
    }

    public short[] getNewOutDigs() {
        return statoOutDigNew;
    }

    public boolean isInAnasChanged() {
        for (short idx = 0; idx < numInAna; idx++) {
            if (isInAnaChanged(idx)) {
                return true;
            }
        }
        return false;
    }

    public void updateInAnas() {
        for (short idx = 0; idx < numInAna; idx++) {
            updateInAna(idx);
        }
    }

    public boolean isInAnaChanged(short idx) {
        if (statoInAna[idx] != statoInAnaOld[idx]) {
            return true;
        }
        return false;
    }

    public void updateInAna(short idx) {
        statoInAnaOld[idx] = statoInAna[idx];
    }

    public boolean isOutAnasChanged() {
        for (short idx = 0; idx < numOutAna; idx++) {
            if (isOutAnaChanged(idx)) {
                return true;
            }
        }
        return false;
    }

    public void updateOutAnas() {
        for (short idx = 0; idx < numOutAna; idx++) {
            updateOutAna(idx);
        }
    }

    public boolean isOutAnaChanged(short idx) {
        if (statoOutAna[idx] != statoOutAnaOld[idx]) {
            return true;
        }
        return false;
    }

    public void updateOutAna(short idx) {
        statoOutAnaOld[idx] = statoOutAna[idx];
    }

    public boolean isInDigsChanged() {
        for (short idx = 0; idx < numInDig; idx++) {
            if (isInDigChanged(idx)) {
                return true;
            }
        }
        return false;
    }

    public void updateInDigs() {
        for (short idx = 0; idx < numInDig; idx++) {
            updateInDig(idx);
        }
    }

    public boolean isInDigChanged(short idx) {
        if (statoInDig[idx] != statoInDigOld[idx]) {
            return true;
        }
        return false;
    }

    public void updateInDig(short idx) {
        statoInDigOld[idx] = (byte) statoInDig[idx];
    }

    public boolean isOutDigsChanged() {
        for (short idx = 0; idx < numOutDig; idx++) {
            if (isOutDigChanged(idx)) {
                return true;
            }
        }
        return false;
    }

    public void updateOutDigs() {
        for (short idx = 0; idx < numOutDig; idx++) {
            updateOutDig(idx);
        }
    }

    public boolean isOutDigChanged(short idx) {
        if (statoOutDig[idx] != statoOutDigNew[idx]) {
            return true;
        }
        return false;
    }

    public void updateOutDig(short idx) {
        statoOutDig[idx] = statoOutDigNew[idx];
        if (tipoOutDig[idx] == 'T') {
            statoOutDigNew[idx] = 0;
        }
    }

    public boolean getNodoProgrammato() {
        return nodoProgrammato;
    }

    void setNodoProgrammato(boolean value) {
        nodoProgrammato = value;
    }

    public void setTipoOutDig(char tipo, short idx) {
        tipoOutDig[idx] = tipo;
    }

    public char getTipoOutDig(short idx) {
        return tipoOutDig[idx];
    }

    public String getLinesConfig() {
        String str = new String(tipoOutDig);
        return str;
    }

    public boolean isProgrammata() {
        return lineaProgrammata;
    }

    public void setProgrammata(boolean val) {
        lineaProgrammata = val;
    }

    public int getValue(char type, short idx) {
        if (type == 'i') {
            return statoInDig[idx];
        }
        if (type == 'o') {
            return statoOutDig[idx];
        }
        return -1;
    }

    public boolean needToBeWritten() {
        return toBeWritten;
    }

    public void resetToBeWritten() {
        toBeWritten = false;
        updateOutDigs();

    }

    public void setToBeWritten() {
        toBeWritten = true;
    }

    public void setOutToBeWritten(short idx, short status) {
        statoOutDigNew[idx] = status;
        toBeWritten = true;
    }

    public void setAutoconf(String autoconf) {
        this.autoConfiguration = autoconf;
    }

    public void setObjectType(String objType) {
        this.objectType = objType;
    }

    public String getObjectType() {
        return this.objectType;
    }

    public String getAutoConf() {
        return this.autoConfiguration;
    }
}
