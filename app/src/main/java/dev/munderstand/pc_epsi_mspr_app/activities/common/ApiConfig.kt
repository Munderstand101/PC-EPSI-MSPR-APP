package dev.munderstand.pc_epsi_mspr_app.activities.common

class ApiConfig {
    companion object{
        const val BASE_URL_PHOTOS = "http://192.168.1.17:8000/photos/"
        const val BASE_URL_API = "http://192.168.1.17:8000/api"
        const val LOGIN_ENDPOINT = "$BASE_URL_API/login_check"
        const val REGISTER_ENDPOINT = "$BASE_URL_API/register"

        const val ACCOUNT_ENDPOINT = "$BASE_URL_API/main/account"

        const val REQUESTS_ENDPOINT = "$BASE_URL_API/requests/"
        const val REQUESTS_MY_ENDPOINT = "$BASE_URL_API/requests/my"
        const val REQUESTS_ADD_ENDPOINT = "$BASE_URL_API/requests/new"
        const val REQUESTS_DELETE_ENDPOINT = "$BASE_URL_API/requests/delete"
        const val REQUESTS_UPDATE_ENDPOINT = "$BASE_URL_API/requests/update"

        const val PLANTS_ENDPOINT = "$BASE_URL_API/plante/"
        const val PLANT_ADD_ENDPOINT = "$BASE_URL_API/plante/add"

        const val BOTANIST_ENDPOINT = "$BASE_URL_API/botanist/"
//    val url = "http://127.0.0.1:8002/api/main/botaniste/"

        // Add other endpoints as needed
    }
}
