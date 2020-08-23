package com.example.entity.`type`

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Geo(lat: Float, lng: Float)

object Geo {
  trait GeoType {
    implicit val geoDecoder: Decoder[Geo] = deriveDecoder[Geo]
    implicit val geoEncoder: Encoder[Geo] = deriveEncoder[Geo]
  }
}
