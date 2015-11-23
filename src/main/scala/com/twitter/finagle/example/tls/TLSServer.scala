package com.twitter.finagle.example.tls

import java.security.KeyStore
import java.security.cert.X509Certificate
import javax.net.ssl.{SSLContext, TrustManagerFactory, KeyManagerFactory, SSLEngine}

import com.twitter.finagle.{Service}

import com.twitter.finagle.thrift.ThriftServerFramedCodec
import com.twitter.finagle.transport.Transport
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder
import gov.ic.cte.server.thrift.Hello.{FinagledService, FutureIface}
import org.apache.thrift.protocol.TBinaryProtocol.Factory


import org.apache.thrift.protocol.TBinaryProtocol

object TLSServer {

  def main(args: Array[String]) {

    val processor = new FutureIface {
      override def hi(): Future[String] = {
        Future.value(Transport.peerCertificate.get.asInstanceOf[X509Certificate].getSubjectDN.getName)
      }
    }

    val service = new FinagledService(processor, new Factory())
    val filters = Seq(new TestFilterA, new TestFilterB)
    val filteredService = filters.foldRight(service: Service[Array[Byte], Array[Byte]] )(_ andThen _)

    val server = ServerBuilder()
      .codec(ThriftServerFramedCodec())
      .newSslEngine(() => {
        val engine: SSLEngine = createSslContext.createSSLEngine()
        engine.setNeedClientAuth(true)
        engine
      })
      .bindTo(new InetSocketAddress(8080))
      .name("TLSServer").build(filteredService)

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

