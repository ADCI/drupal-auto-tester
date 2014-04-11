import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;



public class FirefoxTest {

	private FirefoxDriver driver;
	private String filePath;
	private WebDriverWait wait;
	private final int waitTime = 30;
	private String randomStringCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private String randomNumberStringCharacters = "0123456789";	
	
	
	public FirefoxTest(String filePath) {
		this.driver = new FirefoxDriver();
		this.wait = new WebDriverWait(driver, waitTime);
		this.filePath = filePath;
	}

	public FirefoxDriver getDriver() {
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

	public void takeScreenshot(String filename) {
		File scrFile = ((TakesScreenshot) driver)
				.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(scrFile, new File(this.filePath + "\\"
					+ filename + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loginAs(String username, String password) {
		driver.findElement(By.name("name")).sendKeys(username);
		driver.findElement(By.name("pass")).sendKeys(password);
		this.formSubmit("input[type='submit']");
	}

	public void formSubmit(String selector) {
		driver.findElementByCssSelector(selector).submit();
	}
	
	public void availabilityAndFormSubmit(String selector) {
		try {
			driver.findElementByCssSelector(selector).submit();
		}
		catch (NoSuchElementException error) {
		}
	}

	public void clickElement(String selector) {
		driver.findElementByCssSelector(selector).click();
	}

	public void input(String selector, String input) {
		driver.findElementByCssSelector(selector).sendKeys(input);
	}

	public void waitFor(String selector) {
		wait.until(ExpectedConditions.presenceOfElementLocated(By
				.cssSelector(selector)));
	}

	public void waitWhile(String selector) {
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By
				.cssSelector(selector)));
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

	public boolean isElemExist(String elemSelector) {
		try {
			driver.findElementByCssSelector(elemSelector);
			return true;
		} catch (NoSuchElementException error) {
			return false;
		}
	}

	public WebElement getElem(String selector) {
		try {
			WebElement elem = driver.findElementByCssSelector(selector);
			return elem;
		} catch (NoSuchElementException error) {
			return null;
		}
	}

	public List<WebElement> getElems(String selector) {
		List<WebElement> elems = driver.findElementsByCssSelector(selector);
		return elems;
	}
	
	public List<String> getCurrentPageLinks() {
		List<WebElement> pageLinkElems = driver.findElementsByCssSelector("a");
		List<String> pageLinks = new ArrayList<String>();
		String href = null;
		for (int i = 0; i < pageLinkElems.size(); i++) {
			if((pageLinkElems.get(i)).isDisplayed()) {
				href = pageLinkElems.get(i).getAttribute("href");
				if (href != null && !href.isEmpty()) {
					pageLinks.add(href);
				}	
			}
		}
		return pageLinks;
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

	public void fillFormText(String selector) {
		List<WebElement> fields = new ArrayList<WebElement>();
		fields.addAll(this.getElems(selector + " input[type='text']"));
		fields.addAll(this.getElems(selector + " input[type='password']"));
		fields.addAll(this.getElems(selector + " textarea"));
		for (int i = 0; i < fields.size(); i++) {
			try {
				Random random = new Random();
				int length = random.nextInt(9) + 1;
				String randomString = this.generateString(random, this.randomStringCharacters, length);
				fields.get(i).clear();
				fields.get(i).sendKeys(randomString);
			} 
			catch (ElementNotVisibleException error) {
				//System.out.println("Hide field");
			}
			catch (InvalidElementStateException error) {
				//System.out.println("Invalid field");
			}
		}
	}
	
	public void fillFormNumber(String selector) {
		List<WebElement> fields = new ArrayList<WebElement>();
		fields.addAll(this.getElems(selector + " input[type='number']"));
		for (int i = 0; i < fields.size(); i++) {
			try {
				Random random = new Random();
				int length = random.nextInt(9) + 1;
				String randomNumberString = this.generateString(random, this.randomNumberStringCharacters, length);
				fields.get(i).clear();
				fields.get(i).sendKeys(randomNumberString);
			} 
			catch (ElementNotVisibleException error) {
				//System.out.println("Hide field");
			}
			catch (InvalidElementStateException error) {
				//System.out.println("Invalid field");
			}
		}
	}

	public void fillFormSelect(String selector) {
		List<WebElement> selects = new ArrayList<WebElement>();
		selects.addAll(this.getElems(selector
				+ " .form-item .form-select option:last-child"));
		for (int a = 0; a < selects.size(); a++) {
			try {
				selects.get(a).click();
			} 
			catch (ElementNotVisibleException error) {
				//System.out.println("Hide field");
			}
			catch (StaleElementReferenceException error) {
				//System.out.println("Hide field");
			}
		}
	}

	public void fillFormTextDate(String selector) {
		List<WebElement> fields = new ArrayList<WebElement>();
		fields.addAll(this.getElems(selector
				+ " .form-type-date-popup .form-text"));
		for (int c = 0; c < fields.size(); c++) {
			fields.get(c).clear();
			fields.get(c).sendKeys("01/02/2014");
		}
	}

	public void fillFormCheckboxes(String selector) {
		List<WebElement> checkboxes = new ArrayList<WebElement>();
		checkboxes.addAll(this.getElems(selector
				+ " .form-type-checkbox:first-child input"));
		for (int c = 0; c < checkboxes.size(); c++) {
			try {
				checkboxes.get(c).click();
				this.waitWhile(".ajax-throbber");
			} catch (ElementNotVisibleException error) {
				//System.out.println("Hide field");
			}
		}
	}
	
	public void fillFormRadios(String selector) {
		List<WebElement> radioButton = new ArrayList<WebElement>();
		radioButton.addAll(this.getElems(selector
				+ " .form-type-radio:first-child input"));
		for (int c = 0; c < radioButton.size(); c++) {
			try {
				radioButton.get(c).click();
				this.waitWhile(".ajax-throbber");
			} catch (ElementNotVisibleException error) {
				//System.out.println("Hide field");
			}
		}
	}
	
	public String generateString(Random rng, String characters, int length) {
	   char[] text = new char[length];
	   for (int i = 0; i < length; i++) {
	       text[i] = characters.charAt(rng.nextInt(characters.length()));
	   }
	   return new String(text);
	}	
	
	public boolean isPageTitleContains(String text) {
		boolean titleContains = driver.getTitle().contains(text);
		return titleContains;
	}
	
	public boolean generateRandomBooleanValue() {
		Random rand = new Random();
		return rand.nextBoolean();
	}
	
	public void fieldsetsOpen(String selector) { 
		List<WebElement> fieldsets = new ArrayList<WebElement>();	
		fieldsets.addAll(this.getElems(selector + " fieldset.collapsed .fieldset-title"));
		for (int a = 0; a < fieldsets.size(); a++) {
			try {
				fieldsets.get(a).click();
				this.waitForVisible(".fieldset-wrapper");
			} 
			catch (ElementNotVisibleException error) {
			}
		}
	}
	
	public void collapsedTableOpen(String selector) {
		List<WebElement> fieldsets = new ArrayList<WebElement>();	
		fieldsets.addAll(this.getElems(selector + " .name.clicked"));
		for (int a = 0; a < fieldsets.size(); a++) {
			try {
				fieldsets.get(a).click();
			} 
			catch (ElementNotVisibleException error) {
			}
			catch (StaleElementReferenceException error) {
			}
		}	
	}

	
	public void documentCreate(String name, String value) {
		try {
			File flt = new File(name + ".txt");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(flt)));
			out.print(value);
			out.flush();
			System.out.println("Output is generated in a file " + name + ".txt");				
		}
		catch (IOException error) {
			
		}
	 
	}

		
}




















