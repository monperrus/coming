package fr.inria.remining.coming;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import fr.inria.sacha.gitanalyzer.interfaces.FileCommit;
import fr.inria.sacha.remining.coming.analyzer.ChangeInstanceMiner;
import fr.inria.sacha.remining.coming.entity.ActionType;
import fr.inria.sacha.remining.coming.entity.EntityType;
import fr.inria.sacha.remining.coming.util.XMLOutput;
import fr.labri.gumtree.actions.Action;

/**C:\Personal\develop\repositoryResearch\commons-lang-git
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ChangeInstancesSearchTest {
  //--
  public static String common_collection_git = "C:/Personal/develop/repositoryResearch/apache-commons-collections/commons-collections";
  public static String commons_math_git = "C:/Personal/develop/repositoryResearch/commons-math";
  public static String commons_lang_git =  "C:/Personal/develop/repositoryResearch/commons-lang-git";
  public static String commons_io_git =  "C:/Personal/develop/repositoryResearch/commons-io";
  public static String commons_configuration_git =  "C:/Personal/develop/repositoryResearch/commons-configuration";
  public static String commons_codec_git =  "C:/Personal/develop/repositoryResearch/commons-codec";
  public static String commons_bcel_git =  "C:/Personal/develop/repositoryResearch/commons-bcel";
  public static String commons_beanutils_git =  "C:/Personal/develop/repositoryResearch/commons-beanutils";
  public static String commons_compress_git =  "C:/Personal/develop/repositoryResearch/commons-compress";
  public static String commons_compress_cxf =  "C:/Personal/develop/repositoryResearch/commons-cxf";
  public static String commons_fileupload =  "C:/Personal/develop/repositoryResearch/commons-fileupload";
  public static String commons_loggin_pad = "C:/Personal/develop/repositoryResearch/commons-logging";
  public static String commons_net_git = "C:/Personal/develop/repositoryResearch/commons-net";
  public static String commons_pool_git = "C:/Personal/develop/repositoryResearch/commons-pool";
  public static String commons_digester_git = "C:/Personal/develop/repositoryResearch/commons-digester";
  public static String commons_dbcp_git = "C:/Personal/develop/repositoryResearch/commons-dbcp";
  
  
  //--
  public static String pico_git_pad = "C:/Personal/develop/repositoryResearch/picocontainer";
  public static String junit_git_pad = "C:/Personal/develop/repositoryResearch/junit";
  public static String dnsjava_git_pad = "C:/Personal/develop/repositoryResearch/dnsjava";
  public static String log4j_git_pad = "C:/Personal/develop/repositoryResearch/log4jgit";
  public static String jetty_git_pad = "C:/Personal/develop/repositoryResearch/jetty-project";
  public static String xalanj_git_pad = "C:/Personal/develop/repositoryResearch/xalan-j";
  public static String tomcat_git_pad = "C:/Personal/develop/repositoryResearch/tomcat";
  public static String jbossas_git_pad = "C:/Personal/develop/repositoryResearch/wildfly";
  

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

	@Before
	public void setUp() throws Exception {

		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(console);
	}



  //@Test
  public void testInstancesInCommonCollection() throws Exception {
    ChangeInstanceMiner c = new ChangeInstanceMiner();
    Map<FileCommit, List> instancesFound = c.analize(common_collection_git, "0145c16a6ff8dced5e3cdb899a250dcd49ef780f", EntityType.IF_STATEMENT.name(), ActionType.UPD, false);
    System.out.println("Instances " + instancesFound.size());
    c.printResult(instancesFound);
  }

  //@Test
  public void singleInstancesInCommonCollectionInsert() throws Exception {
    ChangeInstanceMiner c = new ChangeInstanceMiner();
    Map<FileCommit, List> instancesFound = c.analize(common_collection_git,  EntityType.IF_STATEMENT.name(), ActionType.INS);
    c.printResult(instancesFound);
  //  assertEquals(1, instancesFound.size());
  }

  @Test
  public void searchBugsOnIfConditionals() throws Exception {
    ChangeInstanceMiner c = new ChangeInstanceMiner();
    String messageHeuristic = "MATH-";//"IO-";//"LANG";//"COLLECTIONS-";//"MATH";
    Map<FileCommit, List> instancesFound = c.analize(
    	//	commons_digester_git
    		//commons_pool_git	
    		//commons_fileupload
    		//commons_loggin_pad	
    		//commons_compress_cxf	
    		//commons_compress_git
    		//	commons_beanutils_git
    		//commons_bcel_git
    		//commons_codec_git
    		//	commons_configuration_git	
    		//commons_io_git
    		//commons_lang_git
    		////common_collection_git	
    	  		commons_math_git
    		, EntityType.IF_STATEMENT.name(), ActionType.UPD, messageHeuristic);
    c.printResultDetails(instancesFound);
    XMLOutput.print(instancesFound);
  }
  
  

  
  //@Test
  public void searchBugsOnWhile() throws Exception {
    ChangeInstanceMiner c = new ChangeInstanceMiner();
    String messageHeuristic = "";//"LANG";//"COLLECTIONS-";//"MATH";
    Map<FileCommit, List> instancesFound = c.analize(
    		commons_lang_git
    		//common_collection_git	
    	  	//	commons_math_git
    		,EntityType.WHILE_STATEMENT.name(), ActionType.UPD, messageHeuristic);
    c.printResultDetails(instancesFound);
    XMLOutput.print(instancesFound);
  }
  

  /**
   * Commits with False Positives. 
   * @throws Exception
   */
 //@Test
  public void singleInstancesInCommonCollectionUpdate() throws Exception {
    ChangeInstanceMiner c = new ChangeInstanceMiner();
    Map<FileCommit, List> instancesFound = c.analizeSingleCommit(common_collection_git,"f43e0a53c1f4d3335e720131973dfc8e1103f5f9" /*"0145c16a6ff8dced5e3cdb899a250dcd49ef780f"*/, EntityType.IF_STATEMENT.name(), ActionType.INS, true);
    c.printResult(instancesFound);
    assertEquals(2,instancesFound.values().size());
  //  0145c16a6ff8dced5e3cdb899a250dcd49ef780f
    //"db708754f61ed6e39e9b4e14aa0c6dab669ab959"
    //assertEquals(0,instancesFound.size());
    //bc1660101609fd4861d20a07b8a05cdfb57c347c
    //0122245f02ba7b22dd40f38c98aa2f08984707bd
    //24921ebe3e93a1d77f2cc4725d1eee66f68a856a 
    //db708754f61ed6e39e9b4e14aa0c6dab669ab959
  }

 
}
