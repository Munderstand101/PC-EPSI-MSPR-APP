package dev.munderstand.pc_epsi_mspr_app.activities.common

class ApiConfig {
    companion object{
        const val BASE_URL_PHOTOS = "http://88.125.155.66:32768/photos/"
        const val BASE_URL_API = "http://88.125.155.66:32768/api"
        const val LOGIN_ENDPOINT = "$BASE_URL_API/login_check"
        const val REGISTER_ENDPOINT = "$BASE_URL_API/register"

        const val ACCOUNT_ENDPOINT = "$BASE_URL_API/main/account"

        const val REQUESTS_ENDPOINT = "$BASE_URL_API/requests/"
        const val REQUESTS_MY_ENDPOINT = "$BASE_URL_API/requests/my/"
        const val REQUESTS_ADD_ENDPOINT = "$BASE_URL_API/requests/new"
        const val REQUESTS_DELETE_ENDPOINT = "$BASE_URL_API/requests/delete"
        const val REQUESTS_UPDATE_ENDPOINT = "$BASE_URL_API/requests/update"

        const val PLANTS_ENDPOINT = "$BASE_URL_API/plante/"
        const val PLANT_ADD_ENDPOINT = "$BASE_URL_API/plante/add"
        const val PLANT_SCAN_ENDPOINT = "$BASE_URL_API/plante/scan"

        const val BOTANIST_ENDPOINT = "$BASE_URL_API/botanist/"


        const val CONVERSATIONS_ENDPOINT = "$BASE_URL_API/chat/chats/"
        const val START_CONVERSATIONS_ENDPOINT = "$BASE_URL_API/chat/start_conv/3"
        const val MESSAGES_ENDPOINT = "$BASE_URL_API/chat/messages/"
        const val SEND_MESSAGES_ENDPOINT = "$BASE_URL_API/chat/send-message/"

        const val API_PLANT_ID_KEY = "zjvEs5ss0vRkBEiWgF8tiWYaEQfzp7uHycojAxbgtLZ8lAmuG9"

        // Add other endpoints as needed
    }
}
