package com.example.mangiaebasta.model

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val sid: String,
    val uid: Int
)

@Serializable
data class ResponseError(val message: String)

@Serializable
data class DeliveryLocationWithSid(
    val sid: String,
    val deliveryLocation: Location
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

@Serializable
data class Menu(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int
)

@Serializable
data class MenuImage(
    val base64: String
)


@Serializable
data class MenuDetail(
    val mid: Int,
    val name: String,
    val price: Double,
    val location: Location,
    val imageVersion: Int,
    val shortDescription: String,
    val deliveryTime: Int,
    val longDescription: String
)

@Serializable
data class UserInfo (
    val firstName: String? = null,
    val lastName: String? = null,
    val cardFullName: String? = null,
    val cardNumber: String? = null,
    val cardExpireMonth: Int? = null,
    val cardExpireYear: Int? = null,
    val cardCVV: String? = null,
    val uid: Int,
    val lastOid: Int? = null,
    val orderStatus: String? = null
)

@Serializable
data class UserUpdateRequest(
    val firstName: String?,
    val lastName: String? ,
    val cardFullName: String?,
    val cardNumber: String?,
    val cardExpireMonth: Int?,
    val cardExpireYear: Int?,
    val cardCVV: String?,
    val sid: String
)

@Serializable
data class OrderInfo(
    val oid: Int,
    val mid: Int,
    val uid: Int,
    val creationTimestamp: String,
    val status: String,
    val deliveryLocation: Location,
    val deliveryTimestamp: String? = null,
    val expectedDeliveryTimestamp: String? = null,
    val currentPosition: Location
)


