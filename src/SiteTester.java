import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class SiteTester {
	
	public FirefoxTest ff;
	final private String filePath = "C:/screenshots";
	private String testUserLogin = "Java_test@agent.agent";
	private String testUserPass = "111";
	final private String host = "http://127.0.0.1:4567";
	int submitFormsCount = 0;
	int errorFormsCount = 0;
	List<String> formsError = new ArrayList<String>();
	private String accessDeniedPageTitle = "Access denied";
	private String pageNotFoundTitle = "Page not found";
	boolean needLogin = true; 
	boolean needRandomLogin = false;
	boolean fillForms = true;
	
	//Links params filters
	boolean getParamsFilter = true;
	boolean visitOnlyCurrentHost = true;		
	
	
	private List<String> dontVisitPages = new ArrayList<String>();
	private List<String> dontFillForm = new ArrayList<String>();
	public SiteTester() {
		this.ff = new FirefoxTest(this.filePath);
		//Add don't visit pages
		this.dontVisitPages.add(this.getHost() + "/user/logout");
		this.dontVisitPages.add(this.getHost() + "/user/2/edit");
		this.dontVisitPages.add(this.getHost() + "/user");
		this.dontVisitPages.add(this.getHost() + "/masquerade/unswitch?token=OKIq7TsC-8BP07rN-1svlRwcd1t4ppz8mcrd6LhtmlM");
		this.dontFillForm.add("user-login-form");
		this.dontFillForm.add("user-register-form");
		this.dontFillForm.add("user-pass");		
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

	public void login() {
		this.ff.getPage(this.getHost());
		this.ff.loginAs(this.getTestUserLogin(), this.getTestUserPass());
	}
	
	public void needlogin() {
		if (needLogin == true) {
			this.login();
		}
	}
	
	public void randomLogin() {
		if (needLogin == true) {
			needRandomLogin = false;
		}
		if (needRandomLogin == true) {
			boolean generaterandomLogin = this.ff.generateRandomBooleanValue();
			if (generaterandomLogin == true) {
				this.login();
			}
		}
	}
		
	
	@Test
	public String siteTesterScan() {
	//public void siteTesterScan() {
		this.needlogin();
		this.randomLogin();
		List<String> links = new ArrayList<String>();
		links.add(this.getHost());
		List<String> visitedLinks = new ArrayList<String>();
		List<String> currentPageLinks = new ArrayList<String>();
		List<String> pathsErrorPages = new ArrayList<String>();		
		List<String> pathsNotFoundPages = new ArrayList<String>();
		List<String> pathsAccessDeniedPages = new ArrayList<String>();					
		Map<String, String> parentagePagesPaths = new HashMap<String, String>();
		String link = null;
		boolean errorMessage = false;
		String nextPage = null;
		String childPagePath = null;
		int accessDeniedPageNumber = 0;
		int pageNotFoundNumber = 0;
		int hasErrorPage = 0;		
		int visitedPagesCount = 0;
		for (int i = 0; i < links.size(); i++) {
			String getCurrentPage = links.get(i);
			nextPage = this.filterQuery(this.getHost(), getCurrentPage);
			// Don't visit pages, that were visited already
			if (nextPage.isEmpty() || visitedLinks.contains(nextPage) || this.dontVisitPages.contains(nextPage)) {
				continue;
			}			
			//System.out.println(nextPage);					
			this.ff.getPage(nextPage);
			// Add page to visited list
			visitedLinks.add(nextPage);				
			//Take page path of Access denied page
			boolean isAccessDenied = this.ff.isPageTitleContains(accessDeniedPageTitle);
			if (isAccessDenied == true) {
				accessDeniedPageNumber = accessDeniedPageNumber + 1;
        		for (Map.Entry<String, String> entry : parentagePagesPaths.entrySet()) { 
        			childPagePath = entry.getKey(); 
        			if (childPagePath.equals(nextPage)) {
        			//	System.out.println("Access denied! "  + "Path: " + "Parent page - "+ entry.getValue()  + " Page - " + entry.getKey());	
        				pathsAccessDeniedPages.add("Access denied! "  + "Path: " + "Parent page - "+ entry.getValue()  + ", Page - " + entry.getKey());
        			}
        		}				
			}
			//Take page path of Page not found
			boolean pageNotFound = this.ff.isPageTitleContains(pageNotFoundTitle);
			if (pageNotFound == true) {
				pageNotFoundNumber = pageNotFoundNumber + 1;
				for (Map.Entry<String, String> entry : parentagePagesPaths.entrySet()) { 
        			childPagePath = entry.getKey(); 
        			if (childPagePath.equals(nextPage)) {
        			//	System.out.println("Page not found! "  + "Path: " + "Parent page - "+ entry.getValue()  + " Page - " + entry.getKey());	
        				pathsNotFoundPages.add("Page not found! "  + "Path: " + "Parent page - "+ entry.getValue()  + ", Page - " + entry.getKey());
        			}
        		}				
			}
			// Check there are no error messages on current page
			errorMessage = this.ff.isElemExist(".error");
			if (errorMessage) {
				// Take screen shot of error page
				this.ff.takeScreenshot("page- " + i);
				// Add Error message to log
				hasErrorPage = hasErrorPage + 1;
         		// Take parent page path of error page
        		for (Map.Entry<String, String> entry : parentagePagesPaths.entrySet()) { 
        			childPagePath = entry.getKey(); 
        			if (childPagePath.equals(nextPage)) {
        			//	System.out.println("Error! " + "Path: " + "Parent page - "+ entry.getValue()  + " Error page - " + entry.getKey());	
        			//	System.out.println(" Screenshot - c:\\screenshots\\" + "page- " + i + ".png");
        				pathsErrorPages.add("Error! " + "Path: " + "Parent page - "+ entry.getValue()  + ", Error page - " + entry.getKey() + ", Screenshots - " + "c:\\screenshots\\" + "page- " + i + ".png");
        			}
        		}
			}
//			assertFalse("No error messages", errorMessage);			
			currentPageLinks = this.ff.getCurrentPageLinks();
			//Mix list of current page links
			Collections.shuffle(currentPageLinks);
			links.addAll(currentPageLinks);
			visitedPagesCount = visitedPagesCount + 1;
			// Add Pages paths
    		for (int j = 0; j < currentPageLinks.size(); j++) {
    			link = this.filterQuery(this.getHost(), currentPageLinks.get(j));
//        		if (link.isEmpty()) {
//        			continue;
//        		}
        		parentagePagesPaths.put(link, nextPage);
    		} 
			
			// Fill in any form on the current page
			if (fillForms == true) {
				this.fillForm(getCurrentPage);
			}
		}
		//Result output
		System.out.println("Visited pages count  = " + visitedPagesCount);
		System.out.println("Pages containing errors  = " + hasErrorPage);
		System.out.println("Access denied number = " + accessDeniedPageNumber);
		System.out.println("Page not found number = " + pageNotFoundNumber);	
		if (fillForms == true) {
			System.out.println("Submit form number = " + submitFormsCount);
			System.out.println("Error form number = " + errorFormsCount);
		}			
		for (int z = 0; z < pathsErrorPages.size(); z++) {
			System.out.println(pathsErrorPages.get(z));
		}
		for (int e = 0; e < pathsNotFoundPages.size(); e++) {
			System.out.println(pathsNotFoundPages.get(e));
		}			
		for (int f = 0; f < pathsAccessDeniedPages.size(); f++) {
			System.out.println(pathsAccessDeniedPages.get(f));
		}						
		if (fillForms == true) {
			for (int k = 0; k < formsError.size(); k++) {
				System.out.println(formsError.get(k));
			}	
		}	
		
		//Create result file
		String value = "Visited pages count  = " + visitedPagesCount + " ||    ";
		value = value + "Pages containing errors  = " + hasErrorPage + " :   ";
		for (int z = 0; z < pathsErrorPages.size(); z++) {
			value = value + pathsErrorPages.get(z) + ";   ";
		}
		value = value + "Access denied number = " + accessDeniedPageNumber + " :   ";
		for (int f = 0; f < pathsAccessDeniedPages.size(); f++) {
			value = value + pathsAccessDeniedPages.get(f) + ";   ";
		}
		value = value + "Page not found number = " + pageNotFoundNumber + " :   ";
		for (int e = 0; e < pathsNotFoundPages.size(); e++) {
			value = value + pathsNotFoundPages.get(e) + ";   ";
		}
		if (fillForms == true) {
			value = value + "Submit form number = " + submitFormsCount + " ||    ";
			value = value + "Error form number = " + errorFormsCount + " :   ";
			for (int k = 0; k < formsError.size(); k++) {
				value = value + formsError.get(k) + ";   ";
			}	
		}
		this.ff.documentCreate("Test_result", value);
				
		this.ff.closeBrowser();	
		return "test complete";
	}

	
	private boolean fillForm(String getCurrentPage) {	
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
				else {
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
							submitFormsCount = submitFormsCount + 1;
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
						errorFormsCount = errorFormsCount + 1;
						// Take screen shot of error page
						this.ff.takeScreenshot("form_id=" + diezIdForm);
						// Add Error message to log
					//	System.out.println("Form Error! " + getCurrentPage +" id=" + diezIdForm);
						formsError.add("Form Error! Page - " + getCurrentPage + ", id=" + diezIdForm + ", Screenshots - " + "c:\\screenshots\\" + "form_id=" + diezIdForm + ".png");
					}
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
