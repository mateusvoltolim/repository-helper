package br.com.mnvs.repository.app;

import br.com.mnvs.repository.service.MavenBuildService;
import org.apache.maven.shared.invoker.MavenInvocationException;

/**
 * Main App
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public class MainRepositoryHelper {
	
	private static final MavenBuildService MAVEN_BUILD_SERVICE = MavenBuildService.instance();
	
	public static void main(String[] args) throws MavenInvocationException {
		MAVEN_BUILD_SERVICE.build();
		
	}
	
}
