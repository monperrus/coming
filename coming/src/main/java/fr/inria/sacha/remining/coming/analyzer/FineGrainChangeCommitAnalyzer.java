package fr.inria.sacha.remining.coming.analyzer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.compare.rangedifferencer.RangeDifference;
import org.eclipse.jdt.core.dom.ASTNode;

import comparison.Fragmentable;
import comparison.FragmentableComparator;
import comparison.LineComparator;
import fr.inria.sacha.gitanalyzer.interfaces.Commit;
import fr.inria.sacha.gitanalyzer.interfaces.CommitAnalyzer;
import fr.inria.sacha.gitanalyzer.interfaces.FileCommit;
import fr.inria.sacha.remining.coming.entity.ActionType;
import fr.inria.sacha.remining.coming.entity.EntityType;
import fr.inria.sacha.remining.coming.entity.GranuralityType;
import fr.inria.sacha.remining.coming.util.ConfigurationProperties;
import fr.labri.gumtree.ProduceFileTree;
import fr.labri.gumtree.actions.Action;
import fr.labri.gumtree.actions.Delete;
import fr.labri.gumtree.actions.Insert;
import fr.labri.gumtree.actions.Move;
import fr.labri.gumtree.actions.Update;
import fr.labri.gumtree.gen.jdt.ProduceJDTTree;

