package com.eloraam.redpower.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import com.eloraam.redpower.core.TagFile;

public class TagFile {
	
	TreeMap<String, Object> contents = new TreeMap<String, Object>();
	TreeMap<String, String> comments = new TreeMap<String, String>();
	String filecomment = "";
	
	public void addTag(String name, Object tag) {
		int idx = 0;
		TreeMap<String, Object> sub = this.contents;
		
		while (true) {
			int nid = name.indexOf(46, idx);
			String p;
			if (nid < 0) {
				p = name.substring(idx);
				if (p.equals("")) {
					throw new IllegalArgumentException("Empty key name");
				} else {
					sub.put(p, tag);
					return;
				}
			}
			
			p = name.substring(idx, nid);
			idx = nid + 1;
			if (p.equals("")) {
				throw new IllegalArgumentException("Empty key name");
			}
			
			Object ob = sub.get(p);
			if (ob == null) {
				TreeMap<String, Object> tmp = new TreeMap<String, Object>();
				sub.put(p, tmp);
				sub = tmp;
			} else {
				if (!(ob instanceof TreeMap)) {
					throw new IllegalArgumentException("Key not a dictionary");
				}
				
				sub = (TreeMap<String, Object>) ob;
			}
		}
	}
	
	public Object getTag(String name) {
		int idx = 0;
		TreeMap<String, Object> sub = this.contents;
		
		while (true) {
			int nid = name.indexOf(46, idx);
			String p;
			if (nid < 0) {
				p = name.substring(idx);
				return sub.get(p);
			}
			
			p = name.substring(idx, nid);
			idx = nid + 1;
			Object ob = sub.get(p);
			if (!(ob instanceof TreeMap)) {
				return null;
			}
			
			sub = (TreeMap<String, Object>) ob;
		}
	}
	
	public Object removeTag(String name) {
		int idx = 0;
		TreeMap<String, Object> sub = this.contents;
		
		while (true) {
			int nid = name.indexOf(46, idx);
			String p;
			if (nid < 0) {
				p = name.substring(idx);
				return sub.remove(p);
			}
			
			p = name.substring(idx, nid);
			idx = nid + 1;
			Object ob = sub.get(p);
			if (!(ob instanceof TreeMap)) {
				return null;
			}
			
			sub = (TreeMap<String, Object>) ob;
		}
	}
	
	public void commentTag(String k, String v) {
		this.comments.put(k, v);
	}
	
	public void commentFile(String cmt) {
		this.filecomment = cmt;
	}
	
	public void addString(String name, String value) {
		this.addTag(name, value);
	}
	
	public void addInt(String name, int value) {
		this.addTag(name, Integer.valueOf(value));
	}
	
	public String getString(String name) {
		Object ob = this.getTag(name);
		return !(ob instanceof String) ? null : (String) ob;
	}
	
	public String getString(String name, String def) {
		Object ob = this.getTag(name);
		return !(ob instanceof String) ? def : (String) ob;
	}
	
	public int getInt(String name) {
		Object ob = this.getTag(name);
		return !(ob instanceof Integer) ? 0 : ((Integer) ob).intValue();
	}
	
	public int getInt(String name, int def) {
		Object ob = this.getTag(name);
		return !(ob instanceof Integer) ? def : ((Integer) ob).intValue();
	}
	
	private void writecomment(PrintStream ps, String indent, String cmt) {
		if (cmt != null) {
			String[] arr$ = cmt.split("\n");
			int len$ = arr$.length;
			
			for (int i$ = 0; i$ < len$; ++i$) {
				String s = arr$[i$];
				ps.printf("%s# %s\n", new Object[] { indent, s });
			}
			
		}
	}
	
	private String collapsedtag(TreeMap<String, Object> tag, String key, String ft) {
		String cn = key;
		
		String k;
		for (Object ob = tag.get(key); this.comments.get(ft) == null; ft = ft
				+ "." + k) {
			if (ob instanceof String) {
				return cn + "=\"" + ((String) ob).replace("\"", "\\\"") + "\"";
			}
			
			if (ob instanceof Integer) {
				return cn + "=" + ob;
			}
			
			tag = (TreeMap<String, Object>) ob;
			if (tag.size() != 1) {
				return null;
			}
			
			k = (String) tag.firstKey();
			cn = cn + "." + k;
			ob = tag.get(k);
		}
		
		return null;
	}
	
