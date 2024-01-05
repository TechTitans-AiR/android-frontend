package hr.foi.techtitans.ttpay.accountManagement.model_accountManagement

data class UpdateUserProfileRequest(
    val updatedFields: Map<String, String>
)