package com.twitter.finagle.example.tls

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLEngine, SSLContext, TrustManagerFactory, KeyManagerFactory}

import com.twitter.finagle.Thrift
import com.twitter.finagle.ssl.{Engine, Ssl}
import com.twitter.finagle.transport.Transport
import com.twitter.util.{Await, Future}

import thrift.{DogBeauty, BeautifulDogResponse, BeautifulDogRequest}

object TLSServer {

  def main(args: Array[String]) {

    val server = Thrift.server
      .configured(Transport.TLSServerEngine(Some(() => {
        val engine: SSLEngine = createSslContext.createSSLEngine()
        engine.setNeedClientAuth(true)
        new Engine(engine)
      })))
      .serveIface(":8080", new DogBeauty.FutureIface {
      override def isBreedBeautiful(request: BeautifulDogRequest): Future[BeautifulDogResponse] = {

        Future.value(new BeautifulDogResponse {
          override def name: String = Transport.peerCertificate.get.asInstanceOf[X509Certificate].getSubjectX500Principal.getName

          override def beautiful: Boolean = {
            request.breed != "pomeranian"
          }
        })
      }
    })
    Await.ready(server)
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

