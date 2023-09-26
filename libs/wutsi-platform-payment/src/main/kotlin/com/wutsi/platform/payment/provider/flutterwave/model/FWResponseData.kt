package com.wutsi.platform.payment.provider.flutterwave.model

data class FWResponseData(
    val id: Long = -1,
    val amount: Double = 0.0,
    val currency: String = "",
    val status: String = "",
    val account_number: String? = null,
    val bank_code: String? = null,
    val full_name: String? = null,
    val created_at: String? = null,
    val fee: Double? = null,
    val reference: String? = null,
    val narration: String? = null,
    val complete_message: String? = null,
    val requires_approval: Int? = null,
    val is_approved: Int? = null,
    val bank_name: String? = null,
    val tx_ref: String? = null,
    val flw_ref: String? = null,
    val app_fee: Double? = null,
    val merchant_fee: Double? = null,
    val fraud_status: String? = null,
    val charge_type: String? = null,
    val processor_response: String? = null,
    val customer: FWCustomer? = null,
    val meta: Any? = null,
) {
    fun metaAsMap(): Map<String, Any?>? =
        if (meta is Map<*, *>) { // Crapy API!!! They change the schema!!
            meta as Map<String, Any?>
        } else if (meta is List<*>) {
            if (meta[0] is Map<*, *>) {
                meta[0] as Map<String, Any?>
            } else {
                null
            }
        } else {
            null
        }
}
