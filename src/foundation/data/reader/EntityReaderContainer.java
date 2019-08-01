package foundation.data.reader;

import java.util.HashMap;
import java.util.Map;

public class EntityReaderContainer {

	private static EntityReaderContainer instance;
	private Map<Class<?>, IEntityReader> items;

	private EntityReaderContainer() {
		items = new HashMap<Class<?>, IEntityReader>();
	}

	public static synchronized EntityReaderContainer getInstance() {
		if (instance == null) {
			instance = new EntityReaderContainer();
		}

		return instance;
	}

	public IEntityReader getEntityReader(Class<?> clazz) throws Exception {

		IEntityReader reader = items.get(clazz);

		if (reader == null) {
			reader = EntityReader.getInstance(clazz);

			// 保证集合类class、Map类class不在items中
			if (reader instanceof CollectionReader || reader instanceof MapReader) {
				return reader;
			}
			items.put(clazz, reader);
		}

		return reader;
	}

	public ObjectReader getObjectReader(Class<?> clazz) throws Exception {
		IEntityReader reader = items.get(clazz);

		if (reader == null) {
			reader = new ObjectReader(clazz);
			items.put(clazz, reader);
		}

		return (ObjectReader) reader;
	}

	public MapReader getMapReader(Class<?> clazz) throws Exception {
		IEntityReader reader = items.get(clazz);

		if (reader == null) {
			reader = new MapReader(clazz);
			items.put(clazz, reader);
		}

		return (MapReader) reader;
	}

	public CollectionReader getCollectionReader(Class<?> clazz) throws Exception {
		IEntityReader reader = items.get(clazz);

		if (reader == null) {
			reader = new CollectionReader(clazz);
			items.put(clazz, reader);
		}

		return (CollectionReader) reader;
	}

}
