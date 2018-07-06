package example

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.lambda.model.{InvokeRequest, InvokeResult}
import com.amazonaws.services.lambda.{AWSLambda, AWSLambdaClientBuilder}
import java.nio.ByteBuffer
import java.nio.charset.Charset

object Hello extends App {

  val credentials: DefaultAWSCredentialsProviderChain = new DefaultAWSCredentialsProviderChain()
  val lambdaClient: AWSLambda = AWSLambdaClientBuilder.standard
    .withCredentials(credentials)
    .withRegion("ap-southeast-2")
    .build()

  def byteBufferToString(buffer:ByteBuffer, charset: Charset = Charset.forName("UTF-8")) = {
    var bytes =  Array[Byte]()
    if (buffer.hasArray) {
      bytes = buffer.array
    }
    else {
      val bytes = new Array[Byte](buffer.remaining)
      buffer.get(bytes)
    }
    new String(bytes, charset)
  }

  def createRequest(payload: String, functionName: String) ={
    new InvokeRequest().withFunctionName(functionName).withPayload(payload)
  }

  def getPayload(postcode:Int) =
    s""" {
      "query": {
        "postcode": ${postcode}
      }
    }"""
  def getJSONResponseForLambdaInvoke(postcode: Int, functionName: String) : Option[String] = {
    val invokeRequest= createRequest(getPayload(postcode), functionName)
    val res: InvokeResult = lambdaClient.invoke(invokeRequest)
    res.getFunctionError match{
      case null =>     Some(byteBufferToString(res.getPayload))
      case e =>        println(e) ; None
    }
  }
  println(getJSONResponseForLambdaInvoke(2140,"postcodeMapping-uat-getEntry"))
  println(getJSONResponseForLambdaInvoke(2140,"postcodeWeatherZone-uat-index"))

}