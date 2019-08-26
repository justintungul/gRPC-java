package com.justin.grpc.calculator.client;

import com.justin.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class CalculatorClient {
  public static void main(String[] args) {
    System.out.println("---- Calculator Client ----");

    CalculatorClient main = new CalculatorClient();
    main.run();
  }

  public void run() {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating stub");
    //doSum(channel);
    doSqrtError(channel);

    System.out.println("Shutting Down Channel");
    channel.shutdown();
  }

  public void doSum(ManagedChannel channel) {
    CalculateServiceGrpc.CalculateServiceBlockingStub calculatorClient = CalculateServiceGrpc.newBlockingStub(channel);

    CalculateRequest calculateRequest = CalculateRequest.newBuilder()
        .setNum1(5)
        .setNum2(5)
        .build();

    CalculateResponse calculateResponse = calculatorClient.calculate(calculateRequest);

    System.out.println(calculateResponse.getResult());
  }

  public void doSqrtError(ManagedChannel channel) {
    CalculateServiceGrpc.CalculateServiceBlockingStub sqrtClient = CalculateServiceGrpc.newBlockingStub(channel);

    Integer number = -1;

    try {
      SquareRootRequest sqrtRequest = SquareRootRequest.newBuilder()
          .setNumber(number)
          .build();
      SquareRootResponse sqrtResponse = sqrtClient.squareRoot(sqrtRequest);
      System.out.println(sqrtResponse.getResult());
    } catch (StatusRuntimeException e) {
      System.out.println("Got an exception for square root!");
      e.printStackTrace();
    }
  }

}
