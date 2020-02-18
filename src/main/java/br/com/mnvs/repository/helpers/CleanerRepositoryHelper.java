package br.com.mnvs.repository.helpers;

import static org.apache.commons.lang.StringUtils.endsWith;

import br.com.mnvs.repository.properties.MavenModel;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Helper to filter and clean the garbage in local repository path
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public final class CleanerRepositoryHelper {
	
	private static final Logger LOGGER = LogManager.getLogger(CleanerRepositoryHelper.class);
	
	private static CleanerRepositoryHelper instance;
	
	private CleanerRepositoryHelper() {
	}
	
	public static synchronized CleanerRepositoryHelper instance() {
		if (instance == null) {
			instance = new CleanerRepositoryHelper();
		}
		return instance;
	}
	
	public void clean(String pathRepository, Integer maxAgeFiles) {
		try (Stream<Path> paths = Files.list(Paths.get(pathRepository))) {
			paths.forEach(path -> tryDeleteFiles(maxAgeFiles, path));
		} catch (IOException e) {
			LOGGER.error("Failure to list directories", e);
		}
	}
	
	private void tryDeleteFiles(Integer maxAgeFiles, Path path) {
		try (Stream<Path> subFiles = Files.list(path)) {
			deleteFiles(getFilteredFiles(subFiles, maxAgeFiles));
		} catch (IOException | XmlPullParserException e) {
			LOGGER.error("Failure to list files", e);
		}
	}
	
	private void deleteFiles(Stream<Path> filteredFiles) {
		filteredFiles.forEach(file -> {
			LOGGER.info("Removing folders: {}", file.toAbsolutePath());
			try {
				FileUtils.deleteDirectory(file.toFile());
			} catch (IOException e) {
				LOGGER.error("Failure to delete folders", e);
			}
		});
	}
	
	private Stream<Path> getFilteredFiles(Stream<Path> subFiles, Integer maxAgeFiles)
			throws IOException, XmlPullParserException {
		Model model = MavenModel.getMavenModel();
		
		return subFiles.filter(sf -> {
			File file = sf.toFile();
			LocalDate lastModifiedDate = getLastModifiedDate(file);
			LocalDate today = LocalDate.now();
			String absolutePath = sf.toAbsolutePath().toString();
			return file.isDirectory() && isNotActualVersion(model, absolutePath) &&
					(isOldSnapshots(absolutePath) || ChronoUnit.DAYS.between(lastModifiedDate, today) >= maxAgeFiles);
		});
		
	}
	
	private LocalDate getLastModifiedDate(File file) {
		return Instant.ofEpochMilli(file.lastModified()).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	private boolean isOldSnapshots(String absolutePath) {
		return endsWith(absolutePath, "SNAPSHOT");
	}
	
	private boolean isNotActualVersion(Model model, String absolutePath) {
		return !endsWith(absolutePath, model.getVersion());
	}
	
}
