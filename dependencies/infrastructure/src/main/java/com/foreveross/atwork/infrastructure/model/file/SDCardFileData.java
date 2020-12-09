package com.foreveross.atwork.infrastructure.model.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * SDCard文件数据结构
 */
public class SDCardFileData {
	public ArrayList<FileInfo> fileInfos;
	public ArrayList<Integer> selectedId;
	public String path;
	public boolean searchingTag = false;

	public SDCardFileData(ArrayList<FileInfo> fileInfos,
                          ArrayList<Integer> selectedId, String path) {
		if (fileInfos == null)
			this.fileInfos = new ArrayList<FileInfo>();
		else
			this.fileInfos = fileInfos;
		if (selectedId == null)
			this.selectedId = new ArrayList<Integer>();
		else
			this.selectedId = selectedId;
		if (path == null)
			this.path = "/sdcard";
		else
			this.path = path;

		File[] files = new File(path).listFiles();
		if(files == null) {
			return;
		}
		for (File file : files) {
			if(file != null && file.getName().startsWith(".")) {
				continue;
			}
			this.fileInfos.add(new FileInfo(file.getPath()));
		}
		Collections.sort(fileInfos);
	}

	public static class FileInfo implements Comparable<FileInfo> {

		public String name = null;
		public int size = 0;
		public String path = null;
		public String date = null;
		public boolean isDirectory = false;
		public boolean selected = false;
        public FileData.FileType type = FileData.FileType.File_Unknown;

		public FileInfo(String path) {
			File file = new File(path);
			this.name = file.getName();
			this.size = (int) file.length();
			String suffix;
			int la = name.lastIndexOf('.');
			if (la == -1)
				suffix = null;
			else
				suffix = name.substring(la + 1).toLowerCase();
			this.path = path;
			// this.date = date;
			this.isDirectory = file.isDirectory();
            this.type = FileData.getFileType(file.getAbsolutePath());
        }

        public FileData getFileData(FileInfo fileInfo) {
            if (fileInfo == null) {
                return null;
            }
            FileData fileData = new FileData();
            fileData.filePath = fileInfo.path;
            fileData.isDir = fileInfo.isDirectory;
            fileData.isSelect = fileInfo.selected;
            fileData.size = fileInfo.size;
            fileData.title = fileInfo.name;
            fileData.fileType = fileInfo.type;

            return fileData;
        }
		
		public final String name() {
			return this.name;
		}

		public final String path() {
			return this.path;
		}

		public final void invertSelected() {
			selected = !selected;
		}

		public final boolean selectted() {
			return selected;
		}

		public final void setSelected(boolean s) {
			selected = s;
		}

		public final int size() {
//			if (size == null) {
//				File file = new File(path);
//				this.size = FileCommon.formatString(String.valueOf(file
//						.length()));
//			}
			return this.size;
		}

		public final boolean directory() {
			return this.isDirectory;
		}
		
		@Override
		public int compareTo(FileInfo another) {
			if (another.isDirectory) {
				if (!isDirectory)
					return 1;
				return this.name.toLowerCase().compareTo(another.name.toLowerCase());
			}
			if (isDirectory)
				return -1;
			return this.name.toLowerCase().compareTo(another.name.toLowerCase());
		}
	}

	public static class FileCommon {
		public static String formatString(String str) {
			int i = str.length();
			while (i > 3) {
				str = str.substring(0, i - 3) + "," + str.substring(i - 3);
				i -= 3;
			}
			return str;
		}

		/**
		 * / --> / /path --> /path /path/1 --> 1 /path/1/ --> ""
		 */
		public static String getPathName(String path) {
			int index = path.lastIndexOf('/');
			if (index == -1 || index == 0)
				return path;
			return path.substring(index + 1);
		}

		/**
		 * / --> / /path --> path /path/1 --> 1 /path/1/ --> 1
		 * */
		public static String getPathName2(String path) {
			int index = path.lastIndexOf('/');
			if (index == -1 || path.equals("/"))
				return path;
			if (index == 0 && path.length() > 1) {
				return path.substring(1);
			}
			if (index == (path.length() - 1))
				return getPathName2(path.substring(0, path.length() - 1));
			return path.substring(index + 1);
		}

		public static String fileNameAppend(String name, String apd) {
			int i = name.lastIndexOf(".");
			if (i == -1 || i == 0)
				return name + apd;
			return name.substring(0, i) + apd
					+ name.substring(i, name.length());
		}

		public static String pathNameAppend(String path, String apd) {
			String name = getPathName(path);
			int i = name.lastIndexOf(".");
			if (i == -1 || i == 0)
				return path + apd;
			int l = path.lastIndexOf("/") + 1;
			return path.substring(0, l + i) + apd + path.substring(l + i);
		}

		public static String getParentPath(String path) {
			if (path.equals("/"))
				return path;
			if (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);
			path = path.substring(0, path.lastIndexOf("/"));
			return path.equals("") ? "/" : path;
		}




	}
}
