package com.justin.grpc.blog.client;

import com.justin.grpc.greeting.client.GreetingRefactoredClient;
import com.justin.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class BlogClient {
  public static void main(String[] args) {
    System.out.println("---- Client for gRPC Blog Service ----");

    BlogClient main = new BlogClient();
    main.run();
  }

  private void run() {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50052)
        .usePlaintext()
        .build();

    BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

    // CREATE a blog
    Blog blog = Blog.newBuilder()
        .setAuthorId("Siobhan")
        .setTitle("Being the CEO of Waystar Atlantic")
        .setContent("Do CEO stuff all the time.")
        .build();

    CreateBlogResponse createBlogResponse = blogClient.createBlog(
        CreateBlogRequest.newBuilder()
            .setBlog(blog)
            .build()
    );

    System.out.println("Received create blog response");
    System.out.println(createBlogResponse.toString());

    // READ blog that was created above
    System.out.println("Reading blog...");
    String blogId = createBlogResponse.getBlog().getId();

    ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
        .setBlogId(blogId)
        .build());
    System.out.println(readBlogResponse.toString());

    // READ blog that does not exist
//    System.out.println("Reading blog that does not exist...");
//    ReadBlogResponse readBlogResponseNotFound = blogClient.readBlog(ReadBlogRequest.newBuilder()
//        .setBlogId("5d62dc4129f4cf48f51595c5") // non-existing
//        .setBlogId("fake-id") // improper format
//        .build());

    // UPDATE/replace blog by id
    Blog updatedBlog = Blog.newBuilder()
        .setId(blogId)
        .setAuthorId("Changed Author")
        .setTitle("Updated Title!")
        .setContent("This blog has been updated")
        .build();

    System.out.println("Updating blog...");
    UpdateBlogResponse updateBlogResponse = blogClient.updateBlog(
        UpdateBlogRequest.newBuilder()
            .setBlog(updatedBlog)
            .build()
    );

    System.out.println("Blog has been updated!");
    System.out.println(updateBlogResponse.toString());

    // DELETE
    System.out.println("Deleting blog...");
    DeleteBlogResponse deleteBlogResponse = blogClient.deleteBlog(
        DeleteBlogRequest.newBuilder()
            .setBlogId(blogId)
            .build()
    );
    System.out.println("Blog deleted!");

//    System.out.println("Reading the deleted blog for testing...");
//    ReadBlogResponse readBlogResponseAfterDeletion = blogClient.readBlog(ReadBlogRequest.newBuilder()
//        .setBlogId(blogId)
//        .build());

    // LIST Blogs
    blogClient.listBlogs(ListBlogsRequest.newBuilder().build()).forEachRemaining(
        listBlogsResponse -> System.out.println(listBlogsResponse.getBlog().toString())
    );
  }
}
