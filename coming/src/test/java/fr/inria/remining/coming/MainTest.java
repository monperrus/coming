package fr.inria.remining.coming;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.sacha.remining.coming.analyzer.Main;
/**
 * 
 * @author  Matias Martinez, matias.martinez@inria.fr
 *
 */
public class MainTest {

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
		
		Main.main(new String[]{"C:/Personal/develop/repositoryResearch/commons-math",
				"IF_STATEMENT","UPD"});
	}

	@Test
	public void testMineIfsCommitsText() {
		
		Main.main(new String[]{"C:/Personal/develop/repositoryResearch/commons-math",
				"IF_STATEMENT","UPD","MATH-"});
	}

}
