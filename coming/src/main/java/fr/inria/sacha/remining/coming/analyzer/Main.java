package fr.inria.sacha.remining.coming.analyzer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import fr.inria.sacha.gitanalyzer.interfaces.FileCommit;
import fr.inria.sacha.remining.coming.entity.ActionType;
import fr.inria.sacha.remining.coming.entity.EntityType;
import fr.inria.sacha.remining.coming.util.XMLOutput;
/**
 * 
 * @author  Matias Martinez, matias.martinez@inria.fr
 *
 */
public class Main {

	public static void main(String[] args) {
	
		String message = "USAGE --parameters-- [projectLocation] [entity] [action] [message (optional)] ; to get the entities use -e, to get the actions use -a";
		
		if(args == null ){
				System.out.println(message);
				return;
		}

		if(args.length == 1)
			if(args[0].equals("-e"))
				System.out.println("ENTITIES: "+ Arrays.toString(EntityType.values()));
			else
				if(args[0].equals("-a"))
					System.out.println("ACTIONS: "+ Arrays.toString(ActionType.values()));
			
		if(args.length < 3)
			return ;
			
	    ChangeInstanceMiner c = new ChangeInstanceMiner();
	    String projectLocation = args[0];
	    String entity = args[1];
	    String action = args[2];
	    String messageHeuristic = (args.length == 4)? args[3]: "";
	    Map<FileCommit, List> instancesFound = c.analize(
	    		projectLocation
	    		, "HEAD", 
	    		EntityType.valueOf(entity).name(), ActionType.valueOf(action), messageHeuristic);
	    c.printResultDetails(instancesFound);
	    XMLOutput.print(instancesFound);
	}
}
