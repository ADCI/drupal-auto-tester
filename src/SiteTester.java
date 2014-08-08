package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class SiteTester {

    private final BrowserDriver browser;
    private final Reporter reporter;
    private final DrupalController drupal;
    private final int browserType = 1; // 1 - firefox, 2 - chrome.
    final private String filePath = "C:/drupal-test";
    final private String reportName = "report.log";
    private final String testUserLogin;
    private final String testUserPass;
    final private String host;
    private final String accessDeniedPageTitle = "Access denied";
    private final String pageNotFoundTitle = "Page not found";
    private final int needLogin; // 0 - no login, 1 - login required, 2 -
                                 // random login.
    private final boolean fillForms;
    private final boolean resize = false;
    private final boolean takeScreenshots = false;
    private final int screenHeight = 700;
    private final Dimension defaultBrowserDimension = new Dimension(1200, this.screenHeight);
    private final List<Dimension> resolutions = new ArrayList<Dimension>();
    // Links params filters
    boolean getParamsFilter = true;
    boolean visitOnlyCurrentHost = true;

    private final List<String> dontVisitPages = new ArrayList<String>();
    private final List<String> visitedPages = new ArrayList<String>();
    private final List<String> pagesToVisit = new ArrayList<String>();
    private final boolean collectLinks = true;
    private final List<String> dontFillForm = new ArrayList<String>();
    Map<String, String> parentagePagesPaths = new HashMap<String, String>();

    // Class constructor.
    SiteTester(String host, String testUserLogin, String testUserPass, int needLogin, boolean fillForms) {
        this.host = host;
        this.testUserLogin = testUserLogin;
        this.testUserPass = testUserPass;
        this.needLogin = needLogin;
        this.fillForms = fillForms;

        this.browser = new BrowserDriver(this.filePath, this.browserType);
        this.reporter = new Reporter();
        this.drupal = new DrupalController(this.browser);
        // Pages to visit for sure.
        this.pagesToVisit.add(this.getHost());
        this.pagesToVisit.add(this.getHost() + "/user");
        this.pagesToVisit.add(this.getHost() + "/user/register");
        this.pagesToVisit.add(this.getHost() + "/user/password");
        this.pagesToVisit.add(this.getHost() + "/test-page-not-found");
        this.pagesToVisit.add(this.getHost() + "/admin/settings");
        // Add don't visit pages.
        this.dontVisitPages.add(this.getHost() + "/user/logout");
        this.dontFillForm.add("user-login-form");
        this.dontFillForm.add("homedepot-search-books-form");
        // Add resolutions.
        // this.resolutions.add(new Dimension(320, this.screenHeight));
        // this.resolutions.add(new Dimension(980, this.screenHeight));
        this.resolutions.add(new Dimension(1024, this.screenHeight));
        this.resolutions.add(new Dimension(1400, this.screenHeight));
    }

    private String getHost() {
        return this.host;
    }

    private String getTestUserLogin() {
        return this.testUserLogin;
    }

    private String getTestUserPass() {
        return this.testUserPass;
    }

    private boolean login() {
        this.browser.getPage(this.getHost() + "/user/login");
        this.browser.loginAs(this.getTestUserLogin(), this.getTestUserPass());
        return true;
    }

    private boolean loginProcess() {
        if (this.needLogin == 1) {
            return this.login();
        }
        else if (this.needLogin == 2) {
            boolean generaterandomLogin = this.browser.generateRandomBooleanValue();
            if (generaterandomLogin == true) {
                return this.login();
            }
        }
        return false;
    }

    private boolean skipPage(String page) {
        if (page.isEmpty() || this.visitedPages.contains(page) || this.dontVisitPages.contains(page)) {
            return true;
        }
        return false;
    }

    private boolean pageAccessDeniedProcess(String page) {
        boolean isAccessDenied = this.browser.isPageTitleContains(accessDeniedPageTitle);
        if (isAccessDenied == true) {
            this.reporter.accessDeniedAdd();
            String parentPage = this.getParentPage(page);
            String errorMessage = "Access denied! " + "Path: " + "Parent page - " + parentPage + ", Page - " + page;
            this.reporter.addErrorMessage(errorMessage);
        }
        return isAccessDenied;
    }

    private boolean pageNotFoundProcess(String page) {
        boolean ispageNotFound = this.browser.isPageTitleContains(this.pageNotFoundTitle);
        if (ispageNotFound == true) {
            this.reporter.pageNotFoundAdd();
            String parentPage = this.getParentPage(page);
            this.reporter.addErrorMessage("Page not found! " + "Path: " + "Parent page - " + parentPage + ", Page - " + page);
        }
        return ispageNotFound;
    }

    private boolean pageErrorsProcess(String page) {
        boolean isErrorMessage = this.browser.isElemExist(".error");
        if (isErrorMessage) {
            // Take screenshot of error page
            String screenshotFileName = "Page";
            this.browser.takeScreenshot(screenshotFileName);
            reporter.ErrorAdd();
            // Add Error message to log
            String parentPage = this.getParentPage(page);
            this.reporter.addErrorMessage("Error! " + "Path: " + "Parent page - " + parentPage + ", Error page - " + page + ", Screenshots - " + "c:\\screenshots\\" + screenshotFileName + ".png");
        }
        return isErrorMessage;
    }

    private String getParentPage(String page) {
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

    public String siteTesterScan() {
        this.loginProcess();
        List<String> currentPageLinks = new ArrayList<String>();
        String link = null;
        String nextPage = null;
        this.browser.getPage(this.getHost());
        this.browser.chageScreenSize(this.defaultBrowserDimension);
        for (int i = 0; i < this.pagesToVisit.size(); i++) {
            nextPage = this.pagesToVisit.get(i);
            nextPage = this.filterQuery(this.getHost(), nextPage);
            // Check if next page should be processed.
            if (this.skipPage(nextPage)) {
                continue;
            }
            this.browser.getPage(nextPage);
            if (this.resize) {
                this.resize(nextPage);
            }
            // Add page to visited list
            this.visitedPages.add(nextPage);
            reporter.visitedPagesAdd();
            // Take page path of Access denied page
            pageAccessDeniedProcess(nextPage);
            // Take page path of Page not found
            pageNotFoundProcess(nextPage);
            // Check there are no error messages on current page
            pageErrorsProcess(nextPage);
            if (this.collectLinks) {
                currentPageLinks = this.browser.getCurrentPageLinks();
                // Mix list of current page links
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
        this.browser.closeBrowser();
        return "Done";
    }

    private void resize(String page) {
        for (int i = 0; i < this.resolutions.size(); i++) {
            Dimension res = this.resolutions.get(i);
            this.browser.chageScreenSize(res);
            if (this.takeScreenshots) {
                page = page.replaceAll(this.host, "");
                this.browser.takeScreenshot("page-" + page + "-" + res.width + "-" + res.height);
            }
        }
    }

    private boolean fillForm(String page) {
        WebElement submitButton = null;
        boolean formErrorMessage = false;
        try {
            // Exclude login form and registration form.
            List<WebElement> formsOnPage = this.browser.getElems("form");
            for (int v = 0; v < formsOnPage.size(); v++) {
                String idForm = formsOnPage.get(v).getAttribute("id");
                String diezIdForm = ("#" + idForm);
                submitButton = this.browser.getElem(diezIdForm + " input[type='submit']");
                List<WebElement> submitButtons = new ArrayList<WebElement>();
                if (this.dontFillForm.contains(idForm)) {
                    continue;
                }
                // Form infill
                drupal.fieldsetsOpen(diezIdForm);
                drupal.fillFormCheckboxes(diezIdForm);
                drupal.fillFormRadios(diezIdForm);
                drupal.fillFormSelect(diezIdForm);
                drupal.fillFormText(diezIdForm);
                drupal.fillFormNumber(diezIdForm);
                if (submitButton != null) {
                    submitButtons.add(submitButton);
                }
                // Mix submits
                Collections.shuffle(submitButtons);
                // Form submit
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
                this.browser.waitWhile(".ajax-throbber");
                this.browser.waitWhile(".filled");
                formErrorMessage = this.browser.isElemExist(".error");
                if (formErrorMessage) {
                    this.reporter.formErrorAdd();
                    // Take screen shot of error page
                    this.browser.takeScreenshot("form_id=" + diezIdForm);
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