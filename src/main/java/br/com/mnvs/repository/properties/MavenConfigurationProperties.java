package br.com.mnvs.repository.properties;

import static br.com.mnvs.repository.properties.MavenKeysProperties.DIR_BASE_CHECKOUT;
import static br.com.mnvs.repository.properties.MavenKeysProperties.GOALS;
import static br.com.mnvs.repository.properties.MavenKeysProperties.LOCAL_REPOSITORY;
import static br.com.mnvs.repository.properties.MavenKeysProperties.MAVEN_HOME;
import static br.com.mnvs.repository.properties.MavenKeysProperties.MODULES;
import static br.com.mnvs.repository.properties.MavenKeysProperties.PATH_POM_BASE;
import static br.com.mnvs.repository.properties.MavenKeysProperties.THROW_FAILURE;
import static java.text.MessageFormat.format;

import br.com.mnvs.repository.exceptions.InitConfigurationException;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Initiates maven configuration
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public final class MavenConfigurationProperties {
	
	private static final Logger LOGGER = LogManager.getLogger(MavenConfigurationProperties.class);
	private static final String JSON_FILE_NAME = "/maven.json";
	
	private static MavenConfigurationProperties instance;
	
	private final Configuration configuration;
	
	private LocalRepository localRepository;
	
	public static synchronized MavenConfigurationProperties instance() {
		if (instance == null) {
			instance = new MavenConfigurationProperties();
		}
		return instance;
	}
	
	private MavenConfigurationProperties() {
		final Configurations configurations = new Configurations();
		try {
			FileBasedConfigurationBuilder<JSONConfiguration> builder = configurations
					.fileBasedBuilder(JSONConfiguration.class, IOUtils.resourceToURL(JSON_FILE_NAME));
			
			configuration = builder.getConfiguration();
			
		} catch (IOException | ConfigurationException e) {
			String message = format("Failure to initiate configurations: {0}", JSON_FILE_NAME);
			throw new InitConfigurationException(message, e);
		}
	}
	
	
	public String dirBaseCheckout() {
		return configuration.getString(DIR_BASE_CHECKOUT.getValue());
	}
	
	public String pathPomBase() {
		return configuration.getString(PATH_POM_BASE.getValue());
	}
	
	public File mavenHomeFile() {
		String mavenHome = configuration.getString(MAVEN_HOME.getValue());
		return new File(StringUtils.defaultIfBlank(mavenHome, System.getenv(MAVEN_HOME.name())));
	}
	
	public List<String> goals() {
		return configuration.getList(String.class, GOALS.getValue());
	}
	
	public synchronized LocalRepository localRepository() {
		if (localRepository != null) {
			return localRepository;
		}
		
		localRepository = new LocalRepository();
		Iterator<String> keys = configuration.getKeys(LOCAL_REPOSITORY.getValue());
		List<Field> fields = Arrays.asList(LocalRepository.class.getDeclaredFields());
		Iterator<Field> iterator = fields.iterator();
		
		while (keys.hasNext()) {
			try {
				Field field = iterator.next();
				Object value = configuration.get(field.getType(), keys.next());
				field.set(localRepository, value);
			
			} catch (IllegalAccessException e) {
				LOGGER.error("Access to variable not allowed", e);
			}
		}
		return localRepository;
	}
	
	public Map<String, String> modules() {
		Map<String, String> map = Maps.newLinkedHashMap();
		for (String key : configuration.getList(String.class, MODULES.getValue())) {
			map.put(key, dirBaseCheckout() + key + "/pom.xml");
		}
		
		return map;
	}
	
	public boolean isThrowFailure() {
		return configuration.getBoolean(THROW_FAILURE.getValue());
	}
	
	public static class LocalRepository {
		
		Boolean hasToClean;
		Integer maxAgeFiles;
		String path;
		
		public Boolean getHasToClean() {
			return hasToClean;
		}
		
		public Integer getMaxAgeFiles() {
			return maxAgeFiles;
		}
		
		public String getPath() {
			return path;
		}
		
	}
	
}
