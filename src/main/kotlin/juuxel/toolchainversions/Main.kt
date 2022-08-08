package juuxel.toolchainversions

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java
import juuxel.toolchainversions.table.ArchitecturyTable
import juuxel.toolchainversions.table.FabricTable
import juuxel.toolchainversions.table.TableType
import juuxel.toolchainversions.util.ErrorScope
import kotlinx.coroutines.runBlocking
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.IVersionProvider
import picocli.CommandLine.Option
import kotlin.system.exitProcess

const val USER_AGENT = "Juuxel/architectury-versions"

@Command(name = "toolchain-versions", versionProvider = VersionProvider::class, mixinStandardHelpOptions = true)
class Main {
    @Option(names = ["-g", "--game-version"], required = false, description = ["The Minecraft version to query."])
    var gameVersion: String? = null

    private fun displayTable(type: TableType) = runBlocking {
        with(ErrorScope) {
            val client = HttpClient(Java)
            val table = type.createTable(client, gameVersion).bind()
            println(table.display())
        }
    }

    @Command(name = "architectury", aliases = ["arch"], description = ["Displays Architectury versions."])
    fun architectury() = displayTable(ArchitecturyTable)

    @Command(name = "fabric", description = ["Displays Fabric versions."])
    fun fabric() = displayTable(FabricTable)
}

class VersionProvider : IVersionProvider {
    override fun getVersion(): Array<String> {
        val version = VersionProvider::class.java.`package`.implementationVersion
        return if (version != null) arrayOf(version) else emptyArray()
    }
}

fun main(args: Array<String>) {
    val commandLine = CommandLine(Main())
    exitProcess(commandLine.execute(*args))
}
