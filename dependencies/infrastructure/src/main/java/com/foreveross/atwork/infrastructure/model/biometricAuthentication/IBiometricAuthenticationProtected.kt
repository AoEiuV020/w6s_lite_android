package com.foreveross.atwork.infrastructure.model.biometricAuthentication

interface IBiometricAuthenticationProtected {

    fun getBiometricAuthenticationProtectItemTag(): BiometricAuthenticationProtectItemType?

    fun getBiometricAuthenticationProtectTags(): Array<BiometricAuthenticationProtectItemType>?

    fun commandProtected(getResult: (identifier: String, command: Boolean)-> Unit): Boolean = false
}