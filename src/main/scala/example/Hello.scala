package example

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.lambda.model.{InvokeRequest, InvokeResult}
import com.amazonaws.services.lambda.{AWSLambda, AWSLambdaClientBuilder}
import java.nio.ByteBuffer

object Hello extends App {

  val credentials: DefaultAWSCredentialsProviderChain = new DefaultAWSCredentialsProviderChain()
  val lambdaClient: AWSLambda = AWSLambdaClientBuilder.standard
    .withCredentials(credentials)
    .withRegion("ap-southeast-2")
    .build()
  val invokeRequest = new InvokeRequest
  invokeRequest.setPayload("{\"postcode\":\"2140\"}")
  invokeRequest.setFunctionName("postcodeWeatherZone-uat-index")
  val res: InvokeResult = lambdaClient.invoke(invokeRequest)
  import java.nio.charset.Charset

  def byteBufferToString(buffer:ByteBuffer, charset: Charset) = {
    var bytes =  Array[Byte]()
    if (buffer.hasArray) { bytes = buffer.array }
    else {
      val bytes = new Array[Byte](buffer.remaining)
      buffer.get(bytes)
    }
    new String(bytes, charset)
  }
  println(byteBufferToString(
    lambdaClient.invoke(invokeRequest).getPayload(),
    Charset.forName("UTF-8")));
  println(res.toString)

}