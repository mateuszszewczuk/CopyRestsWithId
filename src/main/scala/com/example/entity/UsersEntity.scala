package com.example.entity

import com.example.entity.`type`.{Address, Company}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UsersEntity(
    id: Int,
    name: String,
    email: String,
    address: Address,
    phone: String,
    website: String,
    company: Company
) extends Entity

object UsersEntity
    extends JsonEntity[UsersEntity]
    with Company.CompanyType
    with Address.AddressType {
  final val name = "User"
  override val decoder: Decoder[UsersEntity] = deriveDecoder[UsersEntity]
  override val encoder: Encoder[UsersEntity] = deriveEncoder[UsersEntity]
}
