package com.example.entity

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class PostEntity(
    userId: Int,
    id: Int,
    title: String,
    body: String
) extends Entity

object PostEntity extends JsonEntity[PostEntity] {
  final val name = "Post"
  override val decoder: Decoder[PostEntity] = deriveDecoder[PostEntity]
  override val encoder: Encoder[PostEntity] = deriveEncoder[PostEntity]
}
