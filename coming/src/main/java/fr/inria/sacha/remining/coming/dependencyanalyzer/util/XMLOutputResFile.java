package fr.inria.sacha.remining.coming.dependencyanalyzer.util;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import fr.inria.sacha.gitanalyzer.interfaces.Commit;
import fr.inria.sacha.remining.coming.entity.ActionType;

public class XMLOutputResFile {
	
	private final static String ROOT_NODE_NAME = new String("analysis");
	private final static String ROOT_NODE_COMMIT_LIST_NAME = new String("history");
	private final static String COMMIT_NODE_NAME = new String("commit");
	
	private Document rootXML;
	private Element rootNode;
	private Element rootNodeCommitList;
	private Element gitRepoNameNode;
	private Element totalNumberCommitInGitRepo;
	private Element currentCommitNode;
		
	private int numberResult;
	
	public XMLOutputResFile() {
		this.rootNode = new Element(ROOT_NODE_NAME);
		this.rootXML = new Document(this.rootNode);
		this.rootNodeCommitList = new Element(ROOT_NODE_COMMIT_LIST_NAME);
		this.currentCommitNode = new Element(COMMIT_NODE_NAME);
		this.numberResult = 0;
	}
	
	public void setNumberOfCommitInRepository(int numberOfCommit) {
		this.totalNumberCommitInGitRepo = new Element("number-of-commit");
		this.totalNumberCommitInGitRepo.setText(Integer.toString(numberOfCommit));		
	}
	
	public void setGitRepositoryName(String gitRepositoryName) {
		this.gitRepoNameNode = new Element("git-repository");
		this.gitRepoNameNode.setText(gitRepositoryName);		
	}
	
	public void addAnalyzedCommit(Commit commit) {
		if(!this.currentCommitNode.getChildren().isEmpty()) {
			this.rootNodeCommitList.addContent(this.currentCommitNode);
		}
		
		this.currentCommitNode = new Element(COMMIT_NODE_NAME);
		this.currentCommitNode.setAttribute("number", commit.getName());
	}
	
	public void addCommitFile(String fileName, ActionType action, Collection<String> dependencies) {
		
		Element fileCommitNode = new Element("file");
		fileCommitNode.setAttribute("name", fileName);
		
		switch(action) {
			case INS : fileCommitNode.addContent(new Element("type").setText("insertion")); break;
			case DEL : fileCommitNode.addContent(new Element("type").setText("deletion")); break;
			default: fileCommitNode.addContent(new Element("type").setText("unknown"));
		}
		
		/*
		Element dependenciesNode = new Element("dependencies");
		
		for(String dependency : dependencies) {
			dependenciesNode.addContent(new Element("dependency").setText(dependency));
		}
		
		fileCommitNode.addContent(dependenciesNode);
		*/
		
		this.currentCommitNode.addContent(fileCommitNode);
		this.numberResult++;
	}
	
	public void save() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
		Calendar cal = Calendar.getInstance();
		
		String XMLFilename = dateFormat.format(cal.getTime()) +".xml";
		
		this.rootNodeCommitList.setAttribute("number-of-result", Integer.toString(this.numberResult));
		
		this.rootNode.addContent(this.gitRepoNameNode);
		this.rootNode.addContent(this.totalNumberCommitInGitRepo);
		this.rootNode.addContent(this.rootNodeCommitList);
		
		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(this.rootXML, new FileWriter(XMLFilename));
	}
	
	public void display() {
		 XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		 
	     try {
			sortie.output(this.rootXML, System.out);
		} catch (IOException e) {
			System.out.println("Impossible to display the result xml file");
		}
	}
}
