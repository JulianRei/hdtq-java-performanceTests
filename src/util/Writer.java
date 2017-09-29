package util;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Writer {
	public static void writeToFile(String content, String filename, boolean append) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(filename, append);
			bw = new BufferedWriter(fw);
			bw.write(content);
			bw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public static ArrayList<ComponentQuad> readQueriesFromFile(String filename) throws IOException, ParseException {
		ArrayList<ComponentQuad> list = new ArrayList<ComponentQuad>();
		JSONParser parser = new JSONParser();
		FileReader f = new FileReader(filename);
		JSONArray root = (JSONArray) parser.parse(f);
		Iterator it = root.iterator();
		while(it.hasNext()) {
			JSONObject obj = (JSONObject) it.next();
			ComponentQuad quad = new ComponentQuad();
			quad.setAllFromVirtuoso(
					obj.get("subject").toString(), 
					obj.get("predicate").toString(), 
					obj.get("object").toString(), 
					obj.get("graph").toString()
					);
			list.add(quad);
		}
		return list;
	}

	public static void serialize(HashSet<String> queries, String outputFile) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(outputFile));
		oos.writeObject(queries);
		oos.close();
	}
}
