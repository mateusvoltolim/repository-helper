package br.com.mnvs.repository.service;

import static org.apache.commons.lang3.time.DurationFormatUtils.formatDuration;

import br.com.mnvs.repository.helpers.CleanerRepositoryHelper;
import br.com.mnvs.repository.properties.MavenConfigurationProperties;
import br.com.mnvs.repository.properties.MavenConfigurationProperties.LocalRepository;
import java.io.File;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Map.Entry;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

/**
 * Executes the maven build
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public final class MavenBuildService {
	
	private static final Logger LOGGER = LogManager.getLogger(MavenBuildService.class);
	private static final MavenConfigurationProperties PROPERTIES = MavenConfigurationProperties.instance();
	private static final CleanerRepositoryHelper CLEANER = CleanerRepositoryHelper.instance();
	
	private static MavenBuildService instance;
	
	private MavenBuildService() {
	}
	
	public static synchronized MavenBuildService instance() {
		if (instance == null) {
			instance = new MavenBuildService();
		}
		return instance;
	}
	
	/**
	 * Executes the build of projects set on file configuration {@link resources/maven.json}
	 *
	 * @throws MavenInvocationException if the {@link Invoker} has some problem to execute {@link InvocationRequest}
	 */
	public void build() throws MavenInvocationException {
		cleanRepository();
		
		LOGGER.info("");
		LOGGER.info("######### START BUILD ##########");
		
		executeMavenRequest();
		
		LOGGER.info("");
		LOGGER.info("######### FINISH BUILD #########");
		
	}
	
	/**
	 * Executes maven request for each project
	 *
	 * @throws MavenInvocationException if the {@link Invoker} has some problem to execute {@link InvocationRequest}
	 */
	private void executeMavenRequest() throws MavenInvocationException {
		
		for (Entry<String, String> map : PROPERTIES.modules().entrySet()) {
			LocalTime start = LocalTime.now();
			loggerStart(map.getKey());
			try {
				InvocationRequest request = new DefaultInvocationRequest();
				request.setPomFile(new File(map.getValue()));
				request.setGoals(PROPERTIES.goals());
				
				Invoker invoker = new DefaultInvoker();
				invoker.setMavenHome(PROPERTIES.mavenHomeFile());
				invoker.execute(request);
			} catch (MavenInvocationException e) {
				if (PROPERTIES.isThrowFailure()) {
					throw e;
				}
			}
			loggerEnd(start);
		}
	}
	
	/**
	 * Calls {@link CleanerRepositoryHelper} instance to clean local repository
	 */
	private void cleanRepository() {
		LocalRepository localRepository = PROPERTIES.localRepository();
		if (BooleanUtils.toBoolean(localRepository.getHasToClean())) {
			CLEANER.clean(localRepository.getPath(), localRepository.getMaxAgeFiles());
		}
	}
	
	private void loggerStart(String module) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("");
			LOGGER.info("############ START #############");
			LOGGER.info("######## BUILDING PROJECT ###### {}", module.toUpperCase());
		}
	}
	
	private void loggerEnd(LocalTime start) {
		if (LOGGER.isInfoEnabled()) {
			LocalTime end = LocalTime.now();
			String duration = formatDuration(Duration.between(start, end).toMillis(), "HH:mm:ss");
			LOGGER.info("############# END ############## Execution time: {}", duration);
		}
	}
	
}
