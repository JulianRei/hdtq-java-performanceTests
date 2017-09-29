package enums;

public enum RDFSystem {
	HDT, JENA, VIRTUOSO;
	public static RDFSystem getSystemFromExtension(String sys) {
		switch(sys) {
		case "hdt":
			return RDFSystem.HDT;
		case "tdb":
			return RDFSystem.JENA;
		case "vir":
			return RDFSystem.VIRTUOSO;
		default:
			throw new RuntimeException("Cannot guess system from file extension");
		}
	}
}