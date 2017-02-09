package model;

import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;

/*
 * zipファイルを階層的に解凍するクラス
 * 参考：http://www.saka-en.com/java/java-zip-uncompress/
 */
public class ZipDecompressor {
	private String zipPath;
	private String outPath;

	/*
	 * コンストラクタ
	 * 引数：解凍したいzipファイル、展開先
	 */
	public ZipDecompressor(String zipPath, String outPath) {
		this.zipPath = zipPath;
		this.outPath = outPath;
	}

	public boolean decord() {
		File baseFile = new File(zipPath);
		File baseDir = new File(baseFile.getParent(), baseFile.getName().substring(0, baseFile.getName().lastIndexOf(".")));
		if ( !baseDir.mkdir() )
			System.out.println("Couldn't create directory because directory with the same name exists.: " + baseDir);

		ZipFile zipFile = null;
		try {
			// ZIPファイルオブジェクト作成
			zipFile = new ZipFile(zipPath);

			// ZIPファイル内のファイルを列挙
			Enumeration<? extends ZipEntry>  enumZip = zipFile.entries();

			// ZIPファイル内の全てのファイルを展開
			while (enumZip.hasMoreElements()) {

				// ZIP内のエントリを取得
				ZipEntry zipEntry = (java.util.zip.ZipEntry)enumZip.nextElement();

				//出力ファイル取得
				File unzipFile = new File(outPath);
				File outFile = new File(unzipFile.getAbsolutePath() + "/" + baseDir.getName(), zipEntry.getName());

				if (zipEntry.isDirectory()) outFile.mkdir();
				else {
					// 圧縮ファイル入力ストリーム作成
					BufferedInputStream in = new BufferedInputStream(zipFile.getInputStream(zipEntry));

					// 親ディレクトリがない場合、ディレクトリ作成
					if(!outFile.getParentFile().exists()) outFile.getParentFile().mkdirs();

					// 出力オブジェクト取得
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));

					// 読み込みバッファ作成
					byte[] buffer = new byte[1024];

					// 解凍ファイル出力
					int readSize = 0;
					while((readSize = in.read(buffer)) != -1) {
						out.write(buffer, 0, readSize);
					}
					// クローズ
					try { out.close(); } catch (Exception e) {}
					try { in.close(); } catch (Exception e) {}
				}
			}
			// 解凍処理成功
			return true;
		} catch(Exception e) {
			// エラーログ出力
			System.out.println(e.toString());
			// 解凍処理失敗
			return false;
		} finally {
			if ( zipFile != null )
				try { zipFile.close(); } catch (Exception e) {}
		}
	}
}
