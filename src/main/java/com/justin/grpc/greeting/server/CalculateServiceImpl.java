package com.justin.grpc.greeting.server;

import com.justin.proto.CalculateRequest;
import com.justin.proto.CalculateResponse;
import com.justin.proto.CalculateServiceGrpc;
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
}
