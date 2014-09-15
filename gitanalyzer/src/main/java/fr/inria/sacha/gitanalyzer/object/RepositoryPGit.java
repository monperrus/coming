package fr.inria.sacha.gitanalyzer.object;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import fr.inria.sacha.gitanalyzer.interfaces.Commit;
import fr.inria.sacha.gitanalyzer.interfaces.RepositoryP;


public class RepositoryPGit implements RepositoryP {

	private Repository repository;
	
	private List<Commit> commits;
	private String masterBranch;
	
	/** Init a Git repository navigation
	 * @param pathOfRepo The path of the git repository
	 * @param comp The comparator used to set the grain of fragments
	 * @param branch The branch to analyze
	 */
	public RepositoryPGit(String pathOfRepo, String branch) {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		String path = pathOfRepo;
		if (!path.endsWith("/"))
			path = path + "/";
		path = path + ".git";
		try {
			repository = builder
					.setGitDir(new File(path))
					.readEnvironment().findGitDir().build();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		commits = new ArrayList<Commit>();
		masterBranch = branch;
		try {
			loadCommits();
		} catch (RevisionSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AmbiguousObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IncorrectObjectTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Load all commits of the repository
	 * @throws RevisionSyntaxException
	 * @throws AmbiguousObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws IOException
	 */
	private void loadCommits() throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException, IOException {
		RevWalk revWalk = new RevWalk(repository);
		ObjectId from = repository.resolve(masterBranch);
		revWalk.markStart(revWalk.parseCommit(from));
		revWalk.setTreeFilter(TreeFilter.ALL);
		revWalk.sort(RevSort.REVERSE ,true);

		for (RevCommit c : revWalk) {
			Commit commit = new CommitGit(this, c);
			commits.add(commit);
//			System.out.println("IN REPO LOADING");
//			System.out.println(c.getId().getName());
		}		
	}

	@Override
	public List<Commit> history() {
		return this.commits;
	}

	@Override
	public Repository getRepository() {
		return this.repository;
	}
}
