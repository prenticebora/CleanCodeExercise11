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
	private StringBuffer buffer;

	public TestableHtmlMaker(PageData pageData, boolean includeSuiteSetup) {
		this.pageData = pageData;
		this.includeSuiteSetup = includeSuiteSetup;
	}

	public String invoke() throws Exception {
		wikiPage = pageData.getWikiPage();
		buffer = new StringBuffer();

		if (pageData.hasAttribute("Test")) {
			String mode = "setup";
			if (includeSuiteSetup) {
				String pageName = SuiteResponder.SUITE_SETUP_NAME;

				includePage(mode, pageName);
			}
			String string = "SetUp";
			includePage(mode, string);
		}

		buffer.append(pageData.getContent());
		if (pageData.hasAttribute("Test")) {
			String mode = "teardown";

			String string = "TearDown";
			includePage(mode, string);
			if (includeSuiteSetup) {
				String suiteTeardownName = SuiteResponder.SUITE_TEARDOWN_NAME;
				includePage(mode, suiteTeardownName);
			}
		}

		pageData.setContent(buffer.toString());
		return pageData.getHtml();
	}

	private void includePage(String mode, String suiteTeardownName) throws Exception {
		WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(suiteTeardownName, wikiPage);
		if (suiteTeardown != null) {
			WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteTeardown);
			String pagePathName = PathParser.render(pagePath);
			buffer.append("!include -" + mode + " .").append(pagePathName).append("\n");
		}
	}

}
