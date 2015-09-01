package com.twitter.finagle.example.tls

import java.security.cert.X509Certificate

import com.twitter.finagle.Thrift
import com.twitter.finagle.ssl.Ssl
import com.twitter.finagle.transport.Transport
import com.twitter.util.{Await, Future}

import thrift.{DogBeauty, BeautifulDogResponse, BeautifulDogRequest}

object TLSServer {

  def main(args: Array[String]) {

    val server = Thrift.server
      .configured(Transport.TLSServerEngine(Some(() => Ssl.server(SslFile.serverCrt, SslFile.serverKey, null, null, null))))
      .serveIface(":8080", new DogBeauty.FutureIface {
      override def isBreedBeautiful(request: BeautifulDogRequest): Future[BeautifulDogResponse] = {

        Future.value(new BeautifulDogResponse {
          override def name: String = request.name // Transport.peerCertificate.get.asInstanceOf[X509Certificate].getSubjectX500Principal.getName

          override def beautiful: Boolean = {
            request.breed != "pomeranian"
          }
        })
      }
    })
    Await.ready(server)
  }
}

