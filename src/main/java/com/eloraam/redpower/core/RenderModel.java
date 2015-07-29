package com.eloraam.redpower.core;

import com.eloraam.redpower.RedPowerCore;
import com.eloraam.redpower.core.RenderModel;
import com.eloraam.redpower.core.TexVertex;
import com.eloraam.redpower.core.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;

public class RenderModel {
	
	public Vector3[] vertexList;
	public TexVertex[][] texList;
	int[][][] groups;
	
	public static RenderModel loadModel(String name) {
		InputStream is = RedPowerCore.class.getResourceAsStream(name);
		RenderModel.ModelReader ml = new RenderModel.ModelReader();
		
		try {
			ml.readModel(is);
		} catch (IOException var10) {
			return null;
		}
		
		ArrayList<TexVertex[]> vtl = new ArrayList<TexVertex[]>();
		int i = 0;
		
		int lgs;
		int lgmn;
		int lgsn;
		while (i < ml.faceno.size()) {
			TexVertex[] tr = new TexVertex[4];
			for (lgs = 0; lgs < 4; ++lgs) {
				lgmn = ((Integer) ml.faceno.get(i)).intValue();
				++i;
				if (lgmn < 0) {
					throw new IllegalArgumentException("Non-Quad Face");
				}
				
				lgsn = ((Integer) ml.faceno.get(i)).intValue();
				++i;
				TexVertex t = ((TexVertex) ml.texvert.get(lgsn - 1)).copy();
				t.vtx = lgmn - 1;
				t.v = 1.0D - t.v;
				tr[lgs] = t;
			}
			lgs = ((Integer) ml.faceno.get(i)).intValue();
			++i;
			if (lgs >= 0) {
				throw new IllegalArgumentException("Non-Quad Face");
			}
			vtl.add(tr);
		}
		
		RenderModel var11 = new RenderModel();
		var11.vertexList = (Vector3[]) ml.vertex.toArray(new Vector3[0]);
		var11.texList = (TexVertex[][]) vtl.toArray(new TexVertex[0][]);
		var11.groups = new int[ml.grcnt.size()][][];
		
		for (i = 0; i < ml.grcnt.size(); ++i) {
			lgs = ((Integer) ml.grcnt.get(i)).intValue();
			var11.groups[i] = new int[lgs][];
			for (lgmn = 0; lgmn < ((Integer) ml.grcnt.get(i)).intValue(); ++lgmn) {
				var11.groups[i][lgmn] = new int[2];
			}
		}
		
		i = 0;
		lgs = -1;
		lgmn = -1;
		
		for (lgsn = -1; i < ml.groups.size(); i += 3) {
			if (lgs >= 0) {
				var11.groups[lgmn][lgsn][0] = lgs;
				var11.groups[lgmn][lgsn][1] = ((Integer) ml.groups.get(i + 2)).intValue();
			}
			
			lgmn = ((Integer) ml.groups.get(i)).intValue();
			lgsn = ((Integer) ml.groups.get(i + 1)).intValue();
			lgs = ((Integer) ml.groups.get(i + 2)).intValue();
		}
		if (lgs >= 0) {
			var11.groups[lgmn][lgsn][0] = lgs;
			var11.groups[lgmn][lgsn][1] = ml.fno;
		}
		return var11;
	}
	
	public void scale(double sf) {
		for (int i = 0; i < this.vertexList.length; ++i) {
			this.vertexList[i].multiply(sf);
		}
		
	}
	
	public static class ModelReader {
		public ArrayList<Vector3> vertex = new ArrayList<Vector3>();
		public ArrayList<Integer> faceno = new ArrayList<Integer>();
		public ArrayList<TexVertex> texvert = new ArrayList<TexVertex>();
		public ArrayList<Integer> groups = new ArrayList<Integer>();
		public ArrayList<Integer> grcnt = new ArrayList<Integer>();
		int fno = 0;
		
		private void eatline(StreamTokenizer tok) throws IOException {
			while (true) {
				if (tok.nextToken() != -1) {
					if (tok.ttype != 10) {
						continue;
					}
					
					return;
				}
				
				return;
			}
		}
		
