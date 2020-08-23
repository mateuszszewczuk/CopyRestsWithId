package com.example.source

import akka.actor.ActorSystem
import akka.http.scaladsl.{Http, HttpExt}
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.common.JsonEntityStreamingSupport
import akka.http.scaladsl.model.HttpRequest
import com.example.entity.{Entity, JsonEntity}
import akka.http.scaladsl.unmarshalling._
import akka.stream.scaladsl.{Sink, Source}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import akka.http.scaladsl.model.HttpResponse

import scala.concurrent.ExecutionContext.Implicits.global
import io.circe.Decoder
import io.circe.jawn.decode

import scala.concurrent.Future

case class JsonDataChunkedSourceGenerator[T <: Entity](entity: JsonEntity[T], path: String)(implicit val actorSystem: ActorSystem){
  implicit val decoder: Decoder[T] = entity.decoder
  protected val http: HttpExt = Http()

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  def fromResponse(futureResponse: Future[HttpResponse]): Future[Source[T, Any]] = {
    futureResponse map { response =>
        response.entity.dataBytes
          .via(jsonStreamingSupport.framingDecoder)
          .mapAsync(1)(bytes => Unmarshal(bytes).to[T])
    }
  }

  def getFutureSource: Future[Source[Entity, Any]] = {
    fromResponse(http.singleRequest(HttpRequest(uri = path)))
  }
}
