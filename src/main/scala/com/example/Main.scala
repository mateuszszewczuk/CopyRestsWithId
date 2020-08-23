package com.example

import java.io.File

import akka.actor.ActorSystem
import akka.stream.scaladsl.Sink
import com.example.entity.{
  AlbumsEntity,
  CommentsEntity,
  Entity,
  JsonEntity,
  PhotoEntity,
  PostEntity,
  UsersEntity
}
import com.example.flow.JsonDataFileFlowGenerator
import com.example.source.JsonDataChunkedSourceGenerator
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.typesafe.config.{Config, ConfigFactory}

object Main extends App {
  final val conf: Config = ConfigFactory.load("application.conf")
  final implicit val actorSystem: ActorSystem = ActorSystem("Main")
  private val basePath = conf.getString("main.basePath")

  val entitiesToGet: List[(JsonEntity[_ <: Entity], String)] = List(
    (PostEntity, "https://jsonplaceholder.typicode.com/posts"),
    (PhotoEntity, "https://jsonplaceholder.typicode.com/photos"),
    (CommentsEntity, "https://jsonplaceholder.typicode.com/comments"),
    (AlbumsEntity, "https://jsonplaceholder.typicode.com/albums"),
    (UsersEntity, "https://jsonplaceholder.typicode.com/users")
  )

  entitiesToGet.foreach(entity => {
    val directory = new File(s"$basePath/${entity._1.name}/")
    if (!directory.exists) {
      directory.mkdirs()
    }
  })

  val flows = entitiesToGet.map(entity => {
    val source =
      JsonDataChunkedSourceGenerator(entity._1, entity._2).getFutureSource
    val sink =
      JsonDataFileFlowGenerator(entity._1, s"$basePath/${entity._1.name}/")
    source.map(_.via(sink.flow).runWith(Sink.ignore)).flatten
  })

  Future.sequence(flows).onComplete(_ => System.exit(0))
}
