package de.solarisbank.sdk.core.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


data class ErrorResponseDto(
    var errors: List<Error?>?,
    val backtrace: List<String>?,
    @Json(name = "next_step")var nextStep: String?
)

@JsonClass(generateAdapter = true)
data class Error(
        var id: String?,
        var status: Int?,
        var code: String?,
        var title : String?,
        var detail: String?
)

//{"errors":[
//    {"id":"f6cfcb5e-022f-4b16-bef7-1c85e9b8c21f",
//        "status":400,
//        "code":"invalid_iban",
//        "title":"Invalid IBAN",
//        "detail":"IBAN (DE33700812350002222222) is structurally invalid"}
//        ],
//        "backtrace":[
//    "IBAN (DE33700812350002222222) is structurally invalid",
//    "/app/lib/iban_bic_tools/iban/iban.rb:12:in `rescue in new'",
//    "/app/lib/iban_bic_tools/iban/iban.rb:7:in `new'",
//    "/app/app/services/iban.rb:42:in `lookup'",
//    "/app/app/services/concerns/authorize_actions.rb:111:in `call'",
//    "/app/app/services/concerns/authorize_actions.rb:111:in `block in authorize_action'",
//    "/app/app/api/v1/iban/lookup.rb:21:in `block in \u003cclass:Lookup\u003e'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:59:in `call'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:59:in `block (2 levels) in generate_api_method'",
//    "/usr/local/bundle/gems/activesupport-5.2.6/lib/active_support/notifications.rb:170:in `instrument'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:58:in `block in generate_api_method'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:341:in `execute'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:267:in `block in run'",
//    "/usr/local/bundle/gems/activesupport-5.2.6/lib/active_support/notifications.rb:170:in `instrument'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:247:in `run'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/endpoint.rb:322:in `block in build_stack'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/base.rb:36:in `call!'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/base.rb:29:in `call'",
//    "/usr/local/bundle/gems/solaris-auth-0.4.3/lib/solaris/middleware/auth_context.rb:21:in `call'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/error.rb:39:in `block in call!'",
//    "/usr/local/bundle/gems/grape-1.5.0/lib/grape/middleware/error.rb:38:in `catch'"
//        ]}