/**
 * Commit analyzer: It searches fine grain changes.
 *
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class FineGrainChangeCommitAnalyzer implements CommitAnalyzer {

	Logger log = Logger.getLogger(FineGrainChangeCommitAnalyzer.class.getName());

	// PARAMETERS
	public static int MAX_LINES_PER_HUNK;

	public static int MAX_HUNKS_PER_FILECOMMIT;

	public static int MAX_FILES_PER_COMMIT;

	public static int MAX_AST_CHANGES_PER_FILE;

	public static int MIN_AST_CHANGES_PER_FILE;

	public static boolean excludeCommitWithOutTest;

	// CLASS FIELDS
	ActionType operationType = null;
	/**
	 * NOTE: for the moment, this field is a String (and not an Enum) due one
	 * can consider different taxonomies (JDT, CD)
	 */
	String typeLabel = null;
	FragmentableComparator comparator = new LineComparator(); // new JavaTokenComparator();
	public static GranuralityType granularity = GranuralityType.CD;

	/**
	 *
	 * @param typeLabel node label to mine
	 * @param operationType operation type to mine
	 */
	public FineGrainChangeCommitAnalyzer(String typeLabel, ActionType operationType) {

		this.typeLabel = typeLabel;

		this.operationType = operationType;

		MAX_LINES_PER_HUNK = ConfigurationProperties
				.getPropertyInteger("MAX_LINES_PER_HUNK");

		MAX_HUNKS_PER_FILECOMMIT = ConfigurationProperties
				.getPropertyInteger("MAX_HUNKS_PER_FILECOMMIT");

		MAX_FILES_PER_COMMIT = ConfigurationProperties
				.getPropertyInteger("MAX_FILES_PER_COMMIT");

		MAX_AST_CHANGES_PER_FILE = ConfigurationProperties
				.getPropertyInteger("MAX_AST_CHANGES_PER_FILE");

		MIN_AST_CHANGES_PER_FILE = ConfigurationProperties
				.getPropertyInteger("MIN_AST_CHANGES_PER_FILE");

		excludeCommitWithOutTest = ConfigurationProperties
				.getPropertyBoolean("excludeCommitWithOutTest");
	}

	/**
	 *
	 * @param previousVersion
	 * @param nextVersion
	 * @return
	 */
	protected List<RangeDifference> getNumberChanges(String previousVersion, String nextVersion) {
		List<RangeDifference> ranges = new ArrayList<RangeDifference>();

		Fragmentable fPreviousVersion = comparator.createFragmentable(previousVersion);
		Fragmentable fNextVersion = comparator.createFragmentable(nextVersion);
		RangeDifference[] results = comparator.compare(fPreviousVersion, fNextVersion);

		for (RangeDifference diff : results) {
			if (diff.kind() != RangeDifference.NOCHANGE) {
				int length = diff.rightEnd() - diff.rightStart();
				if (length <= MAX_LINES_PER_HUNK)
					ranges.add(diff);
				/*
				else
					log.info("Hunk discarted by large size");
				*/
			}
		}

		return ranges;
	}

	/**
	 * Analyze a commit finding instances of changes return a Map<FileCommit, List>
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Object analyze(Commit commit) {

		// Retrieve a list of file affected by the commit
		List<FileCommit> javaFiles = commit.getJavaFileCommits();
		int countJava = 0;

		for (FileCommit fileCommit : javaFiles) {
			if (!fileCommit.getCompletePath().toLowerCase().endsWith("package-info.java"))
				countJava++;
		}

		if (countJava > MAX_FILES_PER_COMMIT) {
			// System.out.println("Commit not accepted, many files in the commit");
			// log.info("-----");
			return null;
		}

		int nChanges = 0;
		int nTests = 0;

		// The result is divided by File from the commit.
		Map<FileCommit, List> changeInstancesInCommit = new HashMap<FileCommit, List>();
		
		for (FileCommit fileCommit : javaFiles) {
			if (fileCommit.getCompletePath().toLowerCase().contains("test")) {
				nTests++;
				continue;
			}
			if (fileCommit.getCompletePath().toLowerCase()
					.endsWith("package-info.java")) {
				continue;
			}

			List<RangeDifference> nChangesFile = getNumberChanges(
					fileCommit.getPreviousVersion(),
					fileCommit.getNextVersion());

			// First filter by hunks.
			if (nChangesFile.size() == 0) {
				// log.info("Commit not accepted, 0 hunks");
				return null;
			}
			if (nChangesFile.size() > MAX_HUNKS_PER_FILECOMMIT) {
				// log.info("Commit not accepted, contains many hunks");
				return null;
			}

			String left = fileCommit.getPreviousVersion();
			// IDocument idocleft = new SimpleDocument(left);
			String right = fileCommit.getNextVersion();

			if (!left.trim().isEmpty()) {
				GTFacade cdiff = new GTFacade();
				List<Action> allActions;

				try {
					allActions = cdiff.analyzeContent(left, right, granularity);

					if (allActions.size() > MAX_AST_CHANGES_PER_FILE) {
						// log.info("File Commit not accepted, it has more changes that allowed");
						continue;
					}

					if (allActions.size() < MIN_AST_CHANGES_PER_FILE) {
						// log.info("File Commit not accepted, it has more changes that allowed");
						continue;
					}

					if (allActions.size() > 0) {
						List<Action> filterActions = filterActions(allActions, typeLabel, operationType, granularity);
						nChanges += filterActions.size();

						if (filterActions.size() > 0)
							changeInstancesInCommit.put(fileCommit, filterActions);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (excludeCommitWithOutTest && nTests == 0 && nChanges > 0) {
			log.info("commit: "
					+ commit.getName()
					+ " excluded: it does not have any test, but contains instances.");
			return null;
		}

		if (nTests > 0 && nChanges > 0) {
			int instances = 0;

			for (List instancesOfCommit : changeInstancesInCommit.values())
				instances += instancesOfCommit.size();

			log.info("commit: "+ commit.getName() +", Tests "+ nTests +", Actions: "+ instances);
		}

		return changeInstancesInCommit;
	}

	public int withPattern;
	public int withoutPattern;
	public int withError;

	/**
	 * Return the actions according to a type label.
	 *
	 * @param actions
	 * @param typeLabel
	 * @param operationType
	 * @param granularity2
	 * @return
	 */
	protected List<Action> filterActions(List<Action> actions, String typeLabel, ActionType operationType, GranuralityType granularity) {
		actions.removeAll(Collections.singleton(null));
		List<Action> filter = new ArrayList<Action>();

		for (Action action : actions) {
			try {
				if (action.getNode().getTypeLabel().equals("CompilationUnit"))
					continue;

				EntityType actionET = null;

				if (granularity.equals(GranuralityType.CD))
					actionET = EntityType.values()[action.getNode().getType()]; // entityTypesKeys.get(action.getNode().getType());
				else
					actionET = entityTypesKeys.get(action.getNode().getType());

				if (actionET == null) {
					// TODO:
					// there are some AST element not mapped to ENTITY TYPES
					// e.g. THEN_STATEMENT, THROW
					// System.err.println("Null entity for action "+action);
				} else if (actionET.name().equals(typeLabel) && matchTypes(action, operationType))
					filter.add(action);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return filter;
	}

	public static Map<Integer, EntityType> entityTypesKeys = new HashMap<Integer, EntityType>();
	static {
		if (entityTypesKeys.isEmpty()) {
			for (Field field : EntityType.class.getFields()) {
				try {
					for (Field astField : ASTNode.class.getFields()) {
						if (field.getName().equals(astField.getName())) {
							int type = astField.getInt(ASTNode.class);
							entityTypesKeys.put(type, EntityType.valueOf(field.getName()));
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected boolean matchTypes(Action action, ActionType type) {

		return (type.equals(ActionType.INS) && (action instanceof Insert))
				|| (type.equals(ActionType.DEL) && (action instanceof Delete))
				|| (type.equals(ActionType.MOV) && (action instanceof Move))
				|| (type.equals(ActionType.UPD) && (action instanceof Update));
	}

	public String getTypeLabel() {
		return typeLabel;
	}

	public void setTypeLabel(String typeLabel) {
		this.typeLabel = typeLabel;
	}

	public GranuralityType getGranularity() {
		return granularity;
	}

	public void setGranularity(GranuralityType granularity) {
		this.granularity = granularity;
	}

	public FragmentableComparator getComparator() {
		return comparator;
	}

	public void setComparator(FragmentableComparator comparator) {
		this.comparator = comparator;
	}

	public boolean isExcludeCommitWithOutTest() {
		return excludeCommitWithOutTest;
	}

	public void setExcludeCommitWithOutTest(boolean excludeCommitWithOutTest) {
		this.excludeCommitWithOutTest = excludeCommitWithOutTest;
	}

	public ActionType getOperationType() {
		return operationType;
	}

	public void setOperationType(ActionType operationType) {
		this.operationType = operationType;
	}
}