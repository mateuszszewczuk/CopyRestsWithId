package com.example.entity.`type`

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Address(
    street: String,
    suite: String,
    city: String,
    geo: Geo
)

object Address {
  trait AddressType extends Geo.GeoType {
    implicit val addressDecoder: Decoder[Address] = deriveDecoder[Address]
    implicit val addressEncoder: Encoder[Address] = deriveEncoder[Address]
  }
}
