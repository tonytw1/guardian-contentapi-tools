package nz.gen.wellington.guardian.contentapi.parsing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nz.gen.wellington.guardian.contentapi.cleaning.HtmlCleaner;
import nz.gen.wellington.guardian.model.Article;
import nz.gen.wellington.guardian.model.Section;
import nz.gen.wellington.guardian.model.Tag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ContentApiStyleJSONParser {
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	
	private HtmlCleaner htmlCleaner;
	
	public ContentApiStyleJSONParser() {
		htmlCleaner = new HtmlCleaner();
	}
	
	public List<Section> parseSectionsRequestResponse(String content) {
		try {		
			JSONObject json = new JSONObject(content.toString());
			if (!isResponseOk(json)) {
				return null;
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");

			List<Section> sections = new LinkedList<Section>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject section = results.getJSONObject(i);
				final String sectionName = htmlCleaner.stripHtml(section.getString("webTitle"));
				final String id = section.getString("id");
				sections.add(new Section(id, sectionName));
			}
			return sections;

		} catch (JSONException e) {
			return null;
		}
	}
	
	
	public List<Tag> parseTagSearchResponse(String content, Map<String, Section> sections) {
		try {
			JSONObject json = new JSONObject(content);
			if (!isResponseOk(json)) {
				return null;
			}
			JSONObject response = json.getJSONObject("response");
			JSONArray results = response.getJSONArray("results");

			List<Tag> tags = new LinkedList<Tag>();
			for (int i = 0; i < results.length(); i++) {
				JSONObject tag = results.getJSONObject(i);
				final String id = tag.getString("id");
				final String tagName = tag.getString("webTitle");
				final String type = tag.getString("type");

				Section section = null;
				final String sectionId = tag.getString("sectionId");
				if (tag.has("sectionId")) {
					section = sections.get(sectionId);
				}
				tags.add(new Tag(tagName, id, section, type));			
			}
			return tags;

		} catch (JSONException e) {
			return null;
		}
	}
	
	
	public boolean isResponseOk(JSONObject json) {
		try {
			JSONObject response = json.getJSONObject("response");
			String status = response.getString("status");
			return status != null && status.equals("ok");
		} catch (JSONException e) {
			return false;
		}
	}
	
	
	
	public List<Article> extractContentItems(JSONObject json) throws JSONException {
		JSONObject jsonResponse = json.getJSONObject("response");
		JSONArray results = jsonResponse.getJSONArray("results");
		
		List<Article> articles = new ArrayList<Article>();
		for (int i = 0; i < results.length(); i++) {
			JSONObject content = results.getJSONObject(i);
			articles.add(parseContentItem(content));
		}
		return articles;
	}
	
	public Article extractContentItem(JSONObject json) throws JSONException {
		JSONObject jsonResponse = json.getJSONObject("response");
		JSONObject content = jsonResponse.getJSONObject("content");		
		return parseContentItem(content);
	}

	private Article parseContentItem(JSONObject content) throws JSONException {
		Article article = new Article();
		article.setId(getJsonStringIfPresent(content, "id"));
		article.setPubDate(parseDate(getJsonStringIfPresent(content, "webPublicationDate")));
		article.setWebUrl(getJsonStringIfPresent(content, "webUrl"));
		
		Section section = new Section(
				getJsonStringIfPresent(content, "sectionId"), 
				getJsonStringIfPresent(content, "sectionName"));
		article.setSection(section);
		
		JSONObject fields = content.getJSONObject("fields");		
		article.setHeadline(getJsonStringIfPresent(fields, "headline"));
		article.setByline(getJsonStringIfPresent(fields, "byline"));		
		article.setStandfirst(getJsonStringIfPresent(fields, "standfirst"));
		article.setThumbnailUrl(getJsonStringIfPresent(fields, "thumbnail"));
		article.setDescription(getJsonStringIfPresent(fields, "body"));
		article.setShortUrl(getJsonStringIfPresent(fields, "shortUrl"));
		
		
		return article;
	}
	
	private static Date parseDate(String dateString) {
		 SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		 if (dateString.endsWith("Z")) {
			 dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			 dateFormat.setTimeZone(java.util.TimeZone.getTimeZone("Zulu"));
		 }		 
		 try {
			return dateFormat.parse(dateString);
		} catch (ParseException e) {
			 return null;
		}		
	}
	
	private String getJsonStringIfPresent(JSONObject json, String field) throws JSONException {
		if (json.has(field)) {
			return (String) json.get(field);
		}
		return null;
	}

}
