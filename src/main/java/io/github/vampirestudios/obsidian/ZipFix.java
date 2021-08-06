package io.github.vampirestudios.obsidian;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFix {

	public static void fix(String filePath, String outFolder) throws IOException {
		try(ZipFile zipFile = new ZipFile(filePath)) {
			Enumeration<? extends ZipEntry> zipEnum = zipFile.entries();
			while(zipEnum.hasMoreElements()) {
				ZipEntry zipEntry = zipEnum.nextElement();
				Path outPath = Paths.get(outFolder, zipEntry.getName());
				File outFile = outPath.toFile();
				File parent = outFile.getParentFile();
				if(!parent.exists()) {
					parent.mkdirs();
				}
				if(outFile.exists()) {
					System.out.print("Overriding ");
				} else {
					System.out.print("Extrating ");
				}
				System.out.print(zipEntry.getName() + "... ");
				try {
					Files.copy(zipFile.getInputStream(zipEntry), outPath);
					System.out.println("Success");
				} catch(Exception e) {
					System.out.println("Failed");
					e.printStackTrace();
				}
			}
		}
	}

    /**
     * @param args element 0 should contain the input zip file. Element 1 should contain the output folder.
     */
    public static void main(String[] args) throws IOException {
		String path = "D:\\Backup Main 201120\\testing\\mmc-develop-win32\\MultiMC\\instances\\Modpack\\.minecraft\\server-resource-packs\\";
		String packVersion = "2c10e590b1a8c001324bb11f16804810a7280206";
		fix(path + packVersion, path + packVersion + "_extracted");
    }
}