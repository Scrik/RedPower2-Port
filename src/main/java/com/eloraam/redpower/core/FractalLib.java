package com.eloraam.redpower.core;

import com.eloraam.redpower.core.Vector3;

public class FractalLib {
	
	public static long hash64shift(long key) {
		key = ~key + (key << 21);
		key ^= key >>> 24;
		key = key + (key << 3) + (key << 8);
		key ^= key >>> 14;
		key = key + (key << 2) + (key << 4);
		key ^= key >>> 28;
		key += key << 31;
		return key;
	}
	
	public static double hashFloat(long key) {
		long f = hash64shift(key);
		return Double
				.longBitsToDouble(4607182418800017408L | f & 4503599627370495L) - 1.0D;
	}
	
	public static double noise1D(long seed, double pos, float lac, int octave) {
		double tr = 0.5D;
		double scale = (double) (1 << octave);
		
		for (int i = 0; i < octave; ++i) {
			double p = pos * scale;
			long pint = (long) Math.floor(p);
			double m1 = hashFloat(seed + pint);
			double m2 = hashFloat(seed + pint + 1L);
			p -= Math.floor(p);
			double v = 0.5D + 0.5D * Math.cos(3.141592653589793D * p);
			v = v * m1 + (1.0D - v) * m2;
			tr = (double) (1.0F - lac) * tr + (double) lac * v;
			scale *= 0.5D;
		}
		
		return tr;
	}
	
	public static double perturbOld(long seed, float pos, float lac, int octave) {
		double tr = 0.0D;
		double mscale = 1.0D;
		double scale = 1.0D;
		
		for (int i = 0; i < octave; ++i) {
			long v = (long) Math.floor((double) pos * scale);
			long p = hash64shift(seed + v);
			double mag = Double
					.longBitsToDouble(4607182418800017408L | p & 4503599627370495L) - 1.0D;
			tr += mscale * mag
					* Math.sin(6.283185307179586D * (double) pos * scale);
			scale *= 2.0D;
			mscale *= (double) lac;
		}
		
		return tr;
	}
	
	public static void fillVector(Vector3 v, Vector3 org, Vector3 dest,
			float pos, long seed) {
		double window = 4.0D * Math.sin(3.141592653589793D * (double) pos);
		v.x = org.x + (double) (pos * pos) * dest.x + window
				* perturbOld(seed, pos, 0.7F, 5);
		v.y = org.y + (double) pos * dest.y + window
				* perturbOld(seed + 1L, pos, 0.7F, 5);
		v.z = org.z + (double) (pos * pos) * dest.z + window
				* perturbOld(seed + 2L, pos, 0.7F, 5);
	}
	
	public static int mdist(Vector3 a, Vector3 b) {
		return (int) (Math.abs(Math.floor(a.x) - Math.floor(b.x))
				+ Math.abs(Math.floor(a.y) - Math.floor(b.y)) + Math.abs(Math
				.floor(a.z) - Math.floor(b.z)));
	}
	
	public static class BlockRay {
		
		private Vector3 p1;
		private Vector3 p2;
		private Vector3 dv;
		public Vector3 enter;
		public Vector3 exit;
		public int xp;
		public int yp;
		public int zp;
		public int dir;
		public int face;
		
		public BlockRay(Vector3 s, Vector3 d) {
			this.p1 = new Vector3(s);
			this.p2 = new Vector3(d);
			this.dv = new Vector3(d);
			this.dv.subtract(s);
			this.exit = new Vector3(s);
			this.enter = new Vector3();
			this.xp = (int) Math.floor(s.x);
			this.yp = (int) Math.floor(s.y);
			this.zp = (int) Math.floor(s.z);
			this.dir = 0;
			this.dir |= d.x > s.x ? 4 : 0;
			this.dir |= d.y > s.y ? 1 : 0;
			this.dir |= d.z > s.z ? 2 : 0;
		}
		
		public void set(Vector3 s, Vector3 d) {
			this.p1.set(s);
			this.p2.set(d);
			this.dv.set(d);
			this.dv.subtract(s);
			this.exit.set(s);
			this.xp = (int) Math.floor(s.x);
			this.yp = (int) Math.floor(s.y);
			this.zp = (int) Math.floor(s.z);
			this.dir = 0;
			this.dir |= d.x > s.x ? 4 : 0;
			this.dir |= d.y > s.y ? 1 : 0;
			this.dir |= d.z > s.z ? 2 : 0;
		}
		
		boolean step() {
			double bp = 1.0D;
			int sd = -1;
			double d;
			if (this.dv.x != 0.0D) {
				int x = this.xp;
				if ((this.dir & 4) > 0) {
					++x;
				}
				
				d = ((double) x - this.p1.x) / this.dv.x;
				if (d >= 0.0D && d <= bp) {
					bp = d;
					sd = (this.dir & 4) > 0 ? 4 : 5;
				}
			}
			
			if (this.dv.y != 0.0D) {
				int y = this.yp;
				if ((this.dir & 1) > 0) {
					++y;
				}
				
				d = ((double) y - this.p1.y) / this.dv.y;
				if (d >= 0.0D && d <= bp) {
					bp = d;
					sd = (this.dir & 1) > 0 ? 0 : 1;
				}
			}
			
			if (this.dv.z != 0.0D) {
				int z = this.zp;
				if ((this.dir & 2) > 0) {
					++z;
				}
				
				d = ((double) z - this.p1.z) / this.dv.z;
				if (d >= 0.0D && d <= bp) {
					bp = d;
					sd = (this.dir & 2) > 0 ? 2 : 3;
				}
			}
			
			this.face = sd;
			switch (sd) {
				case 0:
					++this.yp;
					break;
				case 1:
					--this.yp;
					break;
				case 2:
					++this.zp;
					break;
				case 3:
					--this.zp;
					break;
				case 4:
					++this.xp;
					break;
				case 5:
					--this.xp;
			}
			
			this.enter.set(this.exit);
			this.exit.set(this.dv);
			this.exit.multiply(bp);
			this.exit.add(this.p1);
			return bp >= 1.0D;
		}
	}
	
	public static class BlockSnake {
		
		int fep = -1;
		FractalLib.BlockRay ray;
		Vector3 org;
		Vector3 dest;
		Vector3 fracs;
		Vector3 frace;
		long seed;
		
		public BlockSnake(Vector3 o, Vector3 d, long s) {
			this.org = new Vector3(o);
			this.dest = new Vector3(d);
			this.fracs = new Vector3(o);
			this.frace = new Vector3();
			this.seed = s;
			FractalLib.fillVector(this.frace, this.org, this.dest, 0.125F, this.seed);
			this.ray = new FractalLib.BlockRay(this.fracs, this.frace);
		}
		
		public boolean iterate() {
			if (this.fep == -1) {
				++this.fep;
				return true;
			} else if (!this.ray.step()) {
				return true;
			} else if (this.fep == 8) {
				return false;
			} else {
				this.fracs.set(this.frace);
				FractalLib.fillVector(this.frace, this.org, this.dest,
						(float) this.fep / 8.0F, this.seed);
				this.ray.set(this.fracs, this.frace);
				++this.fep;
				return true;
			}
		}
		
		public Vector3 get() {
			return new Vector3((double) this.ray.xp, (double) this.ray.yp,
					(double) this.ray.zp);
		}
	}
}
