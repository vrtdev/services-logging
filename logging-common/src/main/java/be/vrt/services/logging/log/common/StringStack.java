package be.vrt.services.logging.log.common;

public class StringStack {

	private String content;
	private String seperator = ",";
	private static String POP_REGEX = "[^,]*,";

	public StringStack() {
		this("");
	}

	public StringStack(String content) {
		this.content = content == null ? "" : content;
	}

	public void push(String record) {
		record = record.replaceAll(seperator, "");
		content = record + "," + content;
	}

	public String peek() {
		return content.isEmpty() ?  null  : content.split(seperator)[0];
	}
	
	public String pop() {
		String result = peek();
		content = content.replaceFirst(POP_REGEX, "");
		return result;
	}

	@Override
	public String toString() {
		return content;
	}
	
	
}
