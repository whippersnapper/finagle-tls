package com.twitter.finagle.example.tls

import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.ssl.Ssl
import com.twitter.finagle.thrift.{ThriftClientFramedCodec, ThriftClientRequest}
import java.net.InetSocketAddress
import com.twitter.finagle.transport.Transport
import com.twitter.finagle.{Thrift, Service}
import javax.net.ssl.{TrustManagerFactory, KeyManagerFactory, SSLContext}
import java.security.KeyStore

import com.twitter.util.Await
import org.apache.thrift.protocol.TBinaryProtocol
import thrift.{BeautifulDogRequest, DogBeauty}

/**
 * @author Panos Zhu
 *         Email panos.zhu@gmail.com
 */
object TLSClient {
  def main(args: Array[String]) {
    val client = Thrift.client
      .configured(Transport.TLSClientEngine(Some({
        case inet: InetSocketAddress => Ssl.client(createSslContext, inet.getHostName, inet.getPort)
        case _ => Ssl.client(createSslContext)
      })))
      .newIface[DogBeauty.FutureIface](":8080")


    val response1 = client.isBreedBeautiful(new BeautifulDogRequest {
      override def breed: String = "bull-dog"

      override def name: String = "Muffy"
    })

    val response2 = client.isBreedBeautiful(new BeautifulDogRequest {
      override def breed: String = "pomeranian"

      override def name: String = "Wuffins"
    })

    println(Await.result(response1))
    println(Await.result(response2))
  }

  private lazy val createSslContext = {
    val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    val trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm)
    val keyStore = KeyStore.getInstance(KeyStore.getDefaultType)
    val trustKeyStore = KeyStore.getInstance("JKS")
    keyStore.load(SslFile.keyStoreStream, SslFile.clientPassword.toCharArray)
    trustKeyStore.load(SslFile.trustKeyStoreStream, SslFile.clientPassword.toCharArray)
    keyManagerFactory.init(keyStore, SslFile.clientPassword.toCharArray)
    trustManagerFactory.init(trustKeyStore)
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, trustManagerFactory.getTrustManagers, null)
    sslContext
  }
}
