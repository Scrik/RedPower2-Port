package com.eloraam.redpower.core;

import com.eloraam.redpower.core.Quat;
import com.eloraam.redpower.core.Vector3;

import java.util.Formatter;
import java.util.Locale;

public class Matrix3 {
	
	public double xx;
	public double xy;
	public double xz;
	public double yx;
	public double yy;
	public double yz;
	public double zx;
	public double zy;
	public double zz;
	
	public Matrix3() {
	}
	
	public Matrix3(Quat q) {
		this.set(q);
	}
	
	public void set(Quat q) {
		this.xx = q.s * q.s + q.x * q.x - q.z * q.z - q.y * q.y;
		this.xy = 2.0D * (q.s * q.z + q.x * q.y);
		this.xz = 2.0D * (q.x * q.z - q.s * q.y);
		this.yx = 2.0D * (q.x * q.y - q.s * q.z);
		this.yy = q.s * q.s + q.y * q.y - q.z * q.z - q.x * q.x;
		this.yz = 2.0D * (q.s * q.x + q.y * q.z);
		this.zx = 2.0D * (q.s * q.y + q.x * q.z);
		this.zy = 2.0D * (q.y * q.z - q.s * q.x);
		this.zz = q.s * q.s + q.z * q.z - q.y * q.y - q.x * q.x;
	}
	
	public void set(Matrix3 m) {
		this.xx = m.xx;
		this.xy = m.xy;
		this.xz = m.xz;
		this.yx = m.yx;
		this.yy = m.yy;
		this.yz = m.yz;
		this.zx = m.zx;
		this.zy = m.zy;
		this.zz = m.zz;
	}
	
	public Matrix3 multiply(Matrix3 m) {
		Matrix3 tr = new Matrix3();
		tr.xx = this.xx * m.xx + this.xy * m.yx + this.xz * m.zx;
		tr.xy = this.xx * m.xy + this.xy * m.yy + this.xz * m.zy;
		tr.xz = this.xx * m.xz + this.xy * m.yz + this.xz * m.zz;
		tr.yx = this.yx * m.xx + this.yy * m.yx + this.yz * m.zx;
		tr.yy = this.yx * m.xy + this.yy * m.yy + this.yz * m.zy;
		tr.yz = this.yx * m.xz + this.yy * m.yz + this.yz * m.zz;
		tr.zx = this.zx * m.xx + this.zy * m.yx + this.zz * m.zx;
		tr.zy = this.zx * m.xy + this.zy * m.yy + this.zz * m.zy;
		tr.zz = this.zx * m.xz + this.zy * m.yz + this.zz * m.zz;
		return tr;
	}
	
	public static Matrix3 getRotY(double angle) {
		double c = Math.cos(angle);
		double s = Math.sin(angle);
		Matrix3 tr = new Matrix3();
		tr.xx = c;
		tr.xy = 0.0D;
		tr.xz = s;
		tr.yx = 0.0D;
		tr.yy = 1.0D;
		tr.yz = 0.0D;
		tr.zx = -s;
		tr.zy = 0.0D;
		tr.zz = c;
		return tr;
	}
	
	public Vector3 getBasisVector(int n) {
		return n == 0 ? new Vector3(this.xx, this.xy, this.xz) : (n == 1 ? new Vector3(
				this.yx, this.yy, this.yz) : new Vector3(this.zx, this.zy,
				this.zz));
	}
	
	public double det() {
		return this.xx * (this.yy * this.zz - this.yz * this.zy) - this.xy
				* (this.yx * this.zz - this.yz * this.zx) + this.xz
				* (this.yx * this.zy - this.yy * this.zx);
	}
	
	public void rotate(Vector3 v) {
		double tx = this.xx * v.x + this.yx * v.y + this.zx * v.z;
		double ty = this.xy * v.x + this.yy * v.y + this.zy * v.z;
		double tz = this.xz * v.x + this.yz * v.y + this.zz * v.z;
		v.x = tx;
		v.y = ty;
		v.z = tz;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter fmt = new Formatter(sb, Locale.US);
		fmt.format("Matrix:\n", new Object[0]);
		fmt.format(
				"  < %f %f %f >\n",
				new Object[] { Double.valueOf(this.xx), Double.valueOf(this.xy), Double
						.valueOf(this.xz) });
		fmt.format(
				"  < %f %f %f >\n",
				new Object[] { Double.valueOf(this.yx), Double.valueOf(this.yy), Double
						.valueOf(this.yz) });
		fmt.format(
				"  < %f %f %f >\n",
				new Object[] { Double.valueOf(this.zx), Double.valueOf(this.zy), Double
						.valueOf(this.zz) });
		return sb.toString();
	}
}
