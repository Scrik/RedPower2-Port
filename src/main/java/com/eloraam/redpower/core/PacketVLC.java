package com.eloraam.redpower.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

public abstract class PacketVLC extends Packet {
	
	protected int cnt1 = 0;
	protected int size = 0;
	public ByteArrayOutputStream headout = null;
	public ByteArrayOutputStream bodyout = null;
	public ByteArrayInputStream bodyin = null;
	
	public PacketVLC() {
		this.headout = new ByteArrayOutputStream();
		this.bodyout = new ByteArrayOutputStream();
	}
	
	public PacketVLC(byte[] data) {
		this.bodyin = new ByteArrayInputStream(data);
	}
	
	public byte[] toByteArray() {
		try {
			this.bodyout.writeTo(this.headout);
		} catch (IOException var2) {
			;
		}
		return this.headout.toByteArray();
	}
	
	@Override
	public void writePacketData(PacketBuffer out) throws IOException {
		out.readBytes(this.headout.toByteArray()); //out.writeBytes
		out.readBytes(this.bodyout.toByteArray()); //out.writeBytes
		//this.headout.writeTo(out);
		//this.bodyout.writeTo(out);
	}
	
	public void addByte(int b) {
		this.bodyout.write(b);
	}
	
	public void addUVLC(long l) {
		writeUVLC(this.bodyout, l);
	}
	
	public void addVLC(long l) {
		writeVLC(this.bodyout, l);
	}
	
	public void addByteArray(byte[] ba) {
		this.addUVLC((long) ba.length);
		this.bodyout.write(ba, 0, ba.length);
	}
	
	public int getByte() throws IOException {
		int i = this.bodyin.read();
		if (i < 0) {
			throw new IOException("Not enough data");
		} else {
			return i;
		}
	}
	
	public long getUVLC() throws IOException {
		return this.readUVLC(this.bodyin);
	}
	
	public long getVLC() throws IOException {
		return this.readVLC(this.bodyin);
	}
	
	public byte[] getByteArray() throws IOException {
		int ln = (int) this.getUVLC();
		byte[] tr = new byte[ln];
		this.bodyin.read(tr, 0, ln);
		return tr;
	}
	
	protected static void writeVLC(ByteArrayOutputStream os, long l) {
		if (l >= 0L) {
			l <<= 1;
		} else {
			l = -l << 1 | 1L;
		}
		
		writeUVLC(os, l);
	}
	
	protected static void writeUVLC(ByteArrayOutputStream os, long l) {
		do {
			int i = (int) (l & 127L);
			l >>>= 7;
			if (l != 0L) {
				i |= 128;
			}
			
			os.write(i);
		} while (l != 0L);
		
	}
	
	protected long readUVLC(InputStream in) throws IOException {
		long tr = 0L;
		int sc = 0;
		
		do {
			int i = in.read();
			if (i < 0) {
				throw new IOException("Not enough data");
			}
			
			++this.cnt1;
			tr |= (long) ((i & 127) << sc);
			if ((i & 128) == 0) {
				return tr;
			}
			
			sc += 7;
		} while (sc <= 64);
		
		throw new IOException("Bad VLC");
	}
	
	protected long readVLC(InputStream in) throws IOException {
		long tr = this.readUVLC(in);
		if ((tr & 1L) == 0L) {
			tr >>>= 1;
		} else {
			tr = -(tr >>> 1);
		}
		
		return tr;
	}
	
	public int getPacketSize() {
		return this.size;
	}
	
	protected void fixLocalPacket() {
		this.bodyin = new ByteArrayInputStream(this.bodyout.toByteArray());
	}
}
