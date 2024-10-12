package ru.arturprgr.mybrowser.data

class SavesHelper(context: android.content.Context) {
    private val sPrefs = context.getSharedPreferences("sPrefs", android.content.Context.MODE_PRIVATE)

    private fun setPreference(key: String, value: Any) {
        sPrefs.edit().putString(key, "$value").apply()
    }

    private fun getPreference(key: String): String? {
        return sPrefs.getString(key, "0")
    }

    fun getQuantityBookmarks(): Int {
        return getPreference("quantityBookmarks")!!.toInt()
    }

    fun getQuantityDownloads(): Int {
        return getPreference("quantityDownloads")!!.toInt()
    }

    fun getQuantityHistory(): Int {
        return getPreference("quantityHistory")!!.toInt()
    }

    fun setQuantityBookmarks(value: Int) {
        setPreference("quantityBookmarks", value)
    }

    fun setQuantityDownloads(value: Int) {
        setPreference("quantityDownloads", value)
    }

    fun setQuantityHistory(value: Int) {
        setPreference("quantityHistory", value)
    }

    fun getAccount(): String {
        return getPreference("account")!!
    }

    fun getEmail(): String {
        return getPreference("email")!!
    }

    fun getName(): String {
        return getPreference("name")!!
    }

    fun setAccount(value: String) {
        setPreference("account", value)
    }

    fun setEmail(value: String) {
        setPreference("email", value)
    }

    fun setName(value: String) {
        setPreference("name", value)
    }
}