package com.twitter.finagle.example.tls

import com.twitter.finagle.thrift.ThriftServerFramedCodec
import com.twitter.util.Future
import java.net.InetSocketAddress
import com.twitter.finagle.builder.ServerBuilder

import thrift.DogBeauty.FutureIface
import thrift.{DogBeauty, BeautifulDogResponse, BeautifulDogRequest}
import org.apache.thrift.protocol.TBinaryProtocol

object TLSServer {

  def main(args: Array[String]) {

    val processor = new FutureIface {
      override def isBreedBeautiful(request: BeautifulDogRequest): Future[BeautifulDogResponse] = {
        Future.value(new BeautifulDogResponse {
          override def name: String = request.name

          override def beautiful: Boolean = {
            request.breed != "pomeranian"
          }
        })
      }
    }

    val service = new DogBeauty.FinagledService(processor, new TBinaryProtocol.Factory())

    val server = ServerBuilder()
      .codec(ThriftServerFramedCodec())
      .tls(SslFile.serverCrt, SslFile.serverKey)
      .bindTo(new InetSocketAddress(8080))
      .name("TLSServer").build(service)
  }
}

