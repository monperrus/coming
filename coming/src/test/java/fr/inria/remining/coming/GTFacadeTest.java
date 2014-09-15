package fr.inria.remining.coming;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import fr.inria.sacha.remining.coming.analyzer.GTFacade;
import fr.inria.sacha.remining.coming.entity.GranuralityType;
import fr.labri.gumtree.actions.Action;

/**
 * 
 *  @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class GTFacadeTest {



	@Test
	public void testStart() throws URISyntaxException {
		

		
		
		GTFacade gt = new GTFacade();
		File fl = new File(getClass().
				getResource("/test1_left.txt").getFile());
		File fr = new File(getClass().
				getResource("/test1_right.txt").getFile());
		
		List<Action> actions = gt.analyzeFiles(fl, fr, GranuralityType.JDT, true);
		System.out.println(actions);
		assertNotNull(actions);
		assertEquals(3,actions.size());
		
		List<Action> actionsCD = gt.analyzeFiles(fl, fr, GranuralityType.CD, true);
		System.out.println(actionsCD);
		assertNotNull(actionsCD);
		assertEquals(3,actionsCD.size());
	}


	
}
