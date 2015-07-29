package com.eloraam.redpower.core;

public class TexVertex {
	
	public int vtx;
	public double u;
	public double v;
	public float r;
	public float g;
	public float b;
	public int brtex;
	
	public TexVertex() {
	}
	
	public TexVertex(int vti, int tn, double ui, double vi) {
		this.vtx = vti;
		this.u = (tn & 15) * 0.0625D + ui * 0.0625D;
		this.v = (tn >> 4) * 0.0625D + vi * 0.0625D;
		this.r = 1.0F;
		this.g = 1.0F;
		this.b = 1.0F;
	}
	
	public TexVertex(int vti, double ui, double vi) {
		this.vtx = vti;
		this.u = ui;
		this.v = vi;
		this.r = 1.0F;
		this.g = 1.0F;
		this.b = 1.0F;
	}
	
	public void setUV(double ui, double vi) {
		this.u = ui;
		this.v = vi;
	}
	
	public TexVertex copy() {
		TexVertex tr = new TexVertex(this.vtx, this.u, this.v);
		tr.r = this.r;
		tr.g = this.g;
		tr.b = this.b;
		tr.brtex = this.brtex;
		return tr;
	}
}
