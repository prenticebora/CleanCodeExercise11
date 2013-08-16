package clean.code.testablehtml;

import fitnesse.wiki.PageData;

public class TestableHtml {
	public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {

		return new TestableHtmlMaker(pageData, includeSuiteSetup).invoke();

	}
}