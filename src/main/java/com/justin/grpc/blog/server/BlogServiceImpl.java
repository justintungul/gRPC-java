package com.justin.grpc.blog.server;

import com.justin.proto.blog.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

/*
 * Receives the request from the client then talks do mongodb and generates the response
 */
public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

  private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
  private MongoDatabase database = mongoClient.getDatabase("grpc-blog");
  private MongoCollection<Document> collection = database.getCollection("blogs");

  @Override
  public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

    System.out.println("Received Create Blog Request");
    Blog blog = request.getBlog();

    Document doc = new Document()
        .append("author_id", blog.getAuthorId())
        .append("title", blog.getTitle())
        .append("content", blog.getContent());

    System.out.println("Inserting blog to mongo db...");
    // insert (create) the document in mongodb
    collection.insertOne(doc);

    // get the mongodb generated id
    String id = doc.getObjectId("_id").toString();
    System.out.println("Inserted blog id: " + id);

    CreateBlogResponse response = CreateBlogResponse.newBuilder()
        .setBlog(Blog.newBuilder()
            .setAuthorId(blog.getAuthorId())
            .setContent(blog.getContent())
            .setTitle(blog.getTitle())
            .setId(id)
        ).build();

        // OR
        // .setBlog(blog.toBuilder().setId().build()
        // .build();

    // send the response over
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {
    System.out.println("Received Read Blog Request");
    String blogId = request.getBlogId();

    Document result = null;

    try {
      result = collection.find(eq("_id", new ObjectId(blogId)))
          .first();
    } catch (Exception e) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("Blog with id: " + blogId + " is not in proper format")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException()
      );
    }

    if (result == null) {
      System.out.println("Blog with id: " + blogId + " was not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("Blog with id: " + blogId + " was not found")
              .asRuntimeException()
      );
    } else {
      System.out.println("Blog found, sending response...");
      Blog blog = documentToBlog(result);

      responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
      responseObserver.onCompleted();
    }
  }

  @Override
  public void updateBlog(UpdateBlogRequest request, StreamObserver<UpdateBlogResponse> responseObserver) {
    System.out.println("Received Update Blog Request");
    String blogId = request.getBlog().getId();
    Blog blog = request.getBlog();

    System.out.println("Searching blog for update...");
    Document result = null;

    try {
      result = collection.find(eq("_id", new ObjectId(blogId)))
          .first();
    } catch (Exception e) {
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("Blog with id: " + blogId + " is not in proper format")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException()
      );
    }

    if (result == null) {
      System.out.println("Blog with id: " + blogId + " was not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("Blog with id: " + blogId + " was not found")
              .asRuntimeException()
      );
    } else {
      Document replacement = new Document()
          .append("author_id", blog.getAuthorId())
          .append("title", blog.getTitle())
          .append("content", blog.getContent())
          .append("_id", new ObjectId(blogId));

      System.out.println("Replacing blog in database...");
      collection.replaceOne(eq("_id", result.getObjectId("_id")), replacement);

      System.out.println("Replace successful! Creating response...");
      responseObserver.onNext(
          UpdateBlogResponse.newBuilder()
              .setBlog(documentToBlog(replacement))
              .build()
      );
      responseObserver.onCompleted();
    }
  }

  private Blog documentToBlog(Document document) {
    return  Blog.newBuilder()
        .setAuthorId(document.getString("author_id"))
        .setTitle(document.getString("title"))
        .setContent(document.getString("content"))
        .setId(document.getObjectId("_id").toString())
        .build();
  }

  @Override
  public void deleteBlog(DeleteBlogRequest request, StreamObserver<DeleteBlogResponse> responseObserver) {
    System.out.println("Received Delete Blog Request");
    String blogId = request.getBlogId();
    DeleteResult result = null;

    try {
      result = collection.deleteOne(eq("_id", new ObjectId(blogId)));
    } catch (Exception e) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("Blog with id: " + blogId + " is not in proper format")
              .augmentDescription(e.getLocalizedMessage())
              .asRuntimeException()
      );
    }

    if (result.getDeletedCount() == 0) {
      System.out.println("Blog not found");
      responseObserver.onError(
          Status.NOT_FOUND
              .withDescription("Blog with id: " + blogId + " is not in proper format")
              .asRuntimeException()
      );
    } else {
      System.out.println("Blog was deleted");
      responseObserver.onNext(
          DeleteBlogResponse.newBuilder()
              .setBlogId(blogId)
              .build()
      );
    }
    responseObserver.onCompleted();
  }

  @Override
  public void listBlogs(ListBlogsRequest request, StreamObserver<ListBlogsResponse> responseObserver) {
    System.out.println("Received List Blog request");

    collection.find().iterator().forEachRemaining(document ->
        responseObserver.onNext(
            ListBlogsResponse.newBuilder()
                .setBlog(documentToBlog(document))
                .build()
        )
    );

    responseObserver.onCompleted();
  }
}
