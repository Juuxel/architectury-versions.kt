package juuxel.toolchainversions.table

import arrow.core.Either
import arrow.core.continuations.either
import io.ktor.client.HttpClient
import juuxel.toolchainversions.maven.MavenRepository
import juuxel.toolchainversions.modrinth.Modrinth
import juuxel.toolchainversions.util.getLatest
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object ArchitecturyTable : TableType {
    private val MAVEN = MavenRepository("https://maven.architectury.dev")

    override suspend fun createTable(client: HttpClient, gameVersion: String?): Either<String, Table> = coroutineScope {
        either {
            val api = async { Modrinth.getLatestVersion(client, "architectury-api", gameVersion) }
            val loom = async {
                MAVEN.getVersions(client, "dev.architectury.loom", "dev.architectury.loom.gradle.plugin")
            }
            val plugin = async {
                MAVEN.getVersions(client, "architectury-plugin", "architectury-plugin.gradle.plugin")
            }
            val injectables = async {
                MAVEN.getVersions(client, "dev.architectury", "architectury-injectables")
            }

            table {
                row {
                    cell("Architectury API")
                    cell("Architectury Loom")
                }
                row {
                    cell(api.await().bind().versionNumber.substringBeforeLast('+'))
                    cell(getLatest(loom.await().bind()))
                }
                row {
                    cell("Architectury Plugin")
                    cell("Architectury Injectables")
                }
                row {
                    cell(getLatest(plugin.await().bind()))
                    cell(getLatest(injectables.await().bind()))
                }
            }
        }
    }
}