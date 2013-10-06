package fr.ffessm.doris.android.tools;

import java.io.File;
import java.io.FileFilter;

public class DiskUsage implements FileFilter {
	public DiskUsage() {
	};

	private long size = 0;

	public boolean accept(File file) {
		if (file.isFile())
			size += file.length();
		else
			file.listFiles(this);
		return false;
	}

	public long getSize() {
		return size;
	}
}
