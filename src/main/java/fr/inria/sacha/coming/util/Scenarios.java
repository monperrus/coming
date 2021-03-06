package fr.inria.sacha.coming.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.inria.sacha.coming.analyzer.Parameters;
import fr.inria.sacha.coming.analyzer.RepositoryInspector;
import fr.inria.sacha.coming.analyzer.commitAnalyzer.ChildParentFilter;
import fr.inria.sacha.coming.analyzer.commitAnalyzer.FineGrainChangeCommitAnalyzer;
import fr.inria.sacha.coming.analyzer.commitAnalyzer.PatternFilter;
import fr.inria.sacha.coming.analyzer.treeGenerator.PatternAction;
import fr.inria.sacha.coming.analyzer.treeGenerator.PatternEntity;
import fr.inria.sacha.coming.entity.ActionType;
import fr.inria.sacha.coming.entity.EntityType;
import fr.inria.sacha.coming.entity.GranuralityType;
import fr.inria.sacha.gitanalyzer.interfaces.FileCommit;
import fr.labri.gumtree.matchers.Matcher;
/**
 * 
 * @author Matias Martinez
 *
 */
public class Scenarios {

	/**
	 * Get Commits with only one change in a java file (excluding test cases)
	 * @param messageHeuristic
	 * @param repoPath
	 * @return
	 */
	public static Map<FileCommit, List> get1SC_CD(String messageHeuristic , String repoPath ){
		

		PatternFilter pattern = new PatternFilter(
				PatternEntity.ANY_ENTITY,
				ActionType.ANY);
		FineGrainChangeCommitAnalyzer 	fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(pattern,GranuralityType.CD);
		
		RepositoryInspector inspector = new RepositoryInspector();
		Parameters.MAX_FILES_PER_COMMIT = 1;
		Parameters.MAX_AST_CHANGES_PER_FILE = 1;
		Parameters.ONLY_COMMIT_WITH_TEST_CASE = false;

		Map<FileCommit, List> instancesFound = inspector.analize(
			repoPath,
				fineGrainAnalyzer, messageHeuristic);
	
	return instancesFound;
	}
	
	
	public static Map<FileCommit, List> get1SCWithTest_Spoon(String messageHeuristic , String repoPath ){
		

		PatternFilter pattern = new PatternFilter(
				PatternEntity.ANY_ENTITY,
				ActionType.ANY);
		
		FineGrainChangeCommitAnalyzer 	fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(pattern,GranuralityType.SPOON );
		
		RepositoryInspector inspector = new RepositoryInspector();
		Parameters.MAX_FILES_PER_COMMIT = 2;
		Parameters.MAX_AST_CHANGES_PER_FILE = 1;
		Parameters.ONLY_COMMIT_WITH_TEST_CASE = true;

		Map<FileCommit, List> instancesFound = inspector.analize(
			repoPath,
				fineGrainAnalyzer, messageHeuristic);
	
	return instancesFound;
	}
	
	
	public static Map<FileCommit, List> getArithmetics_Spoon(String messageHeuristic , String repoPath ){
		Parameters.MAX_FILES_PER_COMMIT = 1;
		Parameters.ONLY_COMMIT_WITH_TEST_CASE = false;
		Parameters.MAX_AST_CHANGES_PER_FILE = 1;
		
		
		PatternFilter pattern  = new PatternFilter(
				"*",
				ActionType.ANY,
				"Assignment",10 );
		
		FineGrainChangeCommitAnalyzer 	fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(pattern,GranuralityType.SPOON );
				
				
		RepositoryInspector inspector = new RepositoryInspector();

		Map<FileCommit, List> instancesFound = inspector.analize(
				repoPath,
				fineGrainAnalyzer, messageHeuristic);	
		
		return instancesFound;
	}
	
	public static Map<FileCommit, List> getArithmeticsBinary(String messageHeuristic , String repoPath ){
		
		RepositoryInspector inspector = new RepositoryInspector();

				
		FineGrainChangeCommitAnalyzer 	fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(
				//getAnyBinary(),
				//getAnyUnary(),
				//getAnyVariableAccess(),
				getAnyLiteral(),
				GranuralityType.SPOON );
		
		
		Map<FileCommit, List> instancesFound = inspector.analize(
				repoPath,fineGrainAnalyzer
				, messageHeuristic);	
		
		return instancesFound;
	}
	
	protected static PatternFilter getAnyBinary(){
		PatternFilter pattern = new PatternFilter(
				"BinaryOperator",
				ActionType.ANY,
				"Assignment",20 );
		return pattern;
	}
	
