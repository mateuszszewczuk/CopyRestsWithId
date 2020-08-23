package com.example.flow

import java.io.{BufferedWriter, File, FileWriter}

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.example.entity.{Entity, JsonEntity}
import io.circe.Encoder


case class JsonDataFileFlowGenerator[T <: Entity](entity: JsonEntity[T], path: String) {
  implicit val encoder: Encoder[T] = entity.encoder
  val flow: Flow[T, T, NotUsed] = Flow[T].map(
    entity => {
      val file = new File(s"$path/${entity.id}.json")
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(encoder(entity).toString())
      bw.close()
      entity
    }
  )
}
