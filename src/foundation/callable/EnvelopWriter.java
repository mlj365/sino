package foundation.callable;

import foundation.config.Configer;
import foundation.data.Entity;
import foundation.data.EntitySet;
import foundation.data.reader.EntityReaderContainer;
import foundation.data.reader.IEntityReader;
import foundation.util.Util;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

public class EnvelopWriter {

	public final static String Code_URL = "url";
	private final static String NAME_EMPTY = "emptyname";

	protected static Logger logger;
	protected static EntityReaderContainer objectReaderContainer;
	private HttpServletResponse response;
	protected PrintWriter writer; // 响应字符流
	protected OutputStream outputStream; // 响应字节流
	private Stack<Boolean> notEmpty;
	private boolean encode;

	static {
		logger = Logger.getLogger(EnvelopWriter.class);
		objectReaderContainer = EntityReaderContainer.getInstance();
	}

	public EnvelopWriter(HttpServletRequest request, HttpServletResponse response) throws IOException {
		this.response = response;
		notEmpty = new Stack<Boolean>();
		encode = true;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	private void getPrintWriterFromResponse() throws IOException {// 获取指向浏览器的字符输出流
		response.setCharacterEncoding("UTF-8");
		writer = response.getWriter();

		notEmpty.clear();
		notEmpty.add(false);
	}

	public void replay(ResultPool result) throws Exception {
		List<ResultItem> itemList = result.getItemList();

		getPrintWriterFromResponse();

		if (result.isJson()) {
			writer.write(result.getJson());
			return;
		}
		
		writeBegin();
		try {
			writeBoolean(IEnvelop.ResultCode_Success, result.isSuccess());

			if (result.isSuccess()) {
				for (ResultItem item: itemList) {
					if (item instanceof ObjectItem) {
						IBeanWriter beanWriter = item.getBeanWriter();
						writeOneObjectItem(item.getName(), item.getValue(), beanWriter);
					}
					else if (item instanceof JsonItem) {
						writeOneJsonItem(item.getName(), item.getValue());
					}
				}
			}
			else {
				writeString(IEnvelop.ResultCode_ErrorCode, result.getErrorCode());
				writeString(IEnvelop.ResultCode_ErrorMessage, result.getErrorMessage());
			}
		}
		finally {
			writeEnd();
		}
	}

	@SuppressWarnings("rawtypes")
	private void writeOneObjectItem(String name, Object value, IBeanWriter beanWriter) throws Exception {
		if (value instanceof IBeanWriter) {
			writeSmartWriter(name, (IBeanWriter) value);
		}
		else if (value instanceof String) {
			writeString(name, (String) value);// 写字符串
		}
		else if (value instanceof Integer) {
			writeInteger(name, (Integer) value);// 写整型
		}
		else if (value instanceof BigDecimal) {
			writeBigDecimal(name, (BigDecimal) value);// 写大小数
		}
		else if (value instanceof Date) {
			writeDate(name, (Date) value);// 写日期
		}
		else if (value instanceof Boolean) {
			writeBoolean(name, (Boolean) value);// 写boolean
		}
		else if (value instanceof Entity) {
			writeEntity(name, (Entity) value);// 写实体
		}
		else if (value instanceof EntitySet) {
			writeEntitySet(name, (EntitySet) value);// 写实体集
		}
		else if (value instanceof Collection) {
			writeCollection(name, (Collection) value, beanWriter);
		}

		else {
			writeObject(name, value, beanWriter);// 写对象
		}
	}


	private void writeOneJsonItem(String name, Object value) throws Exception {
		writeComma();
		
		writeName(name);
		writer.write((String)value);

		setNotEmpty();
	}

	private void writeSmartWriter(String name, IBeanWriter smartWriter) {
		writeComma();
		writeName(name);
		
		smartWriter.write(this);
		setNotEmpty();
	}
	
	private void writeBegin() {
		writer.write("{");
	}

	private void writeEnd() {
		writer.write("}");
	}

	public void flush() {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			}
			catch (Exception e) {
				logger.error(e);
			}
		}

