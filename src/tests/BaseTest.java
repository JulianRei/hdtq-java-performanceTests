package tests;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import enums.CmdOption;
import util.CmdParser;
import util.ComponentQuad;
import util.Logger;
import util.Writer;
import util.Measures;

public abstract class BaseTest {
	
	public String dataFile;
	private String queryFolder, outputFolder, queryFile;
	public ComponentQuad componentQuad;
	protected boolean verbose;
	private int repetitions;
	private boolean keepCache;
	ArrayList<ComponentQuad> queries;
	
	private long[] numberOfResults;
	
	public final String RESULT_WARM_FILE_NAME = "resultWarm.json";
	public final String RESULT_COLD_FILE_NAME = "resultCold.json";

	public abstract void loadData() throws Exception;
	protected abstract void doWarmup() throws Exception;
	public abstract int doSearch() throws Exception;
	protected abstract void prepareQuery();
	public abstract String getSystem(); 

	public void parseOptions() throws Exception {
		CommandLine cmd = CmdParser.getCommandLine();
		dataFile = cmd.getOptionValue(CmdOption.DATA_FILE.shortOption);
		queryFolder = cmd.getOptionValue(CmdOption.QUERY_FOLDER.shortOption);
		outputFolder = cmd.getOptionValue(CmdOption.OUTPUT_FOLDER.shortOption);
		verbose = cmd.hasOption(CmdOption.VERBOSE.shortOption);
		repetitions = Integer.parseInt(cmd.getOptionValue(CmdOption.REPETITION.shortOption));
		keepCache = cmd.hasOption(CmdOption.KEEP_CACHE.shortOption);
	}
	
	public void setQueryFile(String pattern) {
		queryFile = queryFolder + (queryFolder.endsWith(File.separator) ? "" : File.separator) + pattern + ".txt";
	}
	
	public void performTest() throws Exception  {
		long startTime, endTime;
		int i = 0;
		
		System.out.println("Now reading queries from file: " + queryFile);
		
		// read in queries
		queries = Writer.readQueriesFromFile(queryFile);
		
		long[] measuresCold = new long[queries.size()];
		long[] measuresWarm = new long[queries.size()];
		
		numberOfResults = new long[queries.size()];
		int results = -1;
		for(ComponentQuad query : queries) { // for each query
			componentQuad = query;
			prepareQuery();
			long repeatedTimesCold[] = new long[repetitions];
			long repeatedTimesWarm[] = new long[repetitions];
			for(int j = 0; j < repetitions; j++) {
				Logger.log("Query: " + i + ", repetion: " + j);
				dropCache();
				doWarmup();
				
				// cold:
				startTime = System.nanoTime();
				results = doSearch();
				endTime = System.nanoTime();
				repeatedTimesCold[j] = endTime - startTime;
				
				// warm:
				startTime = System.nanoTime();
				results = doSearch();
				endTime = System.nanoTime();
				repeatedTimesWarm[j] = endTime - startTime;
			}
			measuresCold[i] = (long) Measures.getAverage(repeatedTimesCold);
			measuresWarm[i] = (long) Measures.getAverage(repeatedTimesWarm);
			numberOfResults[i] = results;
			i++;
		}
		
		writeResult(outputFolder + (outputFolder.endsWith(File.separator) ? "" : File.separator) + RESULT_COLD_FILE_NAME, measuresCold);
		writeResult(outputFolder + (outputFolder.endsWith(File.separator) ? "" : File.separator) + RESULT_WARM_FILE_NAME, measuresWarm);
	}
	
	private void dropCache() throws IOException, InterruptedException {
		if(!keepCache) {
			Process p = java.lang.Runtime.getRuntime().exec("sudo sysctl vm.drop_caches=3");
			p.waitFor();
		}
	}

	private void writeResult(String outputFile, long[] measures) throws IOException, ParseException {
		// read JSON
		JSONParser parser = new JSONParser();
		FileReader f = new FileReader(outputFile);
		JSONObject root = (JSONObject) parser.parse(f);
		
		// write JSON
		String system = getSystem();
		String dataset = getDataset();
		String pattern = getFileName(queryFile);
		
		JSONObject patternJSON = new JSONObject();
		
		patternJSON.put("measures", Measures.getAsJSON(measures));
		patternJSON.put("numResults", Measures.getAsJSON(numberOfResults));
		patternJSON.put("min", Measures.getMin(measures));
		patternJSON.put("max", Measures.getMax(measures));
		patternJSON.put("average", Measures.getAverage(measures));
		patternJSON.put("median", Measures.getMedian(measures));
		patternJSON.put("queries", Measures.getNumberOfQueries(measures));
		patternJSON.put("repetitions", repetitions);
		//patternJSON.put("queries", ComponentQuad.getJSONArray(queries));
		
		JSONObject datasetJSON = (JSONObject) root.get(dataset);
		if(datasetJSON == null) {
			datasetJSON = new JSONObject();
			root.put(dataset, datasetJSON);
		}
		
		JSONObject systemJSON = (JSONObject) datasetJSON.get(system);
		if(systemJSON == null) {
			systemJSON = new JSONObject();
			datasetJSON.put(system, systemJSON);
		}
		
		systemJSON.put(pattern, patternJSON);
		
		Writer.writeToFile(root.toString(), outputFile, false);
	}
	
	public String getDataset() {
		String[] pathParts = dataFile.split(Pattern.quote(File.separator));
		return pathParts[pathParts.length - 2];
	}

	private String getFileName(String filePath) {
		File file = new File(filePath);
		String[] parts = file.getName().split("\\.");
		String fileName = "";
		for(int i = 0; i < parts.length - 1; i++) {
			fileName += parts[i];
		}
		return fileName;
	}
	public void ensureOutputFilesExist() {
		File folder = new File(outputFolder);
		folder.mkdirs();
		String resultColdPath = outputFolder + (outputFolder.endsWith(File.separator) ? "" : File.separator) + RESULT_COLD_FILE_NAME;
		File resultColdFile = new File(resultColdPath);
		if(!resultColdFile.exists() || resultColdFile.isDirectory()) { 
			Writer.writeToFile("{}", resultColdPath, false);
		}
		String resultWarmPath = outputFolder + (outputFolder.endsWith(File.separator) ? "" : File.separator) + RESULT_WARM_FILE_NAME; 
		File resultWarmFile = new File(resultWarmPath);
		if(!resultWarmFile.exists() || resultWarmFile.isDirectory()) { 
			Writer.writeToFile("{}", resultWarmPath, false);
		}
	}
}
