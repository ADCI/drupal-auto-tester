package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class SiteTester {
	
	public FirefoxTest ff;
	public Reporter reporter;
	final private String filePath = "C:/drupal-test";
	final private String reportName = "report";
	private String testUserLogin = "";
	private String testUserPass = "";
	final private String host = "http://127.0.0.1:4567";
	private String accessDeniedPageTitle = "Access denied";
	private String pageNotFoundTitle = "Page not found";
	int needLogin = 0; // 0 - no login, 1 - login required, 2 - random login.
	boolean fillForms = false;
	boolean resize = true;
	boolean takeScreenshots = true;
	private final int screenHeight = 600;
	private List<Dimension> resolutions = new ArrayList<Dimension>();
	//Links params filters
	boolean getParamsFilter = true;
	boolean visitOnlyCurrentHost = true;		
	
	private List<String> dontVisitPages = new ArrayList<String>();
	private List<String> visitedPages = new ArrayList<String>();
	private List<String> pagesToVisit = new ArrayList<String>();
	private boolean collectLinks = false;
	private List<String> dontFillForm = new ArrayList<String>();
	Map<String, String> parentagePagesPaths = new HashMap<String, String>();
	
	// Class constructor.
	public SiteTester() {
		this.ff = new FirefoxTest(this.filePath);
		this.reporter = new Reporter();
		// Pages to visit for sure.
		this.pagesToVisit.add(this.getHost());
		this.pagesToVisit.add(this.getHost() + "/user");
		this.pagesToVisit.add(this.getHost() + "/user/login");
		this.pagesToVisit.add(this.getHost() + "/test");
		this.pagesToVisit.add(this.getHost() + "/node/176381");
		// Add don't visit pages.
		this.dontVisitPages.add(this.getHost() + "/user/logout");
		this.dontFillForm.add("user-login-form");
		this.dontFillForm.add("user-register-form");
		// Add resolutions.
		this.resolutions.add(new Dimension(320, this.screenHeight));
		this.resolutions.add(new Dimension(980, this.screenHeight));
		this.resolutions.add(new Dimension(1024, this.screenHeight));
		this.resolutions.add(new Dimension(1250, this.screenHeight));
	}
		
	public String getHost() {
		return this.host;
	}

	public String getTestUserLogin() {
		return this.testUserLogin;
	}

	public String getTestUserPass() {
		return this.testUserPass;
	}

	public boolean login() {
		this.ff.getPage(this.getHost());
		this.ff.loginAs(this.getTestUserLogin(), this.getTestUserPass());
		return true;
	}
	
	public boolean loginProcess() {
		if (this.needLogin == 1) {
			return this.login();
		}
		else if (this.needLogin == 2) {
			boolean generaterandomLogin = this.ff.generateRandomBooleanValue();
			if (generaterandomLogin == true) {
				return this.login();
			}
		}
		return false;
	}
	
	public boolean skipPage(String page) {
		if (page.isEmpty() || this.visitedPages.contains(page) || this.dontVisitPages.contains(page)) {
			return true;
		}
		return false;
	}
	
	public boolean pageAccessDeniedProcess(String page) {
		boolean isAccessDenied = this.ff.isPageTitleContains(accessDeniedPageTitle);
		if (isAccessDenied == true) {
			this.reporter.accessDeniedAdd();
			String parentPage = this.getParentPage(page);
			String errorMessage = "Access denied! "  + "Path: " + "Parent page - "+ parentPage  + ", Page - " + page;
			this.reporter.addErrorMessage(errorMessage);			
		}
		return isAccessDenied;
	}

	public boolean pageNotFoundProcess(String page) {
		boolean ispageNotFound = this.ff.isPageTitleContains(this.pageNotFoundTitle);
		if (ispageNotFound == true) {
			this.reporter.pageNotFoundAdd();
			String parentPage = this.getParentPage(page);
			this.reporter.addErrorMessage("Page not found! " + "Path: " + "Parent page - "+ parentPage  + ", Page - " + page);			
		}
		return ispageNotFound;
	}

	public boolean pageErrorsProcess(String page) {
		boolean isErrorMessage = this.ff.isElemExist(".error");
		if (isErrorMessage) {
			// Take screenshot of error page
			String screenshotFileName = "Page";
			this.ff.takeScreenshot(screenshotFileName);
			reporter.ErrorAdd();
			// Add Error message to log
			String parentPage = this.getParentPage(page);
			this.reporter.addErrorMessage("Error! " + "Path: " + "Parent page - "+ parentPage  + ", Error page - " + page + ", Screenshots - " + "c:\\screenshots\\" + screenshotFileName + ".png");
		}
		return isErrorMessage;
	}
	
	public String getParentPage(String page) {
		String childPagePath = null;
		String parentPagePath = null;
		for (Map.Entry<String, String> entry : this.parentagePagesPaths.entrySet()) { 
			childPagePath = entry.getKey(); 
			if (childPagePath.equals(page)) {
				parentPagePath = entry.getValue();
			}
		}
		return parentPagePath;
	}
	
	@Test
	public void siteTesterScan() {
		this.loginProcess();
		List<String> currentPageLinks = new ArrayList<String>();
		String link = null;
		String nextPage = null;
		for (int i = 0; i < this.pagesToVisit.size(); i++) {
			nextPage = this.pagesToVisit.get(i);
			nextPage = this.filterQuery(this.getHost(), nextPage);
			// Check if next page should be processed.
			if (this.skipPage(nextPage)) {
				continue;
			}			
			this.ff.getPage(nextPage);
			if (this.resize) {
				this.resize(nextPage);
			}
			// Add page to visited list
			this.visitedPages.add(nextPage);			
			reporter.visitedPagesAdd();
			//Take page path of Access denied page
			pageAccessDeniedProcess(nextPage);
			//Take page path of Page not found
			pageNotFoundProcess(nextPage);
			// Check there are no error messages on current page
			pageErrorsProcess(nextPage);
			if (this.collectLinks) {
				currentPageLinks = this.ff.getCurrentPageLinks();
				//Mix list of current page links
				Collections.shuffle(currentPageLinks);
				// Add Pages paths
	    		for (int j = 0; j < currentPageLinks.size(); j++) {
	    			link = this.filterQuery(this.getHost(), currentPageLinks.get(j));
	        		parentagePagesPaths.put(link, nextPage);
					this.pagesToVisit.add(link);
	    		}
			}
			// Fill in any form on the current page
			if (fillForms == true) {
				this.fillForm(nextPage);
			}
		}
		this.reporter.reportConsole();
		this.reporter.reportFile(this.reportName, this.filePath);
		this.ff.closeBrowser();	
	}
	
	private void resize(String page) {
    	for (int i = 0; i < this.resolutions.size(); i++) {
    		Dimension res = this.resolutions.get(i);
        	this.ff.chageScreenSize(res);
        	if (this.takeScreenshots) {
        		page = page.replaceAll(this.host, "");
        		this.ff.takeScreenshot("page-" + page + "-" + res.width + "-" + res.height);
        	}
    	}
	}

	private boolean fillForm(String page) {	
		WebElement submitButton = null;
		boolean formErrorMessage = false;
		try {
			// Exclude login form and registration form.
			List<WebElement> formsOnPage = new ArrayList<WebElement>();
			formsOnPage.addAll(this.ff.getElems("form"));
			for (int v = 0; v < formsOnPage.size(); v++) {
				String idForm = formsOnPage.get(v).getAttribute("id");
				String diezIdForm = ("#" + idForm);					
				submitButton = this.ff.getElem(diezIdForm + " input[type='submit']"); 
				List<WebElement> submitButtons = new ArrayList<WebElement>();
				if (this.dontFillForm.contains(idForm)) { 
					continue;			
				}
				// Form infill
				this.ff.fieldsetsOpen(diezIdForm);
				this.ff.fillFormCheckboxes(diezIdForm);
				this.ff.fillFormRadios(diezIdForm);	
				this.ff.fillFormSelect(diezIdForm);		
				this.ff.fillFormText(diezIdForm);
				this.ff.fillFormNumber(diezIdForm);
				if (submitButton != null) {
					submitButtons.add(submitButton);				
				}
				//Mix submits	
				Collections.shuffle(submitButtons);
				//Form submit
				for (int a = 0; a < submitButtons.size(); a++) {
					try {
						submitButton.click();
						this.reporter.formSubmittedAdd();
						break;
					}
					catch (NoSuchElementException error) {	
						continue;							
					}					
				}
				this.ff.waitWhile(".ajax-throbber");
				this.ff.waitWhile(".filled");
				formErrorMessage = this.ff.isElemExist(".error");
				if (formErrorMessage) {
					this.reporter.formErrorAdd();
					// Take screen shot of error page
					this.ff.takeScreenshot("form_id=" + diezIdForm);
					// Add Error message to log
					this.reporter.addErrorMessage("Form Error! Page - " + page + ", id=" + diezIdForm + ", Screenshots - " + "c:\\screenshots\\" + "form_id=" + diezIdForm + ".png");
				}
			}
		}
		catch (StaleElementReferenceException error) {	
			
		}
		return true;
	}
	
	private String filterQuery(String host, String query) {
		int queryParamsIndex = 0;
		// Remove GET parameters from query
		queryParamsIndex = query.indexOf("?");
		if (queryParamsIndex > 0 && getParamsFilter) {
			query = query.substring(0, queryParamsIndex);
		}
		// Remove anchor
		queryParamsIndex = query.indexOf("#");
		if (queryParamsIndex > 0) {
			query = query.substring(0, queryParamsIndex);
		}
		if (!query.contains(host) && visitOnlyCurrentHost == true) {
			query = "";
		}
		return query;
	}

}
