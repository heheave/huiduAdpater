package unsed;

public class FilePersistenceOpt implements PersistenceOpt {

	public final String filePath;
	public final String fileName;

	public FilePersistenceOpt(String filePath, String fileName) {
		this.filePath = filePath;
		this.fileName = fileName;
	}
}
