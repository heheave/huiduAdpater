package unsed;

public class MongonPersistenceOpt implements PersistenceOpt {

	public final String databaseName;

	public final String tableName;

	public MongonPersistenceOpt(String databaseName, String tableName) {
		this.databaseName = databaseName;
		this.tableName = tableName;
	}
}
