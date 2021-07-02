import com.jaunt.*;
import java.io.*;

public class Example10{

  public static void main(String[] args) throws IOException{

    try{
      UserAgent userAgent = new UserAgent();      
      userAgent.sendGET("http://api.tumblr.com/v2/blog/puppygifs.tumblr.com/posts/");   
      System.out.println("Response:\n" + userAgent.response);  //print response data
    }
    catch(ResponseException e){                                //catch HTTP/Connection error
      HttpResponse response = e.getResponse();                 //or check userAgent.response
      if(response != null){                                    //print response data field by field
        System.err.println("Requested url: " + response.getRequestedUrlMsg()); //print the requested url
        System.err.println("HTTP error code: " + response.getStatus());        //print HTTP error code
        System.err.println("Error message: " + response.getMessage());         //print HTTP status message
      }
      else{
        System.out.println("Connection error, no response!");
      }
    } 
  }
}