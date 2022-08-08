package juuxel.toolchainversions.table

import arrow.core.Either
import arrow.core.continuations.either
import io.ktor.client.HttpClient
import juuxel.toolchainversions.maven.MavenRepository
import juuxel.toolchainversions.modrinth.Modrinth
import juuxel.toolchainversions.util.getLatest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object FabricTable : TableType {
    private val MAVEN = MavenRepository("https://maven.fabricmc.net")

    override suspend fun createTable(client: HttpClient, gameVersion: String?): Either<String, Table> = coroutineScope {
        either {
            val api = async { Modrinth.getLatestVersion(client, "fabric-api", gameVersion) }
            val loader = async { MAVEN.getVersions(client, "net.fabricmc", "fabric-loader") }
            val loom = async { MAVEN.getVersions(client, "fabric-loom", "fabric-loom.gradle.plugin") }
            val flk = async { Modrinth.getLatestVersion(client, "fabric-language-kotlin")}

            table {
                row {
                    cell("Fabric API")
                    cell("Fabric Loader")
                }
                row {
                    cell(api.await().bind().versionNumber)
                    cell(getLatest(loader.await().bind()))
                }
                row {
                    cell("Fabric Loom")
                    cell("Fabric Language Kotlin")
                }
                row {
                    cell(getLatest(loom.await().bind()))
                    cell(flk.await().bind().versionNumber)
                }
            }
        }
    }
}