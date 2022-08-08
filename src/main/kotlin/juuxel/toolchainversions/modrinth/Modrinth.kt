package juuxel.toolchainversions.modrinth

import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.left
import arrow.core.right
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.http.userAgent
import juuxel.toolchainversions.util.Serializers
import juuxel.toolchainversions.USER_AGENT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.time.Instant

@Serializable
data class ModrinthVersion(
    @SerialName("version_number") val versionNumber: String,
    @Serializable(with = Serializers.ForInstant::class)
    @SerialName("date_published") val datePublished: Instant,
    @SerialName("game_versions") val gameVersions: List<String>,
)

object Modrinth {
    private val JSON = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getVersions(client: HttpClient, project: String): Either<String, List<ModrinthVersion>> {
        val response = client.get("https://api.modrinth.com/v2/project/$project/version") { userAgent(USER_AGENT) }
        if (!response.status.isSuccess()) {
            return "Unsuccessful response for $project: ${response.status}".left()
        }

        return JSON.decodeFromString(ListSerializer(ModrinthVersion.serializer()), response.bodyAsText()).right()
    }

    suspend fun getLatestVersion(
        client: HttpClient,
        project: String,
        gameVersion: String? = null
    ): Either<String, ModrinthVersion> = either {
        val versions = getVersions(client, project).bind().toMutableList()

        if (gameVersion != null) {
            versions.removeIf { gameVersion !in it.gameVersions }
        }

        versions.sortBy { it.datePublished }
        versions.lastOrNull() ?: "No versions found for $project".left().bind()
    }
}
