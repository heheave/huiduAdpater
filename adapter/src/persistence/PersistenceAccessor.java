package persistence;

import java.util.Collection;

public interface PersistenceAccessor {
	
	// for once persistence
	void persistenceOne(PersistenceOpt opt, PersistenceData data) throws Exception;
	
	// for batch persistence
	void persistenceBatch(PersistenceOpt opt, Collection<PersistenceData> data) throws Exception;
	
	void close() throws Exception;
}