	public static Map<FileCommit, List> preconditionsCD(String messageHeuristic , String repoPath ){
		
		PatternEntity pentity = new PatternEntity(EntityType.IF_STATEMENT.name());
		PatternAction pactionMain = new PatternAction(pentity, ActionType.INS);
				
		List<PatternAction> pac = new ArrayList<PatternAction>();
		pac.add(pactionMain);
		ChildParentFilter filter = new ChildParentFilter(pac);
		
		
		
		FineGrainChangeCommitAnalyzer 	fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(filter,GranuralityType.CD);
		
		RepositoryInspector inspector = new RepositoryInspector();
		Parameters.MAX_FILES_PER_COMMIT = 10;
		Parameters.MAX_AST_CHANGES_PER_FILE = 20;
		Parameters.ONLY_COMMIT_WITH_TEST_CASE = false;

		Map<FileCommit, List> instancesFound = inspector.analize(
			repoPath,
				fineGrainAnalyzer, messageHeuristic);
	
	return instancesFound;
	}


public static Map<FileCommit, List> getAddIf2SCWithTest(String messageHeuristic , String repoPath ){
	
	
	List<PatternAction> pac = new ArrayList<PatternAction>();
	//Insert of a If statement
	pac.add(new PatternAction(new PatternEntity(EntityType.IF_STATEMENT.name()),ActionType.INS));
	//Move over any entity, its parent is Then statement
	//pac.add(new PatternAction(new PatternEntity("*", new PatternEntity(EntityType.THEN_STATEMENT.name()),1),ActionType.MOV));
	
	PatternFilter filter = new ChildParentFilter(pac);
	
	FineGrainChangeCommitAnalyzer 	fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(filter,GranuralityType.CD );
	
	RepositoryInspector inspector = new RepositoryInspector();
	Parameters.MAX_FILES_PER_COMMIT = 2;
	Parameters.MAX_AST_CHANGES_PER_FILE = 5;//2;//1 for the if, other for the move
	Parameters.ONLY_COMMIT_WITH_TEST_CASE = false;

	Map<FileCommit, List> instancesFound = inspector.analize(
		repoPath,
			fineGrainAnalyzer, messageHeuristic);

return instancesFound;
}
	
	protected static PatternFilter getAnyUnary(){
		PatternFilter pattern = new PatternFilter(
				"UnaryOperator",
				ActionType.ANY,
				"Assignment",20);
		return pattern;
	}
		
	
	protected static PatternFilter getAnyVariableAccess(){
		PatternFilter pattern = new PatternFilter(
				"VariableAccess",
				ActionType.ANY,
				"Assignment",20);
		return pattern;
	}
	
	protected static PatternFilter getAnyFieldAccess(){
		PatternFilter pattern = new PatternFilter(
				"FieldAccess",
				ActionType.ANY,
				"Assignment",20 );
		return pattern;
	}
	protected static PatternFilter getAnyLiteral(){
		PatternFilter pattern = new PatternFilter(
				"Literal",
				ActionType.ANY,
				"Assignment",20);
		return pattern;
	}
	
	public static void main(String[] args) throws Exception {
		
		CommandLineParser parser = new BasicParser();
		CommandLine cmd;
		try {
			cmd = parser.parse( options, args);
		
		Parameters.setUpProperties();
		configureLog();
		if(cmd.hasOption("1sc")){
			String repo = cmd.getOptionValue("repo");
			if(repo != null){
				Map<FileCommit, List> instancesFound = null;
				if(cmd.hasOption("t")){
					instancesFound = Scenarios.get1SCWithTest_Spoon("", repo);
				}else{
					instancesFound = Scenarios.get1SC_CD("", repo);
				}
				
				ConsoleOutput.printResultDetails(instancesFound);
				XMLOutput.print(instancesFound);
				
			return;	
			}
			
		}
		} catch (ParseException e) {
			
			System.out.println(e.getMessage());
		}
		
		printHelp();
		
	}

	public static void printHelp(){
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "Scenarios", options );
	}

	static Options options = new Options();
	
	static {
	// create Options object
	// add t option
	options.addOption("t", false, "The commit must include test cases");
	options.addOption("1sc", false, "Mine 1-SC changes");
	options.addOption("repo", true, "Folder of the git repository");
	
	}	
	
	
	public static void configureLog() throws Exception {

		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(console);
	
		
		java.util.logging.Logger.getLogger("fr.labri.gumtree.matchers").setLevel(java.util.logging.Level.OFF);
		//java.util.logging.Logger.getRootLogger().addAppender(new NullAppender());
		Matcher.LOGGER.setLevel(java.util.logging.Level.OFF);
	}
	
	
}
