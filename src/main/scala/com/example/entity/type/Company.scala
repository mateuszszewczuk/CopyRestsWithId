package com.example.entity.`type`

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Company(name: String, catchPhrase: String, bs: String)

object Company {
  trait CompanyType {
    implicit val companyDecoder: Decoder[Company] = deriveDecoder[Company]
    implicit val companyEncoder: Encoder[Company] = deriveEncoder[Company]
  }
}
