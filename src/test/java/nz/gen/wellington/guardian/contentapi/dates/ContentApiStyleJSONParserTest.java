package nz.gen.wellington.guardian.contentapi.dates;

import static org.junit.Assert.*;

import nz.gen.wellington.guardian.contentapi.dates.ContentApiDateHelper;

import org.junit.Test;

public class ContentApiStyleJSONParserTest {
	
	private static final String ZULU_TIME_EXAMPLE = "2011-02-13T00:04:10Z";
	private static final String BST_TIME_EXAMPLE = "2010-07-15T16:40:42+01:00";

	@Test
	public void shouldHandleZuluFormatDatesCorrectly() throws Exception {
		assertNotNull(ContentApiDateHelper.parseDate(ZULU_TIME_EXAMPLE));
	}
	
	@Test
	public void shouldHandleBSTFormatDatesCorrectly() throws Exception {
		assertNotNull(ContentApiDateHelper.parseDate(BST_TIME_EXAMPLE));
	}
	
}
