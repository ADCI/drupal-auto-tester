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
        String testUserLogin = "";
        String testUserPass = "";
        int needLogin = 0;
        boolean fillForms = false;
        // String collectorName = "";
        String host = args[0];
        String collectorName = args[1];
        try {
            // String collectorNameArgs = args[1];
            String userLoginAgrs = args[2];
            String userPassArgs = args[3];
            String needLoginArgs = args[4];
            String fillFormAgrs = args[5];

            // if (!collectorNameArgs.equals("null")) {
            // collectorName = collectorNameArgs;
            // }
            if (!userLoginAgrs.equals("null")) {
                testUserLogin = userLoginAgrs;
            }
            if (!userPassArgs.equals("null")) {
                testUserPass = userPassArgs;
            }
            if (!needLoginArgs.equals("null")) {
                needLogin = Integer.parseInt(needLoginArgs.toString());
            }
            if (fillFormAgrs.equals("true")) {
                fillForms = true;
            }
        }
        catch (ArrayIndexOutOfBoundsException error) {
        }
        System.out.println(collectorName);

        ParallelTester tester = new ParallelTester();
        tester.siteTesterParallelScan(host, collectorName, testUserLogin, testUserPass, needLogin, fillForms);
    }

    void siteTesterParallelScan(String host, String collectorName, String testUserLogin, String testUserPass, int needLogin, boolean fillForms) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
        CompletionService<String> compService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < this.numThreads; i++) {
            Task task = new Task(host, collectorName, testUserLogin, testUserPass, needLogin, fillForms);
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
        private final String collectorName;
        private final String testUserLogin;
        private final String testUserPass;
        private final int needLogin;
        private final boolean fillForms;

        public Task(String host, String collectorName, String testUserLogin, String testUserPass, int needLogin, boolean fillForms) {
            this.host = host;
            this.collectorName = collectorName;
            this.testUserLogin = testUserLogin;
            this.testUserPass = testUserPass;
            this.needLogin = needLogin;
            this.fillForms = fillForms;
        }

        @Override
        public String call() throws Exception {
            SiteTester scanner = new SiteTester(this.host, this.collectorName, this.testUserLogin, this.testUserPass, this.needLogin, this.fillForms);
            return scanner.siteTesterScan();
        }
    }
}