		if (outputStream != null) {
			try {
				outputStream.flush();
				outputStream.close();
			}
			catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public void write(String value) {
		writer.write(value);
	}

	public void writeString(String name, String value) {
		writeComma();
		writeName(name);

		writer.write("\"");
		writer.write(encode(value));
		writer.write("\"");

		setNotEmpty();
	}
	
	public void writeInteger(String name, Integer value) {
		writeComma();
		writeName(name);

		writer.write(String.valueOf(value));
		setNotEmpty();
	}

	public void writeBigDecimal(String name, BigDecimal value) {
		writeComma();
		writeName(name);

		if (value != null) {
			writer.write(value.toString());
		}
		else {
			writer.write("null");
		}
		
		setNotEmpty();
	}

	public void writeDate(String name, Date value) {
		writeComma();
		writeName(name);

		if (value == null) {
			writer.write("null");
		}
		else {
			writer.write("\"");
			writer.write(Util.DataTimeToString(value));
			writer.write("\"");
		}

		setNotEmpty();
	}

	public void writeBoolean(String name, Boolean value) {
		writeComma();
		writeName(name);

		writer.write(value.toString());
		setNotEmpty();
	}

	public void writeEntity(String name, Entity entity) {
		if (entity == null) {
			return;
		}
		
		if (name == null) {
			name = IEnvelop.ResultCode_DataLine;
		}

		writeComma();
		writeName(name);

		doWriteEntity(entity);
		setNotEmpty();
	}

	private void doWriteEntity(Entity entity) {
		String[] propertyNames = entity.getLowerNames();

		writer.write("{");

		int cnt = propertyNames.length;
		int i;

		for (i = 0; i < cnt; i++) {
			if (i > 0) {
				writer.print(", ");
			}

			writeName(propertyNames[i]);
			try {
				writer.write(entity.getJSONString(i));
			}
			catch (Exception e) {
				writer.write("error");
			}
		}

		writer.write("}");
	}

	public void writeEntitySet(String name, EntitySet entitySet) {
		if (name == null) {
			name = IEnvelop.ResultCode_DataSet;
		}
		
		writeComma();

		if (entitySet == null || entitySet.size() == 0) {
			writer.write("\"total\": 0, ");
			writeName(name);
			writer.write("[]");
			return;
		}

		int cnt = entitySet.size();

		writer.write("\"total\": " + cnt);
		writer.write(", ");
		writeName(name);
		writer.write("[");

		boolean empty = true;

		for (Entity entity : entitySet) {
			if (!empty) {
				writer.write(", ");
			}

			doWriteEntity(entity);

			empty = false;
		}

		writer.write("]");
		setNotEmpty();
	}

	@SuppressWarnings("rawtypes")
	private void writeCollection(String name, Collection collection, IBeanWriter beanWriter) throws Exception {
		writeComma();
		writeName(name);

		beginArray();

		if (collection.isEmpty()) {
			endArray();
			return;
		}

		for (Object obj : collection) {
			writeOneObjectItem(NAME_EMPTY, obj, beanWriter);
		}

		endArray();
	}


	public void writeObject(String name, IBeanWriter smartWriter) {
		writeComma();
		writeName(name);

		beginObject();
		smartWriter.write(this);
		endObject();

		setNotEmpty();
	}

	public void writeObject(String name, Collection<IBeanWriter> smartWriters) {
		writeComma();
		writeName(name);

		beginObject();

		for (IBeanWriter smartWriter : smartWriters) {
			smartWriter.write(this);
		}

		endObject();
		setNotEmpty();
	}

	public void writeArray(String name, Collection<? extends IBeanWriter> smartWriters) {
		writeComma();
		writeName(name);

		beginArray();

		for (IBeanWriter smartWriter : smartWriters) {
			writeComma();
			smartWriter.write(this);
			setNotEmpty();
		}

		endArray();
		setNotEmpty();
	}

	public void writeObject(Object object) {
		writeObject(null, object, null);
	}

	public void writeObject(String name, Object object, IBeanWriter beanWriter) {
		try {
			writeComma();
			writeName(name);
			
			if (beanWriter == null) {
				IEntityReader entityReader = objectReaderContainer.getEntityReader(object.getClass());
				writer.write(entityReader.getJSONString(object));
			}
			else {
				beanWriter.setBean(object);
				beanWriter.write(this);
			}

			setNotEmpty();
		}
		catch (Exception e) {
			e.printStackTrace();
			writeString(name, "error");
		}
	}

	private String encode(String value) {
		if (!encode) {
			return value;
		}
		
		String result = "";

		if (value == null) {
			return result;
		}

		try {
			result = URLEncoder.encode(value, "UTF-8");
			result = result.replace("+", "%20");
		}
		catch (Exception e) {
			result = value;
		}

		return result;
	}

	public void ReplyError(String errorMessage) throws IOException {
		ReplyError(null, errorMessage);
	}

	public void ReplyError(String errorCode, String errorMessage) throws IOException {
		getPrintWriterFromResponse();

		writeBegin();
		try {
			writeBoolean(IEnvelop.ResultCode_Success, false);

			if (!Util.isEmptyStr(errorCode)) {
				writeString(IEnvelop.ResultCode_ErrorCode, errorCode);
			}

			writeString(IEnvelop.ResultCode_ErrorMessage, errorMessage);
		}
		finally {
			writeEnd();
		}
	}

	public void replayTimeout() throws IOException {
		getPrintWriterFromResponse();

		writeBegin();
		try {
			writeBoolean(IEnvelop.ResultCode_Success, false);

			writer.write("\"timeoutPage\": \"" + URLEncoder.encode(Configer.getPage_TimeOut(), "UTF-8") + "\"");
		}
		finally {
			writeEnd();
		}
	}

	public void beginObject(String propName) {
		if (propName == null) {
			propName = "empty";
		}
		
		propName = propName.toLowerCase();
		
		notEmpty.push(false);
		writer.write("\"" + propName + "\": {");
	}
	
	public void beginObject() {
		notEmpty.push(false);
		writer.write("{");
	}

	public void endObject() {
		notEmpty.pop();
		writer.write("}");
		
		setNotEmpty();
	}

	public void beginArray() {
		notEmpty.push(false);
		writer.write("[");
	}

	public void endArray() {
		notEmpty.pop();
		writer.write("]");
		
		setNotEmpty();
	}

	public void writeComma() {
		if (notEmpty.peek()) {
			writer.write(", ");
		}
	}

	private void setNotEmpty() {
		if (notEmpty.peek()) {
			return;
		}

		notEmpty.pop();
		notEmpty.push(true);
	}
	
	private void writeName(String name) {
		if (name == null) {
			writer.write("\"empty\": ");
			return;
		}
		
		if (!NAME_EMPTY.equals(name)) {
			writer.write("\"" + name.toLowerCase() + "\": ");
		}	
	}

	public boolean isEncode() {
		return encode;
	}

	public void setEncode(boolean encode) {
		this.encode = encode;
	}
	
}
