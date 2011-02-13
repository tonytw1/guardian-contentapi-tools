package nz.gen.wellington.guardian.contentapi.urls;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;

public class ContentApiStyleUrlBuilder {
	
	private static final String SEARCH_QUERY = "search";
	private static final String SECTIONS_QUERY = "sections";
	private static final String TAGS = "tags";
	private static final String OR = "|";
	
	private String apiHost;
	private String apiKey;
	private String format = "xml";

	private List<Tag> tags;
	private List<Tag> mustBeOneOfTags;
	private List<String> tagTypes;
	
	private boolean showAll;
	private boolean showRefinements;
	private boolean showMedia;
	private Integer pageSize;
	private String searchTerm;
	private String fromDate;
	private String toDate;
	private String contentId;
	
	public ContentApiStyleUrlBuilder(String apiHost, String apiKey) {
		this.apiHost = apiHost;
		this.apiKey = apiKey;
		this.tags = new ArrayList<Tag>();
		this.mustBeOneOfTags = new ArrayList<Tag>();
		this.tagTypes = new ArrayList<String>();
		this.showAll = false;
		this.showRefinements = false;
		this.showMedia = false;
		this.contentId = null;
	}
	
	public String toSearchQueryUrl() {
		StringBuilder uri = new StringBuilder("/" + SEARCH_QUERY);
		appendCoreParameters(uri);
		
		StringBuilder tagsParameter = new StringBuilder();			
		if (!tags.isEmpty()) {
			appendTagsToBracketedCommaSeperatedList(tagsParameter, tags);
			
			if (!mustBeOneOfTags.isEmpty()) {
				tagsParameter.append(",");
				appendTagsToBracketedCommaSeperatedList(tagsParameter, mustBeOneOfTags);		
			}
			
		} else if (!mustBeOneOfTags.isEmpty()) {
			appendTagsToBracketedCommaSeperatedList(tagsParameter, mustBeOneOfTags);			
		}
		
		if (tagsParameter.length() > 0) {
			String tags = tagsParameter.toString();
			try {
				tags = URLEncoder.encode(tagsParameter.toString(), "UTF8");
			} catch (UnsupportedEncodingException e) {
			}
			uri.append("&tag=");
			uri.append(tags);				
		}
		
		if (fromDate != null) {
			uri.append("&from-date=" + fromDate);
		}
		
		if (toDate != null) {
			uri.append("&to-date=" + toDate);
		}
		
		if (searchTerm != null) {
			uri.append("&q=" + urlEncode(searchTerm));
		}
		
		return prependHost(uri.toString());
	}
	
	
	public String toTagSearchQueryUrl() {		
		StringBuilder uri = new StringBuilder("/" + TAGS);
		appendCoreParameters(uri);		
		appendAllowedTagTypes(uri);		
		uri.append("&q=" + urlEncode(searchTerm));		
		return prependHost(uri.toString());
	}
	
	
	public String toSectionsQueryUrl() {
		StringBuilder uri = new StringBuilder("/" + SECTIONS_QUERY);
		appendCoreParameters(uri);
		return prependHost(uri.toString());
	}
	
	
	public String toContentItemUrl() {
		StringBuilder uri = new StringBuilder("/" + contentId);
		appendCoreParameters(uri);
		return prependHost(uri.toString());
	}
	
	
	private String prependHost(String uri) {
		return apiHost + uri;
	}

	public void addSection(Section section) {
		tags.add(section.getTag());
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}
	
	public void andMustBeOneOff(Tag tag) {
		mustBeOneOfTags.add(tag);
	}
	
	public void andMustBeOneOff(List<Tag> tags) {
		mustBeOneOfTags.addAll(tags);		
	}
		
	public void addTagType(String tagType) {
		tagTypes.add(tagType);
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}
	
	public void setShowRefinements(boolean showRefinements) {
		this.showRefinements = showRefinements;
	}
	
	public void setShowMedia(boolean showMedia) {
		this.showMedia = showMedia;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public void setFromDate(String date) {
		this.fromDate = date;
	}

	public void setToDate(String date) {	// TODO should take a Date, not a string
		this.toDate = date;		
	}
	
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	
	private void appendCoreParameters(StringBuilder url) {
		url.append("?format=" + format);
		if (pageSize != null) {
			url.append("&page-size=" + pageSize);		
		}
		if (showAll) {
			url.append("&show-fields=all");
			url.append("&show-tags=all");
		}
		
		if (apiKey != null && !apiKey.trim().equals("")) {
			url.append("&api-key=" + apiKey);
		}
		
		if (showRefinements) {
			url.append("&show-refinements=all");
		}
		
		if (showMedia) {
			url.append("&show-media=all");
		}
	}
	
	private void appendTagsToBracketedCommaSeperatedList(StringBuilder tagsParameter, List<Tag> tags) {
		boolean isFirst = true;
		tagsParameter.append("(");
		for (Tag tag : tags) {
			if (!isFirst) {
				tagsParameter.append(OR);
			}
			tagsParameter.append(tag.getId());
			isFirst = false;
		}
		tagsParameter.append(")");
	}
	
	private void appendAllowedTagTypes(StringBuilder uri) {
		if (!mustBeOneOfTags.isEmpty()) {
			uri.append("&type=");
			for (Iterator<String> iterator = tagTypes.iterator(); iterator.hasNext();) {
				uri.append(iterator.next());
				if (iterator.hasNext()) {
					uri.append(urlEncode(","));
				}
			}
		}
	}
	
	private String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF8");
		} catch (UnsupportedEncodingException e) {
			return value;
		}
	}
	
}
