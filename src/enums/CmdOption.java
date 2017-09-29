package enums;

public enum CmdOption{
	//		  	  short, long,         hasArg, moreThanOneArg, required, description
	HELP      	  ("h",  "help",       false,  false,          false,    "show help."),
	QUERY_FOLDER  ("q",  "queryfolder",true,   false,          true,     "query folder"),
	REPETITION    ("r",  "repetition", true,   false,          true,     "how often each query is executed"),
	DATA_FILE	  ("f",  "file",       true,   false,          true,     "datafile to use"),
	OUTPUT_FOLDER ("o",  "output",     true,   false,        true,     "output file"),
	VERBOSE	  	  ("v",  "verbose",    false,  false,          false,    "verbose mode. Printing query results"),
	PATTERNS      ("p",  "patterns",   true,   true,           true,     "patterns to be executed"),
	KEEP_CACHE    ("k",  "keep cache", false,  false,          false,    "if set, the cache is kept and NOT dropped"),
	FORCE		  ("F",  "Force",      false,  false,          false,    "if set, all provided patterns will be done, even if they are already done");
	
	public final String shortOption;
	public final String longOption;
	public final boolean required;
	public final String description;
	public final boolean hasArg;
	public final boolean moreThanOneArg;
	
	private CmdOption(String so, String lo, boolean ha, boolean moreThanOne, boolean req, String desc){
		shortOption = so;
		longOption = lo;
		required = req;
		description = desc;
		hasArg = ha;
		moreThanOneArg = moreThanOne;
	}
}
