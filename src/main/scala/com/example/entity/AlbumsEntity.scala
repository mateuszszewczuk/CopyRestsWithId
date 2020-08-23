package com.example.entity

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class AlbumsEntity(
    userId: Int,
    id: Int,
    title: String
) extends Entity

object AlbumsEntity extends JsonEntity[AlbumsEntity] {
  final val name = "Album"
  override val decoder: Decoder[AlbumsEntity] = deriveDecoder[AlbumsEntity]
  override val encoder: Encoder[AlbumsEntity] = deriveEncoder[AlbumsEntity]
}
