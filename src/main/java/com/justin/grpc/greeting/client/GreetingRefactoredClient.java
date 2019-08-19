package com.justin.grpc.greeting.client;

import com.justin.proto.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GreetingRefactoredClient {
  public static void main(String[] args) {
    System.out.println("---- Client for Learning gRPC ----");

    GreetingRefactoredClient main = new GreetingRefactoredClient();
    main.run();
  }

  private void run() {
    // build the channel
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext() // DEV-ONLY: forcing SSL to be deactivated
        .build();

    // doUnaryCall(channel);
    // doServerStreamingCall(channel);
    // doClientStreamingCall(channel);
    doBiDiStreamingCall(channel);

    System.out.println("Shutting Down Channel");
    channel.shutdown();
  }

  private void doUnaryCall(ManagedChannel channel) {
    // create a greet service (blocking - sync)
    System.out.println("Creating stub");
    GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

    // create protobuf greeting message
    Greeting greeting = Greeting.newBuilder()
        .setFirstName("Justin")
        .setLastName("Tungul")
        .build();

    // create protobuf GreetingRequest message
    System.out.println("Generating request");
    GreetRequest greetRequest = GreetRequest.newBuilder()
        .setGreeting(greeting)
        .build();

    // call the rpc and get back the GreetResponse (via protobuff)
    System.out.println("Making an RPC call");
    GreetResponse greetResponse = greetClient.greet(greetRequest);
  }

  private void doServerStreamingCall(ManagedChannel channel) {    System.out.println("Creating stub");
    GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

    GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
        .setGreeting(
            Greeting.newBuilder().setFirstName("Justin")
        ).build();

    greetClient.greetManyTimes(greetManyTimesRequest)
        .forEachRemaining(greetManyTimesResponse -> {
          System.out.println(greetManyTimesResponse.getResult());
        });
  }

  private void doClientStreamingCall(ManagedChannel channel) {
    // create an async client
    GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

    // very common pattern for async
    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
      @Override
      public void onNext(LongGreetResponse value) {
        // we get a response from a server -- called only once
        System.out.println("Received a response from the server");
        System.out.println(value.getResult());
      }

      @Override
      public void onError(Throwable t) {
        // we get an error from the server
      }

      @Override
      public void onCompleted() {
        // the server is done -- called right after onNext()
        System.out.println("Serve has completed sending responses");
        latch.countDown();
      }
    });

    System.out.println("Sending message 1");
    requestObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(
            Greeting.newBuilder()
                .setFirstName("Justin").build()
        ).build());

    System.out.println("Sending message 2");
    requestObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(
            Greeting.newBuilder()
                .setFirstName("John").build()
        ).build());

    System.out.println("Sending message 3");
    requestObserver.onNext(LongGreetRequest.newBuilder()
        .setGreeting(
            Greeting.newBuilder()
                .setFirstName("Siobhan").build()
        ).build());

    // tell the server that the client is done sending data
    requestObserver.onCompleted();

    try {
      latch.await(3L, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void doBiDiStreamingCall(ManagedChannel channel) {
    // create an async client
    GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

    // very common pattern for async
    CountDownLatch latch = new CountDownLatch(1);

    StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(
        new StreamObserver<GreetEveryoneResponse>() {
          @Override
          public void onNext(GreetEveryoneResponse value) {
            System.out.println("Response from server: " + value.getResult());
          }

          @Override
          public void onError(Throwable t) {
            latch.countDown();
          }

          @Override
          public void onCompleted() {
            System.out.println("Server is done sending data");
            latch.countDown();
          }
        }
    );

    Arrays.asList("Justin", "Kendal", "Siobhan", "Froe").forEach(
        name -> {
          System.out.println("Sending: " + name);
          requestObserver.onNext(GreetEveryoneRequest.newBuilder()
              .setGreeting(Greeting.newBuilder()
                  .setFirstName(name)
              ).build()
          );
        }
    );

    requestObserver.onCompleted();

    try {
      latch.await( 3, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
