package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Reporter {

    private int formSubmittedCount = 0;
    private int errorsCount = 0;
    private int formErrorsCount = 0;
    private int accessDeniedCount = 0;
    private int pageNotFoundCount = 0;
    private int visitedPagesCount = 0;
    private final List<String> errorMessages = new ArrayList<String>();

    public void visitedPagesAdd() {
        this.visitedPagesCount++;
    }

    public void ErrorAdd() {
        this.errorsCount++;
    }

    public void formErrorAdd() {
        this.formErrorsCount++;
    }

    public void formSubmittedAdd() {
        this.formSubmittedCount++;
    }

    public void accessDeniedAdd() {
        this.accessDeniedCount++;
    }

    public void pageNotFoundAdd() {
        this.pageNotFoundCount++;
    }

    public void addErrorMessage(String errorMessage) {
        this.errorMessages.add(errorMessage);
    }

    public void reportConsole() {
        System.out.println("Visited pages = " + this.visitedPagesCount);
        System.out.println("Pages containing errors  = " + this.errorsCount);
        System.out.println("Access denied = " + this.accessDeniedCount);
        System.out.println("Page not found  = " + this.pageNotFoundCount);
        System.out.println("Submitted forms = " + this.formSubmittedCount);
        System.out.println("Form errors = " + this.formErrorsCount);
        for (int i = 0; i < this.errorMessages.size(); i++) {
            System.out.println(this.errorMessages.get(i));
        }
    }

    public void reportFile(String fileName, String filePath) {
        // Create result file
        String value = "Visited pages count  = " + this.visitedPagesCount + "\n";
        value = value + "Pages containing errors  = " + this.errorsCount + "\n";
        for (int i = 0; i < this.errorMessages.size(); i++) {
            value += this.errorMessages.get(i) + "\n";
        }
        this.documentCreate(fileName, filePath, value);
    }

    public void documentCreate(String name, String path, String value) {
        try {
            File flt = new File(path + name);
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(flt)));
            out.print(value);
            out.flush();
            System.out.println("Output is generated in a file " + name);
            out.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }
    }
}