package com.wutsi.membership.access

import com.wutsi.membership.access.dto.CreateAccountRequest
import com.wutsi.membership.access.dto.CreateAccountResponse
import com.wutsi.membership.access.dto.EnableBusinessRequest
import com.wutsi.membership.access.dto.GetAccountDeviceResponse
import com.wutsi.membership.access.dto.GetAccountResponse
import com.wutsi.membership.access.dto.GetCategoryResponse
import com.wutsi.membership.access.dto.GetPlaceResponse
import com.wutsi.membership.access.dto.SaveAccountDeviceRequest
import com.wutsi.membership.access.dto.SaveCategoryRequest
import com.wutsi.membership.access.dto.SavePlaceRequest
import com.wutsi.membership.access.dto.SearchAccountRequest
import com.wutsi.membership.access.dto.SearchAccountResponse
import com.wutsi.membership.access.dto.SearchCategoryRequest
import com.wutsi.membership.access.dto.SearchCategoryResponse
import com.wutsi.membership.access.dto.SearchPlaceRequest
import com.wutsi.membership.access.dto.SearchPlaceResponse
import com.wutsi.membership.access.dto.UpdateAccountAttributeRequest
import com.wutsi.membership.access.dto.UpdateAccountStatusRequest
import feign.Headers
import feign.Param
import feign.RequestLine
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface MembershipAccessApi {
    @RequestLine("POST /v1/accounts")
    @Headers(value = ["Content-Type: application/json"])
    public fun createAccount(request: CreateAccountRequest): CreateAccountResponse

    @RequestLine("POST /v1/accounts/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchAccount(request: SearchAccountRequest): SearchAccountResponse

    @RequestLine("GET /v1/accounts/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getAccount(@Param("id") id: Long): GetAccountResponse

    @RequestLine("GET /v1/accounts/@{name}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getAccountByName(@Param("name") name: String): GetAccountResponse

    @RequestLine("POST /v1/accounts/{id}/status")
    @Headers(value = ["Content-Type: application/json"])
    public fun updateAccountStatus(@Param("id") id: Long, request: UpdateAccountStatusRequest): Unit

    @RequestLine("GET /v1/accounts/{id}/device")
    @Headers(value = ["Content-Type: application/json"])
    public fun getAccountDevice(@Param("id") id: Long): GetAccountDeviceResponse

    @RequestLine("POST /v1/accounts/{id}/device")
    @Headers(value = ["Content-Type: application/json"])
    public fun saveAccountDevice(@Param("id") id: Long, request: SaveAccountDeviceRequest): Unit

    @RequestLine("POST /v1/accounts/{id}/attributes")
    @Headers(value = ["Content-Type: application/json"])
    public fun updateAccountAttribute(@Param("id") id: Long, request: UpdateAccountAttributeRequest): Unit

    @RequestLine("POST /v1/accounts/{id}/business")
    @Headers(value = ["Content-Type: application/json"])
    public fun enableBusiness(@Param("id") id: Long, request: EnableBusinessRequest): Unit

    @RequestLine("DELETE /v1/accounts/{id}/business")
    @Headers(value = ["Content-Type: application/json"])
    public fun disableBusiness(@Param("id") id: Long): Unit

    @RequestLine("POST /v1/categories/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchCategory(request: SearchCategoryRequest): SearchCategoryResponse

    @RequestLine("GET /v1/categories/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getCategory(@Param("id") id: Long): GetCategoryResponse

    @RequestLine("POST /v1/categories/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun saveCategory(@Param("id") id: Long, request: SaveCategoryRequest): Unit

    @RequestLine("GET /v1/categories/import?language={language}")
    @Headers(value = ["Content-Type: application/json"])
    public fun importCategory(@Param("language") language: String): Unit

    @RequestLine("GET /v1/places/{id}")
    @Headers(value = ["Content-Type: application/json"])
    public fun getPlace(@Param("id") id: Long): GetPlaceResponse

    @RequestLine("POST /v1/places")
    @Headers(value = ["Content-Type: application/json"])
    public fun savePlace(request: SavePlaceRequest): Unit

    @RequestLine("POST /v1/places/search")
    @Headers(value = ["Content-Type: application/json"])
    public fun searchPlace(request: SearchPlaceRequest): SearchPlaceResponse

    @RequestLine("GET /v1/places/import?country={country}")
    @Headers(value = ["Content-Type: application/json"])
    public fun importPlace(@Param("country") country: String): Unit
}
