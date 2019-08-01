package foundation.callable;

import foundation.util.Util;

public class NewObjectWriter implements IBeanWriter {

	@Override
	public void write(EnvelopWriter writer) {
		writer.beginObject();
		
		writer.writeString("id", Util.newShortGUID());
		
		writer.endObject();
	}

	@Override
	public void setBean(Object object) {
		
	}

}
