package com.justin.grpc.calculator.server;

import com.justin.proto.calculator.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class CalculateServiceImpl extends CalculateServiceGrpc.CalculateServiceImplBase {

  @Override
  public void calculate(CalculateRequest request, StreamObserver<CalculateResponse> responseObserver) {

    Integer result = request.getNum1() + request.getNum2();

    CalculateResponse response = CalculateResponse.newBuilder()
        .setResult(result)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void squareRoot(SquareRootRequest request, StreamObserver<SquareRootResponse> responseObserver) {
    Integer number = request.getNumber();

    if (number >= 0) {
      double numberRoot = Math.sqrt(number);
      responseObserver.onNext(
          SquareRootResponse.newBuilder()
              .setResult(numberRoot)
              .build()
      );
      responseObserver.onCompleted();
    } else {
      // construct the exception
      responseObserver.onError(
          Status.INVALID_ARGUMENT
          .withDescription("The number sent is not positive")
              .augmentDescription("Number sent: " + number)
          .asRuntimeException()
      );
    }

  }
}
