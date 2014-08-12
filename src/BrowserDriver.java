package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import com.opera.core.systems.OperaDriver;

public class BrowserDriver {

    private WebDriver driver;
    private final String filePath;
    private final WebDriverWait wait;
    private final int waitTime = 30;
    private final String randomStringCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final String randomNumberStringCharacters = "0123456789";

    public BrowserDriver(String filePath, int browser) {
        if (browser == 2) {
            System.setProperty("webdriver.chrome.driver", "../chromedriver.exe");
            this.driver = new ChromeDriver();
        }
        else {
            this.driver = new FirefoxDriver();
        }
        this.wait = new WebDriverWait(driver, waitTime);
        this.filePath = filePath;
    }

    public WebDriver getDriver() {
        return this.driver;
    }

    public void getPage(String url) {
        driver.get(url);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public void reloadPage() {
        driver.get(this.getCurrentUrl());
    }

    public void takeScreenshot(String filename, String path) {
        filename = this.filterScreenshotFileName(filename);
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            // FileUtils.copyFile(scrFile, new File(this.filePath + "\\" +
            // filename + ".png"));
            FileUtils.copyFile(scrFile, new File(path + filename + ".png"));
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String filterScreenshotFileName(String fileName) {
        fileName = fileName.replaceAll("/", "-");
        fileName = fileName.replaceAll("http://", "");
        return fileName;
    }

    public void loginAs(String username, String password) {
        driver.findElement(By.name("name")).sendKeys(username);
        driver.findElement(By.name("pass")).sendKeys(password);
        this.formSubmit("input[type='submit']");
    }

    public void formSubmit(String selector) {
        driver.findElement(By.cssSelector(selector)).submit();
    }

    public void availabilityAndFormSubmit(String selector) {
        try {
            driver.findElement(By.cssSelector(selector)).submit();
        }
        catch (NoSuchElementException error) {
        }
    }

    public void clickElement(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }

    public void input(String selector, String input) {
        driver.findElement(By.cssSelector(selector)).sendKeys(input);
    }

    public void waitFor(String selector) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
    }

    public void waitWhile(String selector) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(selector)));
    }

    public void waitForVisible(final String elemSelector) {
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                return getElem(elemSelector).isDisplayed();
            }
        });
    }

    public void clearSession() {
        driver.manage().deleteAllCookies();
    }

    public void delay(int seconds) {
        driver.manage().timeouts().implicitlyWait(seconds, TimeUnit.SECONDS);
    }

    public boolean isElemExist(String selector) {
        try {
            driver.findElement(By.cssSelector(selector));
            return true;
        }
        catch (NoSuchElementException error) {
            return false;
        }
    }

    public WebElement getElem(String selector) {
        try {
            WebElement elem = driver.findElement(By.cssSelector(selector));
            return elem;
        }
        catch (NoSuchElementException error) {
            return null;
        }
    }

    public List<WebElement> getElems(String selector) {
        List<WebElement> elems = driver.findElements(By.cssSelector(selector));
        return elems;
    }

    public List<String> getCurrentPageLinks() {
        List<WebElement> pageLinkElems = driver.findElements(By.cssSelector("a"));
        List<String> pageLinks = new ArrayList<String>();
        String href = null;
        for (int i = 0; i < pageLinkElems.size(); i++) {
            if ((pageLinkElems.get(i)).isDisplayed()) {
                // if (isAttributeExist(pageLinkElems.get(i), "href") == true) {
                try {
                    href = pageLinkElems.get(i).getAttribute("href");
                }
                catch (Exception exception) {
                    System.out.println("Vot on");
                }
                if (href != null && !href.isEmpty()) {
                    pageLinks.add(href);
                }
                // }
            }
        }
        return pageLinks;
    }

    public boolean isAttributeExist(WebElement element, String Attribute) {
        try {
            element.getAttribute("href");
        }
        catch (StaleElementReferenceException error) {
            return false;
        }
        return true;
    }

    public void chageScreenSize(Dimension dimension) {
        this.driver.manage().window().setSize(dimension);
    }

    public void closeBrowser() {
        this.driver.close();
        this.driver.quit();
    }

    public void uploadFile(String selector, String fileName) {
        String dir = new File(".").getAbsolutePath();
        String pathToFile = dir + "/" + fileName;
        WebElement fileInput = this.getElem(selector);
        fileInput.sendKeys(pathToFile);
    }

    public boolean isPageTitleContains(String text) {
        boolean titleContains = driver.getTitle().contains(text);
        return titleContains;
    }

    public String generateRandomString(boolean numbersOnly) {
        String text = null;
        String characters = randomStringCharacters;
        if (numbersOnly) {
            characters = this.randomNumberStringCharacters;
        }
        Random random = new Random();
        int length = random.nextInt(9) + 1;
        for (int i = 0; i < length; i++) {
            text += characters.charAt(random.nextInt(characters.length()));
        }
        return text;
    }

    public boolean generateRandomBooleanValue() {
        Random rand = new Random();
        return rand.nextBoolean();
    }

    public void filesDelete(String path) {
        try {
            for (File file : new File(path).listFiles())
                if (file.isFile()) file.delete();
        }
        catch (NullPointerException error) {
            System.out.println(path + " folder is not found");
        }
    }

}