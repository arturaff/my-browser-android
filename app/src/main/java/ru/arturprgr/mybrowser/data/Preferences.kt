package ru.arturprgr.mybrowser.data

class Preferences(context: android.content.Context) {
    private val sPrefs = context.getSharedPreferences("sPrefs", android.content.Context.MODE_PRIVATE)

    fun setPreference(key: String, value: Any) {
        sPrefs.edit().putString(key, "$value").apply()
    }

    fun getPreference(key: String): String? {
        return sPrefs.getString(key, "")
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

    fun getQuantityCollections(): Int {
        return getPreference("quantityCollections")!!.toInt()
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

    fun setQuantityCollections(value: Int) {
        setPreference("quantityCollections", value)
    }

//    fun setCollections(array: ArrayList<String>) {
//        for (index in 0..array.size) {
//            if (array.size == 0) {
//                sPrefs.edit().putString("collection1", "*Нет коллекций*").apply()
//                break
//            } else {
//                sPrefs.edit().putString("collection$index", array[index]).apply()
//            }
//        }
//    }
//
//    fun getCollections(): ArrayList<String> {
//
//    }

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