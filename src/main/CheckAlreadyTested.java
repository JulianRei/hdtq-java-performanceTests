package main;

import java.io.IOException;

import org.json.simple.parser.ParseException;

public class CheckAlreadyTested {
	public static void main(String[] args) throws IOException, ParseException {
		//system
		String resultFile = args[0];
		String dataset = args[1];
		String[] patterns = new String[]{"SPOG", "SPOV", "SPVG", "SPVV", "SVOG", "SVOV", "SVVG", "SVVV", "VPOG", "VPOV", "VPVG", "VPVV", "VVOG", "VVOV", "VVVG", "VVVV", "VPVVS", "VPVVL"};
		String[] systems = new String[]{"HDTAT", "HDTAG", "JENA", "VIRTUOSO", "VIRTUOSO+"};
		for(String pattern: patterns) {
			for(String system : systems) {
				if(Run.alreadyTested(resultFile, dataset, system, pattern)) {
					System.out.print("█");
				} else {
					System.out.print("x");
				}
			}
			System.out.print(" ");
		}
	}
}
