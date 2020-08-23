package com.example.entity

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class PhotoEntity(
                        albumId: Int,
                        id: Int,
                        title: String,
                        url: String,
                        thumbnailUrl: String
                      ) extends Entity

object PhotoEntity extends JsonEntity[PhotoEntity]{
  final val name = "Photo"
  override val decoder: Decoder[PhotoEntity] = deriveDecoder[PhotoEntity]
  override val encoder: Encoder[PhotoEntity] = deriveEncoder[PhotoEntity]
}