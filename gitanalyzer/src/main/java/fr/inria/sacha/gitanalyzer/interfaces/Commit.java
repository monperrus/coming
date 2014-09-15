package fr.inria.sacha.gitanalyzer.interfaces;

import java.util.List;

// This object contain's the date of the commit and all (new ?) fragment
public interface Commit {
	
	/** Return a list of FileCommit affected by commit
	 * @return list of pFileCommit
	 */
	public List<FileCommit> getFileCommits();
	public List<FileCommit> getJavaFileCommits();

	/** Get the name of the commit (SHA-1)
	 * @return the commint name (SHA-1 code)
	 */
	public String getName();

	public boolean containsJavaFile();

	public String getShortMessage();
	
	public String getFullMessage();

	public int getRevCommitTime();
	
	public String getRevDate();

	
	
}
