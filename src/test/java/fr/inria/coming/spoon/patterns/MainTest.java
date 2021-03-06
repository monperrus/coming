package fr.inria.coming.spoon.patterns;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.Before;
import org.junit.Test;

import fr.inria.sacha.coming.analyzer.Main;
import fr.inria.sacha.coming.analyzer.treeGenerator.TreeGeneratorRegistry;
import fr.inria.sacha.coming.spoon.treeGenerator.SpoonTreeGenerator;
import fr.labri.gumtree.matchers.Matcher;
/**
 * 
 * @author  Matias Martinez, matias.martinez@inria.fr
 *
 */
public class MainTest {

	
	@Before
	public void setUp() throws Exception {

		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(console);
		
		TreeGeneratorRegistry.addGenerator(new SpoonTreeGenerator());

		//Parameters.setUpProperties();
		
		
		java.util.logging.Logger.getLogger("fr.labri.gumtree.matchers").setLevel(java.util.logging.Level.OFF);
		//java.util.logging.Logger.getRootLogger().addAppender(new NullAppender());
		Matcher.LOGGER.setLevel(java.util.logging.Level.OFF);
	}
	
	@Test
	public void testListEntities() {
		
		Main.main(new String[]{"-e"});
	}
	
	@Test
	public void testListActions() {
		
		Main.main(new String[]{"-a"});
	}
	@Test
	public void testMineIfs() {
		
		Main.main(new String[]{"repogit4testv0",
				"IF_STATEMENT","UPD"});
	}

	@Test
	public void testMineIfsCommitsText() {
		
		Main.main(new String[]{"repogit4testv0",
				"IF_STATEMENT","UPD","MATH-"});
	}

}
