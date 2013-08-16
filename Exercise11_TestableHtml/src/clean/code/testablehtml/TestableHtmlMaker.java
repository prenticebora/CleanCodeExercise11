/**
 * 
 */
package clean.code.testablehtml;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.PageCrawlerImpl;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;

/**
 * @author brliu
 * 
 */
public class TestableHtmlMaker {

	private final PageData pageData;
	private final boolean includeSuiteSetup;
	private final WikiPage wikiPage;
	private String content;

	public TestableHtmlMaker(PageData pageData, boolean includeSuiteSetup) {
		this.pageData = pageData;
		this.includeSuiteSetup = includeSuiteSetup;
		this.wikiPage = pageData.getWikiPage();
		this.content = "";
	}

	public String invoke() throws Exception {

		if (pageData.hasAttribute("Test")) {
			content += includeSetupPage();

			content += pageData.getContent();

			content += includeTeardownPage();
		}

		pageData.setContent(content.toString());
		return pageData.getHtml();
	}

	private String includeTeardownPage() throws Exception {
		String content = "";

		content += includePage("teardown", "TearDown");
		if (includeSuiteSetup) {
			content += includePage("teardown", SuiteResponder.SUITE_TEARDOWN_NAME);
		}
		return content;
	}

	private String includeSetupPage() throws Exception {
		String content = "";
		if (includeSuiteSetup) {
			content += includePage("setup", SuiteResponder.SUITE_SETUP_NAME);
		}
		content += includePage("setup", "SetUp");

		return content;
	}

	private String includePage(String mode, String suiteTeardownName) throws Exception {
		String content = "";
		WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(suiteTeardownName, wikiPage);

		if (suiteTeardown != null) {
			WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteTeardown);
			String pagePathName = PathParser.render(pagePath);
			content += String.format("!include -%s .%s\n", mode, pagePathName);
		}

		return content;
	}
}
