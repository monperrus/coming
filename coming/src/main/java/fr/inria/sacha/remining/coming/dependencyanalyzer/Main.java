package fr.inria.sacha.remining.coming.dependencyanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import fr.inria.sacha.gitanalyzer.interfaces.Commit;
import fr.inria.sacha.gitanalyzer.interfaces.FileCommit;
import fr.inria.sacha.gitanalyzer.object.RepositoryPGit;
import fr.inria.sacha.remining.coming.analyzer.GTFacade;
import fr.inria.sacha.remining.coming.dependencyanalyzer.util.XMLOutputResFile;
import fr.inria.sacha.remining.coming.entity.ActionType;
import fr.inria.sacha.remining.coming.entity.EntityType;
import fr.labri.gumtree.Tree;
import fr.labri.gumtree.gen.jdt.ProduceJDTTree;
/**
 * 
 * Launches an dependency analysis on a Git project Classes
 * 
 * @author  Romain Philippon
 *
 */
public class Main {

	private final static EntityType ENTITY_TO_ANALYZE = EntityType.CLASS;
	private final static List<ActionType> GIT_ACTION_TO_ANALYZE = Arrays.asList(ActionType.INS, ActionType.DEL);
	
	public static void main(String[] args) {
		
		String previousVersion, nextVersion;
		String message = "USAGE --parameters-- [projectLocation]";
		XMLOutputResFile xml = new XMLOutputResFile();
		
		if(args == null){
				System.out.println(message);
				return;
		}
		
	    String projectLocation = args[0];
	    
	    /* GET ALL COMMITS FROM LOCAL REPOSITORY */
	    RepositoryPGit gitRepository = new RepositoryPGit(projectLocation, "master");
	    List<Commit> allGitCommit = gitRepository.history();
	    
	    System.out.println("Git project analyzed : "+ projectLocation);
	    System.out.println("Contains "+ allGitCommit.size() +" commit(s)");
	    
	    xml.setGitRepositoryName(projectLocation);
	    xml.setNumberOfCommitInRepository(allGitCommit.size());
	    	
    	/* ANALYSIS COMMIT ONE BY ONE */
    	for(Commit commit : allGitCommit) {
    		List<FileCommit> javaCommitFiles = commit.getJavaFileCommits();
    		xml.addAnalyzedCommit(commit);
    		
    		for(FileCommit fileCommit : javaCommitFiles) {
    			previousVersion = fileCommit.getPreviousVersion();
    			nextVersion = fileCommit.getNextVersion();
    			
    			/* ADDED CLASSES */
    			if(previousVersion.isEmpty()) {    				
    				xml.addCommitFile(fileCommit.getFileName(), ActionType.INS, null);
        		}
    			else {
	    			/* DELETED CLASSES */
	    			if(nextVersion.isEmpty()) {
	        			xml.addCommitFile(fileCommit.getFileName(), ActionType.DEL, null);
	    			}
    			}
    		}
    	}
    	
    	System.out.println("End analysis");
    	
    	try {
			xml.save();
		} 
    	catch (IOException ioe) {
			xml.display();
		}
    	
    	System.out.println("Results are saved in a xml file");
	}
}

