package com.eloraam.redpower.core;

import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.Vector3;

import java.util.Formatter;
import java.util.Locale;

public class Quat {
	
	public double x;
	public double y;
	public double z;
	public double s;
	public static final double SQRT2 = Math.sqrt(2.0D);
	
	public Quat() {
		this.s = 1.0D;
		this.x = 0.0D;
		this.y = 0.0D;
		this.z = 0.0D;
	}
	
	public Quat(Quat q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.s = q.s;
	}
	
	public Quat(double si, double xi, double yi, double zi) {
		this.x = xi;
		this.y = yi;
		this.z = zi;
		this.s = si;
	}
	
	public void set(Quat q) {
		this.x = q.x;
		this.y = q.y;
		this.z = q.z;
		this.s = q.s;
	}
	
	public static Quat aroundAxis(double xi, double yi, double zi, double a) {
		a *= 0.5D;
		double sn = Math.sin(a);
		return new Quat(Math.cos(a), xi * sn, yi * sn, zi * sn);
	}
	
	public void multiply(Quat q) {
		double ts = this.s * q.s - this.x * q.x - this.y * q.y - this.z * q.z;
		double tx = this.s * q.x + this.x * q.s - this.y * q.z + this.z * q.y;
		double ty = this.s * q.y + this.x * q.z + this.y * q.s - this.z * q.x;
		double tz = this.s * q.z - this.x * q.y + this.y * q.x + this.z * q.s;
		this.s = ts;
		this.x = tx;
		this.y = ty;
		this.z = tz;
	}
	
	public void rightMultiply(Quat q) {
		double ts = this.s * q.s - this.x * q.x - this.y * q.y - this.z * q.z;
		double tx = this.s * q.x + this.x * q.s + this.y * q.z - this.z * q.y;
		double ty = this.s * q.y - this.x * q.z + this.y * q.s + this.z * q.x;
		double tz = this.s * q.z + this.x * q.y - this.y * q.x + this.z * q.s;
		this.s = ts;
		this.x = tx;
		this.y = ty;
		this.z = tz;
	}
	
	public double mag() {
		return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.s * this.s);
	}
	
	public void normalize() {
		double d = this.mag();
		if (d != 0.0D) {
			d = 1.0D / d;
			this.x *= d;
			this.y *= d;
			this.z *= d;
			this.s *= d;
		}
	}
	
	public void rotate(Vector3 v) {
		double ts = -this.x * v.x - this.y * v.y - this.z * v.z;
		double tx = this.s * v.x + this.y * v.z - this.z * v.y;
		double ty = this.s * v.y - this.x * v.z + this.z * v.x;
		double tz = this.s * v.z + this.x * v.y - this.y * v.x;
		v.x = tx * this.s - ts * this.x - ty * this.z + tz * this.y;
		v.y = ty * this.s - ts * this.y + tx * this.z - tz * this.x;
		v.z = tz * this.s - ts * this.z - tx * this.y + ty * this.x;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter(sb, Locale.US);
		fmt.format("Quaternion:\n", new Object[0]);
		fmt.format(
				"  < %f %f %f %f >\n",
				new Object[] { Double.valueOf(this.s), Double.valueOf(this.x), Double
						.valueOf(this.y), Double.valueOf(this.z) });
		fmt.close();
		return sb.toString();
	}
	
}