		private void endline(StreamTokenizer tok) throws IOException {
			if (tok.nextToken() != 10) {
				throw new IllegalArgumentException("Parse error");
			}
		}
		
		private double getfloat(StreamTokenizer tok) throws IOException {
			if (tok.nextToken() != -2) {
				throw new IllegalArgumentException("Parse error");
			} else {
				return tok.nval;
			}
		}
		
		private int getint(StreamTokenizer tok) throws IOException {
			if (tok.nextToken() != -2) {
				throw new IllegalArgumentException("Parse error");
			} else {
				return (int) tok.nval;
			}
		}
		
		private void parseface(StreamTokenizer tok) throws IOException {
			while (true) {
				tok.nextToken();
				if (tok.ttype == -1 || tok.ttype == 10) {
					this.faceno.add(Integer.valueOf(-1));
					++this.fno;
					return;
				}
				
				if (tok.ttype != -2) {
					throw new IllegalArgumentException("Parse error");
				}
				
				int n1 = (int) tok.nval;
				if (tok.nextToken() != 47) {
					throw new IllegalArgumentException("Parse error");
				}
				
				int n2 = this.getint(tok);
				this.faceno.add(Integer.valueOf(n1));
				this.faceno.add(Integer.valueOf(n2));
			}
		}
		
		private void setgroup(int gr, int sub) {
			this.groups.add(Integer.valueOf(gr));
			this.groups.add(Integer.valueOf(sub));
			this.groups.add(Integer.valueOf(this.fno));
			if (this.grcnt.size() < gr) {
				throw new IllegalArgumentException("Parse error");
			} else {
				if (this.grcnt.size() == gr) {
					this.grcnt.add(Integer.valueOf(0));
				}
				
				this.grcnt.set(gr, Integer.valueOf(Math.max(
						((Integer) this.grcnt.get(gr)).intValue(), sub + 1)));
			}
		}
		
		private void parsegroup(StreamTokenizer tok) throws IOException {
			int n1 = this.getint(tok);
			int n2 = 0;
			tok.nextToken();
			if (tok.ttype == 95) {
				n2 = this.getint(tok);
				tok.nextToken();
			}
			
			this.setgroup(n1, n2);
			if (tok.ttype != 10) {
				throw new IllegalArgumentException("Parse error");
			}
		}
		
		public void readModel(InputStream fis) throws IOException {
			BufferedReader r = new BufferedReader(new InputStreamReader(fis));
			StreamTokenizer tok = new StreamTokenizer(r);
			tok.commentChar(35);
			tok.eolIsSignificant(true);
			tok.lowerCaseMode(false);
			tok.parseNumbers();
			tok.quoteChar(34);
			tok.ordinaryChar(47);
			
			while (tok.nextToken() != -1) {
				if (tok.ttype != 10) {
					if (tok.ttype != -3) {
						throw new IllegalArgumentException("Parse error");
					}
					
					if (tok.sval.equals("v")) {
						Vector3 f1 = new Vector3();
						f1.x = this.getfloat(tok);
						f1.y = this.getfloat(tok);
						f1.z = this.getfloat(tok);
						this.vertex.add(f1);
						this.endline(tok);
					} else {
						double f2;
						double f11;
						if (tok.sval.equals("vt")) {
							f11 = this.getfloat(tok);
							f2 = this.getfloat(tok);
							this.texvert.add(new TexVertex(0, f11, f2));
							this.endline(tok);
						} else if (tok.sval.equals("vtc")) {
							f11 = this.getfloat(tok);
							f2 = this.getfloat(tok);
							TexVertex tv = new TexVertex(0, f11, f2);
							tv.r = (float) this.getfloat(tok);
							tv.g = (float) this.getfloat(tok);
							tv.b = (float) this.getfloat(tok);
							this.texvert.add(tv);
							this.endline(tok);
						} else if (tok.sval.equals("f")) {
							this.parseface(tok);
						} else if (tok.sval.equals("g")) {
							this.parsegroup(tok);
						} else {
							this.eatline(tok);
						}
					}
				}
			}
			fis.close();
		}
	}
}
