package src;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelTester {
    int numThreads = 1;

    public static final void main(String[] args) throws InterruptedException, ExecutionException {
        String host = args[0];
        String builderName = args[1];
        String pageNotFoundTitle = "Page not found";
        String accessDeniedPageTitle = "Access denied";
        int needLogin = 0;
        String testUserLogin = "";
        String testUserPass = "";
        boolean fillForms = false;
        try {
            String pageNotFoundTitleArgs = args[2];
            String accessDeniedPageTitleArgs = args[3];
            String needLoginArgs = args[4];
            String userLoginAgrs = args[5];
            String userPassArgs = args[6];
            String fillFormAgrs = args[7];

            if (!pageNotFoundTitleArgs.equals("null")) {
                pageNotFoundTitle = pageNotFoundTitleArgs;
            }
            if (!accessDeniedPageTitleArgs.equals("null")) {
                accessDeniedPageTitle = accessDeniedPageTitleArgs;
            }
            if (!needLoginArgs.equals("null")) {
                try {
                    needLogin = Integer.parseInt(needLoginArgs.toString());
                }
                catch (NumberFormatException error) {
                }
            }
            if (!userLoginAgrs.equals("null")) {
                testUserLogin = userLoginAgrs;
            }
            if (!userPassArgs.equals("null")) {
                testUserPass = userPassArgs;
            }
            if (fillFormAgrs.equals("true")) {
                fillForms = true;
            }
        }
        catch (ArrayIndexOutOfBoundsException error) {
        }
        System.out.println(host);
        System.out.println(builderName);
        System.out.println(pageNotFoundTitle);
        System.out.println(accessDeniedPageTitle);
        System.out.println(needLogin);
        System.out.println(testUserLogin);
        System.out.println(testUserPass);
        System.out.println(fillForms);

        ParallelTester tester = new ParallelTester();
        tester.siteTesterParallelScan(host, builderName, pageNotFoundTitle, accessDeniedPageTitle, needLogin, testUserLogin, testUserPass, fillForms);
    }

    void siteTesterParallelScan(String host, String builderName, String pageNotFoundTitle, String accessDeniedPageTitle, int needLogin, String testUserLogin, String testUserPass, boolean fillForms) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
        CompletionService<String> compService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < this.numThreads; i++) {
            Task task = new Task(host, builderName, pageNotFoundTitle, accessDeniedPageTitle, needLogin, testUserLogin, testUserPass, fillForms);
            compService.submit(task);
        }
        for (int i = 0; i < this.numThreads; i++) {
            Future<String> future = compService.take();
            System.out.println(future.get());
        }
        executor.shutdown(); // always reclaim resources
    }

    private final class Task implements Callable<String> {

        private final String host;
        private final String builderName;
        private final String pageNotFoundTitle;
        private final String accessDeniedPageTitle;
        private final int needLogin;
        private final String testUserLogin;
        private final String testUserPass;
        private final boolean fillForms;

        public Task(String host, String builderName, String pageNotFoundTitle, String accessDeniedPageTitle, int needLogin, String testUserLogin, String testUserPass, boolean fillForms) {
            this.host = host;
            this.builderName = builderName;
            this.pageNotFoundTitle = pageNotFoundTitle;
            this.accessDeniedPageTitle = accessDeniedPageTitle;
            this.needLogin = needLogin;
            this.testUserLogin = testUserLogin;
            this.testUserPass = testUserPass;
            this.fillForms = fillForms;
        }

        @Override
        public String call() throws Exception {
            SiteTester scanner = new SiteTester(this.host, this.builderName, this.pageNotFoundTitle, this.accessDeniedPageTitle, this.needLogin, this.testUserLogin, this.testUserPass, this.fillForms);
            return scanner.siteTesterScan();
        }
    }
}