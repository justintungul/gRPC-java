package com.justin.grpc.greeting.client;

import com.justin.proto.CalculateRequest;
import com.justin.proto.CalculateResponse;
import com.justin.proto.CalculateServiceGrpc;
import com.justin.proto.Calculator;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class CalculatorClient {
  public static void main(String[] args) {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build();

    System.out.println("Creating stub");
    CalculateServiceGrpc.CalculateServiceBlockingStub calculatorClient = CalculateServiceGrpc.newBlockingStub(channel);

    CalculateRequest calculateRequest = CalculateRequest.newBuilder()
        .setNum1(5)
        .setNum2(5)
        .build();

    CalculateResponse calculateResponse = calculatorClient.calculate(calculateRequest);

    System.out.println(calculateResponse.getResult());

    System.out.println("Shutting Down Channel");
    channel.shutdown();
  }
}
