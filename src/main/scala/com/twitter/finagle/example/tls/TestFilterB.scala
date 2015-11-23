package com.twitter.finagle.example.tls

import java.security.cert.X509Certificate

import com.twitter.finagle.transport.Transport
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.Future

/**
  * Created by bling683 on 11/23/15.
  */
class TestFilterB extends SimpleFilter[Array[Byte], Array[Byte]] {
  def apply(req: Array[Byte], service: Service[Array[Byte], Array[Byte]]): Future[Array[Byte]] = {
    println(this.getClass.getName + ": " + Transport.peerCertificate.get.asInstanceOf[X509Certificate].getSubjectDN.getName)
    service(req)
  }
}
