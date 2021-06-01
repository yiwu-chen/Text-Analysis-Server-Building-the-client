import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;

import io.swagger.client.*;
import io.swagger.client.api.TextbodyApi;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;


public class Client {
  //private static String localUrl = "http://localhost:8080/server_war_exploded/server/textbody/wordcount";
  private static String ec2Url = "http://54.242.178.140:8080/server_war/server/textbody/wordcount";
  private static int numThreads;
  private static String filePath;
  static CyclicBarrier barrier;
  static int count = 0;
  static AtomicInteger successfulRequests = new AtomicInteger();
  static AtomicInteger unsuccessfulRequests = new AtomicInteger();
  private static Logger logger;

  public synchronized void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }


  public static void main(String[] args) throws InterruptedException, IOException,
          BrokenBarrierException {
    //args[0]: an input file. Use this one for testing
    //args[1]: number of threads to run - maxThreads
    filePath = args[0];
    numThreads = Integer.parseInt(args[1]);
    //Process and validate the input parameters
    File file = new File(filePath);
    BufferedReader br = new BufferedReader(new FileReader(file));
    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    barrier = new CyclicBarrier(numThreads + 1);
    final Client counter = new Client();
    Producer producer = new Producer(queue, br, logger);
    new Thread(producer).start();

    ApiClient apiClient = new ApiClient().setBasePath(ec2Url);
    final TextbodyApi api = new TextbodyApi(apiClient);

    long startTime = System.currentTimeMillis();

    //Multiple consumers that continuously send a line from the file to the server.
    Consumer[] consumers = new Consumer[numThreads];
    for (int i = 0; i < numThreads; i++) {
      consumers[i] = new Consumer(queue, api, barrier, counter, successfulRequests,
              unsuccessfulRequests, logger);
      new Thread(consumers[i]).start();
    }
    barrier.await();
    long endTime = System.currentTimeMillis();
    double wallTime = (double) (endTime - startTime) / 1000;
    System.out.println("Total number of threads: " + numThreads);
    System.out.println("Total number of successful requests: " + successfulRequests);
    System.out.println("Total number of unsuccessful requests: " + unsuccessfulRequests);
    System.out.println("The total run time(wall time): " + wallTime + " seconds");
    int totalRequests = successfulRequests.intValue() + unsuccessfulRequests.intValue();
    System.out.println("Throughput: " + totalRequests / wallTime + " requests/seconds");
    System.out.println("Total running threads should be equal to " + numThreads + ". It is: "
            + counter.getVal());
  }
}