package main;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.log4j.varia.NullAppender;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import enums.CmdOption;
import enums.RDFSystem;
import tests.BaseTest;
import tests.TestFactory;
import util.CmdParser;

public class Run {
	public static void main(String[] args) throws Exception {
		org.apache.log4j.BasicConfigurator.configure(new NullAppender());
		CmdParser.setArgs(args);
		BaseTest test = TestFactory.getTest(RDFSystem.getSystemFromExtension(getExtension()));
		test.parseOptions();
		test.ensureOutputFilesExist();
		ArrayList<String> patterns = getPatterns(test);
		if(patterns.size() == 0) {
			System.out.println("All patterns already tested. Use -F to force the test.");
		} else {
			test.loadData();
			int i = 1;
			for(String pattern: patterns) {
				System.out.println("current pattern: " + pattern + " (" + i + "/" + patterns.size() + ")");
				test.setQueryFile(pattern);
				test.performTest();
				i++;
			}
		}
	}
	
	private static String getExtension() throws Exception {
		CommandLine cmd = CmdParser.getCommandLine();
		String file = cmd.getOptionValue(CmdOption.DATA_FILE.shortOption);
		String[] parts = file.split("\\.");
		return parts[parts.length - 1];
	}
	
	private static ArrayList<String> getPatterns(BaseTest test) throws Exception {
		CommandLine cmd = CmdParser.getCommandLine();
		String[] requestedPatterns = cmd.getOptionValues(CmdOption.PATTERNS.shortOption);
		String outputFolder = cmd.getOptionValue(CmdOption.OUTPUT_FOLDER.shortOption);
		String warmFile = outputFolder + File.separator + test.RESULT_WARM_FILE_NAME;
		String coldFile = outputFolder + File.separator + test.RESULT_COLD_FILE_NAME;
		ArrayList<String> result = new ArrayList<String>();
		for(String pattern: requestedPatterns) {
			if(!queryFileExists(pattern)) {
				System.out.println("Query file does not exist: " + test.getDataset() + " "  + test.getSystem() + " " + pattern);
				continue;
			}
			if(!cmd.hasOption(CmdOption.FORCE.shortOption) && 
				alreadyTested(warmFile, test.getDataset(), test.getSystem(), pattern) &&
				alreadyTested(coldFile, test.getDataset(), test.getSystem(), pattern)
				){
				System.out.println("Already tested: " + test.getDataset() + " " + test.getSystem() + " " + pattern);
				continue;
			}
			// all good, query file exists and not yet tested (or forcing test)
			result.add(pattern);
		}
		return result;
	}
	
	public static boolean alreadyTested(String resultFile, String dataset, String system, String pattern) throws IOException, ParseException {
		// read JSON
		JSONParser parser = new JSONParser();
		FileReader f = new FileReader(resultFile);
		JSONObject root = (JSONObject) parser.parse(f);
		if(!root.containsKey(dataset)) {
			return false;
		}
		JSONObject jsonDataset = (JSONObject) root.get(dataset);
		if(!jsonDataset.containsKey(system)) {
			return false;
		}
		JSONObject jsonSystem = (JSONObject) jsonDataset.get(system);
		if(!jsonSystem.containsKey(pattern)) {
			return false;
		}
		return true;
	}
	
	private static boolean queryFileExists(String pattern) throws Exception {
		CommandLine cmd = CmdParser.getCommandLine();
		String queryFolder = cmd.getOptionValue(CmdOption.QUERY_FOLDER.shortOption);
		File file = new File(queryFolder + File.separator + pattern + ".txt");
		return file.exists();
	}
}
