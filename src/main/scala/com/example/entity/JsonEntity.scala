package com.example.entity

import io.circe.{Decoder, Encoder}

trait JsonEntity[T <: Entity] {
  val name: String
  final type BaseType = T with Entity
  val decoder: Decoder[T]
  val encoder: Encoder[T]
}
