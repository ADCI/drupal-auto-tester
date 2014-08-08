package src;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class DrupalController {

    private final BrowserDriver browser;

    DrupalController(BrowserDriver browser) {
        this.browser = browser;
    }

    public void fillFormText(String selector) {
        List<WebElement> fields = new ArrayList<WebElement>();
        fields.addAll(browser.getElems(selector + " input[type='text']"));
        fields.addAll(browser.getElems(selector + " input[type='password']"));
        fields.addAll(browser.getElems(selector + " textarea"));
        for (int i = 0; i < fields.size(); i++) {
            try {
                String randomString = browser.generateRandomString(false);
                fields.get(i).clear();
                fields.get(i).sendKeys(randomString);
            }
            catch (ElementNotVisibleException error) {
                // System.out.println("Hide field");
            }
            catch (InvalidElementStateException error) {
                // System.out.println("Invalid field");
            }
        }
    }

    public void fillFormNumber(String selector) {
        List<WebElement> fields = browser.getElems(selector + " input[type='number']");
        for (int i = 0; i < fields.size(); i++) {
            try {
                String randomNumberString = browser.generateRandomString(true);
                fields.get(i).clear();
                fields.get(i).sendKeys(randomNumberString);
            }
            catch (ElementNotVisibleException error) {
                // System.out.println("Hide field");
            }
            catch (InvalidElementStateException error) {
                // System.out.println("Invalid field");
            }
        }
    }

    public void fillFormSelect(String selector) {
        List<WebElement> selects = browser.getElems(selector + " .form-item .form-select option:last-child");
        for (int i = 0; i < selects.size(); i++) {
            try {
                selects.get(i).click();
            }
            catch (ElementNotVisibleException error) {
                // System.out.println("Hide field");
            }
            catch (StaleElementReferenceException error) {
                // System.out.println("Hide field");
            }
        }
    }

    public void fillFormTextDate(String selector) {
        List<WebElement> fields = browser.getElems(selector + " .form-type-date-popup .form-text");
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).clear();
            fields.get(i).sendKeys("01/02/2014");
        }
    }

    public void fillFormCheckboxes(String selector) {
        List<WebElement> checkboxes = browser.getElems(selector + " .form-type-checkbox:first-child input");
        for (int i = 0; i < checkboxes.size(); i++) {
            try {
                checkboxes.get(i).click();
                browser.waitWhile(".ajax-throbber");
            }
            catch (ElementNotVisibleException error) {
                // System.out.println("Hide field");
            }
        }
    }

    public void fillFormRadios(String selector) {
        List<WebElement> radioButton = browser.getElems(selector + " .form-type-radio:first-child input");
        for (int i = 0; i < radioButton.size(); i++) {
            try {
                radioButton.get(i).click();
                browser.waitWhile(".ajax-throbber");
            }
            catch (ElementNotVisibleException error) {
                // System.out.println("Hide field");
            }
        }
    }

    public void fieldsetsOpen(String selector) {
        List<WebElement> fieldsets = browser.getElems(selector + " fieldset.collapsed .fieldset-title");
        for (int i = 0; i < fieldsets.size(); i++) {
            try {
                fieldsets.get(i).click();
                browser.waitForVisible(".fieldset-wrapper");
            }
            catch (ElementNotVisibleException error) {
            }
        }
    }

    public void collapsedTableOpen(String selector) {
        List<WebElement> fieldsets = browser.getElems(selector + " .name.clicked");
        for (int i = 0; i < fieldsets.size(); i++) {
            try {
                fieldsets.get(i).click();
            }
            catch (ElementNotVisibleException error) {
            }
            catch (StaleElementReferenceException error) {
            }
        }
    }

}