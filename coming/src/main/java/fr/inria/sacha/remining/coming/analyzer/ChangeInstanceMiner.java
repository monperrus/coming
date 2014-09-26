package fr.inria.sacha.remining.coming.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.sacha.gitanalyzer.filter.DummyFilter;
import fr.inria.sacha.gitanalyzer.filter.IFilter;
import fr.inria.sacha.gitanalyzer.filter.KeyWordsMessageFilter;
import fr.inria.sacha.gitanalyzer.interfaces.Commit;
import fr.inria.sacha.gitanalyzer.interfaces.FileCommit;
import fr.inria.sacha.gitanalyzer.interfaces.RepositoryP;
import fr.inria.sacha.gitanalyzer.object.RepositoryPGit;
import fr.inria.sacha.remining.coming.entity.ActionType;
import fr.labri.gumtree.actions.Action;
import fr.labri.gumtree.actions.Update;


/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * ex: ChangeInstancesSearch
 * 
 */
public class ChangeInstanceMiner {

	Logger log = Logger.getLogger(ChangeInstanceMiner.class.getName());

	public static int PARAM_GIT_PATH = 0;
	public static int PARAM_MASTER_BRANCH = 1;
	public static int PARAM_LABEL = 2;
	public static int PARAM_OP_TYPE = 3;

	public static void main(String[] args) throws Exception {

		String repositoryPath, masterBranch, label, optype;
		repositoryPath = args[PARAM_GIT_PATH];
		masterBranch = "master";
		if (args.length > 1)
			masterBranch = args[PARAM_MASTER_BRANCH];
		ChangeInstanceMiner c = new ChangeInstanceMiner();
		optype = args[PARAM_OP_TYPE];
		label = args[PARAM_LABEL];

		c.analize(repositoryPath, masterBranch, label, ActionType.valueOf(optype), false);

	}

	public Map<FileCommit, List> analize(String repositoryPath, String masterBranch, String typeLabel,
			ActionType operationType, boolean onlyRoot) {
		
		return analize(repositoryPath, masterBranch, typeLabel, operationType,null);
	}	
	
	public Map<FileCommit, List> analize(String repositoryPath,  String typeLabel,
			ActionType operationType, String keywordsMessageHeuristic) {
		return analize(repositoryPath, "HEAD", typeLabel, operationType,  keywordsMessageHeuristic);
	}

	public Map<FileCommit, List> analize(String repositoryPath,  String typeLabel,
			ActionType operationType) {
		return analize(repositoryPath, "HEAD", typeLabel, operationType,"");
	}

	@SuppressWarnings("rawtypes")
	public Map<FileCommit, List> analize(String repositoryPath, String masterBranch, String typeLabel,
			ActionType operationType, String keywordsMessageHeuristic) {
		RepositoryP repo = new RepositoryPGit(repositoryPath, masterBranch);

		FineGrainChangeCommitAnalyzer fineGrainAnalyzer = new FineGrainChangeCommitAnalyzer(typeLabel,operationType );
		

		IFilter filter; 
		if(keywordsMessageHeuristic == null || keywordsMessageHeuristic.isEmpty()){
			filter = new DummyFilter();
		}
		else {
			filter = new KeyWordsMessageFilter(keywordsMessageHeuristic);
		}

		// For each commit of a repository
		List<Commit> history = repo.history();
		int i = 0;

		Map<FileCommit, List> allInstances = new HashMap<FileCommit, List>();
		for (Commit c : history) {
			i++;
			// System.out.println((i++)+"/"+history.size());
			if (filter.acceptCommit(c)) {
							
				Map<FileCommit, List> resultCommit = (Map) fineGrainAnalyzer.analyze(c);
				// System.out.println(c);
				if (resultCommit != null && !resultCommit.isEmpty())
					allInstances.putAll(resultCommit);
			}
		}

		System.out.println("Result "+fineGrainAnalyzer.withPattern + " "+fineGrainAnalyzer.withoutPattern+" "+fineGrainAnalyzer.withError);
		System.out.println("\n commits analyzed "+i);
		return allInstances;
	}

	public Map<FileCommit, List> analizeSingleCommit(String repositoryPath, String masterBranch, String typeLabel,
			ActionType operationType, boolean onlyRoot) {

		RepositoryP repo = new RepositoryPGit(repositoryPath, masterBranch);

		FineGrainChangeCommitAnalyzer analyzer = new FineGrainChangeCommitAnalyzer(typeLabel,operationType);
		
		IFilter filter = new DummyFilter();

		// For each commit of a repository
		List<Commit> history = repo.history();
		Commit lastCommit = history.get(history.size() - 1);// the previous
		log.info("Analyzing commit " + masterBranch);
		if (filter.acceptCommit(lastCommit)) {
			Map<FileCommit, List> resultCommit = (Map) analyzer.analyze(lastCommit);
			return resultCommit;
		}

		return null;
	}

	/**
	 * 
	 * @param result
	 */
	public void printResult(Map<FileCommit, List> result) {
		
		log.info("MAX_LINES_PER_HUNK: " + FineGrainChangeCommitAnalyzer.MAX_LINES_PER_HUNK);
		log.info("MAX_HUNKS_PER_FILECOMMIT: " + FineGrainChangeCommitAnalyzer.MAX_HUNKS_PER_FILECOMMIT);
		log.info("MAX_FILES_PER_COMMIT: " + FineGrainChangeCommitAnalyzer.MAX_FILES_PER_COMMIT);
		log.info("MAX_AST_CHANGES_PER_FILE: " + FineGrainChangeCommitAnalyzer.MAX_AST_CHANGES_PER_FILE);

		log.info("End of processing: Result " + result.size());
		for (FileCommit fc : result.keySet()) {
			List<Action> actionsfc = result.get(fc);
			log.info("Commit " + fc.getCommit().getName()+", "+fc.getCommit().getFullMessage().replace('\n', ' ') + ", file " + fc.getFileName() + " , instances  "
					+ actionsfc.size());
		}
	}
	
	/**
	 * 
	 * @param result
	 */
	public void printResultDetails(Map<FileCommit, List> result) {
		
		log.info("MAX_LINES_PER_HUNK: " + FineGrainChangeCommitAnalyzer.MAX_LINES_PER_HUNK);
		log.info("MAX_HUNKS_PER_FILECOMMIT: " + FineGrainChangeCommitAnalyzer.MAX_HUNKS_PER_FILECOMMIT);
		log.info("MAX_FILES_PER_COMMIT: " + FineGrainChangeCommitAnalyzer.MAX_FILES_PER_COMMIT);
		log.info("MAX_AST_CHANGES_PER_FILE: " + FineGrainChangeCommitAnalyzer.MAX_AST_CHANGES_PER_FILE);
		log.info("MUST_INCLUDE_TEST: " + FineGrainChangeCommitAnalyzer.excludeCommitWithOutTest);

		log.info("End of processing: Result " + result.size());
		for (FileCommit fc : result.keySet()) {
			List<Action> actionsfc = result.get(fc);
			log.info("Commit " + fc.getCommit().getName()+", "+fc.getCommit().getFullMessage().replace('\n', ' ') + ", file " + fc.getFileName() + " , instances  "
					+ actionsfc.size());
			System.out.println("file: "+fc.getFileName());

			for (Action action : actionsfc) {
				//--
				if(action instanceof Update) {
					Update up = (Update) action;
					//System.out.println(up);
					System.out.println(up.getNode().getLabel() );
					System.out.println(up.getValue() );
					System.out.println("-");
				}
				else {
					System.err.println("error");
				}
			}
			System.out.println("---");
		}
	}
}
