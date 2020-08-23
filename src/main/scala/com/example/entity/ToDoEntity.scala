package com.example.entity

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class ToDoEntity(
    userId: Int,
    id: Int,
    title: String,
    completed: Boolean
) extends Entity

object ToDoEntity extends JsonEntity[ToDoEntity] {
  final val name = "ToDo"
  override val decoder: Decoder[ToDoEntity] = deriveDecoder[ToDoEntity]
  override val encoder: Encoder[ToDoEntity] = deriveEncoder[ToDoEntity]
}
