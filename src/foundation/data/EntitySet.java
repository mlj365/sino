package foundation.data;

import foundation.persist.TableMeta;
import foundation.persist.TableMetaCenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class EntitySet implements Iterable<Entity> {

	private static TableMetaCenter metaCenter = TableMetaCenter.getInstance();
	private TableMeta tableMeta;
	private List<Entity> entityList;
	private int pos;

	public EntitySet(TableMeta tableMeta) {
		this(tableMeta, 15);
	}

	public EntitySet(TableMeta tableMeta, int size) {
		this.tableMeta = tableMeta;
		entityList = new ArrayList<Entity>(size);
		pos = -1;
	}

	public static EntitySet getInstance(String tableName) throws Exception {
		TableMeta tableMeta = metaCenter.get(tableName);

		if (tableMeta == null) {
			throw new Exception("table not exists: " + tableName);
		}

		return new EntitySet(tableMeta);
	}

	public void append(EntitySet set) {
		if (set != null) {
			entityList.addAll(set.getEntityList());
		}
	}

	public void append(Entity entity) {
		entityList.add(entity);

	}

	public Entity append() {
		Entity entity = new Entity(tableMeta);
		entityList.add(entity);

		return entity;
	}

	public List<Entity> getEntityList() {
		return entityList;
	}

	public List<Entity> getSubData(int fromIndex, int size) {
        return  entityList.subList(fromIndex, fromIndex + size);
	}

	public Entity next() {
		pos++;

		if (pos < entityList.size()) {
			return entityList.get(pos);
		}

		return null;
	}

	public String[] getLowerNames() {
		return tableMeta.getLowerNames();
	}

	public String[] getVirtualNames() {
		return tableMeta.getVirtualNames();
	}

	public String[] getVirtualLowerNames() {
		return tableMeta.getVirtualLowerNames();
	}

	public boolean isEmpty() {
		return entityList.isEmpty();
	}

	public String getName() {
		return tableMeta.getName();
	}

	public int getFieldCount() {
		return tableMeta.getFieldCount();
	}

	public void first() {
		pos = -1;
	}

	public TableMeta getTableMeta() {
		return tableMeta;
	}

	@Override
	public Iterator<Entity> iterator() {
		return entityList.iterator();
	}

	public void clear() {
		entityList.clear();
		pos = -1;
	}

	public int size() {
		return entityList.size();
	}

	public void appendValidField(List<String> validColumns, List<String> validFields) {
		getTableMeta().loadVaildField(validColumns, validFields);
	}

	public List<String>  getFieldList(String fieldName) {
		if (entityList == null || entityList.size() == 0) {
			return null;
		}
		List<String> orders =entityList.stream().map(entity -> entity.getString(fieldName)).collect(Collectors.toList());
		return orders;
	}
}
