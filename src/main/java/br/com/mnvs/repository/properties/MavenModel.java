package br.com.mnvs.repository.properties;

import java.io.FileReader;
import java.io.IOException;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * Allows parse and read a parent pom.xml
 *
 * @author Mateus N V Satelis
 * @since 18/02/2020
 */
public final class MavenModel {
	
	private static final MavenConfigurationProperties PROPERTIES = MavenConfigurationProperties.instance();
	
	private MavenModel() {
	}
	
	public static Model getMavenModel() throws IOException, XmlPullParserException {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		return reader.read(new FileReader(PROPERTIES.pathPomBase()));
	}
	
}
