package com.justin.grpc.greeting.server;

import com.justin.proto.*;
import io.grpc.stub.StreamObserver;

import java.util.stream.Stream;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
  @Override
  public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
    // extract the fields we need
    System.out.println("Received request");
    Greeting greeting = request.getGreeting();

    // handle the request
    System.out.println("Processing request");
    String firstName = greeting.getFirstName();
    String result = "Hello " + firstName;

    // create the response
    System.out.println("Generating Response");
    GreetResponse response = GreetResponse.newBuilder()
        .setResult(result)
        .build();

    // send the response
    System.out.println("Sending Response to Client");
    responseObserver.onNext(response);

    // complete the RPC call
    responseObserver.onCompleted();

    // don't need
    // super.greet(request, responseObserver);
  }

  @Override
  public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
    String firstName = request.getGreeting().getFirstName();
    try {
      for (int i = 0; i < 10; i++) {
        String result = "Hello " + firstName + ", response number: " + i;
        GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
            .setResult(result)
            .build();

        responseObserver.onNext(response);
        Thread.sleep(1000L); // L for long
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      responseObserver.onCompleted();
    }
  }

  @Override
  public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
    StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {

      String result = "";

      @Override
      public void onNext(LongGreetRequest value) {
        // client sends a message
        result += "Hello " + value.getGreeting().getFirstName() + "!\n";
      }

      @Override
      public void onError(Throwable t) {
        // client sends an error - ignore for now
      }

      @Override
      public void onCompleted() {
        // client is done - where we want to send a response (responseObserver)
        responseObserver.onNext(
            LongGreetResponse.newBuilder()
            .setResult(result)
            .build()
        );
        responseObserver.onCompleted();
      }
    };
    return requestObserver;
  }

  @Override
  public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
    StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
      @Override
      public void onNext(GreetEveryoneRequest value) {
        System.out.println("Received request from client w/ data: " + value.getGreeting().getFirstName());
        String result = "Hello " + value.getGreeting().getFirstName();
        GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder()
            .setResult(result)
            .build();
        responseObserver.onNext(greetEveryoneResponse);
      }

      @Override
      public void onError(Throwable t) {}

      @Override
      public void onCompleted() {
        System.out.println("Client is done sending data");
        responseObserver.onCompleted();
      }
    };
    return requestObserver;
  }
}
