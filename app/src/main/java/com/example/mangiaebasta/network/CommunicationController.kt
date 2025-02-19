package com.example.mangiaebasta.network

import android.net.Uri
import android.util.Log
import com.example.mangiaebasta.model.DeliveryLocationWithSid
import com.example.mangiaebasta.model.Location
import com.example.mangiaebasta.model.Menu
import com.example.mangiaebasta.model.MenuDetail
import com.example.mangiaebasta.model.MenuImage
import com.example.mangiaebasta.model.OrderInfo
import com.example.mangiaebasta.model.ResponseError
import com.example.mangiaebasta.model.UserInfo
import com.example.mangiaebasta.model.UserResponse
import com.example.mangiaebasta.model.UserUpdateRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object CommunicationController {
    private const val BASE_URL = "https://develop.ewlab.di.unimi.it/mc/2425"
    private var sid: String? = null
    private val TAG = CommunicationController::class.simpleName

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    enum class HttpMethod {
        GET,
        POST,
        DELETE,
        PUT
    }

    private suspend fun genericRequest(
        url: String, method: HttpMethod,
        queryParameters: Map<String, Any> = emptyMap(),
        requestBody: Any? = null
    ): HttpResponse {

        val urlUri = Uri.parse(url)
        val urlBuilder = urlUri.buildUpon()
        queryParameters.forEach { (key, value) ->
            urlBuilder.appendQueryParameter(key, value.toString())
        }
        val completeUrlString = urlBuilder.build().toString()
        Log.d(TAG, completeUrlString)

        val request: HttpRequestBuilder.() -> Unit = {
            requestBody?.let {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        }

        val result = when (method) {
            HttpMethod.GET -> client.get(completeUrlString, request)
            HttpMethod.POST -> client.post(completeUrlString, request)
            HttpMethod.DELETE -> client.delete(completeUrlString, request)
            HttpMethod.PUT -> client.put(completeUrlString, request)
        }
        return result
    }

    suspend fun createUser(): UserResponse? {
        Log.d(TAG, "chiamo createUser")

        val url = "$BASE_URL/user"
        val httpResponse = genericRequest(url, HttpMethod.POST)

        return if (httpResponse.status.value in 200..299) {
            val result: UserResponse = httpResponse.body()
            Log.d(TAG, "Utente creato con successo")
            sid = result.sid
            result
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nella creazione dell'utente: ${error.message}")
            null
        }
    }

    suspend fun getMenu(userResponse: UserResponse, deliveryLocation: Location): List<Menu>? {
        Log.d(TAG, "chiamo getMenu")

        val url = "$BASE_URL/menu"
        val queryParams = mapOf(
            "lat" to deliveryLocation.lat,
            "lng" to deliveryLocation.lng,
            "sid" to userResponse.sid
        )

        return try {
            val httpResponse = genericRequest(url, HttpMethod.GET, queryParams)

            if (httpResponse.status.value in 200..299) {
                httpResponse.body<List<Menu>>()
            } else {
                Log.e(TAG, "Errore nel recupero del menu: ${httpResponse.body<ResponseError>().message}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Eccezione nel recupero del menu: ${e.message}")
            null
        }
    }

    suspend fun getMenuImage(userResponse: UserResponse, mid: Int): MenuImage? {
        val sid = userResponse.sid

        Log.d(TAG, "chiamo getMenuImage")
        val url = "$BASE_URL/menu/$mid/image"
        val queryParams = mapOf("sid" to sid)
        val httpResponse = genericRequest(url, HttpMethod.GET, queryParams)

        return if (httpResponse.status.value in 200..299) {
            val result: MenuImage = httpResponse.body()
            Log.d(TAG, "Immagine menu ottenuta con successo")
            result
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nel recupero dell'immagine del menu: ${error.message}")
            null
        }
    }

    suspend fun getMenuDetails(
        userResponse: UserResponse,
        mid: Int,
        deliveryLocation: Location
    ): MenuDetail? {
        Log.d(TAG, "chiamo getMenuDetails")
        val sid = userResponse.sid

        val queryParameters = mapOf(
            "sid" to sid,
            "lat" to deliveryLocation.lat,
            "lng" to deliveryLocation.lng
        )
        val url = "$BASE_URL/menu/$mid"
        val httpResponse = genericRequest(url, HttpMethod.GET, queryParameters)

        return if (httpResponse.status.value in 200..299) {
            val result: MenuDetail = httpResponse.body()
            Log.d(TAG, "Dettagli menu $mid ottenuti con successo")
            result
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nel recupero dei dettagli del menu: ${error.message}")
            null
        }
    }

    suspend fun getUserDetail(userResponse: UserResponse): UserInfo? {
        Log.d(TAG, "chiamo getUserDetail")
        val sid = userResponse.sid
        val queryParams = mapOf("sid" to sid)
        val url = "$BASE_URL/user/${userResponse.uid}"
        val httpResponse = genericRequest(url, HttpMethod.GET, queryParams)

        return if (httpResponse.status.value in 200..299) {
            val result: UserInfo = httpResponse.body()
            Log.d(TAG, "Dettagli utente ottenuti con successo")
            result
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nel recupero dei dettagli utente: ${error.message}")
            null
        }
    }

    suspend fun updateUserDetails(userResponse: UserResponse, userInfo: UserInfo): Boolean {
        Log.d(TAG, "chiamo updateUserDetails")

        val url = "$BASE_URL/user/${userResponse.uid}"

        val requestBody = UserUpdateRequest(
            firstName = userInfo.firstName,
            lastName = userInfo.lastName,
            cardFullName = userInfo.cardFullName,
            cardNumber = userInfo.cardNumber,
            cardExpireMonth = userInfo.cardExpireMonth,
            cardExpireYear = userInfo.cardExpireYear,
            cardCVV = userInfo.cardCVV,
            sid = userResponse.sid
        )

        val httpResponse = genericRequest(url, HttpMethod.PUT, requestBody = requestBody)

        return if (httpResponse.status.value in 200..299) {
            Log.d(TAG, "Dettagli utente aggiornati con successo")
            true
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nell'aggioranre dettagli utente: ${error.message}")
            false
        }
    }

    suspend fun placeOrder(mid: Int, userResponse: UserResponse, deliveryLocation: Location): OrderInfo? {
        Log.d(TAG, "chiamo placeOrder")
        val url = "$BASE_URL/menu/$mid/buy"
        val requestBody = DeliveryLocationWithSid(
            sid =  userResponse.sid,
            deliveryLocation = deliveryLocation
        )
        val httpResponse = genericRequest(url, HttpMethod.POST, requestBody = requestBody)

        if (httpResponse.status.value in 200..299) {
            val result: OrderInfo = httpResponse.body()
            Log.d(TAG, "Ordine effettuato con successo")
            return result
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nel piazzamento dell'ordine: ${error.message}")
            return null
        }
    }

    suspend fun getOrderDetails(userResponse: UserResponse, oid: Int): OrderInfo? {
        Log.d(TAG, "Chiamo getOrderDetails per oid: $oid")
        val url = "$BASE_URL/order/$oid"
        val queryParams = mapOf("sid" to userResponse.sid)
        val httpResponse = genericRequest(url, HttpMethod.GET, queryParams)

        if (httpResponse.status.value in 200..299) {
            val result: OrderInfo = httpResponse.body()
            Log.d(TAG, "Dettagli ordine ottenuti con successo")
            return result
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nel recupero dei dettagli dell'ordines: ${error.message}")
            return null
        }
    }

    suspend fun deleteLastOrder(userResponse: UserResponse): Boolean {
        Log.d(TAG, "Chiamo deleteLastOrder")
        val url = "$BASE_URL/order"
        val queryParams = mapOf("sid" to userResponse.sid)
        val httpResponse = genericRequest(url, HttpMethod.DELETE, queryParams)

        if (httpResponse.status.value in 200..299) {
            Log.d(TAG, "Ultimo oridne cancellato con successo")
            return true
        } else {
            val error: ResponseError = httpResponse.body()
            Log.e(TAG, "Errore nella cancellazione dell'ultimo ordine: ${error.message}")
            return false
        }
    }

}


