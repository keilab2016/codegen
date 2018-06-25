package model;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/*
 * フォルダの中身を階層的にまとめてzip圧縮するクラス
 */
public class ZipCompressor {
	private String filePath;
	private String zipPath;
	
	/*
	 * コンストラクタ
	 * 引数：圧縮したいファイルパス、圧縮先
	 */
	public ZipCompressor(String filePath, String zipPath) {
		this.filePath = filePath;
		this.zipPath = zipPath;
	}
	
	/*
	 * 圧縮メソッド
	 * 引数：ファイル名(末尾に.zipは不要)
	 * 返り値：boolean
	 */
	public boolean compress(String name) {
		String zipName = null;
		try {
			zipName = name + ".zip";
			zipFileMake(filePath, zipPath + "/" + zipName);
		} catch (Exception e) {
			System.out.println(e.toString());
			if (e.getCause() != null) {
				for (StackTraceElement ste : e.getCause().getStackTrace()) {
					System.out.println(ste.toString());
				}
			} else {
				for (StackTraceElement ste : e.getStackTrace()) {
					System.out.println(ste.toString());
				}
			}
		}
		return true;
	}
	
	private void zipFileMake(String dir, String zipPath) throws Exception {
		File zipFile = new File(zipPath);
		if (!zipFile.getParentFile().exists()) zipFile.getParentFile().mkdirs();
		ZipOutputStream zo = new ZipOutputStream(new FileOutputStream(zipPath));
		zo.setLevel(5);
		File tgtdir = new File(dir);
		File[] tgtpaths = tgtdir.listFiles();
		for (int i = 0; i < tgtpaths.length; i++) {
			zipFileMakeProc(zo, tgtpaths[i], "");
		}
		zo.flush();
		zo.close();
	}
	
	private void zipFileMakeProc(ZipOutputStream zo, File path, String hrc) throws Exception {
		if (path.isDirectory()) {
			ZipEntry entry = new ZipEntry(hrc + path.getName().replace("\\", "/") + "/");
			entry.setMethod(ZipEntry.STORED);
			entry.setSize(0);
			entry.setCrc(0);
			zo.putNextEntry(entry);
			zo.closeEntry();
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				zipFileMakeProc(zo, files[i], hrc + path.getName() + "/");
			}
		} else {
			ZipEntry entry = new ZipEntry(hrc + path.getName().replace("\\", "/"));
			zo.putNextEntry(entry);
			byte buf[] = new byte[4096];
			BufferedReader in = new BufferedReader(new FileReader(path));
			String s = null;
			while ((s = in.readLine()) != null) {
				buf = s.getBytes("UTF-8");
				zo.write(buf, 0, buf.length);
			}
			/*
			int size;
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(path.getPath()));
			while ((size = in.read(buf, 0, 1024)) != -1) {
				zo.write(buf, 0, size);
			}
			*/
			zo.closeEntry();
			in.close();
		}
	}

}
