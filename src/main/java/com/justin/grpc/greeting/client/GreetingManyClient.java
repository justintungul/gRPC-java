package com.justin.grpc.greeting.client;

import com.justin.proto.GreetManyTimesRequest;
import com.justin.proto.GreetServiceGrpc;
import com.justin.proto.Greeting;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GreetingManyClient {
  public static void main(String[] args) {
    System.out.println("---- Client for Learning gRPC ----");

    // build the channel
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext() // DEV-ONLY: forcing SSL to be deactivated
        .build();

    System.out.println("Creating stub");
    GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

    GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
        .setGreeting(
            Greeting.newBuilder().setFirstName("Justin")
        ).build();

    greetClient.greetManyTimes(greetManyTimesRequest)
        .forEachRemaining(greetManyTimesResponse -> {
          System.out.println(greetManyTimesResponse.getResult());
        });

    System.out.println("Shutting Down Channel");
    channel.shutdown();
  }
}
