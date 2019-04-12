package cn.flyself.updateapp

data class UpdateEntity(
    var versionCode: Long,
    var versionName: String,
    var versionInfo: String,
    var downloadUrl: String,
    var apkSize: Long,
    var MD5: String
)