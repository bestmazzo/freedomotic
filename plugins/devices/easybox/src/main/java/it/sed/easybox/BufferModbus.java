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

class BufferModbus
	{
	private byte[] buffer;
	private short size;

	public BufferModbus(short size)
		{
		this.buffer = new byte[size];
		this.size = size;
		}

	public byte[] getBuffer()
		{
		return buffer;
		}

	public short getSize()
		{
		return size;
		}

	public byte getElement(short idx)
		{
		return buffer[idx];
		}

	public short getRegister(short idx)
		{
		short valReg = 0;

		valReg = (short)(buffer[idx] << 8 | buffer[idx+1]);
		return valReg;
		}

	public byte getCommand()
		{
		return buffer[1];
		}

	public byte getNodo()
		{
		return buffer[0];
		}

	public byte getNumByte()
		{
		return buffer[2];
		}
	}