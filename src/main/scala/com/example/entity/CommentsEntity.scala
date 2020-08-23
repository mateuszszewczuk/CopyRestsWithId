package com.example.entity

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class CommentsEntity(
                           postId: Int,
                           id: Int,
                           name: String,
                           email: String,
                           body: String
                         )  extends Entity

object CommentsEntity extends JsonEntity[CommentsEntity]{
  final val name = "Comment"
  override val decoder: Decoder[CommentsEntity] = deriveDecoder[CommentsEntity]
  override val encoder: Encoder[CommentsEntity] = deriveEncoder[CommentsEntity]
}
