package com.justin.grpc.calculator.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class CalculatorServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    System.out.println("---- Server for Calculator gRPC ----");

    // create server instance
    Server server = ServerBuilder.forPort(50051)
        // add the implementation to the server
        .addService(new CalculateServiceImpl())
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