	private void savetag(PrintStream ps, TreeMap<String, Object> tag, String name, String indent) throws IOException {
		Iterator<String> i$ = tag.keySet().iterator();
		
		while (i$.hasNext()) {
			String k = (String) i$.next();
			String ft = name != null ? name + "." + k : k;
			this.writecomment(ps, indent, (String) this.comments.get(ft));
			Object ob = tag.get(k);
			if (ob instanceof String) {
				ps.printf(
						"%s%s=\"%s\"\n",
						new Object[] { indent, k, ((String) ob).replace("\"",
								"\\\"") });
			} else if (ob instanceof Integer) {
				ps.printf("%s%s=%d\n", new Object[] { indent, k, (Integer) ob });
			} else if (ob instanceof TreeMap) {
				String ct = this.collapsedtag(tag, k, ft);
				if (ct != null) {
					ps.printf("%s%s\n", new Object[] { indent, ct });
				} else {
					ps.printf("%s%s {\n", new Object[] { indent, k });
					this.savetag(ps, (TreeMap<String, Object>) ob, ft, indent + "    ");
					ps.printf("%s}\n\n", new Object[] { indent });
				}
			}
		}
		
	}
	
	public void saveFile(File file) {
		try {
			FileOutputStream e = new FileOutputStream(file);
			PrintStream ps = new PrintStream(e);
			this.writecomment(ps, "", this.filecomment);
			this.savetag(ps, this.contents, (String) null, "");
			ps.close();
		} catch (IOException var4) {
			var4.printStackTrace();
		}
		
	}
	
	private static void readtag(TreeMap<String, Object> tag, StreamTokenizer tok) throws IOException {
		label61: while (true) {
			if (tok.nextToken() != -1 && tok.ttype != 125) {
				if (tok.ttype == 10) {
					continue;
				}
				
				if (tok.ttype != -3) {
					throw new IllegalArgumentException("Parse error");
				}
				
				String key = tok.sval;
				TreeMap<String, Object> ltag = tag;
				
				while (true) {
					TreeMap<String, Object> ttag;
					Object obtag;
					switch (tok.nextToken()) {
						case 46:
							obtag = ltag.get(key);
							if (!(obtag instanceof TreeMap)) {
								ttag = new TreeMap<String, Object>();
								ltag.put(key, ttag);
								ltag = ttag;
							} else {
								ltag = (TreeMap<String, Object>) obtag;
							}
							
							tok.nextToken();
							if (tok.ttype != -3) {
								throw new IllegalArgumentException(
										"Parse error");
							}
							
							key = tok.sval;
							break;
						case 61:
							tok.nextToken();
							if (tok.ttype == -2) {
								ltag.put(key, Integer.valueOf((int) tok.nval));
							} else {
								if (tok.ttype != 34) {
									throw new IllegalArgumentException(
											"Parse error");
								}
								
								ltag.put(key, tok.sval);
							}
							
							tok.nextToken();
							if (tok.ttype == 10) {
								continue label61;
							}
							
							throw new IllegalArgumentException("Parse error");
						case 123:
							obtag = ltag.get(key);
							if (!(obtag instanceof TreeMap)) {
								ttag = new TreeMap<String, Object>();
								ltag.put(key, ttag);
								ltag = ttag;
							} else {
								ltag = (TreeMap<String, Object>) obtag;
							}
							
							readtag(ltag, tok);
							tok.nextToken();
							if (tok.ttype == 10) {
								continue label61;
							}
							
							throw new IllegalArgumentException("Parse error");
						default:
							throw new IllegalArgumentException("Parse error");
					}
				}
			}
			
			return;
		}
	}
	
	public static TagFile loadFile(File file) {
		TagFile tr = new TagFile();
		
		try {
			FileInputStream e = new FileInputStream(file);
			tr.readStream(e);
		} catch (IOException var3) {
			var3.printStackTrace();
		}
		
		return tr;
	}
	
