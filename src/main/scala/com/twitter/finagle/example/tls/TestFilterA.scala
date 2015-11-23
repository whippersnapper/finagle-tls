package com.twitter.finagle.example.tls

import java.security.cert.X509Certificate

import com.twitter.finagle.transport.Transport
import com.twitter.finagle.{Service, SimpleFilter, TransportException}

import com.twitter.finagle.Service
import com.twitter.finagle.thrift.ThriftClientRequest
import com.twitter.util.Future


class TestFilterA extends SimpleFilter[Array[Byte], Array[Byte]] {
  def apply(req: Array[Byte], service: Service[Array[Byte], Array[Byte]]): Future[Array[Byte]] = {
    println(this.getClass.getName + ": " + Transport.peerCertificate.get.asInstanceOf[X509Certificate].getSubjectDN.getName)
    service(req)
  }
}

