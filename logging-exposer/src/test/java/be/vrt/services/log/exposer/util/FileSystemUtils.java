package be.vrt.services.log.exposer.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileSystemUtils {

	public static void deleteRecursivly(Path path) {
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String read(URL url) {
		try {
			Path path = Paths.get(url.toURI());
			return new String (Files.readAllBytes(path));
		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(e);
		}
	}
}
