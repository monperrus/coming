package fr.inria.sacha.gitanalyzer.interfaces;

/** analizes a commit */
public interface CommitAnalyzer {

  Object analyze(Commit commit);

}
