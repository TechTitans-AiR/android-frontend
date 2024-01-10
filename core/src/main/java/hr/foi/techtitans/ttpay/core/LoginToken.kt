package hr.foi.techtitans.ttpay.core

interface LoginToken {
    fun getAuthorizers(): Map<String, String>
}