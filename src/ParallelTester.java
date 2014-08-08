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
        System.out.println(args[0]);
        System.out.println(args[1]);
        System.out.println(args[2]);
        System.out.println(args[3]);
        System.out.println(args[4]);

        String host = args[0];
        String testUserLogin = args[1];
        String testUserPass = args[2];
        int needLogin = Integer.parseInt(args[3].toString());
        boolean fillForms = false;
        if (args[4].equals("true")) {
            fillForms = true;
        }

        ParallelTester tester = new ParallelTester();
        tester.siteTesterParallelScan(host, testUserLogin, testUserPass, needLogin, fillForms);

        // SiteTester tester = new SiteTester(host, testUserLogin, testUserPass,
        // needLogin, fillForms);
        // tester.siteTesterScan();
    }

    void siteTesterParallelScan(String host, String testUserLogin, String testUserPass, int needLogin, boolean fillForms) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
        CompletionService<String> compService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < this.numThreads; i++) {
            Task task = new Task(host, testUserLogin, testUserPass, needLogin, fillForms);
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
        private final String testUserLogin;
        private final String testUserPass;
        private final int needLogin;
        private final boolean fillForms;

        public Task(String host, String testUserLogin, String testUserPass, int needLogin, boolean fillForms) {
            this.host = host;
            this.testUserLogin = testUserLogin;
            this.testUserPass = testUserPass;
            this.needLogin = needLogin;
            this.fillForms = fillForms;
        }

        @Override
        public String call() throws Exception {
            SiteTester scanner = new SiteTester(this.host, this.testUserLogin, this.testUserPass, this.needLogin, this.fillForms);
            return scanner.siteTesterScan();
        }
    }
}