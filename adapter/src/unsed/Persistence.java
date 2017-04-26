package unsed;

import java.util.Collection;

public interface Persistence<E, T> {

	E getAccesor(String dbName) throws Exception;

	void closeAccesor(E accesor) throws Exception;

	boolean persistence(PersistenceOpt po, T event) throws Exception;

	boolean persistenceByBatch(PersistenceOpt po, Collection<T> events) throws Exception;

	void flush() throws Exception;

	void close();

}
