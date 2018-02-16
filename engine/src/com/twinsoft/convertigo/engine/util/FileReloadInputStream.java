package com.twinsoft.convertigo.engine.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class FileReloadInputStream extends InputStream {
	
	Path path;
	InputStream is;
	WatchService ws;
	WatchKey wk;
	boolean close = false;
	
	public FileReloadInputStream(File file) throws IOException {
		path = file.toPath();
		is = Files.newInputStream(path, StandardOpenOption.READ);
		
		Thread th = new Thread(() -> {
			try {
				Path ppath = path.getParent();
				ws = ppath.getFileSystem().newWatchService();
				this.wk = ppath.register(ws, StandardWatchEventKinds.ENTRY_CREATE);
				while (true) {
					WatchKey wk = ws.take();
					for (final WatchEvent<?> event: wk.pollEvents()) {
						Path ctx = (Path) event.context();
						
						if (path.endsWith(ctx)) {
							synchronized (path) {
								is.close();
								is = Files.newInputStream(path, StandardOpenOption.READ);
							}
						}
					}
					
	                if (!wk.reset() || close) {
	                	wk.cancel();
	                	ws.close();
	                    break;
	                }
				}
			} catch (Exception e) {
				
			}
		});
		th.setName("FileReloadInputStream:" + file.getName());
		th.setDaemon(true);
		th.start();
	}

	@Override
	public int read() throws IOException {
		synchronized (path) {
			return is.read();
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		synchronized (path) {
			return is.read(b);
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		synchronized (path) {
			return is.read(b, off, len);
		}
	}

	@Override
	public long skip(long n) throws IOException {
		synchronized (path) {
			return is.skip(n);
		}
	}

	@Override
	public int available() throws IOException {
		synchronized (path) {
			return is.available();
		}
	}

	@Override
	public void close() throws IOException {
		synchronized (path) {
			close = true;
			try {
				ws.close();
			} catch (Exception e) {
			}
			try {
				wk.cancel();
			} catch (Exception e) {
			}
			is.close();
		}
	}

	@Override
	public synchronized void mark(int readlimit) {
		synchronized (path) {
			is.mark(readlimit);
		}
	}

	@Override
	public synchronized void reset() throws IOException {
		synchronized (path) {
			is.reset();
		}
	}

	@Override
	public boolean markSupported() {
		synchronized (path) {
			return is.markSupported();
		}
	}	
}
