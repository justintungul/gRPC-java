package com.justin.grpc.greeting.client;

import com.justin.proto.greet.GreetRequest;
import com.justin.proto.greet.GreetResponse;
import com.justin.proto.greet.GreetServiceGrpc;
import com.justin.proto.greet.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingClient {
  public static void main(String[] args) {
    System.out.println("---- Client for Learning gRPC ----");

    // build the channel
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext() // DEV-ONLY: forcing SSL to be deactivated
        .build();

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

    // do something with the response
    System.out.println(greetResponse.getResult());

    // old dummy service
    // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);

    // async
    // DummyServiceGrpc.DummyServiceFutureStub asnycClient = DummyServiceGrpc.newFutureStub(channel);

    System.out.println("Shutting Down Channel");
    channel.shutdown();
  }
}
