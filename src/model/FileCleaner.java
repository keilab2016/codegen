package model;

import java.io.File;

/*
 * ファイル削除クラス
 */
public class FileCleaner {
	
	/*
	 * ファイル削除クラス
	 * 階層的に削除していく
	 */
	public static void delete(File file) {
		if(!file.exists()) return;
		if(file.isFile()) {
			file.delete();
			return;
		}
		if(file.isDirectory()) {
			for(File child : file.listFiles()) {
				delete(child);
			}
		}
		file.delete();
	}
}
