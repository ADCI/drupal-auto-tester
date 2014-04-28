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
    
	public static final void main(String... aArgs) throws InterruptedException, ExecutionException {
		ParallelTester tester = new ParallelTester();
		tester.siteTesterParallelScan();
	}
	
	void siteTesterParallelScan()  throws InterruptedException, ExecutionException  {
	    ExecutorService executor = Executors.newFixedThreadPool(this.numThreads);
	    CompletionService<String> compService = new ExecutorCompletionService<>(executor);
	    for (int i = 0; i < this.numThreads; i++) {
	    	Task task = new Task();
	      	compService.submit(task);
	    }
	    for(int i = 0; i < this.numThreads; i++){
	    	Future<String> future = compService.take();
	    	System.out.println(future.get());
	    }
	    executor.shutdown(); //always reclaim resources
	}
	
	private final class Task implements Callable<String> {

		@Override public String call() throws Exception {
	    	SiteTester scanner = new SiteTester();
	    	return scanner.siteTesterScan();
	    }
	}
}