	public void readFile(File file) {
		try {
			FileInputStream e = new FileInputStream(file);
			this.readStream(e);
		} catch (IOException var3) {
			var3.printStackTrace();
		}
		
	}
	
	public void readStream(InputStream fis) {
		try {
			BufferedReader e = new BufferedReader(new InputStreamReader(fis));
			StreamTokenizer tok = new StreamTokenizer(e);
			tok.commentChar(35);
			tok.eolIsSignificant(true);
			tok.lowerCaseMode(false);
			tok.parseNumbers();
			tok.quoteChar(34);
			tok.ordinaryChar(61);
			tok.ordinaryChar(123);
			tok.ordinaryChar(125);
			tok.ordinaryChar(46);
			readtag(this.contents, tok);
			fis.close();
		} catch (IOException var4) {
			var4.printStackTrace();
		}
		
	}
	
	TagFile.Query query(String pattern) {
		return new TagFile.Query(pattern);
	}
	
	public class Query implements Iterable<Object> {
		
		String[] pattern;
		
		private Query(String pat) {
			this.pattern = pat.split("\\.");
		}
		
		@Override
		public Iterator<Object> iterator() {
			return new TagFile.Query.QueryIterator();
		}
		
		public class QueryIterator implements Iterator<Object> {
			
			ArrayList<QueryEntry> path;
			String lastentry;
			
			private QueryIterator() {
				this.path = new ArrayList<QueryEntry>();
				TreeMap<String, Object> p = TagFile.this.contents;
				Object path = null;
				if (!this.step0(0, TagFile.this.contents, "")) {
					this.step();
				}
				
			}
			
			private void step() {
				while (true) {
					if (this.path != null) {
						if (!this.step1()) {
							continue;
						}
						
						return;
					}
					
					return;
				}
			}
			
			private boolean step1() {
				TagFile.QueryEntry qe = (TagFile.QueryEntry) this.path
						.get(this.path.size() - 1);
				if (!qe.iter.hasNext()) {
					this.path.remove(this.path.size() - 1);
					if (this.path.size() == 0) {
						this.path = null;
					}
					
					return false;
				} else {
					String str = (String) qe.iter.next();
					String sp = qe.path.equals("") ? str : qe.path + "." + str;
					if (qe.lvl == Query.this.pattern.length - 1) {
						this.lastentry = sp;
						return true;
					} else {
						Object ob = qe.tag.get(str);
						return !(ob instanceof TreeMap) ? false : this.step0(
								qe.lvl + 1, (TreeMap<String, Object>) ob, sp);
					}
				}
			}
			
			private boolean step0(int lvl0, TreeMap<String, Object> p, String sp) {
				int lvl = lvl0;
				
				while (true) {
					if (lvl < Query.this.pattern.length) {
						if (Query.this.pattern[lvl].equals("%")) {
							TagFile.QueryEntry var6 = new TagFile.QueryEntry();
							var6.path = sp;
							var6.tag = p;
							var6.lvl = lvl;
							var6.iter = p.keySet().iterator();
							this.path.add(var6);
							return false;
						}
						
						Object ob = p.get(Query.this.pattern[lvl]);
						if (sp.equals("")) {
							sp = Query.this.pattern[lvl];
						} else {
							sp = sp + "." + Query.this.pattern[lvl];
						}
						
						if (ob instanceof TreeMap) {
							p = (TreeMap<String, Object>) ob;
							++lvl;
							continue;
						}
						
						if (lvl == Query.this.pattern.length - 1) {
							this.lastentry = sp;
							return true;
						}
					}
					
					this.path.remove(this.path.size() - 1);
					if (this.path.size() == 0) {
						this.path = null;
					}
					
					return false;
				}
			}
			
			@Override
			public boolean hasNext() {
				return this.path != null;
			}
			
			@Override
			public String next() {
				String tr = this.lastentry;
				this.step();
				return tr;
			}
			
			@Override
			public void remove() {
			}
		}
	}
	
	private static class QueryEntry {
		public TreeMap<String, Object> tag;
		public Iterator<String> iter;
		public String path;
		int lvl;
		
		private QueryEntry() {
		}
	}
}
