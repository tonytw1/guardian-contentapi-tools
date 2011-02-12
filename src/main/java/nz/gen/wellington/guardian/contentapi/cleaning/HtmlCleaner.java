package nz.gen.wellington.guardian.contentapi.cleaning;

import java.util.regex.Pattern;

public class HtmlCleaner {

	private Pattern h2end = Pattern.compile("</h2>");
	private Pattern p = Pattern.compile("</p>");
	private Pattern br = Pattern.compile("<br ?/>");
	private Pattern tags = Pattern.compile("<.*?>");
	
	public String stripHtml(String content) {
		if (content == null) {
			return null;
		}
		
		content = h2end.matcher(content).replaceAll("\n\n");
		content = p.matcher(content).replaceAll("\n\n");
		content = br.matcher(content).replaceAll("\n");
		
		content  = tags.matcher(content).replaceAll("");
		
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&nbsp;", " ");
		
		content = content.replaceAll("\n{2,}", "\n\n");
		return content.trim();
	}

}
