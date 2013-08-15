package clean.code.testablehtml;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageCrawlerImpl;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;

public class TestableHtmlMaker {

	private final PageData pageData;
	private final boolean isSuiteSetupIncluded;
	private final WikiPage wikiPage;
	private final StringBuffer contentPage;
	private final PageCrawler pageCrawler;

	public TestableHtmlMaker(PageData pageData, boolean includeSuiteSetup) {
		this.pageData = pageData;
		this.isSuiteSetupIncluded = includeSuiteSetup;
		this.wikiPage = pageData.getWikiPage();
		this.contentPage = new StringBuffer();
		this.pageCrawler = wikiPage.getPageCrawler();
	}

	public String invoke() throws Exception {
		if (pageData.hasAttribute("Test")) {
			includeSetup();

			contentPage.append(pageData.getContent());

			includeTeardown();

			pageData.setContent(contentPage.toString());
		}
		return pageData.getHtml();
	}

	private void includeTeardown() throws Exception {
		inclucdeInheritedPage("teardown", "TearDown");

		if (isSuiteSetupIncluded) {
			inclucdeInheritedPage("teardown", SuiteResponder.SUITE_TEARDOWN_NAME);
		}
	}

	private void includeSetup() throws Exception {
		if (isSuiteSetupIncluded) {
			inclucdeInheritedPage("setup", SuiteResponder.SUITE_SETUP_NAME);
		}
		inclucdeInheritedPage("setup", "SetUp");
	}

	private void inclucdeInheritedPage(String mode, String pageName) throws Exception {
		WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
		if (suiteSetup != null) {
			includePage(mode, suiteSetup);
		}
	}

	private void includePage(String mode, WikiPage page) throws Exception {
		WikiPagePath pagePath = pageCrawler.getFullPath(page);
		String pagePathName = PathParser.render(pagePath);
		contentPage.append(String.format("!include -%s .%s\n", mode, pagePathName));
	}

}
