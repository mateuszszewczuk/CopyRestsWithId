package com.example

import akka.actor.ActorSystem
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.HttpEntity.Chunked
import akka.http.scaladsl.model.{ContentTypes, StatusCodes}
import akka.http.scaladsl.util.FastFuture
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike
import akka.http.scaladsl.model.HttpResponse
import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.example.entity.PostEntity
import com.example.source.JsonDataChunkedSourceGenerator

import scala.concurrent.Await
import akka.stream.scaladsl.JsonFraming.PartialObjectException
import io.circe.Decoder

import scala.concurrent.duration.DurationInt

class SourceTest
    extends TestKit(ActorSystem("MySpec"))
    with AnyWordSpecLike
    with ImplicitSender
    with BeforeAndAfterAll {
  final val jdcsg: JsonDataChunkedSourceGenerator[PostEntity] =
    JsonDataChunkedSourceGenerator(PostEntity, "test")

  "An Source" must {
    "should read one valid post" in {
      val chunk =
        "[{\"userId\":1,\"id\":1,\"title\":\"test\",\"body\":\"testBody\"}]"

      val response = FastFuture.successful {
        HttpResponse(
          status = StatusCodes.OK,
          entity = Chunked.fromData(
            ContentTypes.`text/plain(UTF-8)`,
            Source(
              List(
                ByteString(chunk)
              )
            )
          )
        )
      }

      implicit val de: Decoder[PostEntity] = PostEntity.decoder
      implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
        EntityStreamingSupport.json()

      Await.result(
        Await
          .result(jdcsg.fromResponse(response), 10.second)
          .runWith(
            Sink.foreach(result =>
              assert(result == PostEntity(1, 1, "test", "testBody"))
            )
          ),
        10.second
      )
    }
    "should throw error on partial buffer" in {
      val chunk = "[{\"userId\":1,\"id\":1,\"title\":]"

      val response = FastFuture.successful {
        HttpResponse(
          status = StatusCodes.OK,
          entity = Chunked.fromData(
            ContentTypes.`text/plain(UTF-8)`,
            Source(
              List(
                ByteString(chunk)
              )
            )
          )
        )
      }

      implicit val de: Decoder[PostEntity] = PostEntity.decoder
      implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
        EntityStreamingSupport.json()
      try {
        Await.result(
          Await
            .result(jdcsg.fromResponse(response), 10.second)
            .runWith(Sink.ignore),
          10.second
        )
        fail()
      } catch {
        case _: PartialObjectException =>
      }
    }
    "should handle split json" in {
      val chunk1 = "[{\"userId\":1,\"id\":1,\"title\":\"test\""
      val chunk2 = ",\"body\":\"testBody\"}]"

      val response = FastFuture.successful {
        HttpResponse(
          status = StatusCodes.OK,
          entity = Chunked.fromData(
            ContentTypes.`text/plain(UTF-8)`,
            Source(
              List(
                ByteString(chunk1),
                ByteString(chunk2)
              )
            )
          )
        )
      }

      implicit val de: Decoder[PostEntity] = PostEntity.decoder
      implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
        EntityStreamingSupport.json()

      Await.result(
        Await
          .result(jdcsg.fromResponse(response), 10.second)
          .runWith(
            Sink.foreach(result =>
              assert(result == PostEntity(1, 1, "test", "testBody"))
            )
          ),
        10.second
      )
    }
    "should handle multiple Jsons" in {
      val chunk1 =
        "[{\"userId\":1,\"id\":1,\"title\":\"test\",\"body\":\"testBody\"},"
      val chunk2 =
        "{\"userId\":2,\"id\":2,\"title\":\"test2\",\"body\":\"testBody2\"}]"

      val response = FastFuture.successful {
        HttpResponse(
          status = StatusCodes.OK,
          entity = Chunked.fromData(
            ContentTypes.`text/plain(UTF-8)`,
            Source(
              List(
                ByteString(chunk1),
                ByteString(chunk2)
              )
            )
          )
        )
      }

      implicit val de: Decoder[PostEntity] = PostEntity.decoder
      implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
        EntityStreamingSupport.json()

      val resultSeq: Seq[PostEntity] = Await.result(
        Await
          .result(jdcsg.fromResponse(response), 10.second)
          .runWith(Sink.seq[PostEntity]),
        10.second
      )
      assert(
        resultSeq.equals(
          Seq(
            PostEntity(1, 1, "test", "testBody"),
            PostEntity(2, 2, "test2", "testBody2")
          )
        )
      )
    }
  }
}
