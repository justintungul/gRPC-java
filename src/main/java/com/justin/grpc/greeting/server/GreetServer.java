package com.justin.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GreetServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("---- Server for Learning gRPC ----");

    // create server instance
    Server server = ServerBuilder.forPort(50051)
        // add the implementation to the server
        .addService(new GreetServiceImpl())
        .build();

    // starting the server
    server.start();

    // stopping the server
    Runtime.getRuntime().addShutdownHook(new Thread( () -> {
      System.out.println("Received Shutdown Request");
      server.shutdown();
      System.out.println("Successfully Stopped the Server");
    } ));

    // in gRPC this server needs to be blocking to the main thread
    server.awaitTermination();
  }
}
