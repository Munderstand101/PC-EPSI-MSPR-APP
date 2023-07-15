package dev.munderstand.pc_epsi_mspr_app.activities.common

class ApiConfig {
    companion object {
        const val BASE_URL = "http://88.125.155.66:32768/api"
        const val LOGIN_ENDPOINT = "$BASE_URL/login_check"
        const val REGISTER_ENDPOINT = "$BASE_URL/register"

        const val ACCOUNT_ENDPOINT = "$BASE_URL/main/account"


        const val PLANTS_ENDPOINT = "$BASE_URL/plante/"
        const val PLANT_ADD_ENDPOINT = "$BASE_URL/plante/add"

        const val BOTANIST_ENDPOINT = "$BASE_URL/botanist/"
//    val url = "http://127.0.0.1:8002/api/main/botaniste/"

        // Add other endpoints as needed
    }
}
