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
	private WikiPage wikiPage;
	private StringBuffer content;

	public TestableHtmlMaker(PageData pageData, boolean includeSuiteSetup) {
		this.pageData = pageData;
		this.includeSuiteSetup = includeSuiteSetup;
	}

	public String invoke() throws Exception {
		wikiPage = pageData.getWikiPage();
		content = new StringBuffer();

		if (pageData.hasAttribute("Test")) {
			includeSetupPage();

			content.append(pageData.getContent());

			includeTeardownPage();
		}

		pageData.setContent(content.toString());
		return pageData.getHtml();
	}

	private void includeTeardownPage() throws Exception {
		includePage("teardown", "TearDown");
		if (includeSuiteSetup) {
			includePage("teardown", SuiteResponder.SUITE_TEARDOWN_NAME);
		}
	}

	private void includeSetupPage() throws Exception {
		if (includeSuiteSetup) {
			includePage("setup", SuiteResponder.SUITE_SETUP_NAME);
		}
		includePage("setup", "SetUp");
	}

	private void includePage(String mode, String suiteTeardownName) throws Exception {
		WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(suiteTeardownName, wikiPage);
		if (suiteTeardown != null) {
			WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteTeardown);
			String pagePathName = PathParser.render(pagePath);
			content.append("!include -" + mode + " .").append(pagePathName).append("\n");
		}
	}

}
