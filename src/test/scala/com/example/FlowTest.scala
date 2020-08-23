package com.example

import java.io.File

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.{ImplicitSender, TestKit}
import com.example.entity.{Entity, PostEntity}
import com.example.flow.JsonDataFileFlowGenerator
import com.typesafe.config.{Config, ConfigFactory}
import io.circe.Decoder
import io.circe.jawn.decode
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

import scala.reflect.io.Directory

class FlowTest
    extends TestKit(ActorSystem("MySpec"))
    with AnyWordSpecLike
    with ImplicitSender
    with BeforeAndAfterAll {
  final val conf: Config = ConfigFactory.load("application.conf")
  final val testPath: String = conf.getString("main.testPath")
  final val testedFlow: Flow[Entity, Entity, NotUsed] =
    JsonDataFileFlowGenerator(PostEntity, testPath).flow
  final val (pub, sub) = TestSource
    .probe[Entity]
    .via(testedFlow)
    .toMat(TestSink.probe[Entity])(Keep.both)
    .run()
  final val p1 = PostEntity(1, 1, "testTitle", "testBody")
  final val p2 = PostEntity(2, 2, "testTitle", "testBody")
  final val p3 = PostEntity(1, 3, "testTitle", "testBody")
  final val p4 = PostEntity(2, 4, "testTitle", "testBody")

  override def afterAll: Unit = {
    val directory = new Directory(new File(testPath))
    if (directory.exists) {
      directory.deleteRecursively()
    }
  }

  "An writing to file flow" must {
    val directory = new Directory(new File(testPath))
    if (!directory.exists) {
      directory.createDirectory()
    }

    "not change any data" in {
      sub.request(n = 2)
      pub.sendNext(p1)
      pub.sendNext(p2)
      sub.expectNextUnordered(
        PostEntity(1, 1, "testTitle", "testBody"),
        PostEntity(2, 2, "testTitle", "testBody")
      )
    }
    "write a file" must {
      sub.request(n = 2)
      pub.sendNext(p3)
      pub.sendNext(p4)
      sub.expectNextUnordered(
        PostEntity(1, 3, "testTitle", "testBody"),
        PostEntity(2, 4, "testTitle", "testBody")
      )

      "file exist" in {
        assert(new File(s"./$testPath/3.json").exists())
        assert(new File(s"./$testPath/4.json").exists())
      }

      "file be valid json" in {
        implicit val decoder: Decoder[PostEntity] = PostEntity.decoder

        val source3 = scala.io.Source.fromFile(s"./$testPath/3.json")
        val post3 = decode[PostEntity](
          source3.mkString
        )
        source3.close

        val source4 = scala.io.Source.fromFile(s"./$testPath/4.json")
        val post4 = decode[PostEntity](
          source4.mkString
        )
        source4.close

        post3 match {
          case Left(_)      => fail()
          case Right(post3) => assert(post3 == p3)
        }
        post4 match {
          case Left(_)      => fail()
          case Right(post4) => assert(post4 == p4)
        }
      }
    }
  }
}
